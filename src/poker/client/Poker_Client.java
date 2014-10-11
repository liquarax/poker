/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poker.client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import poker.*;
import poker.communication.CommunicationCommons;

/**
 * Hrafický klient serveru
 *
 * @author Jaroslav Brabec
 */
public class Poker_Client {

    private static boolean onMove;
    private static boolean moved;
    private static String myMove;
    private volatile static LargeCardPanel centralCp;
    private volatile static SmallCardPanel rightCp;
    private static ArrayList<SmallCardPanel> CounterPlayers;
    private static ArrayList<Card> cards, hidecards;
    private static JLabel chips, bet, lmove, end, pot;
    private static TextField raise;

    /**
     * metoda simulující průběh hry, přijímá instrukce, od serveru
     *
     * @param in příjímané informace soketem
     * @param out odesílané informace
     * @throws IOException
     */
    public static void game(BufferedReader in, PrintWriter out) throws IOException {
        String s;
        int winner = 0, me;
        cards = new ArrayList<Card>();
        hidecards = new ArrayList<Card>();
        onMove = false;
        //prijmu id
        me = Integer.parseInt(in.readLine());
        while (true) {
            s = in.readLine();
            System.out.println(s);
            if (s.equals("chips")) {    //pocet zetonnu na zacatku hry
                while (chips == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                    }
                }
                chips.setText(in.readLine());
            }
            if (s.equals("winning player")) {
                winner = Integer.parseInt(in.readLine());
            }
            if (s.equals("winning cards") && winner != me) {
                try {
                    if (winner == 3) //pripad pro 4 hrace
                    {
                        winner = me;
                    }
                    hidecards.clear();
                    hidecards.add(new Card(in.readLine()));
                    hidecards.add(new Card(in.readLine()));
                    CounterPlayers.get(winner).set(hidecards);
                    Thread.sleep(5000);
                    // CounterPlayers.get(0).clear();
                    CounterPlayers.get(winner).clear();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Poker_Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (s.equals("You have won!")) {
                lmove.setText("You have won!");
            }
            if (s.equals("You have lost!")) {
                lmove.setText("You have lost!");
            }
            if (s.equals("hide cards")) {
                try {
                    hidecards.clear();
                    hidecards.add(new Card(in.readLine()));
                    hidecards.add(new Card(in.readLine()));
                    while (rightCp == null) //tohle je pekna prasarna, ale funguje to 
                    {
                        Thread.sleep(100);
                    }
                    rightCp.set(hidecards);
                    centralCp.clear();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Poker_Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (s.equals("flop")) {
                cards.clear();
                cards.add(new Card(in.readLine()));
                cards.add(new Card(in.readLine()));
                cards.add(new Card(in.readLine()));
                centralCp.set();
            }
            if (s.equals("turn") || s.equals("river")) {
                cards.add(new Card(in.readLine()));
                centralCp.set();
            }
            if (s.equals(CommunicationCommons.potISMessage)){
                pot.setText(in.readLine());
            }
            if (s.equals("bet is")) {   //kdyz prijde sazka spousti se interakce
                onMove = true;
                moved = false;
                lmove.setText("It's your turn!");
                bet.setText(in.readLine());
                while (!moved) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                    }
                }
                out.println(myMove);
                if (myMove.equals("raise")) {
                    out.println(Integer.parseInt(bet.getText()) + Integer.parseInt(raise.getText()));
                }
                onMove = false;
                lmove.setText("Wait for other players");
            }
        }
    }

    /**
     * zajistí spojení klienta a serveru
     */
    private static void ServerConection(String address) {
        Socket s;
        String str;
        try {
            s = new Socket(InetAddress.getByName(address), 7777);
            try {
//                    GameThread GT=new GameThread(new BufferedReader(new InputStreamReader(s.getInputStream())),(new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true)));
//                    GT.start();
                game(new BufferedReader(new InputStreamReader(s.getInputStream())), new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true));
            } finally {
                s.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Poker_Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * zobrazení karet
     */
    private static class LargeCardPanel extends JPanel {

        private ArrayList<JLabel> jcd = new ArrayList<JLabel>();

        public LargeCardPanel(LayoutManager l) {
            super(l);
            for (int i = 0; i < 5; i++) {
                JLabel cd = new JLabel();
                cd.setText("XX");
                cd.setOpaque(true);
                cd.setBackground(Color.BLACK);
                cd.setForeground(Color.red);
                cd.setFont(new Font("Serif", Font.PLAIN, 30));
                jcd.add(cd);
                this.add(cd);
            }
        }

        @Override
        public void paint(Graphics grphcs) {
            super.paint(grphcs); //To change body of generated methods, choose Tools | Templates. 
        }

        public void clear() {
            this.setBackground(Color.getHSBColor((float) 0.5, (float) 0.5, (float) 0.7));
            for (JLabel jd : jcd) {
                jd.setText("XX");
                jd.setOpaque(true);
                jd.setBackground(Color.BLACK);
                jd.setForeground(Color.red);
            }
        }

        public void set() {
            this.setBackground(Color.getHSBColor((float) 0.5, (float) 0.5, (float) 0.7));
            int i = 0;
            for (Card c : cards) {
                jcd.get(i).setText(c.toString());
                jcd.get(i).setOpaque(true);
                jcd.get(i).setBackground(Color.WHITE);
                jcd.get(i).setForeground(c.getColor().toInt() < 2 ? Color.red : Color.BLACK);
                i++;
            }
        }
    }

    /**
     * zobrazení hide cards
     */
    private static class SmallCardPanel extends JPanel {

        private ArrayList<JLabel> jcd = new ArrayList<JLabel>();

        public SmallCardPanel(LayoutManager l) {
            super(l);
            for (int i = 0; i < 2; i++) {
                JLabel cd = new JLabel();
                cd.setText("XX");
                cd.setOpaque(true);
                cd.setBackground(Color.BLACK);
                cd.setForeground(Color.red);
                cd.setFont(new Font("Serif", Font.PLAIN, 30));
                jcd.add(cd);
                this.add(cd);
            }
        }

        public void clear() {
            this.setBackground(Color.getHSBColor((float) 0.5, (float) 0.5, (float) 0.7));
            for (JLabel jd : jcd) {
                jd.setText("XX");
                jd.setOpaque(true);
                jd.setBackground(Color.BLACK);
                jd.setForeground(Color.red);
            }
        }

        @Override
        public void paint(Graphics grphcs) {
            super.paint(grphcs); //To change body of generated methods, choose Tools | Templates. 
        }

        public void set(ArrayList<Card> cds) {
            this.setBackground(Color.getHSBColor((float) 0.5, (float) 0.5, (float) 0.7));
            int i = 0;
            for (Card c : cds) {
                jcd.get(i).setText(c.toString());
                jcd.get(i).setOpaque(true);
                jcd.get(i).setBackground(Color.WHITE);
                jcd.get(i).setForeground(c.getColor().toInt() < 2 ? Color.red : Color.BLACK);
                i++;
            }
        }
    }

//    private static JPanel InitCards(){
//        JPanel p=new JPanel(new GridLayout(1, 5,4,0));
//        p.setBackground(Color.getHSBColor((float) 0.5,(float) 0.5,(float) 0.7));
//        cdl=new ArrayList<CardLabel>(5);
//        for (int i = 0; i < 5; i++) {
//            cdl.add(new CardLabel());
//            cdl[i].save("2♥");
//            p.add(cdl[i]);
//        }
//        
//        return p;
//    }
    /**
     * Připraví gui
     */
    private static void createAndShowGui() {
        JFrame window = new JFrame("Poker");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel(new BorderLayout());

        JPanel centralPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        centralPanel.setBackground(Color.getHSBColor((float) 0.5, (float) 0.5, (float) 0.7));

        final JLabel caption1 = new JLabel("Texas Hold'em");
        final JLabel caption2 = new JLabel("Poker");
        final JLabel caption3 = new JLabel("Chips: ");
        final JLabel caption4 = new JLabel("Bet is: ");
        final JLabel potIsLabel = new JLabel("Pot is: ");
        caption1.setFont(new Font("Jokerman", Font.PLAIN, 48));
        caption2.setFont(new Font("Jokerman", Font.PLAIN, 48));
        bet = new JLabel("0");
        pot = new JLabel("0");
        lmove = new JLabel("Wait for other players");

        centralCp = new LargeCardPanel(new GridLayout(1, 5, 4, 0));
        rightCp = new SmallCardPanel(new GridLayout(1, 2, 4, 0));
        CounterPlayers = new ArrayList<SmallCardPanel>();
        CounterPlayers.add(new SmallCardPanel(new GridLayout(1, 2, 4, 0)));
        CounterPlayers.add(new SmallCardPanel(new GridLayout(1, 2, 4, 0)));
        CounterPlayers.add(new SmallCardPanel(new GridLayout(1, 2, 4, 0)));

        c.gridwidth = GridBagConstraints.REMAINDER;
        centralPanel.add(caption1, c);
        centralPanel.add(centralCp, c);
        centralPanel.add(caption2, c);
        centralPanel.add(caption4);
        centralPanel.add(bet);
        centralPanel.add(lmove, c);
        centralPanel.add(potIsLabel, c);
        centralPanel.add(pot, c);

        JPanel controler = new JPanel(); //ma implicitni flow layout
        controler.setBackground(Color.getHSBColor((float) 0.5, (float) 0.5, (float) 0.7));

        JButton buttonRaise = new JButton("Raise");
        JButton buttonCall = new JButton("Call/Check");
        JButton buttonFold = new JButton("Fold");
        JButton buttonAll = new JButton("All in");

        //akce
        buttonCall.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (onMove) {
                    myMove = "call";
                    Integer chipsLeft = Integer.parseInt(chips.getText()) - Integer.parseInt(bet.getText());
                    chips.setText(chipsLeft.toString());
                    moved = true;
                }
            }
        });

        buttonRaise.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (onMove) {
                    myMove = "raise";
                    Integer chipsLeft = Integer.parseInt(chips.getText()) - Integer.parseInt(bet.getText()) - Integer.parseInt(raise.getText());
                    chips.setText(chipsLeft.toString());
                    moved = true;
                }
            }
        });

        buttonFold.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (onMove) {
                    myMove = "fold";

                    moved = true;
                }
            }
        });

        buttonAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (onMove) {
                    myMove = "all in";
                    chips.setText("0");
                    moved = true;
                }
            }
        });

        chips = new JLabel("2000");
        raise = new TextField("10");


        controler.add(raise);
        controler.add(buttonRaise);
        controler.add(buttonCall);
        controler.add(buttonFold);
        controler.add(buttonAll);
        controler.add(caption3);
        controler.add(chips);

        JPanel rightPanel = new JPanel(new GridLayout(2, 1));
        rightPanel.setBackground(Color.getHSBColor((float) 0.5, (float) 0.5, (float) 0.7));
        rightPanel.add(rightCp);
        rightPanel.add(CounterPlayers.get(0));

        JPanel leftPanel = new JPanel(new GridLayout(2, 1));
        leftPanel.setBackground(Color.getHSBColor((float) 0.5, (float) 0.5, (float) 0.7));
        leftPanel.add(CounterPlayers.get(1));
        leftPanel.add(CounterPlayers.get(2));

        panel.add(centralPanel, BorderLayout.CENTER);
        panel.add(controler, BorderLayout.SOUTH);
        panel.add(rightPanel, BorderLayout.EAST);
        panel.add(leftPanel, BorderLayout.WEST);

        window.getContentPane().add(panel);
        window.setSize(200, 200);
        window.pack();
        window.setVisible(true);

    }

    public static void main(String[] args) {
        String ip;
        
        WelcomeFrame wFrame = new WelcomeFrame();
        
        ip = wFrame.getIp();
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGui();
            }
        });
        
        
        
        ServerConection(ip);//spojime se se serverem
        
    }
}

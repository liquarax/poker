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
import java.lang.reflect.InvocationTargetException;
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
public class PokerClient {

    private static boolean onMove;
    private static boolean moved;
    private static String myMove;
    private static String winOrLostResault;
    private volatile static LargeCardPanel centralCp;
    private volatile static SmallCardPanel rightCp;
    private static ArrayList<SmallCardPanel> counterPlayers;
    private static ArrayList<Card> cards, hidecards;
    private static JLabel chips, bet, lmove, end, pot;
    private static TextField raise;
    private static int myLastBetInRound;

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
        winOrLostResault = "";
        myLastBetInRound = 0;
        //prijmu id
        me = Integer.parseInt(in.readLine());
        System.out.println(me);
        try {
            while (true) {
                s = in.readLine();
                if(s==null)
                    throw new SocketException("Connection terminated");
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

                if (s.equals(CommunicationCommons.showingCardsMessage)) {
                    int showingCount = Integer.parseInt(in.readLine());
                    for (int i = 0; i < showingCount; i++) {
                        s = in.readLine();
                        System.out.println(s);
                        if (s.equals("winning player")) {
                            winner = Integer.parseInt(in.readLine());
                            s = in.readLine();
                        }
                        if (s.equals("winning cards")) {
                            if (winner != me) {
                                if (winner > me) {
                                    winner--;
                                }
                                hidecards.clear();
                                hidecards.add(new Card(in.readLine()));
                                hidecards.add(new Card(in.readLine()));
                                counterPlayers.get(winner).set(hidecards, Color.YELLOW);
                            } else {
                                //2x prectu sve karty
                                hidecards.clear();
                                hidecards.add(new Card(in.readLine()));
                                hidecards.add(new Card(in.readLine()));
                                rightCp.set(hidecards, Color.GREEN);
                            }
                            continue;
                        }
                        int looser = -1;
                        if (s.equals(CommunicationCommons.loosingPlayerIdMessage)) {
                            looser = Integer.parseInt(in.readLine());
                            s = in.readLine();
                        }
                        if (s.equals(CommunicationCommons.loosingCardsMessage)) {
                            if (looser != me) {
                                if (looser > me) {
                                    looser--;
                                }
                                hidecards.clear();
                                hidecards.add(new Card(in.readLine()));
                                hidecards.add(new Card(in.readLine()));
                                counterPlayers.get(looser).set(hidecards, Color.WHITE);
                            } else {
                                //2x prectu sve karty
                                in.readLine();
                                in.readLine();
                            }
                        }
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(PokerClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    for (int i = 0; i < counterPlayers.size(); i++) {
                        counterPlayers.get(i).clear();
                    }
                }


                if (s.equals("You have won!")) {
                    winOrLostResault="You have won!";
                    lmove.setText("You have won!");
                    out.println(CommunicationCommons.communicationEndedMessage);
                }
                if (s.equals("You have lost!")) {
                    winOrLostResault="You have lost!";
                    lmove.setText("You have lost!");
                    out.println(CommunicationCommons.communicationEndedMessage);
                }
                if (s.equals("hide cards")) { //zacina dalsi kolo hry
                    try {
                        myLastBetInRound = 0;
                        hidecards.clear();
                        hidecards.add(new Card(in.readLine()));
                        hidecards.add(new Card(in.readLine()));
                        while (rightCp == null) //tohle je pekna prasarna, ale funguje to 
                        {
                            Thread.sleep(100);
                        }
                        rightCp.set(hidecards, Color.WHITE);
                        centralCp.clear();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(PokerClient.class.getName()).log(Level.SEVERE, null, ex);
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
                if (s.equals(CommunicationCommons.setBlindMessage)) {
                    myLastBetInRound = Integer.parseInt(in.readLine());
                    Integer chipsLeft = Integer.parseInt(chips.getText()) - myLastBetInRound;
                    chips.setText(chipsLeft.toString());
                }
                if (s.equals(CommunicationCommons.potIsMessage)) {
                    pot.setText(in.readLine());
                }
                if (s.equals("bet is")) {   //kdyz prijde sazka spousti se interakce
                    onMove = true;
                    moved = false;
                    lmove.setText("It's your turn!");
                    int betSize = Integer.parseInt(in.readLine());
                    bet.setText(Integer.toString(betSize - myLastBetInRound));
                    while (!moved) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                        }
                    }
                    out.println(myMove);
                    if (myMove.equals("raise")) {
                        out.println(betSize + Integer.parseInt(raise.getText()));
                    }
                    onMove = false;
                    lmove.setText("Wait for other players");
                }
            }
        } catch (SocketException se) {
            se.getMessage();
            //pohlceni vyjimky. hra konci
        }
    }

    /**
     * zajistí spojení klienta a serveru
     */
    private static void ServerConection(String address) {
        Socket s;
        try {
            s = new Socket(InetAddress.getByName(address), 7777);
            try {
                game(new BufferedReader(new InputStreamReader(s.getInputStream())), new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true));
            } finally {
                s.close();
            }
        } catch (IOException ex) {
            //pohltim vyjimku, v dalsim kodu vim, ze se nemohu pripojit na server
            //Logger.getLogger(PokerClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(winOrLostResault == null || winOrLostResault.equals(""))
            lmove.setText("Cannot conect to server!");
        else
            lmove.setText("Game ended:" + winOrLostResault);
    }

    /**
     * Třída pro zobrazení karet.
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
            super.paint(grphcs);  
        }

        /**
         * Zobrazí rub karty.
         */
        public void clear() {
            this.setBackground(Color.getHSBColor((float) 0.5, (float) 0.5, (float) 0.7));
            for (JLabel jd : jcd) {
                jd.setText("XX");
                jd.setOpaque(true);
                jd.setBackground(Color.BLACK);
                jd.setForeground(Color.red);
            }
        }

        /**
         * Zobrazí líc karty.
         */
        public void set() {
            this.setBackground(Color.getHSBColor((float) 0.5, (float) 0.5, (float) 0.7));
            int i = 0;
            for (Card c : cards) {
                jcd.get(i).setText(c.toSymbol());
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

        protected ArrayList<JLabel> jcd = new ArrayList<JLabel>();

        /**
         * Konstruktor přednastavá vzhled
         * @param l LayoutManager
         * @param c Barva front-endu barvy
         */
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

        /**
         * Zobrazí rub karet
         */
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

        /**
         * Zobrazí líc karet
         * @param cds karty
         * @param frontEndCardColor Barva podkladu karty. Používá se: Bílá - standard, Žlutá - soupeř vyhrál, Zelená - hráč vyhrál. 
         */
        public void set(ArrayList<Card> cds, Color frontEndCardColor) {
            this.setBackground(Color.getHSBColor((float) 0.5, (float) 0.5, (float) 0.7));
            int i = 0;
            for (Card c : cds) {
                jcd.get(i).setText(c.toSymbol());
                jcd.get(i).setOpaque(true);
                jcd.get(i).setBackground(frontEndCardColor);
                jcd.get(i).setForeground(c.getColor().toInt() < 2 ? Color.red : Color.BLACK);
                i++;
            }
        }
    }

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
        counterPlayers = new ArrayList<SmallCardPanel>();
        counterPlayers.add(new SmallCardPanel(new GridLayout(1, 2, 4, 0)));
        counterPlayers.add(new SmallCardPanel(new GridLayout(1, 2, 4, 0)));
        counterPlayers.add(new SmallCardPanel(new GridLayout(1, 2, 4, 0)));

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
                    chipsLeft = Math.max(chipsLeft, 0);
                    chips.setText(chipsLeft.toString());
                    myLastBetInRound += Integer.parseInt(bet.getText());
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
                    myLastBetInRound += Integer.parseInt(bet.getText()) + Integer.parseInt(raise.getText());
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
        rightPanel.add(counterPlayers.get(0));

        JPanel leftPanel = new JPanel(new GridLayout(2, 1));
        leftPanel.setBackground(Color.getHSBColor((float) 0.5, (float) 0.5, (float) 0.7));
        leftPanel.add(counterPlayers.get(1));
        leftPanel.add(counterPlayers.get(2));

        panel.add(centralPanel, BorderLayout.CENTER);
        panel.add(controler, BorderLayout.SOUTH);
        panel.add(rightPanel, BorderLayout.EAST);
        panel.add(leftPanel, BorderLayout.WEST);

        window.getContentPane().add(panel);
        window.setSize(200, 200);
        window.pack();
        window.setVisible(true);

    }

    /**
     * @param args Měli by být prázdné 
     */
    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        String ip;

        WelcomeFrame wFrame = new WelcomeFrame();

        ip = wFrame.getIp();

        javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                createAndShowGui();
            }
        });

        ServerConection(ip);//spojime se se serverem

    }
}

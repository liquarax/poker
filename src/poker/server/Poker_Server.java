/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poker.server;

import java.io.*;
import java.net.ServerSocket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import poker.*;
import poker.communication.ClientClosedException;
import poker.communication.CommunicationCommons;
import poker.communication.OnlyOnePlayerException;

/**
 * Server, zajišťuje průběh hry a komunikuje s klientem Skončí, když už nemá
 * žádné hráče
 *
 * @author Jaroslav Brabec
 */
public class Poker_Server {

    static int actbet;

    /**
     * nageneruje 5 karet, pro ladeni
     *
     * @return
     */
    public static Card[] GenCards() {
        Deck d = new Deck();
        Card[] cds = new Card[5];
        for (int i = 0; i < 5; i++) {
            cds[i] = d.draw_card();
            System.out.println(cds[i]);
        }
        return cds;
    }

    public static class WinnerException extends Exception {
    }

    /**
     * Jedno kolo sázek
     *
     * @param players hráči připojení do hry
     * @param bet dosavadní sázka
     * @param pot dosavadní hodnota banku
     * @param first od kterého hráče má začít sázení
     * @return
     * @throws poker.server.Poker_Server.WinnerException
     */
    public static int Bets(ArrayList<PokerSocket> players, int bet, int pot, int first) throws WinnerException {
        boolean change, first_time; //zmenila se toto kolo sazka?
        int plays = 0, allin = 0, last = -1;      //pocet hracu, co hraje
        int all_players = players.size();
        first %= all_players;
        for (PokerSocket p : players) {
            if (p.isPlaying()) {
                plays++;
                if (p.is_All_in()) {
                    allin++;
                }
            }
        }
        if (plays - allin < 2) {
            return pot;
        }
        while (true) {
            change = false;
            first_time = true; //prvni pruchod cyklem
            for (int i = first; i != first || first_time; i = (i + 1) % all_players) {
                try {//pokud je pri sazeni zachycena vyjimka, bylo zavreno okno clienta
                    if (i == last) //naposledy vsazeno
                    {
                        break;
                    }
                    first_time = false;
                    if (players.get(i).isPlaying() && !players.get(i).is_All_in()) {
                        players.get(i).send(CommunicationCommons.potIsMessage);
                        players.get(i).send(pot);
                        players.get(i).send("bet is");
                        players.get(i).send(bet);
                        String s = players.get(i).recv();
                        if (s.equals("fold")) //polozil karty
                        {
                            players.get(i).endPlaying(false, pot);
                            plays--;
                        }
                        if (s.equals("call")) //dorovnal
                        {
                            if (bet >= players.get(i).chips()) {
                                s = "all in";
                            } else {
                                pot += bet - players.get(i).getBet(); //prirustek
                                players.get(i).setBet(bet);
                            }
                        }
                        if (s.equals("raise")) { //zvysil sazku
                            s = players.get(i).recv();
                            bet = Integer.parseInt(s);
                            if (bet >= players.get(i).chips()) {
                                s = "all in";
                            } else {
                                last = i;
                                pot += bet - players.get(i).getBet();
                                players.get(i).setBet(bet);
                                change = true;
                            }
                        }
                        if (s.equals("all in")) {
                            pot -= players.get(i).getBet();
                            players.get(i).all_in();
                            pot += players.get(i).getBet();
                            if (players.get(i).getBet() > bet) {
                                bet = players.get(i).getBet();
                                change = true;
                                last = i;
                            }
                        }
                        players.get(i).send(CommunicationCommons.potIsMessage);
                        players.get(i).send(pot);
                    }
                    if (plays < 2) {
                        throw new WinnerException(); //hra ukoncena, jedna sazka prevysila ostatni
                    }
                } catch (ClientClosedException cce) {
                    players.remove(i);
                    if (players.size() < 2) {
                        throw new OnlyOnePlayerException();
                    }
                    continue;
                }
            }
            if (!change) {
                break; //konec sazeni
            }
        }
        actbet = bet;
        return pot;
    }

    /**
     * Metoda simulující celou hru dle pravidel.
     *
     * @param players hráči, kteří se zůčastní hry
     * @param cash suma, kterou dostane každý hráč na začátku hry
     */
    public static void GameLoop(ArrayList<PokerSocket> players, int cash) {
        //nepotrebuju pot, sazky mam ulozene u hracu
        Deck d = new Deck();
        int pot, id = 0;
        int blind = cash / 100;
        Card[] cd;
        Card my_cd;
        int first = 0;
        //nejdrive dostane kazdy hrac id
        for (PokerSocket p : players) {
            p.setID(id);
            id++;
        }
        while (players.size() > 1) { //kazde kolo hry
            try {

                d.shufle();
                pot = 0;
                //hide cards
                for (PokerSocket p : players) {
                    p.send(CommunicationCommons.potIsMessage); //pot na zacatku
                    p.send(0);
                    p.setPlaying();
                    p.send("hide cards");
                    my_cd = d.draw_card();
                    p.addCard(my_cd);
                    p.send(my_cd);
                    my_cd = d.draw_card();
                    p.addCard(my_cd);
                    p.send(my_cd);
                }
                //blindy - nastaveni blindu a preskoceni hracu
                pot += blind;
                players.get(first).setBet(blind);
                players.get(first).send(CommunicationCommons.setBlindMessage);
                players.get(first).send(blind);
                actbet = blind * 2;
                first = (first + 1) % players.size();
                pot += actbet;
                players.get(first).setBet(actbet);
                players.get(first).send(CommunicationCommons.setBlindMessage);
                players.get(first).send(actbet);

                first = (first + 1) % players.size();
                //prvni kolo sazek
                pot = Bets(players, actbet, pot, first);
                cd = new Card[3];
                cd[0] = d.draw_card();
                cd[1] = d.draw_card();
                cd[2] = d.draw_card();
                //ukazu karty hracum
                for (PokerSocket p : players) {
                    p.send("flop");
                    for (Card c : cd) {
                        p.send(c);
                        p.addCard(c);
                    }
                }
                //zvysim sazku
                pot = Bets(players, actbet, pot, first);
                cd = new Card[1];
                cd[0] = d.draw_card();
                //ukazu hracum turn
                for (PokerSocket p : players) {
                    p.send("turn");
                    p.send(cd[0]);
                    p.addCard(cd[0]);
                }
                pot = Bets(players, actbet, pot, first);
                cd[0] = d.draw_card();
                //ukazu river
                for (PokerSocket p : players) {
                    p.send("river");
                    p.send(cd[0]);
                    p.addCard(cd[0]);
                }
                pot = Bets(players, actbet, pot, first); //zaverecny pot
                //vyhodnoceni
                ArrayList<poker_hand> ph = new ArrayList<poker_hand>();
                for (PokerSocket p : players) {
                    if (p.isPlaying()) {
                        ph.add(p.gethand());
                    }
                    //nastaveni posledni zname sazky
                    p.setEndBet(p.getBet());
                }
                Collections.sort(ph);

                int winCount = 0; //pocet vyhercu
                ArrayList<Integer> winners = new ArrayList<Integer>();
                ArrayList<Integer> loosers = new ArrayList<Integer>();

                while (pot > 0) {
                    int localWinCount = 0;
                    ArrayList<PlayerBetAndIdPair> localWinners = new ArrayList<PlayerBetAndIdPair>();
                    for (PokerSocket p : players) {
                        if (p.isPlaying()) {
                            if (p.gethand().equals(ph.get(0)) && p.getEndBet() > 0) { //pridam ho pouze pokud vyhral
                                localWinCount++;
                                localWinners.add(new PlayerBetAndIdPair(p.getEndBet(), players.indexOf(p))); //uvidime, jak moc dobre funguje
                            }
                        }
                        p.send("Winning combination:" + ph.get(0));
                    }

                    winCount += localWinCount;

                    Collections.sort(localWinners);//serazeno podle sazky
                    //vyresime vyhry, potom prohry
                    for (int i = 0; i < localWinCount; i++) {
                        if (players.get(localWinners.get(i).getId()).is_All_in()) {
                            //spocteme vyhru
                            int my_bet = players.get(localWinners.get(i).getId()).getEndBet() / localWinCount;
                            int won = 0;
                            for (PokerSocket p : players) {
                                //prohravajici hrac
                                if (!p.isPlaying() || (p.isPlaying() && !p.gethand().equals(ph.get(0)))) {
                                    if (p.getEndBet() >= my_bet) {
                                        won += my_bet;
                                        p.setEndBet(p.getEndBet() - my_bet);
                                    } else {
                                        won += p.getEndBet() / (localWinCount - i);
                                        p.setEndBet(p.getEndBet() - p.getEndBet() / (localWinCount - i));
                                    }
                                }
                            }
                            won += my_bet * localWinCount; //nesmim zapomenout na to, co jsem vsadil
                            players.get(localWinners.get(i).getId()).setEndBet(0);
                            players.get(localWinners.get(i).getId()).endPlaying(true, won);
                            pot -= won;
                        } else {
                            int won = pot / (localWinCount - i);
                            players.get(localWinners.get(i).getId()).endPlaying(true, won);
                            pot -= won;
                        }
                    }

                    for (PlayerBetAndIdPair p : localWinners) {
                        winners.add(p.getId());
                    }
                    //odstranim prvni vyherni kombinace
                    poker_hand winHand = ph.get(0);
                    while (!ph.isEmpty() && winHand.equals(ph.get(0))) {
                        ph.remove(0);
                    }
                }
                for (PokerSocket p : players) {
                    if (p.isPlaying()) {
                        loosers.add(players.indexOf(p));
                    }
                }
                for (PokerSocket p : players) {
                    p.send(CommunicationCommons.showingCardsMessage);
                    p.send(winners.size() + loosers.size());
                    for (int i = 0; i < winners.size(); i++) {
                        p.send("winning player");
                        p.send(players.get(winners.get(i)).getID());
                        p.send("winning cards");
                        p.send(players.get(winners.get(i)).GetHideCards().get(0));
                        p.send(players.get(winners.get(i)).GetHideCards().get(1));
                    }
                    for (int i = 0; i < loosers.size(); i++) {
                        p.send(CommunicationCommons.loosingPlayerIdMessage);
                        p.send(players.get(loosers.get(i)).getID());
                        p.send(CommunicationCommons.loosingCardsMessage);
                        p.send(players.get(loosers.get(i)).GetHideCards().get(0));
                        p.send(players.get(loosers.get(i)).GetHideCards().get(1));

                    }
                    if (p.isPlaying()) {
                        p.endPlaying(false, pot);
                    }
                }
            } catch (WinnerException we) {
                //tady se vyresi kdo vyhral - jediny hrac, co jeste hraje
                pot = 0;
                for (PokerSocket p : players) {
                    pot += p.getBet();
                }
                for (PokerSocket p : players) {
                    if (p.isPlaying()) {
                        p.endPlaying(true, pot);
                    }
                }
            } catch (OnlyOnePlayerException oope) {
                continue;//while cyklus se sam vyporada s problemem
            }
            first = (first - 1 + players.size()) % players.size();
            //kontrola, jestli neni bez penez
            for (int i = 0; i < players.size(); i++) {
                if (!players.get(i).has_chips()) {
                    players.get(i).sendChipsStatus();
                    players.get(i).send("You have lost!");
                    players.get(i).send(CommunicationCommons.potIsMessage);
                    players.get(i).send(0);
                    players.remove(i);
                    i--;//provede se ++
                }
            }
        }//end while
        //posledni hrac
        players.get(0).send(CommunicationCommons.potIsMessage);
        players.get(0).send(0);
        players.get(0).sendChipsStatus();
        players.get(0).send("You have won!");
        String response = players.get(0).recv();
        if (!response.equals(CommunicationCommons.communicationEndedMessage)) {
            throw new RuntimeException();//pokud neskonci komunikace spravne mela by byt vyhozena nejaka vyjimka
        }
    }

    /**
     * spousti hru pro urceny pocet hracu
     *
     * @param playerCount
     * @param cash
     */
    public static void theGame(int playerCount, int cash) {
        try {
            ServerSocket s = new ServerSocket(7777);
            try {
                ArrayList<PokerSocket> soket = new ArrayList<PokerSocket>();
                for (int i = 0; i < playerCount; i++) {
                    soket.add(new PokerSocket(s.accept(), cash));
                }
                GameLoop(soket, cash);
            } finally {
                s.close();
            }
        } catch (java.net.BindException be) {
            System.out.println("port 7777 se jiz pouziva");
        } catch (IOException ex) {
            Logger.getLogger(Poker_Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        if (args.length > 1 && args.length<3) {
            int i;
            if ((i = Integer.parseInt(args[0])) <= 4) {
                theGame(i, Integer.parseInt(args[1]));
            } else {
                System.out.println("moc hracu");
            }
        } else {
            System.out.println("spatny pocet argumentu");
        }
//        theGame(2, 1000);
    }
}

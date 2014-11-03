/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poker.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import poker.*;
import poker.communication.ClientClosedException;

/**
 * Soket, používaný na spojení s klientem. Pamatuje si v sobě informace o hráči.
 * Například, kolik má ještě žetonů, jaké má karty atd.
 *
 * Každý hráč má svůj soket.
 * @author Jaroslav Brabec
 */
public class PokerSocket {

    private BufferedReader in;
    private PrintWriter out;
    private Socket soket;
    private int chips;
    private int bet, id;
    private int endBet;

    //posledni znama sazka
    public int getEndBet() {
        return endBet;
    }

    public void setEndBet(int endBet) {
        this.endBet = endBet;
    }
    private boolean plays;
    private boolean all_in;
    private poker_hand my_hand;
    private LinkedList<Card> c;

    public ArrayList<Card> GetHideCards() {
        ArrayList<Card> al = new ArrayList<Card>();
        al.add(c.get(0));
        al.add(c.get(1));
        return al;
    }

    public void setID(int id) {
        this.id = id;
        send(id);
    }

    public int getID() {
        return this.id;
    }

    public int chips() {
        return this.chips;
    }

    public void setPlaying() {
        sendChipsStatus();
        plays = true;
        all_in = false;
        bet = 0;
        c.clear();
    }

    public void sendChipsStatus(){
        this.send("chips");
        this.send(chips);
    }
    
    public boolean is_All_in() {
        return all_in;
    }

    public boolean has_chips() {
        return chips > 0 ? true : false;
    }

    public void addCard(Card cd) {
        c.add(cd);
    }

    /**
     * Metoda pro přerozdělení výhry po skončení hry
     *
     * @param won informace, zda hráč vyhrál
     * @param pot kolik je jeho případná odměna
     */
    public void endPlaying(boolean won, int pot) {
        plays = false;
        chips -= bet;
        if (won) {
            chips += pot;
        }
    }

    public boolean isPlaying() {
        return plays;
    }

    public void all_in() {
        this.bet = this.chips;
        this.all_in = true;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public int getBet() {
        return bet;
    }

    public PokerSocket(Socket soket, int chips) {
        this.chips = chips;
        this.c = new LinkedList<Card>();
        this.soket = soket;
        try {
            in = new BufferedReader(new InputStreamReader(soket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(soket.getOutputStream()), true);
        } catch (IOException ex) {
            Logger.getLogger(PokerSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * vrati nejlepsi kombinaci
     *
     * @return
     */
    public poker_hand gethand() {
        return poker_rules.best_hand(c.toArray(new Card[c.size()]));
    }

    public void send(Object msg) {
        out.println(msg);
    }

    public String recv() {
        try {
            return in.readLine();
        } catch (IOException ex) {
            throw new ClientClosedException();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        soket.close();
    }
}

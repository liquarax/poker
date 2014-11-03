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
 *
 * @author Jaroslav Brabec
 */
public class PokerSocket {

    private BufferedReader in;
    private PrintWriter out;
    private Socket soket;
    private int chips;
    private int bet, id;
    private int endBet;
    private boolean plays;
    private boolean all_in;
    private poker_hand my_hand;
    private LinkedList<Card> c;

    /**
     * @return Poslední známá sázka hráče.
     */
    public int getEndBet() {
        return endBet;
    }

    /**
     * @param endBet Poslední známá sázka hráče.
     */
    public void setEndBet(int endBet) {
        this.endBet = endBet;
    }

    /**
     * @return Karty, které jsou jen hráče.
     */
    public ArrayList<Card> GetHideCards() {
        ArrayList<Card> al = new ArrayList<Card>();
        al.add(c.get(0));
        al.add(c.get(1));
        return al;
    }

    /**
     * Voláno na začátku hry. Každý hráč má své jednoznačné id.
     * @param id 
     */
    public void setID(int id) {
        this.id = id;
        send(id);
    }

    /**
     * @return Jednoznačné pořadí hráče. 
     */
    public int getID() {
        return this.id;
    }

    /**
     * @return Počet chipů. které má hráč u sebe.  
     */
    public int chips() {
        return this.chips;
    }

    /**
     * Na začátku každého kola se hráč aktivuje.
     */
    public void setPlaying() {
        sendChipsStatus();
        plays = true;
        all_in = false;
        bet = 0;
        c.clear();
    }

    /**
     * Pošle clientovi info o jeho žetonech
     */
    public void sendChipsStatus() {
        this.send("chips");
        this.send(chips);
    }

    /**
     * @return Vsadil hráč všechno?
     */
    public boolean is_All_in() {
        return all_in;
    }

    /**
     * @return test na prohru hráče
     */
    public boolean has_chips() {
        return chips > 0 ? true : false;
    }

    /**
     * Hráčovi se podávají karty, které vidí jen on.
     * @param cd 
     */
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

    /**
     * @return Ukončil již hráč kolo, nebo je stále ve hře?
     */
    public boolean isPlaying() {
        return plays;
    }

    /**
     * Hráč sází vše.
     */
    public void all_in() {
        this.bet = this.chips;
        this.all_in = true;
    }

    /**
     * Nastavení sázky
     * @param bet sázka
     */
    public void setBet(int bet) {
        this.bet = bet;
    }

    /**
     * @return kolik hráč v kole vsadil? 
     */
    public int getBet() {
        return bet;
    }

    /**
     * Konstruktor - uloží soket na kterém bude komunikovat s klientem-
     * @param soket
     * @param chips počet žetonů hráče na začátku hry. 
     */
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
     * @return vrati nejlepsi kombinaci, kterou má hráč v ruce
     */
    public poker_hand gethand() {
        return poker_rules.best_hand(c.toArray(new Card[c.size()]));
    }

    /**
     * Odešle klientovi zprávu
     * @param msg zpráva
     */
    public void send(Object msg) {
        out.println(msg);
    }

    /**
     * @return přijme zprávu od klienta 
     */
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

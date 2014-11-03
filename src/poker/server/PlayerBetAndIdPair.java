/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poker.server;

/**
 * Třída ve které je uložena dvojice se sázkami a id hráčů.
 * @author Jaroslav Brabec
 */
public class PlayerBetAndIdPair implements Comparable<PlayerBetAndIdPair>{
    private int bet,id;

    /**
     * Přiřazovací konstruktor
     * @param bet sázka
     * @param id pořadí hráče v listu
     */
    public PlayerBetAndIdPair(int bet, int id) {
        this.bet = bet;
        this.id = id;
    }

    public int getBet() {
        return bet;
    }

    public int getId() {
        return id;
    }

    
    /**
     * Slouží pro seřazení listu podle sázek.
     * @param t Jiná instance třídy.
     * @return 
     */
    @Override
    public int compareTo(PlayerBetAndIdPair t) {
        return t.getBet()<this.bet?1:-1;
    }
    
}

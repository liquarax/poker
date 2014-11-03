/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poker.server;

/**
 *
 * @author Jaroslav Brabec
 */
public class PlayerBetAndIdPair implements Comparable<PlayerBetAndIdPair>{
    private int bet,id;

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

    @Override
    public int compareTo(PlayerBetAndIdPair t) {
        return t.getBet()<this.bet?1:-1;
    }
    
}

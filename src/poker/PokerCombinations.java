/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poker;

/**
 * Způsob uložení druhů kombinací a jejich hiearchie
 * @author Jaroslav Brabec
 */
public enum PokerCombinations {
    
    Straight_Flush(8),
    Four_of_Kind(7),
    Full_House(6),
    Flush(5),
    Straight(4),
    Three_Of_Kind(3),
    Two_Pair(2),
    One_Pair(1),
    High_Card(0);
    
    private final int value;
    
    /**
     * Konstruktor
     * @param value kolikátá je to kombinace 
     */
    PokerCombinations(int value){
        this.value=value;
    }
    
    /**
     * @return Číslo kombinace
     */
    public int toInt(){
        return this.value;
    }
    
    @Override
    public String toString() {
        return this.name();
    }
    
}

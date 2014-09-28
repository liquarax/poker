/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poker;

/**
 *Způsob uložení druhů kombinací a jejich hiearchie
 * @author Jaroslav Brabec
 */
public enum poker_combinations {
    
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
    
    poker_combinations(int value){
        this.value=value;
    }
    
    public int toInt(){
        return this.value;
    }
    
    @Override
    public String toString() {
        return this.name();
    }
    
}

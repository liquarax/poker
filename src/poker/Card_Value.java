/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poker;
/**
 *Obsahuje informaci o hodnotě
 * @author Jaroslav Brabec
 */
public enum Card_Value {
    TWO(0),
    THREE(1),
    FOUR(2),
    FIVE(3),
    SIX(4),
    SEVEN(5),
    EIGHT(6),
    NINE(7),
    TEN(8),
    JACK(9),
    QUEEN(10),
    KING(11),
    ACE(12);

    private final int value;
    
    Card_Value(int value){
        this.value=value;
    }
    
    public int toInt(){
        return this.value;
    }
    
    public static int CharToInt(char c){
        if (c=='1') 
            return 8;
        if(c<='9')
            return c-'2';
        else
        {
            switch(c){
                case 'J': return 9;
                case 'Q': return 10;
                case 'K': return 11;
                case 'A': return 12;
                default: throw new Error();
            }
        }
    }
    
    public String symbol(){
        if(this.value<9)
            return ""+(this.value+2);
        else
        {
            switch(this.value){
                case 9: return "J";
                case 10: return "Q";
                case 11: return "K";
                case 12: return "A";
                default: throw new Error();
            }
        }
    }
    @Override
    public String toString() {
        return this.name();
    }
}

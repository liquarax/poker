/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poker;
/**
 *Obsahuje informaci o hodnotě karty (2,3 ... královna,král,eso)
 * @author Jaroslav Brabec
 */
public enum CardValue {
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
    
    /**
     * Konstruktor
     * @param value hodnota 0-12 
     */
    CardValue(int value){
        this.value=value;
    }
    
    /**
     * @return Hodnota jako integer 
     */
    public int toInt(){
        return this.value;
    }
    
    /**
     * Převod ze znaku reprezentujícího hodnotu na int
     * @param c
     * @return 
     */
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
    
    /**
     * @return Vrací hodnotu 2-A karty
     */
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

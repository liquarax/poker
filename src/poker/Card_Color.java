/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poker;

/**
 * Informace o barvě
 * @author Jaroslav Brabec
 */
public enum Card_Color {
   
    HEARTHS(0),
    DIAMONDS(1),
    CLUBS(2),
    SPADES(3);
    
    private final int value;
    private static final String chars="♥♦♣♠";
    
    Card_Color(int value){
        this.value=value;
    }
    
    public int toInt(){
        return this.value;
    }
    public static int CharToInt(char c){
        return chars.indexOf(c);
    }
    
    public char symbol(){
        return chars.charAt(this.value);
    }
    
    @Override
    public String toString() {
        return this.name();
    }
}

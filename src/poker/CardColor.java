/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poker;

/**
 * Informace o barvě
 * @author Jaroslav Brabec
 */
public enum CardColor {
   
    HEARTHS(0),
    DIAMONDS(1),
    CLUBS(2),
    SPADES(3);
    
    private final int value;
    private static final String img="♥♦♣♠";
    private static final String chars="HDCS";
    
    /**
     * Konstruktor
     * @param value hodnota 0..4
     */
    CardColor(int value){
        this.value=value;
    }
    
    /**
     * @return vrátí hodnotu jako číslo 
     */
    public int toInt(){
        return this.value;
    }
    
    /**
     * @param c Znak z "HDCS"
     * @return index
     */
    public static int CharToInt(char c){
        return chars.indexOf(c);
    }
    
    /**
     * @return Vrátí vnitřní reprezentaci znaku. 
     */
    public char charRepre(){
        return chars.charAt(this.value);
    }
    
    /**
     * @return Vrátí symbol ♥♦♣♠, reprezentující hodnotu.
     */
    public char symbol(){
        return img.charAt(this.value);
    }
    
    @Override
    public String toString() {
        return this.name();
    }
}

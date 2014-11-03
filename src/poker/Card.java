/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poker;

/**
 * Třída obsahující informaci o kartě, její barvu a hodnotu
 * @see Card_Color
 * @see Card_Value
 * @author Jaroslav Brabec
 */
public class Card {

    private Card_Value value;
    private Card_Color color;

    /**
     * Konsturktor
     *
     * @param value hodnota karty
     * @param color barva karty
     */
    public Card(Card_Value value, Card_Color color) {
        this.value = value;
        this.color = color;
    }

    /**
     * Konstruktor s čísly
     *
     * @param value hodnota karty
     * @param color barva karty
     */
    public Card(int value, int color) {
        Card_Color cc = Card_Color.HEARTHS;
        Card_Value cv = Card_Value.TWO;
        switch (color) {
            case 0:
                cc = Card_Color.HEARTHS;
                break;
            case 1:
                cc = Card_Color.DIAMONDS;
                break;
            case 2:
                cc = Card_Color.CLUBS;
                break;
            case 3:
                cc = Card_Color.SPADES;
                break;
        }
        switch (value) {
            case 0:
                cv = Card_Value.TWO;
                break;
            case 1:
                cv = Card_Value.THREE;
                break;
            case 2:
                cv = Card_Value.FOUR;
                break;
            case 3:
                cv = Card_Value.FIVE;
                break;
            case 4:
                cv = Card_Value.SIX;
                break;
            case 5:
                cv = Card_Value.SEVEN;
                break;
            case 6:
                cv = Card_Value.EIGHT;
                break;
            case 7:
                cv = Card_Value.NINE;
                break;
            case 8:
                cv = Card_Value.TEN;
                break;
            case 9:
                cv = Card_Value.JACK;
                break;
            case 10:
                cv = Card_Value.QUEEN;
                break;
            case 11:
                cv = Card_Value.KING;
                break;
            case 12:
                cv = Card_Value.ACE;
                break;
        }
        this.color = cc;
        this.value = cv;
    }

    /**
     * Konstruktor ze stringu, rozparsuje a použije standartní konstruktor
     *
     * @param val
     */
    public Card(String val) {
        this(Card_Value.CharToInt(val.charAt(0)), Card_Color.CharToInt(val.charAt(val.length() - 1)));
    }

    /**
     * @return Vrátí barvu karty
     */
    public Card_Color getColor() {
        return color;
    }

    /**
     * @return Vrátí hodnotu karty
     */
    public Card_Value getValue() {
        return value;
    }

    /**
     * @return Vrati string. Barva je jako jednoduché písmeno, kvůli přenosu. 
     */
    @Override
    public String toString() {
        return "" + value.symbol() + color.charRepre();
    }

    /**
     * @return Vrati string. Barva je jako symbol z:"♥♦♣♠" Hodí se hlavně k vykreslování. 
     */
    public String toSymbol() {
        //return value+" of "+color;
        return "" + value.symbol() + color.symbol();
    }
}

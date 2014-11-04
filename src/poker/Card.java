/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poker;

/**
 * Třída obsahující informaci o kartě, její barvu a hodnotu
 * @see CardColor
 * @see CardValue
 * @author Jaroslav Brabec
 */
public class Card {

    private CardValue value;
    private CardColor color;

    /**
     * Konsturktor
     *
     * @param value hodnota karty
     * @param color barva karty
     */
    public Card(CardValue value, CardColor color) {
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
        CardColor cc = CardColor.HEARTHS;
        CardValue cv = CardValue.TWO;
        switch (color) {
            case 0:
                cc = CardColor.HEARTHS;
                break;
            case 1:
                cc = CardColor.DIAMONDS;
                break;
            case 2:
                cc = CardColor.CLUBS;
                break;
            case 3:
                cc = CardColor.SPADES;
                break;
        }
        switch (value) {
            case 0:
                cv = CardValue.TWO;
                break;
            case 1:
                cv = CardValue.THREE;
                break;
            case 2:
                cv = CardValue.FOUR;
                break;
            case 3:
                cv = CardValue.FIVE;
                break;
            case 4:
                cv = CardValue.SIX;
                break;
            case 5:
                cv = CardValue.SEVEN;
                break;
            case 6:
                cv = CardValue.EIGHT;
                break;
            case 7:
                cv = CardValue.NINE;
                break;
            case 8:
                cv = CardValue.TEN;
                break;
            case 9:
                cv = CardValue.JACK;
                break;
            case 10:
                cv = CardValue.QUEEN;
                break;
            case 11:
                cv = CardValue.KING;
                break;
            case 12:
                cv = CardValue.ACE;
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
        this(CardValue.CharToInt(val.charAt(0)), CardColor.CharToInt(val.charAt(val.length() - 1)));
    }

    /**
     * @return Vrátí barvu karty
     */
    public CardColor getColor() {
        return color;
    }

    /**
     * @return Vrátí hodnotu karty
     */
    public CardValue getValue() {
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

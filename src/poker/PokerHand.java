/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poker;

/**
 * Posloupnost pěti karet. Nese informaci o hodnotě kombinace. Karty jsou seřazené dle velikosti.
 * @author Jaroslav Brabec
 */
public class PokerHand implements Comparable<PokerHand> {
    private PokerCombinations value;
    private Card[] cards;

    /**
     * @return Jak velkou kombinaci struktura drží. 
     */
    public PokerCombinations getValue() {
        return value;
    }

    /**
     * @param i Karta na i-té pozici
     * @return Objekt karty
     */
    public Card getCard(int i) {
        return cards[i];
    }

    
    /**
     * Konstruktor, jen dosadí hodnoty.
     * @param value
     * @param cards 
     */
    public PokerHand(PokerCombinations value, Card[] cards) {
        this.value=value;
        this.cards=cards;
    }

    @Override
    public String toString() {
        String str=value.name()+" : ";
        for(Card c:cards){
            str+=c+",";
        }
        return str.substring(0, str.length()-1);
    }

    /**
     * Porovná, jestli jsou dvě hands stejné.
     * @param ph druhá poker hand
     * @return 
     */
    public boolean equals(PokerHand ph) {
        boolean res=false;
        if(this.value.toInt() == ph.getValue().toInt()){
            res=true;
            for (int i = 0; i < this.cards.length; i++)
                if(cards[i].getValue().toInt()!=ph.getCard(i).getValue().toInt()){
                    res=false;
                    break;
                }
        }
        return res; 
    }

    
    /**
     * Komparátor, ke zjištění, která poker hand je lepší
     * V programu je použit rovnou ke třídění. 
     * @param ph karty protihráče
     * @return 
     */
    @Override
    public int compareTo(PokerHand ph) {
        int val=0;
        if(this.value.toInt() > ph.getValue().toInt())
            val=-1;
        else if(this.value.toInt() < ph.getValue().toInt())
            val=1;
        else // jsou stejne kombinace: podle poradi
        {
            for (int i = 0; i < this.cards.length; i++) {
                if(cards[i].getValue().toInt()>ph.getCard(i).getValue().toInt()){
                    val=-1;
                    break;
                }else
                if(cards[i].getValue().toInt()<ph.getCard(i).getValue().toInt())
                {
                    val=1;
                    break;
                }
            }
        }
        return val;
    }
    
}

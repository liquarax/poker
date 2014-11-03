package poker;


import java.util.*;


/**
 *Balík karet.
 * @see Card
 * @author Jaroslav Brabec
 */
public class Deck {
    private LinkedList<Card> DArr;
    private LinkedList<Card> Burned;
    
    /**
     *Vytvoří všechny možné karty a uloží je do balíku 
     */
    private void init(){
        DArr=new LinkedList<Card>();
        Burned=new LinkedList<Card>();
        for (Card_Color CC: Card_Color.values()) 
            for(Card_Value CV: Card_Value.values())
            {   
                DArr.add(new Card(CV, CC));
            }
        
    }
    
    /**
     * Vytvoří náhodnou permutaci karet v balíku
     */
    public final void shufle(){
        DArr.addAll(Burned);
        Random rnd=new Random();
        for (int i = DArr.size()-1; i >=0 ; i--) {
            int idx=rnd.nextInt(i+1);
            Card c=DArr.get(i);
            DArr.set(i, DArr.get(idx));
            DArr.set(idx,c);
        }
        Burned.clear();
    }
    
    /**
     * 
     * @return Vrátí kartu, která je na vrchu balíku a "spáli" kartu pod ní 
     */
    public Card draw_card(){
        Card c=DArr.pop();
        Burned.push(c);
        return c;
    }
    
    /**
     * Konstruktor vytvoří balík a zamíchá ho.
     */
    public Deck() {
        init();
        shufle();
    }
    
    @Override
    public String toString() {
        String s="";
        for(Card c:DArr){
            s+=c+"\n";
        }
        return s;
    }
    
    
}

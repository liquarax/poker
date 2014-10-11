/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poker.server;

import java.io.*;
import java.net.ServerSocket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import poker.*;
import poker.communication.CommunicationCommons;

/**
 *Server, zajišťuje průběh hry a komunikuje s klientem
 * Skončí, když už nemá žádné hráče
 * @author Jaroslav Brabec
 */
public class Poker_Server {

    
    static int actbet;
    
    
    /**
     *nageneruje 5 karet, pro ladeni 
     * @return 
     */
    public static Card[] GenCards(){
        Deck d = new Deck();
        Card[] cds = new Card[5];
        for (int i = 0; i < 5; i++) {
            cds[i] = d.draw_card();
            System.out.println(cds[i]);
        }
        return cds;
    }
    
    public static class WinnerException extends Exception{
    }
    
    /**
     * Jedno kolo sázek
     * @param players hráči připojení do hry
     * @param bet dosavadní sázka
     * @param pot dosavadní hodnota banku
     * @param first od kterého hráče má začít sázení 
     * @return
     * @throws poker.server.Poker_Server.WinnerException 
     */
    public static int Bets(ArrayList<PokerSocket> players,int bet,int pot,int first) throws WinnerException{
        boolean change,first_time; //zmenila se toto kolo sazka?
        int plays=0,allin=0,last=-1;      //pocet hracu, co hraje
        int all_players=players.size();
        first%=all_players;
        for(PokerSocket p:players){
            if(p.isPlaying()){
                plays++;
                if(p.is_All_in())
                    allin++;
            }
        }
        if(plays-allin<2)
            return pot;
        while(true){
            change=false; 
            first_time=true; //prvni pruchod cyklem
            for(int i=first; i!=first || first_time ;  i=(i+1)%all_players){
                if(i==last) //naposledy vsazeno
                    break;
                first_time=false;
                if(players.get(i).isPlaying() && !players.get(i).is_All_in()){
                    players.get(i).send(CommunicationCommons.potIsMessage);
                    players.get(i).send(pot);
                    players.get(i).send("bet is");
                    players.get(i).send(bet);
                    String s=players.get(i).recv();
                    if(s.equals("fold")) //polozil karty
                    {
                        players.get(i).EndPlaying(false, pot);
                        plays--;
                    }
                    if(s.equals("call")) //dorovnal
                    {
                        if(bet>=players.get(i).chips())
                        s="all in";
                        else
                        {
                            pot+=bet-players.get(i).getBet(); //prirustek
                            players.get(i).setBet(bet);    
                        }
                    }
                    if(s.equals("raise")){ //zvysil sazku
                        s=players.get(i).recv();
                        bet=Integer.parseInt(s);
                        if(bet>=players.get(i).chips())
                        s="all in";
                        else
                        {
                            last=i;
                            pot+=bet-players.get(i).getBet();
                            players.get(i).setBet(bet);
                            change=true;
                        }
                    }
                    if(s.equals("all in")){
                        pot-=players.get(i).getBet();
                        players.get(i).all_in();
                        pot+=players.get(i).getBet();
                        if(players.get(i).getBet()>bet){
                            bet=players.get(i).getBet();
                            change=true;
                            last=i;
                        }
                    }
                    players.get(i).send(CommunicationCommons.potIsMessage);
                    players.get(i).send(pot);
                }
                if(plays<2) throw new WinnerException(); //hra ukoncena, jedna sazka prevysila ostatni
            }
            if(!change) break; //konec sazeni
        }
        actbet=bet;
        return pot;
    }
    
    /**
     * Metoda simulující celou hru dle pravidel. 
     * @param players hráči, kteří se zůčastní hry
     * @param cash suma, kterou dostane každý hráč na začátku hry
     */
    public static void GameLoop(ArrayList<PokerSocket> players,int cash){
        //nepotrebuju pot, sazky mam ulozene u hracu
        Deck d = new Deck();
        int pot,id=0;
        int blind=cash/100;
        Card[] cd;
        Card my_cd;
        int first=0;
        //nejdrive dostane kazdy hrac id
        for(PokerSocket p:players){
            p.setID(id);
            id++;
        }
        while(players.size()>1){ //kazde kolo hry
         try{   
            
            d.shufle();
            pot=0;
            //hide cards
            for(PokerSocket p :players){
                p.SetPlaying();
                p.send("hide cards");
                my_cd=d.draw_card();
                p.addCard(my_cd);
                p.send(my_cd);
                my_cd=d.draw_card();
                p.addCard(my_cd);
                p.send(my_cd);
            }
            //blindy - nastaveni blindu a preskoceni hracu
             pot+=blind;
             players.get(first).setBet(blind);
             players.get(first).send(CommunicationCommons.setBlindMessage);
             players.get(first).send(blind);
             actbet=blind*2;
             first=(first+1)%players.size();
             pot+=actbet;
             players.get(first).setBet(actbet);
             players.get(first).send(CommunicationCommons.setBlindMessage);
             players.get(first).send(actbet);
           
             first=(first+1)%players.size();
             //prvni kolo sazek
            pot=Bets(players,actbet,pot,first);
            cd=new Card[3];
            cd[0]=d.draw_card();
            cd[1]=d.draw_card();
            cd[2]=d.draw_card();
            //ukazu karty hracum
            for(PokerSocket p : players){
                p.send("flop");
                for(Card c:cd){
                    p.send(c);
                    p.addCard(c);
                }
            }
            //zvysim sazku
            pot=Bets(players,actbet,pot,first);
            cd=new Card[1];
            cd[0]=d.draw_card();
            //ukazu hracum turn
            for(PokerSocket p : players){
                p.send("turn");
                    p.send(cd[0]);
                    p.addCard(cd[0]);
            }
            pot=Bets(players,actbet,pot,first);
            cd[0]=d.draw_card();
            //ukazu river
            for(PokerSocket p : players){
                p.send("river");
                    p.send(cd[0]);
                    p.addCard(cd[0]);
            }
            pot=Bets(players,actbet,pot,first); //zaverecny pot
            //vyhodnoceni
            ArrayList<poker_hand> ph=new ArrayList<poker_hand>();
            for(PokerSocket p : players){
                if(p.isPlaying())
                    ph.add(p.gethand());
            }
            Collections.sort(ph);
            int winCount=0; //pocet vyhercu
            ArrayList<Integer> winners = new ArrayList<Integer>();
            for(PokerSocket p : players){
                if(p.isPlaying()&& p.gethand().equals(ph.get(0))){
                    winCount++;
                    winners.add(players.indexOf(p)); //uvidime, jak moc dobre funguje
                }             
                p.send("Winning combination:"+ph.get(0));
            }
            //vyresime vyhry, potom prohry
            ArrayList<Card> win_car=players.get(winners.get(0)).GetHideCards();
            int WID = players.get(winners.get(0)).getID();
            for (int i = 0; i < winCount; i++) {
                if(players.get(winners.get(i)).is_All_in()){
                    int my_bet=players.get(winners.get(i)).getBet();
                    players.get(winners.get(i)).EndPlaying(true, (Math.max(1,pot/my_bet)*my_bet)/winCount);
                }else
                 players.get(winners.get(i)).EndPlaying(true, pot/winCount);
            }
            for(PokerSocket p : players){
                p.send("winning player");
                p.send(WID);
                p.send("winning cards");
                p.send(win_car.get(0));
                p.send(win_car.get(1));
                if(p.isPlaying())
                    p.EndPlaying(false, pot);
            }
         }catch(WinnerException we){
             //tady se vyresi kdo vyhral - jediny hrac, co jeste hraje
             pot = 0;
             for(PokerSocket p:players)
                 pot+=p.getBet();
             for (PokerSocket p:players) {
                 if(p.isPlaying())
                     p.EndPlaying(true, pot);
             }
         }
         first=(first-1+players.size())%players.size();
         //kontrola, jestli neni bez penez
            for(int i=0; i<players.size();i++){
                 if(!players.get(i).has_chips()){
                    players.get(i).send("You have lost!");
                    players.remove(i);
                    i=0;
                }
            }
        }//end while
        //posledni hrac
        players.get(0).send("You have won!");
    }
    
    /**
     * spousti hru pro urceny pocet hracu
     * @param playerCount
     * @param cash 
     */
    public static void theGame(int playerCount, int cash) {
        try {
            ServerSocket s = new ServerSocket(7777);
            try {
                ArrayList<PokerSocket> soket =new ArrayList<PokerSocket>();
                for (int i = 0; i < playerCount; i++) {
                    soket.add(new PokerSocket(s.accept(),cash));
                }
                GameLoop(soket,cash);
            } finally {
                s.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Poker_Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        if(args.length>1){
            int i;
            if((i=Integer.parseInt(args[0]))<=4)
                theGame(i,Integer.parseInt(args[1]));
            else System.out.println("moc hracu");
        }else System.out.println("malo argumentu");
//        theGame(2, 1000);
    }
}

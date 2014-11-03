/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poker.communication;

/**
 *
 * @author Jaroslav Brabec
 *
 * Třída ve které budou uloženy zprávy, které si posílá server s klientem
 */
public class CommunicationCommons {

    public static final String potIsMessage = "pot is"; //posila aktualni hodnotu potu
    public static final String setBlindMessage = "set blind"; //nasleduje hodnota blindu: v klientovi implementovat jako nastaveni a preskoceni
    public static final String clientClosedMessage = "client has been closed";
    public static final String showingCardsMessage = "round has endded, showing cards"; //na konci hry, pokud dojde k porovnavani karet
    public static final String showingPlayerCountMessage = "count of players that will show cards:";//nasleduje int
    public static final String showingLoosingPlayerCountMessage ="count of players that lose";//nasleduje int
    public static final String loosingPlayerIdMessage ="loosing player";//nasleduje int
    public static final String loosingCardsMessage = "loosing cards";
    public static final String communicationEndedMessage = "bye";//po zprave you have lost/won 
}

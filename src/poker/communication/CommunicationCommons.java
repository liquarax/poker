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
    public static final String potIsMessage = "Pot Is"; //posila aktualni hodnotu potu
    public static final String setBlindMessage = "Set blind"; //nasleduje hodnota blindu: v klientovi implementovat jako nastaveni a preskoceni
    public static final String clientClosedMessage ="Client has been closed";
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poker.client;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Jaroslav Brabec
 */
public final class WelcomeFrame {

    private String ip;

    public String getIp() {
        return ip;
    }

    public WelcomeFrame() {
        this.ip = "127.0.0.1";
        this.createAndShowGui();
    }

    private void createAndShowGui() {
        final JFrame frame = new JFrame("Poker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JLabel label = new JLabel("Zadejte ip adresu serveru");
        final TextField ipField = new TextField(this.ip); //loopback
        JButton okButton = new JButton("OK");

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                ip = ipField.getText();
                frame.dispose();
            }
        });

        c.gridx = 0;
        c.gridy = 0;
        panel.add(label, c);
        c.gridx = 0;
        c.gridy = 1;
        panel.add(ipField, c);
        c.gridx = 0;
        c.gridy = 2;
        panel.add(okButton, c);

        frame.getContentPane().add(panel);
        frame.setSize(200, 200);
        frame.pack();
        frame.setVisible(true);

        while (frame.isDisplayable()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
        }
    }
}

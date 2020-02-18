/*
Startar spelet.
*/

import javax.swing.JFrame;
import java.awt.BorderLayout;

public class Game {
    static Server server;
    static Client client;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Boats");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        frame.add(new GamePanel(), BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

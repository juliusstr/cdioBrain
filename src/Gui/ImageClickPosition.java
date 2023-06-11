package Gui;

import jdk.dynalink.NamedOperation;
import misc.Vector2Dv1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ImageClickPosition {

    public static ArrayList<Vector2Dv1> pos = null;

    private static int amount = -1;

    private static Image image = null;

    private static String title = "";

    public ImageClickPosition(int amount, Image image, String title, ArrayList<Vector2Dv1> pos){
        this.pos = pos;
        this.amount = amount;
        this.image = image;
        this.title = title;
    }
    public void run() {
        SwingUtilities.invokeLater(ImageClickPosition::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Load an image
        Image imageIcon = image;
        JLabel imageLabel = new JLabel((Icon) imageIcon);

        // Create a custom mouse adapter to handle mouse click events
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                pos.add(new Vector2Dv1(x,y));
                System.out.println("Clicked at position: (" + x + ", " + y + ")");
                amount--;
                if(amount == 0){
                    frame.dispose();
                }
            }
        };

        // Add the mouse adapter to the image label
        imageLabel.addMouseListener(mouseAdapter);

        // Add the image label to the frame
        frame.getContentPane().add(imageLabel);

        frame.pack();
        frame.setVisible(true);
    }
}

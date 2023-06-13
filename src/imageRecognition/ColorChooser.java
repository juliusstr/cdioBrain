package imageRecognition;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ColorChooser extends JFrame implements ActionListener {

    public static int Blue;
    public static int Green;
    public static int Red;

    JButton button;
    Container page;
   public static JColorChooser color_picker = new JColorChooser();

    ColorChooser(){
        page = getContentPane();
        page.setLayout(new FlowLayout());
        button = new JButton("color");
        button.addActionListener(this);
        page.add(button);


    }

    public void actionPerformed(ActionEvent e) {
            AbstractColorChooserPanel[] panel = color_picker.getChooserPanels();
            panelRemover(panel);
            color_picker.setChooserPanels(panel);
            Color picked = color_picker.showDialog(this,"Select a Color",Color.RED,true);
            page.setBackground(picked);
            Red = picked.getRed();
            Blue = picked.getBlue();
            Green = picked.getGreen();

            System.out.println("RED:" + Red + "\n" + "BLUE:" + Blue + "\n" + "GREEN:" + Green + "\n");
    }

    public static void panelRemover(AbstractColorChooserPanel[] panel){
        for (AbstractColorChooserPanel p : panel) {
            switch (p.getDisplayName()) {
                case "RGB":
                    System.out.println("IDK im not doing what im supposed to");
                    color_picker.removeChooserPanel(color_picker.removeChooserPanel(panel[2]));
            }
        }
    }

    public static void main(String[] args) {
        ColorChooser ch=new ColorChooser();
        ch.setSize(400,400);
        ch.setVisible(true);
        ch.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
/*
    class ColorSelection implements ChangeListener {
        public void stateChanged(ChangeEvent e) {

            Color color;
            color = jcc.getColor();

            Blue = color.getBlue();
            Red = color.getRed();
            Green = color.getGreen();

            System.out.println(Red + "\n");
            System.out.println(Green + "\n");
            System.out.println(Blue + "\n");
            label.setForeground(color);
        }
    }
*/
}
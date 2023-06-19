package Gui;

import Gui.Image.GuiImage;
import misc.Robotv1;
import misc.Vector2Dv1;
import org.opencv.core.Mat;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class LiveView extends Thread {

    private GuiImage image;
    private Robotv1 robot;
    private static ArrayList<Vector2Dv1> rout = null;
    private static Mat mat = null;

    private Mat curMat;

    private JLabel label = null;

    public LiveView(Mat mat, Robotv1 robot){
        image = new GuiImage(mat);
        this.mat = mat;
        this.curMat = mat;
        this.robot = robot;
        show();
    }

    private void show(){
        JFrame frame = new JFrame();
        label = new JLabel(image.getIcon());
        // Add the image label to the frame
        frame.getContentPane().add(label);
        //frame.add(imageLabel);
        frame.pack();
        frame.setVisible(true);
    }

    public void run(){
        show();
        while(true){
            if(mat != curMat)
                update(mat);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setRout(ArrayList<Vector2Dv1> r){
        rout = r;
    }

    public void setMat(Mat m){
        mat = m;
    }
    public void update(Mat mat){
        curMat = mat;
        image = new GuiImage(mat);
        image.Draw(new GuiImage.GuiCircle(robot.aScale.getPosVector(), 2, Color.BLACK, 3), true);
        image.Draw(new GuiImage.GuiCircle(robot.bScale.getPosVector(), 2, Color.BLUE, 3), true);
        image.Draw(new GuiImage.GuiCircle(robot.aUnScale.getPosVector(), 2, Color.BLACK, 3), true);
        image.Draw(new GuiImage.GuiCircle(robot.bUnScale.getPosVector(), 2, Color.BLUE, 3), true);
        if(rout != null){
            Vector2Dv1 last = robot.getPosVector();
            for (Vector2Dv1 v : rout) {
                image.Draw(new GuiImage.GuiLine(last, v, Color.GREEN, 2), false);
                image.Draw(new GuiImage.GuiCircle(v, 3, Color.RED, 3), false);
                last = v;
            }
            image.update();
        }
        label.setIcon(image.getIcon());
    }



}

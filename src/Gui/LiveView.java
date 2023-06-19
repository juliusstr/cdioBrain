package Gui;

import Gui.Image.GuiImage;
import misc.Robotv1;
import misc.Vector2Dv1;
import org.opencv.core.Mat;

import java.awt.*;

public class LiveView {

    private GuiImage image;
    private Robotv1 robot;
    public LiveView(Mat mat, Robotv1 robot){
        image = new GuiImage(mat);
        this.robot = robot;
    }

    public void update(Mat mat, Vector2Dv1 nextWaypoint){
        image = new GuiImage(mat);
        image.Draw(new GuiImage.GuiCircle(robot.aScale.getPosVector(), 2, Color.BLACK, 3), true);
        image.Draw(new GuiImage.GuiCircle(robot.bScale.getPosVector(), 2, Color.BLUE, 3), true);
        if(nextWaypoint != null)
            image.Draw(new GuiImage.GuiCircle(nextWaypoint, 2, Color.RED, 2), true);
    }

}

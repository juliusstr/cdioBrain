package misc.ball;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class Ball extends PrimitiveBall{

    public enum Type {
        BALL,
        ROBOT_FRONT,
        ROBOT_BACK,
        UKNOWN
    }
    public static final double PX_TO_MM = 1.2;
    public static final int BALL_POS_HIS_MAX_SIZE = 10;
    private int radius;
    private int id;
    private Color color;
    private boolean isInPx;
    private Type type;
    private int lastSeenAlive;
    private ArrayList<Point> ballPosHis;

    public Ball(int xPos, int yPos, int radius, Color color, boolean isInPx, Status status, int id, Type type) {
        super(xPos, yPos);
        this.radius = radius;
        this.color = color;
        this.isInPx = isInPx;
        ballPosHis = new ArrayList<>();
        this.id = id;
        this.type = type;
        lastSeenAlive = -1;
    }

    @Override
    public String toString() {
        return "Ball{" +
                "radius=" + radius +
                ", color=" + color +
                ", isInPx=" + isInPx +
                ", xPos=" + xPos +
                ", yPos=" + yPos +
                ", status=" + status +
                '}';
    }

    public void setId(int id) {
        this.id = id;
    }

    public void incrementLastSeenAlive(){
        lastSeenAlive++;
    }
    public void zeroLastSeenAlive(){
        lastSeenAlive = 0;
    }

    public int getLastSeenAlive(){
        return lastSeenAlive;
    }

    public int getId(){
        return id;
    }

    public void setType(Type type){
        this.type = type;
    }

    public Type getType(){
        return type;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color){
        this.color = new Color(color.getRGB());
    }

    public boolean getIsInPx(){
        return isInPx;
    }


    public void convertPxToMm(){
        if (isInPx){
            xPos = (int) (xPos*PX_TO_MM);
            yPos = (int) (yPos*PX_TO_MM);
            radius = (int) (radius*PX_TO_MM);
        }
        //might want to look at the radius and since we know the ball size we can then calculate the ratio and then use that ratio insted of the PX_TO_MM
    }

    public ArrayList<Point> getBallPosHis(){
        return ballPosHis;
    }


}

package misc;

import java.awt.*;

public class Ball {

    public static final double PX_TO_MM = 1.2;
    private int xPos,
        yPos,
        radius;
    private Color color;
    private boolean isInPx;

    public Ball(int xPos, int yPos, int radius, Color color, boolean isInPx) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.radius = radius;
        this.color = color;
        this.isInPx = isInPx;
    }

    @Override
    public String toString() {
        return "Ball{" +
                "xPos=" + xPos +
                ", yPos=" + yPos +
                ", radius=" + radius +
                ", color=" + color +
                ", isInPx=" + isInPx +
                '}';
    }

    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    public void setYPos(int yPos) {
        this.yPos = yPos;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setType(Color color) {
        this.color = color;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
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

    public Point getPoint(){
        return new Point(xPos, yPos);
    }

    public void convertPxToMm(){
        if (isInPx){
            xPos = (int) (xPos*PX_TO_MM);
            yPos = (int) (yPos*PX_TO_MM);
            radius = (int) (radius*PX_TO_MM);
        }
        //might want to look at the radius and since we know the ball size we can then calculate the ratio and then use that ratio insted of the PX_TO_MM
    }


}

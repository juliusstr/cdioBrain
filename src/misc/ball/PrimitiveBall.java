package misc.ball;

import misc.Vector2Dv1;

import java.awt.*;

public class PrimitiveBall {
    public enum Status {
        IN_PLAY,
        COLLECTED,
        DELIVERED,
        GONE,
        IN_ROUTE,
        ROBOT,
        UNKNOWN
    }

    protected int xPos;
    protected int yPos;
    protected Status status;

    public PrimitiveBall(int xPos, int yPos, Status status) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.status = status;
    }

    public PrimitiveBall(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.status = Status.UNKNOWN;
    }

    public PrimitiveBall(Point point, Status status){
        this.xPos = point.x;
        this.yPos = point.y;
        this.status = status;
    }
    public PrimitiveBall(Point point){
        this.xPos = point.x;
        this.yPos = point.y;
        this.status = Status.UNKNOWN;
    }

    public PrimitiveBall(Vector2Dv1 vector, Status status){
        this.xPos = (int) vector.x;
        this.yPos = (int) vector.y;
        this.status = status;
    }
    public PrimitiveBall(Vector2Dv1 vector){
        this.xPos = (int) vector.x;
        this.yPos = (int) vector.y;
        this.status = Status.UNKNOWN;
    }

    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    public void setYPos(int yPos) {
        this.yPos = yPos;
    }

    public void setPos(Point point){
        xPos = point.x;
        yPos = point.y;
    }
    public Status getStatus(){
        return status;
    }
    public void setStatus(Status status){
        this.status = status;
    }
    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public Point getPoint(){
        return new Point(xPos, yPos);
    }

    public Vector2Dv1 getPosVector() {
        return new Vector2Dv1(xPos, yPos);
    }

    @Override
    public String toString() {
        return "PrimitiveBall{" +
                "xPos=" + xPos +
                ", yPos=" + yPos +
                ", status=" + status +
                '}';
    }


}

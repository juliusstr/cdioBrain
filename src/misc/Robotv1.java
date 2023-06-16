package misc;

import Client.StandardSettings;
import misc.ball.Ball;
import misc.ball.BallClassifierPhaseTwo;
import routePlaner.Route;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Robotv1 {
    private double xPos,
                yPos;
    private Vector2Dv1 directionVector;
    private double  speed,
                    angelSpeed;

    double scale = 0.94;

    public Robotv1(double xPos, double yPos, Vector2Dv1 directionVector) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.directionVector = directionVector.clone();
        speed = 0;
        angelSpeed = 0;
    }

    private ArrayList<Route> heat1Routes = new ArrayList<>();
    private ArrayList<Route> heat2Routes = new ArrayList<>();
    private ArrayList<Route> heat3Routes = new ArrayList<>();
    private ArrayList<Route> heatLiveRoutes = new ArrayList<>();

    private boolean heatReset = false;
    private int heatRouteNum = 1;

    public double getScale(){
        return scale;
    }

    public void endHeatRoutes(){
        if(heatRouteNum > 3)
            heatReset = true;
        heatRouteNum++;
    }

    public ArrayList<Route> getRoutes(int heat) {
        switch (heat){
            case 1:
                return heat1Routes;
            case 2:
                return heat2Routes;
            case 3:
                return heat3Routes;
        }
        return heatLiveRoutes;
    }

    public void addRoute(Route r) {
        switch (heatRouteNum){
            case 1:
                this.heat1Routes.add(r);
            break;
            case 2:
                this.heat2Routes.add(r);
            break;
            case 3:
                this.heat3Routes.add(r);
            break;
            default:
                if(heatReset)
                    this.heatLiveRoutes.clear();
                heatReset = false;
                this.heatLiveRoutes.add(r);
        }
    }

    public double getxPos() {
        return xPos;
    }

    public void setxPos(double xPos) {
        this.xPos = xPos;
    }

    public double getyPos() {
        return yPos;
    }

    public void setyPos(double yPos) {
        this.yPos = yPos;
    }
    public void setPos(double x, double y){
        xPos = x;
        yPos = y;
    }

    public Vector2Dv1 getDirection() {
        return directionVector;
    }

    public Vector2Dv1 getPosVector(){
        return new Vector2Dv1(xPos,yPos);
    }

    public void setDirection(Vector2Dv1 directionVector) {
        this.directionVector = directionVector;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getAngelSpeed() {
        return angelSpeed;
    }

    public void setAngelSpeed(double angelSpeed) {
        this.angelSpeed = angelSpeed;
    }

    public void updatePos(Ball a, Ball b){
        //640X360
        Vector2Dv1 av = a.getPosVector();
        Vector2Dv1 bv = b.getPosVector();
        Vector2Dv1 mid = new Vector2Dv1(640/2,360/2);

        av.subtract(mid);
        av.multiply(scale);
        av.add(mid);

        bv.subtract(mid);
        bv.multiply(scale);
        bv.add(mid);

        a.setPos(av.getPoint());
        b.setPos(bv.getPoint());
        Ball back = b;
        Ball front  = a;
        if (!back.getColor().equals(BallClassifierPhaseTwo.BLACK)){
            Ball temp = front;
            front = back;
            back = temp;
        }
        setPos(front.getxPos(), front.getyPos());
        Vector2Dv1 dir = new Vector2Dv1(front.getxPos(), front.getyPos());
        dir.subtract(new Vector2Dv1(back.getxPos(), back.getyPos()));
        setDirection(dir);
    }



}

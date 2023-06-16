package misc.ball;

import Client.StandardSettings;
import misc.Vector2Dv1;
import misc.Zone;
import routePlaner.Route;

import java.awt.*;
import java.util.ArrayList;

public class Ball extends PrimitiveBall{

    public enum Type {
        BALL,
        ROBOT_FRONT,
        ROBOT_BACK,
        UNKNOWN
    }
    public enum Placement {
        FREE,
        CORNER,
        EDGE,

        PAIR
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
    private int zoneGroupId;

    private Vector2Dv1 pickUpVector = null;

    private Placement placement =null;

    private ArrayList<Route> routes = new ArrayList<>();
    private Route goalRoute = null;


    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<Route> routes) {
        this.routes = routes;
    }
    public Route getGoalRoute() {
        return goalRoute;
    }

    public void setGoalRoute(Route route) {
        this.goalRoute = route;
    }

    public Boolean is(int id){
        return (id == this.id);
    }

    public void removeRoute(Ball b) {
        for (Route r: routes) {
            if(b.is(r.getEnd().getId())){
                routes.remove(r);
                break;
            }
        }
    }

    public void addRoute(Route r) {
        this.routes.add(r);
    }

    public Ball(int xPos, int yPos, int radius, Color color, boolean isInPx, Status status, int id, Type type) {//todo add status to super call
        super(xPos, yPos);
        this.radius = radius;
        this.color = color;
        this.isInPx = isInPx;
        ballPosHis = new ArrayList<>();
        this.id = id;
        this.type = type;
        lastSeenAlive = -1;
        zoneGroupId = -1;
    }
    public Ball(Vector2Dv1 pos, int radius, Color color, boolean isInPx, Status status, int id, Type type) {//todo add status to super call
        super((int)pos.x, (int)pos.y);
        this.radius = radius;
        this.color = color;
        this.isInPx = isInPx;
        ballPosHis = new ArrayList<>();
        this.id = id;
        this.type = type;
        lastSeenAlive = -1;
        zoneGroupId = -1;
    }

    /**
     * ONLY TO USE FOR SIMULATION
     * @param pos
     */
    public Ball(Vector2Dv1 pos){
        super((int)pos.x, (int)pos.y);
        this.radius = StandardSettings.BALL_RADIUS_PX;
        this.color = BallClassifierPhaseTwo.WHITE;
        this.isInPx = true;
        ballPosHis = new ArrayList<>();
        this.id = -1;
        this.type = Type.BALL;
        lastSeenAlive = -1;
        zoneGroupId = -1;
    }

    @Override
    public String toString() {
        return "Ball{" +
                "radius=" + radius +
                ", type=" + type +
                ", isInPx=" + isInPx +
                ", xPos=" + xPos +
                ", yPos=" + yPos +
                ", status=" + status +
                ", zoneGroupeId=" + zoneGroupId +
                ", placement=" + placement +
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

    public int getZoneGroupId() {
        return zoneGroupId;
    }

    public void setZoneGroupId(int zoneGroupId) {
        this.zoneGroupId = zoneGroupId;
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

    public Zone getSafetyZone(){
        return new Zone(this.getPosVector(), Zone.SAFE_ZONE_RADIUS + radius, zoneGroupId);
    }

    public Zone getCriticalZone(){
        return new Zone(this.getPosVector(), Zone.CRITICAL_ZONE_RADIUS + radius, zoneGroupId);
    }

    public void setZoneGroupIdToAdjacentBalls(ArrayList<Ball> balls){
        for (int i = 0; i < balls.size(); i++) {
            if (this == balls.get(i))
                continue;
            if (balls.get(i).getZoneGroupId() == -1){
                double distMax = balls.get(i).getCriticalZone().radius+this.getCriticalZone().radius;
                double dist = balls.get(i).getPosVector().distance(this.getPosVector());
                if(dist<= distMax){
                    balls.get(i).setZoneGroupId(this.zoneGroupId);
                    balls.get(i).setZoneGroupIdToAdjacentBalls(balls);
                }
            }
        }
    }

    public Placement getPlacement() {
        return placement;
    }

    public void setPlacement(Placement placement) {
        this.placement = placement;
    }

    public void setPickUpWaypoint(Vector2Dv1 vector){
        pickUpVector = vector;
    }

    public Vector2Dv1 getPickUpPoint(){

        if(pickUpVector == null)
            return this.getPosVector();
        return getPosVector().getAdded(pickUpVector);
    }

    public Vector2Dv1 getLineUpPoint(){
        Vector2Dv1 dir = this.getPosVector().getSubtracted(getPickUpPoint());
        dir = dir.getNormalized().getMultiplied(StandardSettings.ROUTE_PLANER_GOAL_CASTER_WEEL_LINE_UP);
        return getPickUpPoint().getAdded(dir);
    }


}

package misc;

import java.awt.*;

public class Vector2Dv1 {

    public double x;
    public double y;

    public Vector2Dv1() { }

    public Vector2Dv1(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2Dv1(Vector2Dv1 v) {
        set(v);
    }

    public Vector2Dv1(Point p){
        x = p.x;
        y = p.y;
    }

    public Vector2Dv1(double angle){
        x = Math.cos(angle);
        y = Math.sin(angle);
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void set(Vector2Dv1 v) {
        this.x = v.x;
        this.y = v.y;
    }

    public Point getPoint(){
        return new Point((int) Math.round(this.x), (int)Math.round(this.y));
    }
    public org.opencv.core.Point PointOpenCV(){
        return new org.opencv.core.Point(this.x, this.y);
    }

    public void setZero() {
        x = 0;
        y = 0;
    }

    public double[] getComponents() {
        return new double[]{x, y};
    }

    public double getLength() {
        return Math.sqrt(x * x + y * y);
    }

    public double getLengthSq() {
        return (x * x + y * y);
    }

    public double distanceSq(double vx, double vy) {
        vx -= x;
        vy -= y;
        return (vx * vx + vy * vy);
    }

    public double distanceSq(Vector2Dv1 v) {
        double vx = v.x - this.x;
        double vy = v.y - this.y;
        return (vx * vx + vy * vy);
    }

    public double distance(double vx, double vy) {
        vx -= x;
        vy -= y;
        return Math.sqrt(vx * vx + vy * vy);
    }

    public double distance(Vector2Dv1 v) {
        double vx = v.x - this.x;
        double vy = v.y - this.y;
        return Math.sqrt(vx * vx + vy * vy);
    }

    public double getAngle() {
        return Math.atan2(y, x);
    }

    public void normalize() {
        double magnitude = getLength();
        x /= magnitude;
        y /= magnitude;
    }

    public Vector2Dv1 getNormalized() {
        double magnitude = getLength();
        return new Vector2Dv1(x / magnitude, y / magnitude);
    }

    public static Vector2Dv1 toCartesian(double magnitude, double angle) {
        return new Vector2Dv1(magnitude * Math.cos(angle), magnitude * Math.sin(angle));
    }

    public void add(Vector2Dv1 v) {
        this.x += v.x;
        this.y += v.y;
    }

    public void add(double vx, double vy) {
        this.x += vx;
        this.y += vy;
    }

    public static Vector2Dv1 add(Vector2Dv1 v1, Vector2Dv1 v2) {
        return new Vector2Dv1(v1.x + v2.x, v1.y + v2.y);
    }

    public Vector2Dv1 getAdded(Vector2Dv1 v) {
        return new Vector2Dv1(this.x + v.x, this.y + v.y);
    }

    public void subtract(Vector2Dv1 v) {
        this.x -= v.x;
        this.y -= v.y;
    }

    public void subtract(double vx, double vy) {
        this.x -= vx;
        this.y -= vy;
    }

    public static Vector2Dv1 subtract(Vector2Dv1 v1, Vector2Dv1 v2) {
        return new Vector2Dv1(v1.x - v2.x, v1.y - v2.y);
    }

    public Vector2Dv1 getSubtracted(Vector2Dv1 v) {
        return new Vector2Dv1(this.x - v.x, this.y - v.y);
    }

    public void multiply(double scalar) {
        x *= scalar;
        y *= scalar;
    }

    public Vector2Dv1 getMultiplied(double scalar) {
        return new Vector2Dv1(x * scalar, y * scalar);
    }

    public void divide(double scalar) {
        x /= scalar;
        y /= scalar;
    }

    public Vector2Dv1 getDivided(double scalar) {
        return new Vector2Dv1(x / scalar, y / scalar);
    }

    public Vector2Dv1 getPerp() {
        return new Vector2Dv1(-y, x);
    }

    public double dot(Vector2Dv1 v) {
        return (this.x * v.x + this.y * v.y);
    }

    public double dot(double vx, double vy) {
        return (this.x * vx + this.y * vy);
    }

    public static double dot(Vector2Dv1 v1, Vector2Dv1 v2) {
        return v1.x * v2.x + v1.y * v2.y;
    }

    public double cross(Vector2Dv1 v) {
        return (this.x * v.y - this.y * v.x);
    }

    public double cross(double vx, double vy) {
        return (this.x * vy - this.y * vx);
    }

    public static double cross(Vector2Dv1 v1, Vector2Dv1 v2) {
        return (v1.x * v2.y - v1.y * v2.x);
    }

    public double project(Vector2Dv1 v) {
        return (this.dot(v) / this.getLength());
    }

    public double project(double vx, double vy) {
        return (this.dot(vx, vy) / this.getLength());
    }

    public static double project(Vector2Dv1 v1, Vector2Dv1 v2) {
        return (dot(v1, v2) / v1.getLength());
    }

    public Vector2Dv1 getProjectedVector(Vector2Dv1 v) {
        return this.getNormalized().getMultiplied(this.dot(v) / this.getLength());
    }

    public Vector2Dv1 getProjectedVector(double vx, double vy) {
        return this.getNormalized().getMultiplied(this.dot(vx, vy) / this.getLength());
    }

    public static Vector2Dv1 getProjectedVector(Vector2Dv1 v1, Vector2Dv1 v2) {
        return v1.getNormalized().getMultiplied(Vector2Dv1.dot(v1, v2) / v1.getLength());
    }

    public void rotateBy(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double rx = x * cos - y * sin;
        y = x * sin + y * cos;
        x = rx;
    }

    public Vector2Dv1 getRotatedBy(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vector2Dv1(x * cos - y * sin, x * sin + y * cos);
    }

    public void rotateTo(double angle) {
        set(toCartesian(getLength(), angle));
    }

    public Vector2Dv1 getRotatedTo(double angle) {
        return toCartesian(getLength(), angle);
    }

    public void reverse() {
        x = -x;
        y = -y;
    }

    public Vector2Dv1 getReversed() {
        return new Vector2Dv1(-x, -y);
    }

    @Override
    public Vector2Dv1 clone() {
        return new Vector2Dv1(x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Vector2Dv1) {
            Vector2Dv1 v = (Vector2Dv1) obj;
            return (x == v.x) && (y == v.y);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Vector2d[" + x + ", " + y + "]";
    }

    public boolean samePos(Vector2Dv1 vector2Dv1) {
        if(x == vector2Dv1.x){
            if (y == vector2Dv1.y){
                return true;
            }
        }
        return false;
    }
}
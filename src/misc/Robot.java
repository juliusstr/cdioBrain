package misc;

public class Robot {
    private double xPos,
                yPos;
    private Vector2D directionVector;
    private double  speed,
                    angelSpeed;

    public Robot(double xPos, double yPos, Vector2D directionVector) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.directionVector = directionVector.clone();
        speed = 0;
        angelSpeed = 0;
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

    public Vector2D getDirection() {
        return directionVector;
    }

    public void setDirection(Vector2D directionVector) {
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
        Ball back = b;
        Ball front  = a;
        if (!back.getColor().equals(BallClassifier.BALCK)){
            Ball temp = front;
            front = back;
            back = temp;
        }
        setPos(front.getxPos(), front.getyPos());
        Vector2D dir = new Vector2D(front.getxPos(), front.getyPos());
        dir.subtract(new Vector2D(back.getxPos(), back.getyPos()));
        setDirection(dir);
    }
}

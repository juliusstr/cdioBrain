package misc.ball;
import java.awt.*;
import java.util.List;

public class BallClassifierPhaseTwo {

    public static final Color BLACK = new Color(40, 40, 40);
    public static final Color RED = new Color(253, 97, 60);
    public static final Color WHITE = new Color(255, 255, 255);



    public static void classify(List<Ball> balls){
        for (Ball ball : balls) {
            classify(ball);
        }
    }

    public static void classify(Ball ball){
        ball.setColor(colorCorection(ball.getColor()));
        if(ball.getColor().equals(WHITE)){
            ball.setType(Ball.Type.BALL);
        } else if (ball.getColor().equals(RED)) {
            ball.setType(Ball.Type.ROBOT_FRONT);
            ball.setStatus(PrimitiveBall.Status.ROBOT);
        } else if (ball.getColor().equals(BLACK)) {
            ball.setType(Ball.Type.ROBOT_BACK);
            ball.setStatus(PrimitiveBall.Status.ROBOT);
        } else {
            ball.setType(Ball.Type.UKNOWN);
        }
    }

    private static Color colorCorection(Color color){
        if (color.getRed() < 150 && color.getBlue() < 150 && color.getGreen() < 150){
            return BLACK;
        }
        if(color.getRed() > 200 && color.getBlue() < 150 && color.getGreen() < 150){
            return RED;
        }
        return WHITE;
    }
}

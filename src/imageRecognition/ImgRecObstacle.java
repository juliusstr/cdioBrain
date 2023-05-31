package imageRecognition;

import exceptions.BadDataException;
import misc.Boundry;
import misc.Cross;
import misc.Vector2Dv1;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ImgRecObstacle {

    public static final int arg0 = 20;
    public static final int arg1 = 20;
    public static final int arg2 = 180;
    public static final int arg3 = 130;
    public static final int arg4 = 130;
    public static final int arg5 = 255;

    public static final int arg6 = 4;
    public static final int arg7 = 20;
    public static final int arg8 = 20;
    public static final int arg9 = 2;

    public Boundry boundry;
    public Cross cross;


    public void findeObstacle(Mat img) throws BadDataException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // Load the input image
        //Imgproc.resize(input, input, new Size(640, 360));

        Mat input = img.clone();
        Mat print = input.clone();
        // Convert the image to HSV color space
        Imgproc.GaussianBlur(input, input, new Size(9, 9), 0, 0);

        // Define the range of orange color in HSV
        Scalar lowerOrange = new Scalar(arg0, arg1, arg2);
        Scalar upperOrange = new Scalar(arg3, arg4, arg5);
        Core.inRange(input, lowerOrange, upperOrange, input); //hsv

        // Apply morphology operations to remove noise and fill gaps
        Mat kernel = Imgproc.getStructuringElement((arg9 == 0 ? Imgproc.MORPH_ELLIPSE : (arg9 == 1 ? Imgproc.MORPH_RECT : Imgproc.MORPH_CROSS)) , new Size(arg7, arg8));
        Imgproc.morphologyEx(input, input, Imgproc.MORPH_OPEN, kernel);
        Imgproc.morphologyEx(input, input, Imgproc.MORPH_CLOSE, kernel);

        Mat dots = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(input, contours, dots, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0,0));

        //System.out.println("Test");
        ArrayList<Vector2Dv1> centers = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            //System.out.println("x: " + (rect.x + rect.width/2) + ", y: " + (rect.y + rect.height/2));
            centers.add(new Vector2Dv1((rect.x + rect.width/2),(rect.y + rect.height/2)));
            //Imgproc.rectangle(input, rect, new Scalar(255, 255, 0), 2);
        }
        //Imgproc.GaussianBlur(input, input, new Size(21, 21), 0, 0);
        int index = 0;
        double score = Double.MAX_VALUE;
        for (int i = 0; i < centers.size(); i++) {
            double temp  = 0;
            for (int k = 0; k < centers.size(); k++) {
                if (i!=k){
                    temp += centers.get(i).distance(centers.get(k));
                }
            }
            if (temp < score){
                index = i;
                score = temp;
            }
            System.out.println("center x:" + centers.get(i).x + " y:" + centers.get(i).y);
        }

        if(centers.size() != 5){
            throw new BadDataException("could not finde all the obstacles");
        }



        MatOfPoint2f contour = new MatOfPoint2f();
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        contour.fromList(contours.get(index).toList());
        double peri = Imgproc.arcLength(contour, true);
        Imgproc.approxPolyDP(contour, approxCurve, 0.02 * peri, true);
        RotatedRect rect = Imgproc.minAreaRect(approxCurve);
        double angle = rect.angle;
        if (angle < -45) {
            angle += 90;
        }
        angle -= 45;
        if (angle < -45) {
            angle += 90;
        }
        angle = (Math.PI/180)*angle;
        //System.out.println("angel: " + angle + "rad");
        Vector2Dv1 crossPos = centers.get(index);
        centers.remove(index);
        /*for (java.awt.Point point: cross.crossPoint) {
            System.out.println("" + point.toString());
        }*/
        boundry = new Boundry(centers);
        crossPos.multiply(boundry.scale);
        cross = new Cross(new Vector2Dv1(crossPos.x,crossPos.y),new Vector2Dv1(angle));
    }
}

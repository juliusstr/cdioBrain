package imageRecognition;

import misc.ball.BallClassifierPhaseTwo;
import org.opencv.features2d.SimpleBlobDetector_Params;

public class ImgRecParams {
    SimpleBlobDetector_Params params;
    public ImgRecParams(){
        params = new SimpleBlobDetector_Params();
        params.set_filterByColor(false);
        params.set_minArea(45);
        params.set_maxArea(170);
        //min distance
        params.set_minDistBetweenBlobs(10);
        params.set_minConvexity(0.9F);
        params.set_maxConvexity(1F);
        params.set_minThreshold(1);
        //params.set_maxThreshold(255);
        params.set_collectContours(false);
        params.set_minCircularity(0.999F);
        params.set_minInertiaRatio(0.6F);
    }

    public SimpleBlobDetector_Params getParams() {
        return params;
    }
}

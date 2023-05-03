package imageRecognition;

import org.opencv.features2d.SimpleBlobDetector_Params;

public class ImgRecParams {
    SimpleBlobDetector_Params params;
    public ImgRecParams(){
        params = new SimpleBlobDetector_Params();
        params.set_filterByColor(false);
        params.set_minArea(50);
        params.set_maxArea(170);
        //min distance
        params.set_minDistBetweenBlobs(10);
        params.set_minConvexity(0.7F);
        params.set_maxConvexity(1F);
        params.set_minThreshold(0.8F);
        //params.set_maxThreshold(0.99F);
        params.set_collectContours(false);
        params.set_minCircularity(0.8F);
        params.set_minInertiaRatio(0.8F);
    }

    public SimpleBlobDetector_Params getParams() {
        return params;
    }
}

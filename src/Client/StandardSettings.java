package Client;

public class StandardSettings {
    public static final int VIDIO_CAPTURE_INDEX = 3;


    /**
     * Ball
     */
    public static final int BALL_RADIUS_PX = 8;




    /**
     * -----------------------------
     * NAV Settings
     * -----------------------------
     */

    /**
     * Depth of search in waypoint generator.
     * Above 10 is not needed. Might be able to get away with 5 or 6.
     */
    public static final int NAV_MAX_SEARCH_TREE_DEPTH_WAYPOINT = 10;
    /**
     * sppeds up nav by 300-400% but does not get best route.
     */
    public static final boolean NAV_WAYPOINT_GENERATOR_SPEED_SEARCH = false;
    /**
     * Number of threads to use in search for route in wayPointGen
     */
    public static final int NAV_WAYPOINT_GENERATOR_NUMBER_OF_THREADS = 8;

}

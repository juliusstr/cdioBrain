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
    public static final int NAV_MAX_SEARCH_TREE_DEPTH_WAYPOINT = 6;
    /**
     * sppeds up nav by 300-400% but does not get best route.
     */
    public static final boolean NAV_WAYPOINT_GENERATOR_SPEED_SEARCH = false;

}

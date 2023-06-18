package Client;

public class StandardSettings {
    public static final int VIDIO_CAPTURE_INDEX = 1;


    /**
     * Ball
     */
    public static final int BALL_RADIUS_PX = 5;

    /**
     * Speed boot will reduce img quality in clicker
     */
    public static final boolean SPEED_BOOT = false;

    /**
     * -----------------------------
     * NAV Settings
     * -----------------------------
     */

    /**
     * Depth of search in waypoint generator.
     * Above 10 is not needed. Might be able to get away with 5 or 6.
     */
    public static final int NAV_MAX_SEARCH_TREE_DEPTH_WAYPOINT = 4;
    /**
     * sppeds up nav by 300-400% but does not get best route.
     */
    public static final boolean NAV_WAYPOINT_GENERATOR_SPEED_SEARCH = true;
    /**
     * Number of threads to use in search for route in wayPointGen
     */
    public static final int NAV_WAYPOINT_GENERATOR_NUMBER_OF_THREADS = 8;


    /**
     * -----------------------------
     * BallClassifierPhaseTwo
     * -----------------------------
     */
    public static final int CLASSIFIER_VIRTUAL_WAYPOINT_DISTANCE_FROM_BALL = 42;

    /**
     * -----------------------------
     * RoutPlanerPhaseTwo
     * -----------------------------
     */
    public static final int ROUTE_PLANER_GOAL_RUN_UP_DIST = 43;
    public static final int ROUTE_PLANER_GOAL_CASTER_WEEL_LINE_UP = 30;

    public static final int ROUTE_PLANER_DISTANCE_FROM_LINE_BEFORE_TURN = 40;

    /**
     * CommandGenerator
     */
    public static final double ANGLE_ERROR = Math.PI/180*1;
    public static final double TARGET_DISTANCE_ERROR = 45;
    public static final double WAYPOINT_DISTANCE_ERROR = 5;
    public static final double FINAL_WAYPOINT_DISTANCE_ERROR = 4;

    public static final double MIN_TURN_SPEED = 0.12;

    public static final String COLLECT_COMMAND = "collect";
    public static final String DROP_OFF_COMMAND = "drop";
    public static final String COLLECT_EDGE_COMMAND = "corner";
    public static final String COLLECT_CORNER_COMMAND = "corner";
    public static final String COLLECT_PAIR_COMMAND = "corner";

    /**
     * Boundery
     */
    public static final double BOUNDERY_WAYPOINT_DISTANCE_FROM_BOUNDERY = 40;

}

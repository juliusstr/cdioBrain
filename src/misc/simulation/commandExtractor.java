package misc.simulation;

public class commandExtractor {
    commandExtractor() {
    }

    /**
     * Extract turn direction from the command string.
     * @param command The command send to robot
     * @return String with the turn direction. Either "l" for left or "r" for right
     */
    public String extractTurnDirection(String command) {
        String[] parts = command.split(";");
        String turnCommand = "";

        for (String part : parts) {
            if (part.startsWith("turn")) {
                turnCommand = part;
                break;
            }
        }

        String[] turnTokens = turnCommand.split(" ");
        for (String token : turnTokens) {
            if (token.equals("l")) {
                return "Left";
            } else if (token.equals("r")) {
                return "Right";
            }
        }

        return ""; // Default turn direction if not found
    }
    public double extractTurnSpeed(String command) {
        String[] parts = command.split(";");
        String turnCommand = "";

        for (String part : parts) {
            if (part.startsWith("turn")) {
                turnCommand = part;
                break;
            }
        }

        String[] turnTokens = turnCommand.split(" ");
        for (String token : turnTokens) {
            if (token.startsWith("-s")) {
                String turnSpeedString = token.substring(2);
                return Double.parseDouble(turnSpeedString);
            }
        }

        return 0.0; // Default turn speed if not found
    }
    public double extractSpeed(String command) {
        String[] parts = command.split(";");
        String driveCommand = "";

        for (String part : parts) {
            if (part.startsWith("drive")) {
                driveCommand = part;
                break;
            }
        }

        String[] driveTokens = driveCommand.split(" ");
        for (String token : driveTokens) {
            if (token.startsWith("-s")) {
                String speedString = token.substring(2);
                return Double.parseDouble(speedString);
            }
        }

        return 0.0; // Default speed if not found
    }

}

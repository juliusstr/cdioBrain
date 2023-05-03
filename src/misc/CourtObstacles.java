package misc;

public class CourtObstacles {

    private int xPos, yPos, dir;
    private int[][] CourtPoints= new int[3][1];

    public CourtObstacles(int xPos,int yPos, int dir, int[][] CourtPoints){
        this.xPos = xPos;
        this.yPos = yPos;
        this.dir = dir;

        //not sure if this is overkill.
        for (int i = 0; i < CourtPoints.length; ++i) {
            for(int j = 0; j < CourtPoints[i].length; ++j) {
                this.CourtPoints[i][j] = CourtPoints[i][j];
            }
        }


    }

    public int getxPos() { return xPos; }
    public int getyPos() { return yPos; }

    public int getDir() { return dir; }
    public int[][] getCourtPoints() { return CourtPoints; }

    public void setxPos(int xPos) { this.xPos = xPos; }
    public void setyPos(int yPos) { this.yPos = yPos; }
    public void setDir(int dir) { this.dir = dir; }

    public void setCourtPoints(int[][] courtPoints) { this.CourtPoints = courtPoints; }



}

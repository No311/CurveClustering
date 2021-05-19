package DataStructures.TrajCover;

import DataStructures.Reachability.Reachability;
import Objects.GridPoint;

import java.util.Stack;

public class TrajCoverLog implements TrajCover {
    GridPoint[][] pointmatrix;
    Reachability reach;
    GridPoint[][][] Row1Reach;    //This structure (row1, row2, column) takes two rows and the column of a point on the
                                  //second row, and returns a point on the first row in column that can reach it,
                                  //if it exists.
    GridPoint[][][] POI2;         //This structure contains all gridpoints per row (row 2) that can be reached from
                                  //another specified row (row 1). Note: it is a stack, so cloning is necessary.


    @Override
    public void preprocess(GridPoint[][] pointmatrix, Reachability reach) {
        this.pointmatrix = pointmatrix;
        this.reach = reach;
        Row1Reach = new GridPoint[pointmatrix.length][pointmatrix.length][pointmatrix[0].length];
        for (int srow = 0; srow < pointmatrix.length; srow++){
            int actualsrow = pointmatrix.length-1-srow;
            for (int grow = pointmatrix.length-1; grow >= 0; grow--){
                int actualgrow = pointmatrix.length-1-grow;
                int scol = pointmatrix[0].length-1;
                int gcol = pointmatrix[0].length-1;
                Stack<GridPoint> pointsOfInterest = new Stack<>();
                while (gcol >= 0){
                    GridPoint goal = pointmatrix[actualgrow][gcol];
                    while (goal == null && gcol >= 0){
                        gcol--;
                        goal = pointmatrix[actualgrow][gcol];
                    }
                    if (scol > gcol){
                        scol = gcol;
                    }
                    GridPoint start = pointmatrix[actualsrow][scol];
                    while (start == null && scol >=0){
                        scol--;
                        start = pointmatrix[actualsrow][scol];
                    }
                    if (reach.query(start, goal)){
                        Row1Reach[srow][grow][gcol] = start;
                        pointsOfInterest.push(goal);
                        gcol--;
                    } else{
                        scol--;
                    }
                }
                POI2[srow][grow] = (GridPoint[]) pointsOfInterest.toArray();
                }
            }
    }

    @Override
    public QueryResult query(int pColumn, int qStart, int qEnd) {
        return new QueryResult(null, null);
    }

}

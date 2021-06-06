package DataStructures.TrajCover;

import DataStructures.Reachability.Reachability;
import Objects.GridPoint;

public class TrajCoverNaive extends TrajCover{
    GridPoint[][][] furthestReach; //furthestReach[row1][row2][column] gives the farthest gridpoint on row 2
                                   //that the gridpoint at coordinates (row1, column) can reach.
    GridPoint[][][] queries; //queries[row1][row2][p] gives a gridpoint with the farthest distance between its
                             //furthestReach gridpoint and it in x-direction that fulfills the query.

    GridPoint[][] pointmatrix;
    Reachability reach;



    @Override
    public void preprocess(GridPoint[][] pointmatrix, Reachability reach) {
        super.preprocess(pointmatrix, reach);
        computeRow1Reach(null);
        furthestReach = new GridPoint[pointmatrix.length][pointmatrix.length][pointmatrix[0].length];
        queries = new GridPoint[pointmatrix.length][pointmatrix.length][pointmatrix[0].length];
        //prepping furthestReach
        for (int srow = 0; srow < pointmatrix.length; srow++){
            int actualsrow = pointmatrix.length-1-srow;
            for (int grow = pointmatrix.length-1; grow >= 0; grow--){
                int actualgrow = pointmatrix.length-1-grow;
                int lastgcol = 0;
                for (int scol = 0; scol < pointmatrix[0].length; scol++){
                    int gcol = lastgcol;
                    GridPoint start = pointmatrix[actualsrow][scol];
                    if (start != null) {
                        GridPoint furthest = null;
                        while (gcol < pointmatrix[0].length) {
                            GridPoint goal = pointmatrix[actualgrow][gcol];
                            if (goal != null) {
                                if (reach.query(start, goal)) {
                                    lastgcol = gcol;
                                    furthest = goal;
                                }
                            }
                            gcol++;
                        }
                        furthestReach[srow][grow][scol] = furthest;
                    }
                }
            }
        }

        //doing all possible queries
        for (int srow = 0; srow < pointmatrix.length; srow++){
            int actualsrow = pointmatrix.length-1-srow;
            for (int grow = pointmatrix.length-1; grow >= 0; grow--) {
                for (int p = 0; p < pointmatrix[0].length; p++){
                    GridPoint bestPoint = null;
                    int bestDistance = 0;
                    for (int scol = 0; scol <= p; scol++){
                        GridPoint start = pointmatrix[actualsrow][scol];
                        GridPoint goal = furthestReach[srow][grow][scol];
                        if (start != null && goal != null){
                            if (goal.column >= p){
                                int dist = goal.column - scol;
                                if (dist > bestDistance){
                                    bestDistance = dist;
                                    bestPoint = start;
                                }
                            }
                        }
                    }
                    queries[srow][grow][p] = bestPoint;
                }
            }
        }
    }

    @Override
    public QueryResult query(int qStart, int qEnd, int pColumn) {
        GridPoint start = queries[qStart][qEnd][pColumn];
        if (start == null){
            return null;
        }
        GridPoint goal = furthestReach[qStart][qEnd][start.column];
        return new QueryResult(start, goal);
    }

    @Override
    public int singleFurthestQuery(int qStart, int qEnd, int startPointIndex) {
        GridPoint furthest = furthestReach[qStart][qEnd][startPointIndex];
        if (furthest!= null){
            return furthest.column;
        }
        return -1;
    }
}

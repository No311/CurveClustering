package DataStructures.FSGMethods;

import DataStructures.Reachability.Reachability;
import Objects.GridPoint;
import Objects.Trajectory;

public class FSGMethodQueryNaive extends FSGMethod {
    GridPoint[][][] furthestReach; //furthestReach[row1][row2][column] gives the farthest gridpoint on row 2
                                   //that the gridpoint at coordinates (row1, column) can reach.
    GridPoint[][][] queries; //queries[row1][row2][p] gives a gridpoint with the farthest distance between its
                             //furthestReach gridpoint and it in x-direction that fulfills the query.
    GridPoint[][] pointmatrix;



    @Override
    public void preprocess(GridPoint[][] pointmatrix, Reachability reach, Trajectory first, Trajectory second) {
        super.preprocess(pointmatrix, reach, first, second);
        computeRow1Reach(null, false);
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
    }

    @Override
    public QueryResult query(int qStart, int qEnd, int pColumn) {
        int actualsrow = pointmatrix.length-1-qStart;
        GridPoint bestStart = null;
        GridPoint bestGoal = null;
        int bestDistance = 0;
        for (int scol = 0; scol <= pColumn; scol++){
            GridPoint start = pointmatrix[actualsrow][scol];
            GridPoint goal = furthestReach[qStart][qEnd][scol];
            if (start != null && goal != null){
                if (goal.column >= pColumn){
                    int dist = goal.column - scol;
                    if (dist > bestDistance){
                        bestDistance = dist;
                        bestStart = start;
                        bestGoal = goal;
                    }
                }
            }
        }
        if (bestStart == null){
            return null;
        }
        return new QueryResult(bestStart, bestGoal);
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

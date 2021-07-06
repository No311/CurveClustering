package DataStructures.FSGMethods;

import DataStructures.Reachability.Reachability;
import Objects.GridPoint;
import Objects.Trajectory;

public class FSGMethodNoPrepNaive extends FSGMethod {

    @Override
    public void preprocess(GridPoint[][] pointmatrix, Reachability reach, Trajectory first, Trajectory second) {
        super.preprocess(pointmatrix, reach, first, second);
    }

    @Override
    public QueryResult query(int qStart, int qEnd, int pColumn) {
        int actualsrow = pointmatrix.length-1-qStart;
        int actualgrow = pointmatrix.length-1-qEnd;
        int bestDistance = 0;
        GridPoint bestStart = null;
        GridPoint bestGoal = null;
        for (int scol = 0; scol <= pColumn; scol++){
            for (int gcol = pColumn; gcol < pointmatrix[0].length; gcol++){
                GridPoint start = pointmatrix[actualsrow][scol];
                GridPoint goal = pointmatrix[actualgrow][gcol];
                if (reach.query(start, goal)){
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
        return -10;
    }
}

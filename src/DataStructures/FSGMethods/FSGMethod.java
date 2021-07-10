package DataStructures.FSGMethods;

import Objects.GridEdge;
import Objects.GridPoint;
import DataStructures.Reachability.*;
import Objects.Trajectory;

import java.util.Stack;

public abstract class FSGMethod {
    GridPoint[][] pointmatrix;
    Reachability reach;
    GridPoint[][][] Row1Reach;      //This structure (row1, row2, column) takes two rows and the column of a point on
                                    //the second row, and returns a point on the first row in column that can reach it,
                                    //if it exists.
    Trajectory first;
    Trajectory second;

    public void preprocess(GridPoint[][] pointmatrix, Reachability reach, Trajectory first, Trajectory second) {
        this.first = first;
        this.second = second;
        this.pointmatrix = pointmatrix;
        this.reach = reach;
        Row1Reach = new GridPoint[pointmatrix.length][pointmatrix.length][pointmatrix[0].length];
        //computing extreme can be reached from vertices (minCol, minRow) (O(n^2) time)
        for (int row = 0; row < pointmatrix.length; row++){
            int actualrow = pointmatrix.length-1-row;
            for (int column = 0; column < pointmatrix[0].length; column++){
                GridPoint current = pointmatrix[actualrow][column];
                if (current != null) {
                    for (GridEdge edge : current.incoming) {
                        if (edge != null) {
                            GridPoint minCol = edge.getOrigin().minCol;
                            if (minCol.column <= current.minCol.column) {
                                current.minCol = minCol;
                            }
                            GridPoint minRow = edge.getOrigin().minRow;
                            if (minRow.row <= current.minRow.row) {
                                current.minRow = minRow;
                            }
                        }
                    }
                    if (current.incoming[0] != null) {
                        current.minOnRow = current.incoming[0].getOrigin().minOnRow;
                    }
                }
            }
        }

        //computing extreme reachable vertices (maxCol, maxRow, furthestOnRow) (O(n^2) time)
        for (int row = pointmatrix.length-1; row >= 0; row--){
            int actualrow = pointmatrix.length-1-row;
            for (int column = pointmatrix[0].length-1; column >= 0; column--){
                GridPoint current = pointmatrix[actualrow][column];
                if (current != null) {
                    for (GridEdge edge : current.outgoing) {
                        if (edge != null) {
                            GridPoint maxCol = edge.getTarget().maxCol;
                            if (maxCol.column >= current.maxCol.column) {
                                current.maxCol = maxCol;
                            }
                            GridPoint maxRow = edge.getTarget().maxRow;
                            if (maxRow.row >= current.maxRow.row) {
                                current.maxRow = maxRow;
                            }
                        }
                    }
                    if (current.outgoing[0] != null) {
                        current.maxOnRow = current.outgoing[0].getTarget().maxOnRow;
                    }
                }
            }
        }
    }

    abstract public QueryResult query(int pColumn, int qStart, int qEnd);

    public abstract int singleFurthestQuery(int qStart, int qEnd, int startPointIndex);

    public GridPoint[][][] computeRow1Reach(GridPoint[][][] POI, boolean optimize){
        boolean doPOI = !(POI == null);
        for (int srow = 0; srow < pointmatrix.length; srow++){
            int actualsrow = pointmatrix.length - 1 - srow;
            for (int grow = pointmatrix.length-1; grow >= srow; grow--) {
                int actualgrow = pointmatrix.length - 1 - grow;
                int scol = pointmatrix[0].length - 1;
                int gcol = pointmatrix[0].length - 1;
                if (scol > -1 && gcol > -1) {
                    Stack<GridPoint> pointsOfInterest = new Stack<>();
                    while (gcol >= 0) {
                        GridPoint goal = pointmatrix[actualgrow][gcol];
                        while ((goal == null || goal.minRow.row > srow) && gcol > 0) {
                            gcol--;
                            goal = pointmatrix[actualgrow][gcol];
                        }
                        if (goal == null || goal.minRow.row > srow) {
                            break;
                        }
                        if (scol > gcol) {
                            scol = gcol;
                        }
                        GridPoint start = pointmatrix[actualsrow][scol];
                        while ((start == null || start.maxRow.row < grow) && scol > 0) {
                            scol--;
                            start = pointmatrix[actualsrow][scol];
                        }
                        if (start == null || start.maxRow.row < grow) {
                            break;
                        }
                        if (reach.query(start, goal)) {
                            Row1Reach[srow][grow][gcol] = start;
                            if (goal.maxOnRow == goal || !optimize) {
                                pointsOfInterest.push(goal);
                            }
                            gcol--;
                        } else {

                            scol--;
                        }
                    }
                    if (doPOI) {
                        GridPoint[] POIadd = new GridPoint[pointsOfInterest.size()];
                        int index = 0;
                        while (!pointsOfInterest.isEmpty()) {
                            POIadd[index] = pointsOfInterest.pop();
                            index++;
                        }
                        POI[srow][grow] = POIadd;
                    }
                }
            }
        }
        return POI;
    }

    public int queryRow1Reach(int qStart, int qEnd, int endindex){
        GridPoint closest = Row1Reach[qStart][qEnd][endindex];
        if (closest != null){
            return closest.column;
        }
        return -1;
    }

    public Trajectory getFirst() {
        return first;
    }

    public Trajectory getSecond() {
        return second;
    }
}


package DataStructures.TrajCover;

import DataStructures.Reachability.Reachability;
import Objects.GridEdge;
import Objects.GridPoint;

import java.util.ArrayList;
import java.util.Stack;

public class TrajCoverLog implements TrajCover {
    GridPoint[][] pointmatrix;
    Reachability reach;
    GridPoint[][][] Row1Reach;      //This structure (row1, row2, column) takes two rows and the column of a point on
                                    //the second row, and returns a point on the first row in column that can reach it,
                                    //if it exists.
    GridPoint[][][] POI1;           //This structure contains all gridpoints per row (row 1) that can reach another
                                    //specified row (row 2). Usage: POI1[srow][grow][scol]
    GridPoint[][][] POI2;           //This structure contains all gridpoints per row (row 2) that can be reached from
                                    //another specified row (row 1). Usage: POI2[srow][grow][gcol]


    @Override
    public void preprocess(GridPoint[][] pointmatrix, Reachability reach) {
        this.pointmatrix = pointmatrix;
        this.reach = reach;
        Row1Reach = new GridPoint[pointmatrix.length][pointmatrix.length][pointmatrix[0].length];
        POI1 = new GridPoint[pointmatrix.length][pointmatrix.length][pointmatrix[0].length];
        POI2 = new GridPoint[pointmatrix.length][pointmatrix.length][pointmatrix[0].length];

        //computing extreme can be reached from vertices (minCol, minRow) (O(n^2) time)
//        long starttime = System.currentTimeMillis();
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
//        long endtime = System.currentTimeMillis();
//        double time = ((double) endtime - (double) starttime)/1000;
//        System.out.println("Computing extreme can-be-reached-from vertices takes "+time+" s");

        //computing extreme reachable vertices (maxCol, maxRow, furthestOnRow) (O(n^2) time)
//        starttime = System.currentTimeMillis();
        for (int row = pointmatrix.length-1; row >= 0; row--){
            int actualrow = pointmatrix.length-1-row;
            for (int column = pointmatrix[0].length-1; column >= 0; column--){
                GridPoint current = pointmatrix[actualrow][column];
                if (current != null) {
                    for (GridEdge edge : current.outgoing) {
                        if (edge != null) {
                            GridPoint maxCol = edge.getTarget().maxCol;
                            if (maxCol.x >= current.maxCol.x) {
                                current.maxCol = maxCol;
                            }
                            GridPoint maxRow = edge.getTarget().maxRow;
                            if (maxRow.y >= current.maxRow.y) {
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
//        endtime = System.currentTimeMillis();
//        time = ((double) endtime - (double) starttime)/1000;
//        System.out.println("Computing extreme reachable vertices takes "+time+" s");

        //computing the closest x-coordinate on row 1 a vertex on row 2 can reach. furthestOnRow vertices that can reach
        //a row are determined vertices of interest for POI2. (O(n^3) time)
//        starttime = System.currentTimeMillis();
        for (int srow = 0; srow < pointmatrix.length; srow++){
            int actualsrow = pointmatrix.length - 1 - srow;
            for (int grow = pointmatrix.length-1; grow > srow; grow--) {
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
                            if (goal.maxOnRow == goal) {
                                pointsOfInterest.push(goal);
//                                System.out.println("Just pushed the next point to Of Interest:");
//                                goal.print();
                            }
                            gcol--;
                        } else {
                            scol--;
                        }
                    }
                    GridPoint[] POIadd = new GridPoint[pointsOfInterest.size()];
                    int index = 0;
                    while (!pointsOfInterest.isEmpty()){
                        POIadd[index] = pointsOfInterest.pop();
                        index++;
                    }
                    POI2[srow][grow] = POIadd;
                }
            }
        }
//        endtime = System.currentTimeMillis();
//        time = ((double) endtime - (double) starttime)/1000;
//        System.out.println("Computing Row1Reach and POI2 takes "+time+" s");

        //this bit computes all points of interest for POI1
//        starttime = System.currentTimeMillis();
        for (int srow = 0; srow < pointmatrix.length; srow++){
            int actualsrow = pointmatrix.length-1-srow;
            for (int grow = 0; grow < pointmatrix.length; grow++){
                int actualgrow = pointmatrix.length-1-grow;
                ArrayList<GridPoint> pointsOfInterest = new ArrayList<>();
                for (int scol = 0; scol < pointmatrix[0].length; scol++){
                    GridPoint start = pointmatrix[actualsrow][scol];
                    if (start != null) {
                        if (start.maxRow.row >= grow && (start.minOnRow == start)) {
                            pointsOfInterest.add(start);
                        }
                    }
                }
                POI1[srow][grow] = pointsOfInterest.toArray(new GridPoint[pointsOfInterest.size()]);
            }
        }
//        endtime = System.currentTimeMillis();
//        time = ((double) endtime - (double) starttime)/1000;
//        System.out.println("Computing POI1 takes "+time+" s");
    }

    @Override
    public QueryResult query(int qStart, int qEnd, int pColumn) {
        GridPoint[] pointsOfInterest1 = POI1[qStart][qEnd];
        GridPoint[] pointsOfInterest2 = POI2[qStart][qEnd];

//        System.out.println("The points in Points of Interest 1 are:");
//        for (GridPoint p: pointsOfInterest1){
//            p.print();
//        }
//        System.out.println("The points in Points of Interest 2 are:");
//        for (GridPoint p: pointsOfInterest2){
//            p.print();
//        }

        return recurseQuery(pointsOfInterest1, pointsOfInterest2, 0, pointsOfInterest1.length, pColumn);
    }

    //With 2 rows and a column p, search for a vertex on row 1 with x <= p that can reach a vertex on row 2
    //with y >= p. O(log(n)^2)
    public QueryResult recurseQuery(GridPoint[] pointsOfInterest1, GridPoint[] pointsOfInterest2,
                                    int left, int right, int pColumn){
        if (right == left){
            return null;
        }
        int currentIndex = left + (int) Math.floor((double) (right-left)/2);
        GridPoint currentQueryPoint = pointsOfInterest1[currentIndex];
        if (currentQueryPoint.column > pColumn){
            return recurseQuery(pointsOfInterest1, pointsOfInterest2, left, currentIndex, pColumn);
        }
        GridPoint furthestReachable = furthestQuery(currentQueryPoint, pointsOfInterest2,
                0, pointsOfInterest2.length);
        if (furthestReachable.column < pColumn){
            return recurseQuery(pointsOfInterest1, pointsOfInterest2, currentIndex+1, right, pColumn);
        } else {
            // this QueryPoint has a reachable vertex after column p
            return new QueryResult(currentQueryPoint, furthestReachable);
        }

    }

    //With a vertex on row 1, search for the furthest vertex on row 2 it can reach. (O(log(n))
    private GridPoint furthestQuery(GridPoint currentQueryPoint, GridPoint[] pointsOfInterest2,
                                    int left, int right) {
        if (right == left){
            return pointsOfInterest2[left-1];
        }
        int currentIndex = left + (int) Math.floor((double) (right-left)/2);
        GridPoint currentGoalVertex = pointsOfInterest2[currentIndex];
        if (reach.query(currentQueryPoint, currentGoalVertex)){
            return furthestQuery(currentQueryPoint, pointsOfInterest2, currentIndex+1, right);
        } else {
            GridPoint Row1Rival = Row1Reach[currentQueryPoint.row][currentGoalVertex.row][currentGoalVertex.column];
            if (Row1Rival.column < currentQueryPoint.column){
                return furthestQuery(currentQueryPoint, pointsOfInterest2, currentIndex+1, right);
            } else {
                return furthestQuery(currentQueryPoint, pointsOfInterest2, left, currentIndex);
            }
        }
    }

}

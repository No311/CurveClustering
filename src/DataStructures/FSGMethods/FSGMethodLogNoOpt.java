package DataStructures.FSGMethods;

import DataStructures.Reachability.Reachability;
import Objects.GridPoint;
import Objects.Trajectory;

import java.util.ArrayList;

public class FSGMethodLogNoOpt extends FSGMethod {
    GridPoint[][][] PTQ;           //This structure contains all gridpoints per row (row 1) that can reach another
                                    //specified row (row 2). Usage: PTQ[srow][grow][scol]
    GridPoint[][][] POI;           //This structure contains all gridpoints per row (row 2) that can be reached from
                                    //another specified row (row 1). Usage: POI[srow][grow][gcol]


    @Override
    public void preprocess(GridPoint[][] pointmatrix, Reachability reach, Trajectory first, Trajectory second) {
        super.preprocess(pointmatrix, reach, first, second);
        PTQ = new GridPoint[pointmatrix.length][pointmatrix.length][pointmatrix[0].length];
        POI = new GridPoint[pointmatrix.length][pointmatrix.length][pointmatrix[0].length];

        //computing the closest x-coordinate on row 1 a vertex on row 2 can reach. furthestOnRow vertices that can reach
        //a row are determined vertices of interest for POI. (O(n^3) time)
        computeRow1Reach(POI, false);

        //this bit computes all points of interest for PTQ
        for (int srow = 0; srow < pointmatrix.length; srow++){
            int actualsrow = pointmatrix.length-1-srow;
            for (int grow = 0; grow < pointmatrix.length; grow++){
                ArrayList<GridPoint> pointsToQuery = new ArrayList<>();
                for (int scol = 0; scol < pointmatrix[0].length; scol++){
                    GridPoint start = pointmatrix[actualsrow][scol];
                    if (start != null) {
                        if (start.maxRow.row >= grow) {
                            pointsToQuery.add(start);
                        }
                    }
                }
                PTQ[srow][grow] = pointsToQuery.toArray(new GridPoint[pointsToQuery.size()]);
            }
        }
    }

    @Override
    public QueryResult query(int qStart, int qEnd, int pColumn) {
        GridPoint[] pointsToQuery = PTQ[qStart][qEnd];
        GridPoint[] pointsOfInterest = POI[qStart][qEnd];

        if (pointsToQuery.length == 0 || pointsOfInterest.length == 0){
            return null;
        }
        return recurseQuery(pointsToQuery, pointsOfInterest, 0, pointsToQuery.length, pColumn);
    }

    //With 2 rows and a column p, search for a vertex on row 1 with x <= p that can reach a vertex on row 2
    //with y >= p. O(log(n)^2)
    public QueryResult recurseQuery(GridPoint[] pointsToQuery, GridPoint[] pointsOfInterest,
                                    int left, int right, int pColumn){
        if (right == left){
            return null;
        }
        int currentIndex = left + (int) Math.floor((double) (right-left)/2);
        GridPoint currentQueryPoint = pointsToQuery[currentIndex];
        if (currentQueryPoint.column > pColumn){
            return recurseQuery(pointsToQuery, pointsOfInterest, left, currentIndex, pColumn);
        }
        GridPoint furthestReachable = furthestQuery(currentQueryPoint, pointsOfInterest,
                0, pointsOfInterest.length);
        if (furthestReachable.column < pColumn){
            return recurseQuery(pointsToQuery, pointsOfInterest, currentIndex+1, right, pColumn);
        } else {
            // this QueryPoint has a reachable vertex after column p
            return new QueryResult(currentQueryPoint, furthestReachable);
        }
    }

    //With a vertex on row 1, search for the furthest vertex on row 2 it can reach. (O(log(n))
    private GridPoint furthestQuery(GridPoint currentQueryPoint, GridPoint[] pointsOfInterest,
                                    int left, int right) {
        if (right == left){
            return pointsOfInterest[Math.max(left-1, 0)];
        }
        int currentIndex = left + (int) Math.floor((double) (right-left)/2);
        GridPoint currentGoalVertex = pointsOfInterest[currentIndex];
        if (reach.query(currentQueryPoint, currentGoalVertex)){
            return furthestQuery(currentQueryPoint, pointsOfInterest, currentIndex+1, right);
        } else {
            GridPoint Row1Rival = Row1Reach[currentQueryPoint.row][currentGoalVertex.row][currentGoalVertex.column];
            if (Row1Rival.column < currentQueryPoint.column){
                return furthestQuery(currentQueryPoint, pointsOfInterest, currentIndex+1, right);
            } else {
                return furthestQuery(currentQueryPoint, pointsOfInterest, left, currentIndex);
            }
        }
    }

    @Override
    public int singleFurthestQuery(int qStart, int qEnd, int startPointIndex){
        GridPoint startPoint = pointmatrix[pointmatrix.length-1-qStart][startPointIndex];
        if (startPoint != null && startPoint.maxRow.row >= qEnd) {
            GridPoint[] pointsOfInterest = POI[qStart][qEnd];
            GridPoint furthest = furthestQuery(startPoint, pointsOfInterest, 0, pointsOfInterest.length);
            return furthest.column;
        }
        return -1;
    }


    private void printPointsOfInterest(int qStart, int qEnd, int pColumn, GridPoint[] pointsToQuery, GridPoint[] pointsOfInterest) {
        System.out.println("The Query has parameters Qs = " + qStart + ", Qe = " + qEnd + ", pC = " + pColumn + ".");
        System.out.println("The points in Points To Query are:");
        if(pointsToQuery != null) {
            for (GridPoint p : pointsToQuery) {
                p.print();
            }
        } else {System.out.println(pointsToQuery);}
        System.out.println("The points in Points Of Interest are:");
        if(pointsOfInterest != null) {
            for (GridPoint p : pointsOfInterest) {
                p.print();
            }
        } else {System.out.println(pointsOfInterest);}
    }
}

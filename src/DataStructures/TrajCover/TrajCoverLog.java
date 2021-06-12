package DataStructures.TrajCover;

import DataStructures.Reachability.Reachability;
import Objects.GridEdge;
import Objects.GridPoint;
import Objects.Trajectory;

import java.util.ArrayList;
import java.util.Stack;

public class TrajCoverLog extends TrajCover {
    GridPoint[][][] POI1;           //This structure contains all gridpoints per row (row 1) that can reach another
                                    //specified row (row 2). Usage: POI1[srow][grow][scol]
    GridPoint[][][] POI2;           //This structure contains all gridpoints per row (row 2) that can be reached from
                                    //another specified row (row 1). Usage: POI2[srow][grow][gcol]


    @Override
    public void preprocess(GridPoint[][] pointmatrix, Reachability reach, Trajectory first, Trajectory second) {
        super.preprocess(pointmatrix, reach, first, second);
        POI1 = new GridPoint[pointmatrix.length][pointmatrix.length][pointmatrix[0].length];
        POI2 = new GridPoint[pointmatrix.length][pointmatrix.length][pointmatrix[0].length];

        //computing the closest x-coordinate on row 1 a vertex on row 2 can reach. furthestOnRow vertices that can reach
        //a row are determined vertices of interest for POI2. (O(n^3) time)
        computeRow1Reach(POI2);

        //this bit computes all points of interest for POI1
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
    }

    @Override
    public QueryResult query(int qStart, int qEnd, int pColumn) {
        GridPoint[] pointsOfInterest1 = POI1[qStart][qEnd];
        GridPoint[] pointsOfInterest2 = POI2[qStart][qEnd];

        if (pointsOfInterest1.length == 0 || pointsOfInterest2.length == 0){
            return null;
        }
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

    @Override
    public int singleFurthestQuery(int qStart, int qEnd, int startPointIndex){
        GridPoint startPoint = pointmatrix[pointmatrix.length-1-qStart][startPointIndex];
        if (startPoint != null && startPoint.maxRow.row >= qEnd) {
            GridPoint[] pointsOfInterest2 = POI2[qStart][qEnd];
            GridPoint furthest = furthestQuery(startPoint, pointsOfInterest2, 0, pointsOfInterest2.length);
            return furthest.column;
        }
        return -1;
    }


    private void printPointsOfInterest(int qStart, int qEnd, int pColumn, GridPoint[] pointsOfInterest1, GridPoint[] pointsOfInterest2) {
        System.out.println("The Query has parameters Qs = " + qStart + ", Qe = " + qEnd + ", pC = " + pColumn + ".");
        System.out.println("The points in Points of Interest 1 are:");
        if(pointsOfInterest1 != null) {
            for (GridPoint p : pointsOfInterest1) {
                p.print();
            }
        } else {System.out.println(pointsOfInterest1);}
        System.out.println("The points in Points of Interest 2 are:");
        if(pointsOfInterest2 != null) {
            for (GridPoint p : pointsOfInterest2) {
                p.print();
            }
        } else {System.out.println(pointsOfInterest2);}
    }
}

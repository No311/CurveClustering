package Objects;

import Objects.GridEdge;
import Objects.GridPoint;
import Objects.Trajectory;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class DFDGrid {
    private final GridPoint[][] pointsMatrix;
    private ArrayList<GridPoint> pointList = new ArrayList<>();
    private ArrayList<GridEdge> edgesList = new ArrayList<>();
    private final Trajectory first;
    private final Trajectory second;
    private final int rowmax;

    public DFDGrid(Trajectory first, Trajectory second, int threshold, double standardDist, int size) {
        this.first = first;
        this.second = second;
        this.rowmax = first.getPoints().size()-1;
        GridPoint[][] matrix = new GridPoint[first.getPoints().size()][second.getPoints().size()];
        int row = 0; //points of first
        int column = 0; //points of second
        for (TrajPoint p: first.getPoints()){
            for (TrajPoint q: second.getPoints()){
                double dist = Point2D.distance(p.origx, p.origy, q.origx, q.origy);
                if (dist <= threshold){
                    int actualrow = rowmax - row; //note: actualrow is used for the pointmatrix, which sets the origin
                    //at the lower left corner.
                    GridPoint newpoint = new GridPoint(row, column, p, q, actualrow);
                    matrix[actualrow][column] = newpoint;
                    this.pointList.add(newpoint);
                    boolean diagred = false;
                    if (row > 0){
                        diagred = makeEdge(newpoint, matrix[actualrow+1][column], false, dist,
                                standardDist, size);
                    }
                    if (column > 0){
                        diagred = makeEdge(newpoint, matrix[actualrow][column-1], false, dist,
                                standardDist, size);
                    }
                    if (row > 0 && column > 0){
                        makeEdge(newpoint, matrix[actualrow+1][column-1], diagred, dist, standardDist, size);
                    }
                }
                column++;
            }
            row++;
            column = 0;
        }
        this.pointsMatrix = matrix;
    }

    private boolean makeEdge(GridPoint newpoint, GridPoint startpoint, boolean optional, double dist,
                             double standardDist, int size){
        if (startpoint != null){
            Arrow arrow = new Arrow(standardDist, size);
            GridEdge newHorEdge = new GridEdge(startpoint, newpoint, optional, dist, arrow);
            edgesList.add(newHorEdge);
            startpoint.addGridEdge(newHorEdge);
            newpoint.addGridEdge(newHorEdge);
            return true;
        }
        return false;
    }

    public GridPoint[][] getPointsMatrix() {
        return pointsMatrix;
    }

    public ArrayList<GridPoint> getPointList() {
        return pointList;
    }

    public ArrayList<GridEdge> getEdgesList() {
        return edgesList;
    }

    public Trajectory getFirst() {
        return first;
    }

    public Trajectory getSecond() {
        return second;
    }

    public int getRowmax() {
        return rowmax;
    }
}

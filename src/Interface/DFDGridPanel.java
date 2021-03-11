package Interface;

import Objects.GridEdge;
import Objects.GridPoint;
import Objects.TrajPoint;
import Objects.Trajectory;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class DFDGridPanel extends VisualPanel {
    private int threshold;
    private Trajectory first;
    private Trajectory second;
    private final GridPoint[][] pointmatrix;
    private int rowmax;
    private ArrayList<GridEdge> edges = new ArrayList<>();
    private ArrayList<GridPoint> pointlist = new ArrayList<>();
    private int gridmul = 10;
    private double startx;
    private double starty;


    public DFDGridPanel(int threshold, Trajectory first, Trajectory second) {
        super();
        super.setZoomable(true);
        super.setShowGrid(false);
        super.drawsize = 1;
        this.first = first;
        this.second = second;
        this.threshold = threshold;
        pointmatrix = new GridPoint[first.getPoints().size()][second.getPoints().size()];
        this.rowmax = first.getPoints().size()-1;
        initDFDGrid();
    }

    private void initDFDGrid() {
        int row = 0; //points of first
        int column = 0; //points of second
        for (TrajPoint p: first.getPoints()){
            for (TrajPoint q: second.getPoints()){
                double dist = Point2D.distance(p.origx, p.origy, q.origx, q.origy);
                if (dist <= threshold){
                    int actualrow = rowmax - row;
                    GridPoint newpoint = new GridPoint(row, column, p, q, actualrow);
                    pointmatrix[actualrow][column] = newpoint;
                    pointlist.add(newpoint);
                    boolean diagred = false;
                    if (row > 0){
                        GridPoint bottompoint = pointmatrix[actualrow+1][column];
                        if (bottompoint != null){
                            edges.add(new GridEdge(bottompoint, newpoint, false, dist));
                            diagred = true;
                        }
                    }
                    if (column > 0){
                        GridPoint leftpoint = pointmatrix[actualrow][column-1];
                        if (leftpoint != null){
                            edges.add(new GridEdge(leftpoint, newpoint, false, dist));
                            diagred = true;
                        }
                    }
                    if (row > 0 && column > 0){
                        GridPoint diagpoint = pointmatrix[actualrow+1][column-1];
                        if (diagpoint != null){
                            edges.add(new GridEdge(diagpoint, newpoint, diagred, dist));
                        }
                    }
                }
                column++;
            }
            row++;
            column = 0;
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (GridPoint p: pointlist){
            updateX(p);
            updateY(p);
        }
        //draw background
        g.setColor(backGroundColor);
        double fx = origin.x;
        double fy = gridmul*((((double)-rowmax)*step)/UtG) + origin.y;
        double width = gridmul*((((double) pointmatrix[0].length-1)*step)/UtG);
        double height = gridmul*((((double) rowmax)*step)/UtG);
        g.fillRect((int) fx, (int) fy, (int) width, (int) height);
        //draw the actual DFD Grid
        drawEdges(g);
        drawGrid(g);


    }

    private void drawEdges(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g.setColor(trajectoryColor);
        Stroke oldStroke = g2.getStroke();
        Stroke lineStroke = new BasicStroke((int) Math.ceil(size/4.0));
        g2.setStroke(lineStroke);
        for (GridEdge edge: edges){
            double origx = edge.getOrigin().x;
            double origy = edge.getOrigin().y;
            double tarx = edge.getTarget().x;
            double tary = edge.getTarget().y;
            drawArrow(g, g2, origx, origy, tarx, tary, getStandardDist());
        }
        g2.setStroke(oldStroke);
        g.setColor(Color.BLACK);
    }

    public void drawGrid(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g.setColor(pointsColor);
        for (int i = 0; i <= rowmax; i++){
            for (int j = 0; j < pointmatrix[0].length; j++){
                GridPoint p = pointmatrix[i][j];
                if (p != null) {
                    g.fillOval((int) p.x - (size / 2), (int) p.y - (size / 2), size, size);
                } else {
                    int vx = gridmul*((j*step)/UtG) + origin.x;
                    int vy = gridmul*(((i - rowmax)*step)/UtG) + origin.y;
                    g.setColor(voidPointColor);
                    g.fillOval(vx - (size / 2), vy - (size / 2), size, size);
                    g.setColor(pointsColor);
                }
            }
        }
        g.setColor(Color.BLACK);
    }

    private void updateX(GridPoint p){ p.x = gridmul*((((double) p.column)*step)/UtG) + origin.x; }

    private void updateY(GridPoint p){ p.y = gridmul*((((double) p.actualrow - rowmax)*step)/UtG) + origin.y; }

    private double getStandardDist(){
        return (gridmul*((double) (step)/UtG));
    }

    public int getGridmul() {
        return gridmul;
    }

    public void setGridmul(int gridmul) {
        this.gridmul = gridmul;
    }

    public void printMatrixBool(){
        for (GridPoint[] gridPoints : pointmatrix) {
            for (int j = 0; j < pointmatrix[0].length; j++) {
                if (gridPoints[j] != null) {
                    System.out.print("1, ");
                } else {
                    System.out.print(" , ");
                }
            }
            System.out.println();
        }
    }

}

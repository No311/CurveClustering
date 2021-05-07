package Interface;

import DataStructures.Reachability.Reachability;
import DataStructures.Reachability.ReachabilityNaive;
import Objects.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private Reachability reachdata;
    final Color beforePColor = new Color(113, 20, 219);
    final Color beforeEColor = new Color(164, 84, 255);
    final Color afterPColor = new Color(16, 107, 47);
    final Color afterEColor = new Color(80, 191, 118);
    final Color selectedColor = new Color(196, 177, 0);


    public DFDGridPanel(int threshold, Trajectory first, Trajectory second, int reach, int algo) {
        super();
        super.setZoomable(true);
        super.setShowGrid(false);
        super.drawsize = 1;
        this.first = first;
        this.second = second;
        this.threshold = threshold;
        pointmatrix = new GridPoint[first.getPoints().size()][second.getPoints().size()];
        this.rowmax = first.getPoints().size()-1;
        switch (reach) {
            case 0 -> reachdata = null;
            case 1 -> reachdata = new ReachabilityNaive();
        }
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
                        diagred = makeEdge(newpoint, pointmatrix[actualrow+1][column], false, dist);
                    }
                    if (column > 0){
                        diagred = makeEdge(newpoint, pointmatrix[actualrow][column-1], false, dist);
                    }
                    if (row > 0 && column > 0){
                        makeEdge(newpoint, pointmatrix[actualrow+1][column-1], diagred, dist);
                    }
                }
                column++;
            }
            row++;
            column = 0;
        }
        if (reachdata != null){
            reachdata.preprocess(pointmatrix);
        }
    }

    private boolean makeEdge(GridPoint newpoint, GridPoint startpoint, boolean optional, double dist){
        if (startpoint != null){
            Arrow arrow = new Arrow(getStandardDist(), size);
            GridEdge newHorEdge = new GridEdge(startpoint, newpoint, optional, dist, arrow);
            edges.add(newHorEdge);
            startpoint.addGridEdge(newHorEdge);
            newpoint.addGridEdge(newHorEdge);
            return true;
        }
        return false;
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
            if (edge.getOrigin().getSelected() == 0 || edge.getOrigin().getSelected() == 2){
                g.setColor(afterEColor);
            } else if (edge.getTarget().getSelected() == 0 || edge.getTarget().getSelected() == 1){
                g.setColor(beforeEColor);
            } else {
                g.setColor(trajectoryColor);
            }
            GridPoint o = edge.getOrigin();
            GridPoint t = edge.getTarget();
            edge.getRepresentation().updateCoordinates(o.x, o.y, t.x, t.y, getStandardDist(), size);
            edge.getRepresentation().drawArrow(g2);
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
                    switch (p.getSelected()) {
                        case 0 -> g.setColor(selectedColor);
                        case 1 -> g.setColor(beforePColor);
                        case 2 -> g.setColor(afterPColor);
                        default -> g.setColor(pointsColor);
                    }
                    p.paint(g2, size);
                } else {
                    int vx = gridmul*((j*step)/UtG) + origin.x;
                    int vy = gridmul*(((i - rowmax)*step)/UtG) + origin.y;
                    g.setColor(voidPointColor);
                    g.fillOval(vx - (size / 2), vy - (size / 2), size, size);
                }
                g.setColor(pointsColor);
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
        for (int i = 0; i < pointmatrix.length; i++) {
            System.out.print("Row "+i+":");
            for (int j = 0; j < pointmatrix[0].length; j++) {
                if (pointmatrix[i][j] != null) {
                    System.out.print("1, ");
                } else {
                    System.out.print(" , ");
                }
            }
            System.out.println();
        }
    }

    public void printEdgesInPoints(){
        for (GridPoint p: pointlist){
            p.printGridEdges();
        }
    }

    @Override
    public void addPress(VisualPanel map){
        map.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                map.setLastPress(e.getX(), e.getY());
                map.requestFocusInWindow();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                boolean found = false;
                GridPoint selected = null;
                for (GridPoint p: pointlist){
                    if (p.getDrawable().contains(e.getPoint()) && (!found)){
                        p.setSelected(true);
                        found = true;
                        selected = p;
                    }
                }
                if (found){
                    for (GridPoint p: pointlist){
                        if (p != selected){
                            p.reset();
                        }
                    }
                    for (GridPoint b: selected.getReachedFrom()){
                        b.setBefore(true);
                    }
                    for (GridPoint a: selected.getReachable()){
                        a.setAfter(true);
                    }
                }
                map.repaint();
            }
        });
    }


}

package Objects;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;

public class GridPoint extends Point {
    public int row;
    public int actualrow;
    public int column;
    public double x;
    public double y;
    public GridPoint minOnRow = this;
    public GridPoint maxOnRow = this;
    public GridPoint maxRow = this;
    public GridPoint minRow = this;
    public GridPoint maxCol = this;
    public GridPoint minCol = this;
    private int lastSize = 0;
    private Ellipse2D drawable;
    private boolean selected = false;
    private boolean before = false;
    private boolean after = false;
    private boolean target = false;
    private final TrajPoint first;
    private final TrajPoint second;
    public GridEdge[] incoming = new GridEdge[3]; // 0 = horizontal, 1 = vertical, 2 = diagonal
    public GridEdge[] outgoing = new GridEdge[3]; // 0 = horizontal, 1 = vertical, 2 = diagonal
    private ArrayList<GridPoint> reachable = new ArrayList<>();
    private ArrayList<GridPoint> reachedFrom = new ArrayList<>();


    public GridPoint(int row, int column, TrajPoint first, TrajPoint second, int actualrow){
        this.row = row;
        this.column = column;
        this.first = first;
        this.second = second;
        this.actualrow = actualrow;
        drawable = new Ellipse2D.Double(0, 0, 0, 0);
    }

    public void addGridEdge(GridEdge edge){
        if (edge.getOrigin() == this){
            double tRow = edge.getTarget().row;
            double tColumn = edge.getTarget().column;
            if ((tRow == row+1) && (tColumn == column+1)){
                outgoing[2] = edge;
            } else if ((tRow == row+1)){
                outgoing[1] = edge;
            } else if ((tColumn == column+1)){
                outgoing[0] = edge;
            }
        }
        if (edge.getTarget() == this){
            double oRow = edge.getOrigin().row;
            double oColumn = edge.getOrigin().column;
            if ((oRow == row-1) && (oColumn == column-1)){
                incoming[2] = edge;
            } else if ((oRow == row-1)){
                incoming[1] = edge;
            } else if ((oColumn == column-1)){
                incoming[0] = edge;
            }
        }
    }

    public void printGridEdges(){
        System.out.printf("Gridpoint (%d, %d) has incoming edges:\n", row, column);
        for (GridEdge g: incoming){
            if (g != null){
                g.printEdge();
            } else {
                System.out.print("(No edge)\n");
            }
        }
        System.out.print("and outgoing edges:\n");
        for (GridEdge g: outgoing){
            if (g != null){
                g.printEdge();
            } else {
                System.out.print("(No edge)\n");
            }
        }
        System.out.println();
    }

    public void print(){
        System.out.printf("Gridpoint denoting the combination of point %d of the first trajectory and point %d of " +
                        "the second trajectory\n",
                row, column);
    }

    public ArrayList<GridPoint> getReachable() {
        return reachable;
    }

    public void addReachable(GridPoint p) {
        reachable.add(p);
    }

    public ArrayList<GridPoint> getReachedFrom() {
        return reachedFrom;
    }

    public void addReachedFrom(GridPoint p) {
        reachedFrom.add(p);
    }

    public TrajPoint getFirst() {
        return first;
    }

    public TrajPoint getSecond() {
        return second;
    }

    public void paint(Graphics2D g2, int size){
        lastSize = size;
        drawable.setFrame((int) x - (size / 2), (int) y - (size / 2), size, size);
        g2.fill(drawable);
    }

    public Ellipse2D getDrawable(){
        return drawable;
    }

    public void setSelected(boolean s) {
        selected = s;
    }

    public void setBefore(boolean s) {
        before = s;
    }

    public void setAfter(boolean s) {
        after = s;
    }

    public void setTarget(boolean s) {
        target = s;
    }

    public void reset() {
        selected = false;
        before = false;
        after = false;
        target = false;
    }

    public boolean isTarget(){
        return target;
    }

    public int getSelected(){
        if (selected){return 0;}
        else if (before){return 1;}
        else if (after){return 2;}
        return -1;
    }
}

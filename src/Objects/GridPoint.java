package Objects;

import java.awt.*;

public class GridPoint extends Point {
    public int row;
    public int actualrow;
    public int column;
    public double x;
    public double y;
    public TrajPoint first;
    public TrajPoint second;

    public GridPoint(int row, int column, TrajPoint first, TrajPoint second, int actualrow){
        this.row = row;
        this.column = column;
        this.first = first;
        this.second = second;
        this.actualrow = actualrow;
    }

    public void print(){
        System.out.printf("Gridpoint denoting the combination of point %d of the first trajectory and point %d of " +
                        "the second trajectory",
                row, column);
    }
}

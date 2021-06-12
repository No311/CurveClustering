package Objects;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

public class TrajPoint extends Point implements Comparable<TrajPoint>{
    public final double time;
    public final double origx;
    public final double origy;
    public double x;
    public double y;
    public int index;
    private boolean selected;
    private boolean covered;
    private double clonex;
    private double cloney;
    private Ellipse2D drawable;


    public TrajPoint(double x, double y, double time){
        this.origx = x;
        this.origy = y;
        this.clonex = x;
        this.cloney = y;
        this.time = time;
        drawable = new Ellipse2D.Double(0,0,0,0);
    }

    public void setClone(){
        clonex = origx-50;
        cloney = origy-50;
    }

    public double drawOrigX(){
        return clonex;
    }

    public double drawOrigY(){
        return cloney;
    }

    public void print(){
        System.out.printf("Point with original coordinates (%f , %f), new coordinates (%f , %f) and time %f\n",
                origx, origy, x, y, time);
    }

    public String toString(){
        return origx + " " + origy + " " + time+"\n";
    }

    public void paint(Graphics2D g2, double size){
        drawable.setFrame((int) x - (size / 2), (int) y - (size / 2), size, size);
        g2.fill(drawable);
    }

    @Override
    public int compareTo(TrajPoint p) {
        return java.lang.Double.compare(time, p.time);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isCovered() {
        return covered;
    }

    public void setCovered(boolean covered) { this.covered = covered; }

    public TrajPoint clone(){
        return new TrajPoint(origx, origy, time);
    }

    public Ellipse2D getDrawable() {
        return drawable;
    }
}



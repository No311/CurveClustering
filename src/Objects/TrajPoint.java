package Objects;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class TrajPoint extends Point implements Comparable<TrajPoint> {
    public double time;
    public double origx;
    public double origy;
    public double x;
    public double y;
    private Ellipse2D drawable;


    public TrajPoint(double x, double y, double time){
        this.origx = x;
        this.origy = y;
        this.time = time;
        this.x = 0;
        this.y = 0;
        drawable = new Ellipse2D.Double(0,0,0,0);
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
}



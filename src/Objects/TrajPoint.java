package Objects;

import java.awt.*;

public class TrajPoint extends Point implements Comparable<TrajPoint> {
    public double time;
    public double origx;
    public double origy;
    public double x;
    public double y;


    public TrajPoint(double x, double y, double time){
        this.origx = x;
        this.origy = y;
        this.time = time;
        this.x = 0;
        this.y = 0;
    }

    public void print(){
        System.out.printf("Point with original coordinates (%f , %f), new coordinates (%f , %f) and time %f\n",
                origx, origy, x, y, time);
    }

    public String toString(){
        return String.valueOf(origx) + " " + String.valueOf(origy) + " " + String.valueOf(time)+"\n";
    }

    @Override
    public int compareTo(TrajPoint p) {
        return java.lang.Double.compare(time, p.time);
    }
}



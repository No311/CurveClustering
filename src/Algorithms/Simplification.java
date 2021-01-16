package Algorithms;

import Objects.TrajPoint;
import Objects.Trajectory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Simplification {

    public Trajectory SimplifyTrajectory(Trajectory t, double mu){
        Trajectory result = new Trajectory(t.getName()+"s"+String.valueOf((int) mu));
        ArrayList<TrajPoint> points = new ArrayList<>();
        TrajPoint lastPoint = t.getPoints().get(0);
        for (int i = 0; i<t.getPoints().size(); i++){
            if (i == 0){
                points.add(t.getPoints().get(i));
            }
            else {
                lastPoint = comparePoints(t.getPoints().get(i), lastPoint, mu, points);
            }
        }
        int lastIndex = t.getPoints().size()-1;
        lastPoint = t.getPoints().get(lastIndex);
        for (int i = lastIndex; i>= 0; i--){
            if (i == lastIndex){
                points.add(t.getPoints().get(i));
            }
            else {
                lastPoint = comparePoints(t.getPoints().get(i), lastPoint, mu, points);

            }
        }
        Collections.sort(points);
        for (TrajPoint p : points){
            result.addPoint(p);
        }
        return result;
    }

    public TrajPoint comparePoints(TrajPoint current, TrajPoint lastPoint, double mu, ArrayList<TrajPoint> points){
        double Xdist = Math.abs(current.origx - lastPoint.origx);
        double Ydist = Math.abs(current.origy - lastPoint.origy);
        double dist = Math.sqrt(Math.pow(Xdist, 2) + Math.pow(Ydist, 2));
        if (dist >= mu){
            points.add(current);
            lastPoint = current;
        }
        return lastPoint;
    }
}

package Objects;


import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class Trajectory {
    private ArrayList<TrajPoint> points = new ArrayList<>();
    private File source;
    private boolean selected;
    private boolean editable;
    private boolean haspoints;
    private String name;
    private double lastTime = 0;

    public Trajectory(File source){
        this.source = source;
        String fname = source.getName();
        int pos = fname.lastIndexOf(".");
        if (pos > 0) {
            fname = fname.substring(0, pos);
        }
        this.name = fname;
    }

    public Trajectory(String name) { this.name = name; this.haspoints = false;}

    public void addPoint(TrajPoint point){
        point.index = points.size();
        points.add(point);
        lastTime = points.get(points.size()-1).time;
        haspoints = true;
    }

    public void print() {
        for (TrajPoint p : points){
            System.out.println("x: "+p.origx+", y: "+p.origy+", origtime: "+p.time);
        }
        System.out.println();
    }

    public ArrayList<TrajPoint> getPoints(){
        return points;
    }

    public File getSource() {
        return source;
    }

    public void setSource(File source) {
        this.source = source;
    }

    public void setSelected(boolean b) {
        this.selected = b;
    }

    public void setEditable(boolean b) {
        this.editable = b;
    }

    public boolean getSelected() {
        return selected;
    }

    public boolean getEditable() {
        return editable;
    }

    public String getName() {
        return name;
    }

    public boolean hasPoints() {
        return haspoints;
    }

    public double getLastTime() {
        return lastTime;
    }

    public void removeLast() {
        if (points.size() == 1){
            points.remove(0);
            haspoints = false;
        }
        if (haspoints) {
            points.remove(points.size()-1);
            lastTime = points.get(points.size()-1).time;
        }
    }

    public Trajectory clone(){
        Trajectory result = new Trajectory(name);
        for (TrajPoint p: points){
            result.addPoint(p.clone());
        }
        return result;
    }
}

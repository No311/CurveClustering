package Objects;


import java.io.File;
import java.util.ArrayList;

public class Trajectory {
    private ArrayList<TrajPoint> points = new ArrayList<>();
    private File source;
    private Trajectory Simplified = null;
    private boolean selected;
    private String name;

    public Trajectory(File source){
        this.source = source;
        String fname = source.getName();
        int pos = fname.lastIndexOf(".");
        if (pos > 0) {
            fname = fname.substring(0, pos);
        }
        this.name = fname;
    }

    public Trajectory(String name) { this.name = name; }

    public void addPoint(TrajPoint point){
        points.add(point);
    }

    public void insertPoint(TrajPoint point){
        //to be done.
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
    public boolean getSelected() {
        return selected;
    }

    public String getName() {
        return name;
    }
}

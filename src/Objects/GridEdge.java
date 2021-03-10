package Objects;

public class GridEdge {
    private GridPoint origin;
    private GridPoint target;
    private boolean optional;
    private double distance;

    public GridEdge(GridPoint origin, GridPoint target, boolean optional, double distance){
        this.origin = origin;
        this.target = target;
        this.optional = optional;
        this.distance = distance;
    }

    public GridPoint getOrigin() {
        return origin;
    }

    public GridPoint getTarget() {
        return target;
    }
}

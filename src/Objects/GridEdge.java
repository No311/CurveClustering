package Objects;

public class GridEdge {
    private GridPoint origin;
    private GridPoint target;
    private Arrow representation;
    private boolean optional;
    private double distance;

    public GridEdge(GridPoint origin, GridPoint target, boolean optional, double distance, Arrow arrow){
        this.origin = origin;
        this.target = target;
        this.optional = optional;
        this.distance = distance;
        this.representation = arrow;
    }

    public GridPoint getOrigin() {
        return origin;
    }

    public GridPoint getTarget() {
        return target;
    }

    public void printEdge() {
        System.out.printf("An edge from point (%d, %d) to point (%d, %d)\n", origin.row, origin.column, target.row,
                target.column);
    }
    public Arrow getRepresentation(){
        return representation;
    }

}

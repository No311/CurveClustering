package DataStructures.TrajCover;

import Objects.GridPoint;

public class QueryResult {
    public GridPoint start;
    public GridPoint goal;

    public QueryResult(GridPoint start, GridPoint goal) {
        this.start = start;
        this.goal = goal;
    }
}

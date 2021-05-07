package DataStructures.Reachability;

import Objects.GridPoint;

public interface Reachability {

    void preprocess(GridPoint[][] pointmatrix);

    boolean query(GridPoint start, GridPoint goal);

}

package DataStructures.Reachability;

import Objects.GridPoint;

public abstract class Reachability {

    abstract public void preprocess(GridPoint[][] pointmatrix);

    abstract public boolean query(GridPoint start, GridPoint goal);

}

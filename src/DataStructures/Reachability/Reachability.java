package DataStructures.Reachability;

import Objects.GridPoint;

public abstract class Reachability {
    GridPoint[][] pointmatrix;
    boolean[][][][] reachmatrix; //can the first two coordinates reach the second two coordinates?
    boolean[][][][] reachmatrixswap; //can the first two coordinates reach the second two coordinates?
    //with trajectory 2 as first trajectory and trajectory 1 as second trajectory

    abstract public void preprocess(GridPoint[][] pointmatrix);

    abstract public boolean query(GridPoint start, GridPoint goal);

    public void set(GridPoint[][] pointmatrix, boolean[][][][] reachmatrix, boolean[][][][] reachmatrixswap){
        this.pointmatrix = pointmatrix;
        this.reachmatrix = reachmatrix;
        this.reachmatrixswap = reachmatrixswap;

    }

    public boolean[][][][] getReachMatrix(){
        return reachmatrixswap;
    }

    public boolean[][][][] getSwapped(){
        return reachmatrixswap;
    }
}

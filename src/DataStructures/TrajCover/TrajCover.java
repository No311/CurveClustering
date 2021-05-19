package DataStructures.TrajCover;

import Objects.GridPoint;
import DataStructures.Reachability.*;

public interface TrajCover {

    void preprocess(GridPoint[][] pointmatrix, Reachability reach);

    QueryResult query(int pColumn, int qStart, int qEnd);

}


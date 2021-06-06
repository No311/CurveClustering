package DataStructures.SetSystemQuerier;

import DataStructures.Reachability.Reachability;
import DataStructures.TrajCover.TrajCover;
import Objects.GridPoint;
import Objects.TrajPoint;
import Objects.Trajectory;

import java.util.ArrayList;

public abstract class SetSystemQuerier {
    Reachability reach;
    TrajCover algo;

    public SetSystemQuerier(Reachability reach, TrajCover algo){
        this.reach = reach;
        this.algo = algo;
    }

    //returns all points in the second trajectory covered by all subtrajectories of the first trajectory.
    public abstract SetSystemOracle queryAll(Trajectory first, Trajectory second);

    //returns all points in the second trajectory covered by the subtrajectory of the first trajectory indicated by
    //the points start and end.
    public abstract OracleResult queryOne(TrajPoint start, TrajPoint end, Trajectory second, String firstName);
}

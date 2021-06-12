package DataStructures.SetSystemQuerier;

import DataStructures.Reachability.Reachability;
import DataStructures.TrajCover.TrajCover;
import Objects.GridPoint;
import Objects.TrajPoint;
import Objects.Trajectory;

import java.util.ArrayList;

public abstract class SetSystemQuerier {
    Reachability[] reach;
    TrajCover[] algo;

    public SetSystemQuerier(Reachability[] reach, TrajCover[] algo){
        this.reach = reach;
        this.algo = algo;
    }

    //returns all points in the selection covered by all subtrajectories of the first trajectory.
    public abstract SetSystemOracle queryAll(Trajectory first, ArrayList<Trajectory> selection);

    //returns all points in the selection covered by the subtrajectory of the first trajectory indicated by
    //the points start and end.
    public abstract OracleResult queryOne(TrajPoint start, TrajPoint end, Trajectory first,
                                          ArrayList<Trajectory> selection, String firstName);
}

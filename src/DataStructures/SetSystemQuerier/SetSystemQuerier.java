package DataStructures.SetSystemQuerier;

import DataStructures.Reachability.Reachability;
import DataStructures.FSGMethods.FSGMethod;
import Objects.TrajPoint;
import Objects.Trajectory;

public abstract class SetSystemQuerier {
    Reachability reach;
    FSGMethod algo;

    public SetSystemQuerier(Reachability reach, FSGMethod algo){
        this.reach = reach;
        this.algo = algo;
    }

    //returns all points in the selection covered by all subtrajectories of the first trajectory.
    public abstract boolean[][][] queryAll(Trajectory first, Trajectory second);

    //returns all points in the selection covered by the subtrajectory of the first trajectory indicated by
    //the points start and end.
    public abstract boolean[] queryOne(TrajPoint start, TrajPoint end, Trajectory first,
                                          Trajectory second, String firstName);
}

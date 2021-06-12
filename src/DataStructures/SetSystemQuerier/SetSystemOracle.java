package DataStructures.SetSystemQuerier;

import Objects.TrajPoint;
import Objects.Trajectory;

import java.util.ArrayList;

public class SetSystemOracle {
    private OracleResult[][] oracle;
    private Trajectory first;
    private ArrayList<Trajectory> selection;

    public SetSystemOracle(OracleResult[][] oracle, Trajectory first,
                           ArrayList<Trajectory> selection){
        this.oracle = oracle;
        this.first = first;
        this.selection = selection;
    }
    public OracleResult getCoveredBySub(int start, int end){
        return oracle[start][end];
    }
    public Trajectory getFirst() {
        return first;
    }

    public ArrayList<Trajectory> getSelection() {
        return selection;
    }
}

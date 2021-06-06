package DataStructures.SetSystemQuerier;

import Objects.TrajPoint;
import Objects.Trajectory;

public class SetSystemOracle {
    private OracleResult[][] oracle;
    private boolean[] totalCoveredByFirst;
    private Trajectory first;
    private Trajectory second;

    public SetSystemOracle(OracleResult[][] oracle, boolean[] totalCoveredByFirst, Trajectory first, Trajectory second){
        this.oracle = oracle;
        this.totalCoveredByFirst = totalCoveredByFirst;
        this.first = first;
        this.second = second;
    }

    public boolean[] getTotalCoveredByFirst(){
        return totalCoveredByFirst;
    }
    public OracleResult getCoveredBySub(int start, int end){
        return oracle[start][end];
    }
    public boolean queryOracle(int start, int end, int p){
        return oracle[start][end].getCovered()[p];
    }
    public Trajectory getFirst() {
        return first;
    }

    public Trajectory getSecond() {
        return second;
    }
}

package DataStructures.SetSystemQuerier;

import DataStructures.Reachability.Reachability;
import DataStructures.TrajCover.QueryResult;
import DataStructures.TrajCover.TrajCover;
import Objects.GridPoint;
import Objects.TrajPoint;
import Objects.Trajectory;

import java.util.ArrayList;
import java.util.HashSet;

public class EasyQuerier extends SetSystemQuerier{

    public EasyQuerier(Reachability reach, TrajCover algo) {
        super(reach, algo);
    }

    @Override
    public SetSystemOracle queryAll(Trajectory first, Trajectory second){
        OracleResult[][] oracle =
                new OracleResult[first.getPoints().size()][first.getPoints().size()];
        boolean[] totalcovered = new boolean[second.getPoints().size()];
        for (int i = 0; i < first.getPoints().size(); i++){
            TrajPoint start = first.getPoints().get(i);
            for (int j = i; j < first.getPoints().size(); j++){
                TrajPoint end = first.getPoints().get(j);
                OracleResult result = queryOne(start, end, second, first.getName());

                oracle[start.index][end.index] = result;
                for (int index = 0; index < second.getPoints().size(); index++){
                    if (!(totalcovered[index]) && result.getCovered()[index]){
                        totalcovered[index] = true;
                    }
                }
            }
        }
        return new SetSystemOracle(oracle, totalcovered, first, second);
    }

    @Override
    public OracleResult queryOne(TrajPoint start, TrajPoint end, Trajectory second, String firstName){
        int amountCovered = 0;
        boolean[] covered = new boolean[second.getPoints().size()];
        for (TrajPoint p: second.getPoints()){
            QueryResult result = algo.query(start.index, end.index, p.index);
            if (result != null){
                covered[p.index] = true;
                amountCovered++;
            } else {
                covered[p.index] = false;
            }
        }
        return new OracleResult(covered, amountCovered, start, end);
    }
}

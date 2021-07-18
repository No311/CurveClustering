package DataStructures.Querier;

import DataStructures.Reachability.Reachability;
import DataStructures.FSGMethods.QueryResult;
import DataStructures.FSGMethods.FSGMethod;
import Objects.TrajPoint;
import Objects.Trajectory;

public class EasyQuerier extends Querier {

    public EasyQuerier(Reachability reach, FSGMethod algo) {
        super(reach, algo);
    }

    @Override
    public boolean[][][] queryAll(Trajectory first, Trajectory second){
        boolean[][][] singleSecondOracle = new boolean[first.getPoints().size()][first.getPoints().size()]
                [second.getPoints().size()];
        for (int i = 0; i < first.getPoints().size(); i++){
            TrajPoint start = first.getPoints().get(i);
            for (int j = i; j < first.getPoints().size(); j++){
                TrajPoint end = first.getPoints().get(j);
                boolean[] result = queryOne(start, end, first, second, first.getName());
                singleSecondOracle[start.index][end.index] = result;
            }
        }
        return singleSecondOracle;
    }

    @Override
    public boolean[] queryOne(TrajPoint start, TrajPoint end, Trajectory first, Trajectory second, String firstName){
        int amountCovered = 0;
        boolean[] covered = new boolean[second.getPoints().size()];
        for (TrajPoint p : second.getPoints()) {
            QueryResult result = algo.query(start.index, end.index, p.index);
            if (result != null) {
                covered[p.index] = true;
                amountCovered++;
            } else {
                covered[p.index] = false;
            }
        }
        return covered;
    }
}

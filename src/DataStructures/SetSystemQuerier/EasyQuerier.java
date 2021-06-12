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

    public EasyQuerier(Reachability[] reach, TrajCover[] algo) {
        super(reach, algo);
    }

    @Override
    public SetSystemOracle queryAll(Trajectory first, ArrayList<Trajectory> selection){
        OracleResult[][] oracle =
                new OracleResult[first.getPoints().size()][first.getPoints().size()];
        for (int i = 0; i < first.getPoints().size(); i++){
            TrajPoint start = first.getPoints().get(i);
            for (int j = i; j < first.getPoints().size(); j++){
                TrajPoint end = first.getPoints().get(j);
                OracleResult result = queryOne(start, end, first, selection, first.getName());

                oracle[start.index][end.index] = result;
            }
        }
        return new SetSystemOracle(oracle, first, selection);
    }

    @Override
    public OracleResult queryOne(TrajPoint start, TrajPoint end, Trajectory first, ArrayList<Trajectory> selection, String firstName){
        int amountCovered = 0;
        boolean[][] covered = new boolean[selection.size()][];
        for (Trajectory s: selection){
            covered[s.index] = new boolean[s.getPoints().size()];
        }
        for (Trajectory t: selection) {
            for (TrajPoint p : t.getPoints()) {
                System.out.println("Trajectory Query:"+algo[first.index].getFirst().getName());
                System.out.println("Trajectory two:"+t.getName());
                System.out.println("Start:" + start.index +" End:" + end.index);
                System.out.println("p index: " +p.index);
                QueryResult result = algo[t.index].query(start.index, end.index, p.index);
                if (result != null) {
                    System.out.println("This query was true");
                    covered[t.index][p.index] = true;
                    amountCovered++;
                } else {
                    System.out.println("This query was false");
                    covered[t.index][p.index] = false;
                }
            }
        }
        return new OracleResult(covered, amountCovered, start, end, first, selection);
    }
}

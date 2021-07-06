package DataStructures.SetSystemQuerier;

import Objects.TrajPoint;
import Objects.Trajectory;

import java.util.ArrayList;

public class SetSystemOracle {
    private OracleResult[][] oracle;
    private Trajectory first;
    private ArrayList<Trajectory> selection;

    public SetSystemOracle(boolean[][][][] covered, Trajectory first,
                           ArrayList<Trajectory> selection){
        oracle = new OracleResult[first.getPoints().size()][first.getPoints().size()];
        for (int i = 0; i < first.getPoints().size(); i++){
            for (int j = i; j < first.getPoints().size(); j++){
                boolean[][] covers = new boolean[selection.size()][];
                int amountCovered = 0;
                for (Trajectory s: selection){
                    boolean[] c = covered[s.index][i][j];
                    covers[s.index] = c;
                    for (boolean t: c){
                        if (t){
                            amountCovered++;
                        }
                    }
                }
                oracle[i][j] = new OracleResult(covers, amountCovered, first.getPoints().get(i),
                        first.getPoints().get(j), first, selection);
            }
        }
        this.first = first;
        this.selection = selection;
    }

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

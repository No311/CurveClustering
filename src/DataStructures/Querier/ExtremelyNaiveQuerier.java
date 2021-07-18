package DataStructures.Querier;

import Objects.Trajectory;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class ExtremelyNaiveQuerier {

    public ArrayList<SetSystemOracle> queryAll(ArrayList<Trajectory> selection, int threshold) {
        ArrayList<SetSystemOracle> result = new ArrayList<>();
        for (int i = 0; i < selection.size(); i++){
            selection.get(i).index = i;
        }
        //make the preparatory very naive data structure.
        boolean[][][][][][] naivePrep = makeNaivePrep(selection, threshold);
        for (Trajectory first: selection){
            OracleResult[][] oracleResults = new OracleResult[first.getPoints().size()][first.getPoints().size()];
            for (int i = 0; i < first.getPoints().size(); i++){
                for (int j = i; j < first.getPoints().size(); j++){
                    boolean[][] covered = new boolean[selection.size()][];
                    int amountCovered = 0;
                    for (Trajectory second: selection){
                        boolean[] pCovered = new boolean[second.getPoints().size()];
                        for (int p = 0; p < second.getPoints().size(); p++){
                            for (int s1 = 0; s1 <= p; s1++){
                                boolean breakValue = false;
                                for (int s2 = p; s2 < second.getPoints().size(); s2++){
                                    if (naivePrep[first.index][second.index][i][j][s1][s2]){
                                        pCovered[p] = true;
                                        breakValue = true;
                                        amountCovered++;
                                        break;
                                    }
                                }
                                if (breakValue){
                                    break;
                                }
                            }
                        }
                        covered[second.index] = pCovered;
                    }
                    oracleResults[i][j] = new OracleResult(covered, amountCovered, first.getPoints().get(i),
                            first.getPoints().get(j), first, selection);
                }
            }
            result.add(new SetSystemOracle(oracleResults, first, selection));
        }
        return result;
    }

    public boolean[][][][][][] makeNaivePrep(ArrayList<Trajectory> selection, int threshold) {
        boolean[][][][][][] result = new boolean[selection.size()][selection.size()][][][][];
        for (Trajectory first: selection){
            for (Trajectory second: selection) {
                boolean[][][][] intermediate = new boolean[first.getPoints().size()][first.getPoints().size()]
                        [second.getPoints().size()][second.getPoints().size()];
                //do the computations for points to lines both ways.
                for (int start = 0; start < first.getPoints().size(); start++) {
                    for (int secStart = 0; secStart < second.getPoints().size(); secStart++) {
                        for (int secEnd = secStart; secEnd < second.getPoints().size(); secEnd++) {
                            int lastPoint = secEnd-1;
                            boolean pointWithinThreshold =
                                    (Point2D.distance(first.getPoints().get(start).origx,
                                            first.getPoints().get(start).origy,
                                            second.getPoints().get(secEnd).origx,
                                            second.getPoints().get(secEnd).origy) <= threshold);
                            boolean lastPointWithinThreshold = (
                                    (lastPoint < secStart)
                                            || (intermediate[start][start][secStart][lastPoint])
                                    );
                            intermediate[start][start][secStart][secEnd] =
                                    (pointWithinThreshold && lastPointWithinThreshold);
                        }
                    }
                }
                for (int secStart = 0; secStart < second.getPoints().size(); secStart++) {
                    for (int start = 0; start < first.getPoints().size(); start++) {
                        for (int end = start; end < first.getPoints().size(); end++) {
                            int lastPoint = end-1;
                            boolean pointWithinThreshold =
                                    (Point2D.distance(first.getPoints().get(end).origx,
                                            first.getPoints().get(end).origy,
                                            second.getPoints().get(secStart).origx,
                                            second.getPoints().get(secStart).origy) <= threshold);
                            boolean lastPointWithinThreshold = (
                                    (lastPoint < start) || (intermediate[start][lastPoint][secStart][secStart])
                            );
                            intermediate[start][end][secStart][secStart] =
                                    (pointWithinThreshold && lastPointWithinThreshold);
                        }
                    }
                }
                //do the rest of the computations
                for (int size = 1; size < first.getPoints().size(); size++){
                    for (int start = 0; start < first.getPoints().size(); start++){
                        int end = Math.min(start + size, first.getPoints().size()-1);
                        if (end < start + size){
                            break;
                        }
                        for (int secSize = 1; secSize < second.getPoints().size(); secSize++){
                            for (int secStart = 0; secStart< second.getPoints().size(); secStart++){
                                int secEnd = Math.min(secStart + secSize, second.getPoints().size()-1);
                                if (secEnd < secStart + secSize){
                                    break;
                                }
                                for (int i = start; i < end; i++){
                                    boolean breakValue = false;
                                    for (int j = secStart; j < secEnd; j++){
                                        if (intermediate[start][i][secStart][j] && intermediate[i+1][end][j+1][secEnd]){
                                            intermediate[start][end][secStart][secEnd] = true;
                                            breakValue = true;
                                            break;
                                        }
                                    }
                                    if (breakValue){
                                        break;
                                    }
                                }

                            }
                        }
                    }
                }
                result[first.index][second.index] = intermediate;
            }
        }
        return result;
    }
}

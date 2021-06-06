package DataStructures.SetSystemQuerier;

import DataStructures.Reachability.Reachability;
import DataStructures.TrajCover.QueryResult;
import DataStructures.TrajCover.TrajCover;
import Objects.TrajPoint;
import Objects.Trajectory;

public class LongJumpQuerier extends EasyQuerier{
    public LongJumpQuerier(Reachability reach, TrajCover algo) {
        super(reach, algo);
    }

    @Override
    public OracleResult queryOne(TrajPoint start, TrajPoint end, Trajectory second, String firstName){
        int amountCovered = 0;
        boolean[] covered = new boolean[second.getPoints().size()];
        int i = 0;
        while (i < second.getPoints().size()){
            int maxIndexCovered = algo.singleFurthestQuery(start.index, end.index, i);
            if (maxIndexCovered == -1){
                i++;
            } else {
                for (int j = i; j <= maxIndexCovered; j++){
                    if (!covered[j]) {
                        covered[j] = true;
                        amountCovered++;
                    }
                }
                int next = algo.queryRow1Reach(start.index, end.index, maxIndexCovered);
                if (next < i){
                    String exception = "A Jump Exception has occurred. While trying to cover points from Trajectory "+second.getName()+
                            " with the subtrajectory starting at point "+start.index+" and ending at point "+end.index+" of " +
                            "Trajectory "+firstName+", gridpoint (row: "+start.index+", column: "+i+") can reach gridpoint " +
                            "(row: "+end.index+", column: "+maxIndexCovered+"), which then has gridpoint ("+start.index+", " +
                            "column: "+next+") as column-closest gridpoint to be reached on row "+start.index+".";
                    System.out.println(exception);
                } else if (i == next){
                    i++;
                } else {
                    i = next;
                }
            }
        }
        return new OracleResult(covered, amountCovered, start, end);
    }
}

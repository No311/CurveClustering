package DataStructures.Reachability;

import Objects.GridEdge;
import Objects.GridPoint;
import Objects.Trajectory;

public class ReachabilityNaive extends Reachability {
private Trajectory first;
private Trajectory second;

    @Override
    public void preprocess(GridPoint[][] pointmatrix, Trajectory first, Trajectory second, boolean visual) {
        this.pointmatrix = pointmatrix;
        this.first = first;
        this.second = second;
        newPrep(visual);
    }

    private void newPrep(boolean visual){
        reachmatrix = newPrep(pointmatrix.length, pointmatrix[0].length, false, visual);
    }

    private boolean[][][][] newPrep(int rowlength, int collength, boolean swap, boolean visual){
        boolean[][][][] result = new boolean[rowlength][collength][rowlength][collength];
        for (int orow = 0; orow < rowlength; orow++){
            boolean[][][] orresult = new boolean[collength][rowlength][collength];
            for (int ocol = 0; ocol < collength; ocol++){
                boolean[][] ocresult = new boolean[rowlength][collength];
                for (int trow = 0; trow < rowlength; trow++){
                    boolean[] trresult = new boolean[collength];
                    for (int tcol = 0; tcol < collength; tcol++){
                        boolean tcresult;
                        GridPoint p1 = pointMatrixQuery(orow, ocol, swap);
                        GridPoint p2 = pointMatrixQuery(trow, tcol, swap);
                        if (p1 == null || p2 == null){
                            tcresult = false;
                        } else if (p1.row == p2.row && p1.column == p2.column){
                            tcresult = true;
                        } else {
                            tcresult = false;
                            boolean leftmost = (tcol == 0);
                            boolean bottommost = (trow == 0);
                            if (!leftmost && trresult[tcol-1]){
                                tcresult = true;
                            }
                            if (!bottommost && ocresult[trow-1][tcol]){
                                tcresult = true;
                            }
                            if (!bottommost && !leftmost && ocresult[trow-1][tcol-1]){
                                tcresult = true;
                            }
                            if (tcresult && visual){
                                p1.addReachable(p2);
                                p2.addReachedFrom(p1);
                            }
                        }
                        trresult[tcol] = tcresult;
                    }
                    ocresult[trow] = trresult;
                }
                orresult[ocol] = ocresult;
            }
            result[orow] = orresult;
        }
        return result;
    }

    private GridPoint pointMatrixQuery(int row, int column, boolean swap){
        int rowmax = pointmatrix.length -1;
        if (swap){
            return pointmatrix[rowmax - column][row];
        }
        return pointmatrix[rowmax - row][column];
    }

    @Override
    public boolean query(GridPoint start, GridPoint goal) {
        if (start == null || goal == null) {
            return false;
        }
        return reachmatrix[start.row][start.column][goal.row][goal.column];
    }

    public void printMatrixBool(boolean[][][][] reachmatrix){
        System.out.print("       ");
        for (int trow = 0; trow < reachmatrix[0][0].length; trow++) {
            for (int tcol = 0; tcol < reachmatrix[0][0][0].length; tcol++)
                System.out.printf(" (%d, %d): |", trow, tcol);
        }
        System.out.println();
        for (int orow = 0; orow < reachmatrix.length; orow++) {
            for (int ocol = 0; ocol < reachmatrix[0].length; ocol++){
                System.out.printf("(%d, %d):", orow, ocol);
                for (int trow = 0; trow < reachmatrix[0][0].length; trow++) {
                    for (int tcol = 0; tcol < reachmatrix[0][0][0].length; tcol++)
                    if (reachmatrix[orow][ocol][trow][tcol]) {
                        System.out.print("       1 |");
                    } else {
                        System.out.print("       0 |");
                    }
                }
                System.out.println();
            }
            System.out.println();
            System.out.println();
        }
    }
}

package DataStructures.Reachability;

import Objects.GridEdge;
import Objects.GridPoint;

public class ReachabilityNaive extends Reachability {


    @Override
    public void preprocess(GridPoint[][] pointmatrix) {
        this.pointmatrix = pointmatrix;
        reachmatrix = newPrep(pointmatrix.length, pointmatrix[0].length, false);
        reachmatrixswap = newPrep(pointmatrix[0].length, pointmatrix.length, true);
    }

    private void oldPrep(){
        reachmatrix = new boolean[pointmatrix.length][pointmatrix[0].length]
                [pointmatrix.length][pointmatrix[0].length];
        reachmatrixswap = new boolean[pointmatrix[0].length][pointmatrix.length]
                [pointmatrix[0].length][pointmatrix.length];
        for (int trow = 0; trow < pointmatrix.length; trow++){
            int actualtrow = pointmatrix.length-1-trow;
            for (int tcolumn = pointmatrix[0].length-1; tcolumn >= 0; tcolumn--){
                for (int orow = 0; orow < pointmatrix.length; orow++){
                    int actualorow = pointmatrix.length-1-orow;
                    for (int ocolumn = pointmatrix[0].length-1; ocolumn >= 0; ocolumn--){
                        if (pointmatrix[orow][ocolumn] == null || pointmatrix[trow][tcolumn] == null){
                            reachmatrix[actualorow][ocolumn][actualtrow][tcolumn] = false;
                            reachmatrixswap[ocolumn][actualorow][tcolumn][actualtrow] = false;
                        }
                        else if (orow == trow && ocolumn == tcolumn){
                            reachmatrix[actualorow][ocolumn][actualtrow][tcolumn] = true;
                            reachmatrixswap[ocolumn][actualorow][tcolumn][actualtrow] = true;
                        } else {
                            GridPoint p = pointmatrix[orow][ocolumn];
                            reachmatrix[actualorow][ocolumn][actualtrow][tcolumn] = false;
                            reachmatrixswap[ocolumn][actualorow][tcolumn][actualtrow] = false;
                            for (GridEdge e: p.outgoing){
                                if (e != null) {
                                    if (reachmatrix[e.getTarget().row][e.getTarget().column][actualtrow][tcolumn]) {
                                        reachmatrix[actualorow][ocolumn][actualtrow][tcolumn] = true;
                                        reachmatrixswap[ocolumn][actualorow][tcolumn][actualtrow] = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean[][][][] newPrep(int rowlength, int collength, boolean swap){
        boolean[][][][] result = new boolean[rowlength][collength][rowlength][collength];
        for (int orow = 0; orow < rowlength; orow++){
            //System.out.println("orow = "+orow);
            boolean[][][] orresult = new boolean[collength][rowlength][collength];
            for (int ocol = 0; ocol < collength; ocol++){
                //System.out.println("    ocol = "+ocol);
                boolean[][] ocresult = new boolean[rowlength][collength];
                for (int trow = 0; trow < rowlength; trow++){
                    //System.out.println("        trow = "+trow);
                    boolean[] trresult = new boolean[collength];
                    for (int tcol = 0; tcol < collength; tcol++){
                        //System.out.println("            tcol = "+tcol);
                        boolean tcresult;
                        GridPoint p1 = pointMatrixQuery(orow, ocol, swap);
                        GridPoint p2 = pointMatrixQuery(trow, tcol, swap);
                        if (p1 == null || p2 == null){
                            //System.out.println("            One of the two points does not exist");
                            tcresult = false;
                        } else if (p1.row == p2.row && p1.column == p2.column){
                            //System.out.println("            The same GridPoints");
                            tcresult = true;
                        } else {
                            tcresult = false;
                            boolean leftmost = (tcol == 0);
                            boolean bottommost = (trow == 0);
                            //System.out.println("            leftmost = "+leftmost+", bottommost = "+bottommost);
                            if (!leftmost && trresult[tcol-1]){
                                tcresult = true;
                            }
                            if (!bottommost && ocresult[trow-1][tcol]){
                                tcresult = true;
                            }
                            if (!bottommost && !leftmost && ocresult[trow-1][tcol-1]){
                                tcresult = true;
                            }
                        }
                        //System.out.println("            tcresult = " +tcresult);
                        trresult[tcol] = tcresult;
                    }
                    //System.out.print("      trresult = ");
//                    for (boolean b : trresult){
//                        System.out.print(b+", ");
//                    }
                    //System.out.println();
                    ocresult[trow] = trresult;
                }
                //System.out.println("      ocresult = ");
//                for (boolean[] a : ocresult){
//                    for (boolean b: a){
//                        System.out.print(b+", ");
//                    }
//                    System.out.println();
//                }
                //System.out.println();
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

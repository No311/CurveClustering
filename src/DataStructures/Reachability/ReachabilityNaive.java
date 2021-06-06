package DataStructures.Reachability;

import Objects.GridEdge;
import Objects.GridPoint;

public class ReachabilityNaive extends Reachability {
    GridPoint[][] pointmatrix;
    boolean[][][][] reachmatrix; //can the first two coordinates reach the second two coordinates?

    @Override
    public void preprocess(GridPoint[][] pointmatrix) {
        this.pointmatrix = pointmatrix;
        reachmatrix = new boolean[pointmatrix.length][pointmatrix[0].length][pointmatrix.length][pointmatrix[0].length];
        for (int trow = 0; trow < pointmatrix.length; trow++){
            int actualtrow = pointmatrix.length-1-trow;
            for (int tcolumn = pointmatrix[0].length-1; tcolumn >= 0; tcolumn--){
                for (int orow = 0; orow < pointmatrix.length; orow++){
                    int actualorow = pointmatrix.length-1-orow;
                    for (int ocolumn = pointmatrix[0].length-1; ocolumn >= 0; ocolumn--){
                        if (pointmatrix[orow][ocolumn] == null || pointmatrix[trow][tcolumn] == null){
                            reachmatrix[actualorow][ocolumn][actualtrow][tcolumn] = false;
//                            System.out.printf("Origin (%d, %d) or Target (%d, %d) does not exist\n",
//                                    orow, ocolumn, trow, tcolumn);
                        }
                        else if (orow == trow && ocolumn == tcolumn){
                            reachmatrix[actualorow][ocolumn][actualtrow][tcolumn] = true;
//                            System.out.printf("Origin = (%d, %d) is Target = (%d, %d)\n",
//                                    orow, ocolumn, trow, tcolumn);
                        } else {
//                            System.out.printf("Origin = (%d, %d) and Target = (%d, %d)\n", actualorow, ocolumn, actualtrow, tcolumn);
                            GridPoint p = pointmatrix[orow][ocolumn];
                            reachmatrix[actualorow][ocolumn][actualtrow][tcolumn] = false;
                            for (GridEdge e: p.outgoing){
                                if (e != null) {
                                    if (reachmatrix[e.getTarget().row][e.getTarget().column][actualtrow][tcolumn]) {
                                        reachmatrix[actualorow][ocolumn][actualtrow][tcolumn] = true;
//                                        System.out.printf("Origin = (%d, %d) can reach Target = (%d, %d)\n",
//                                                actualorow, ocolumn, actualtrow, tcolumn);
//                                        p.addReachable(pointmatrix[trow][tcolumn]);
//                                        pointmatrix[trow][tcolumn].addReachedFrom(p);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean query(GridPoint start, GridPoint goal) {
        if (start == null || goal == null){
            return false;
        }
        return reachmatrix[start.row][start.column][goal.row][goal.column];
    }

    public void printMatrixBool(){
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
        }
    }
}

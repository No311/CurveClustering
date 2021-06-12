package DataStructures.SetSystemQuerier;

import Objects.TrajPoint;
import Objects.Trajectory;

import java.util.ArrayList;

public class OracleResult {
    private boolean[][] covered;
    public int amountCovered = 0;
    private int origAmountCovered = 0;
    private Trajectory first;
    private ArrayList<Trajectory> selection;
    private TrajPoint subTrajStart;
    private TrajPoint subTrajEnd;
    private boolean selected = false;

    public OracleResult(boolean[][] covered, int amountCovered, TrajPoint subTrajStart, TrajPoint subTrajEnd,
                        Trajectory first, ArrayList<Trajectory> selection){
        this.covered = covered;
        this.amountCovered = amountCovered;
        this.origAmountCovered = amountCovered;
        this.subTrajStart = subTrajStart;
        this.subTrajEnd = subTrajEnd;
        this.first = first;
        this.selection = selection;
    }

    public boolean[][] getCovered() {
        return covered;
    }

    public void updateAmountCovered(boolean[][] nowCovered){
        amountCovered = 0;
        for (Trajectory t: selection) {
            for (int i = 0; i < covered[t.index].length; i++) {
                if (covered[t.index][i] && !nowCovered[t.index][i]) {
                    amountCovered++;
                }
            }
        }
    }

    public void resetAmountCovered(){
        amountCovered = origAmountCovered;
        for (Trajectory t: selection){
            for (TrajPoint p: t.getPoints()){
                p.setSelected(false);
                p.setCovered(false);
                t.amountSelected--;
            }
        }
    }

    public TrajPoint getSubTrajStart() {
        return subTrajStart;
    }

    public void setSubTrajStart(TrajPoint subTrajStart) {
        this.subTrajStart = subTrajStart;
    }

    public TrajPoint getSubTrajEnd() {
        return subTrajEnd;
    }

    public void setSubTrajEnd(TrajPoint subTrajEnd) {
        this.subTrajEnd = subTrajEnd;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Trajectory getFirst() {
        return first;
    }

    public ArrayList<Trajectory> getSelection() {
        return selection;
    }

}

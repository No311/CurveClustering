package DataStructures.SetSystemQuerier;

import Objects.TrajPoint;

public class OracleResult {
    private boolean[] covered;
    public int amountCovered = 0;
    private int origAmountCovered = 0;
    private TrajPoint subTrajStart;
    private TrajPoint subTrajEnd;
    private boolean selected = false;

    public OracleResult(boolean[] covered, int amountCovered, TrajPoint subTrajStart, TrajPoint subTrajEnd){
        this.covered = covered;
        this.amountCovered = amountCovered;
        this.origAmountCovered = amountCovered;
        this.subTrajStart = subTrajStart;
        this.subTrajEnd = subTrajEnd;
    }

    public boolean[] getCovered() {
        return covered;
    }

    public void updateAmountCovered(boolean[] nowCovered){
        amountCovered = 0;
        for (int i = 0; i < covered.length; i++){
            if (covered[i] && !nowCovered[i]){
                amountCovered++;
            }
        }
    }

    public void resetAmountCovered(){
        amountCovered = origAmountCovered;
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

}

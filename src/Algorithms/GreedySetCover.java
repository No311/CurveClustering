package Algorithms;

import DataStructures.SetSystemQuerier.OracleResult;
import DataStructures.SetSystemQuerier.SetSystemOracle;
import Objects.TrajPoint;
import Objects.Trajectory;

import java.util.ArrayList;
import java.util.Arrays;

public class GreedySetCover {

    public ArrayList<OracleResult> doGreedySetCover(SetSystemOracle oracle, int lMinValue, int lMaxValue) {
        if (lMaxValue == -1 || lMaxValue > oracle.getFirst().getPoints().size()-1){
            lMaxValue = oracle.getFirst().getPoints().size()-1;
        }
        lMaxValue = Math.max(lMaxValue, lMinValue);
        ArrayList<OracleResult> candidates = getCandidateTrajectories(oracle, oracle.getFirst(), lMinValue, lMaxValue);
        boolean[] nowCovered = new boolean[oracle.getSecond().getPoints().size()];
        ArrayList<OracleResult> result = doGreedyPart(candidates, nowCovered, oracle.getTotalCoveredByFirst());
        return result;
    }

    public ArrayList<OracleResult> getCandidateTrajectories(SetSystemOracle oracle, Trajectory first,
                                                            int lMin, int lMax){
        ArrayList<OracleResult> result = new ArrayList<>();
        for (int i = 0; i< first.getPoints().size(); i++){
            for (int j = i+lMin; j<= Math.min(i+lMax, first.getPoints().size()-1); j++){
                result.add(oracle.getCoveredBySub(i, j));
            }
        }
        return result;
    }


    private ArrayList<OracleResult> doGreedyPart(ArrayList<OracleResult> candidates, boolean[] nowCovered,
                                                 boolean[] totalCovered) {
        ArrayList<OracleResult> result = new ArrayList<>();
        if (candidates.isEmpty()){
            return result;
        }
        if (Arrays.equals(nowCovered, totalCovered)){
            return result;
        }
        OracleResult currentChoice = candidates.get(0);
        int maxCovered = candidates.get(0).amountCovered;
        for (OracleResult c: candidates){
            if (c.amountCovered > maxCovered){
                currentChoice = c;
                maxCovered = c.amountCovered;
            }
            else if (c.amountCovered == maxCovered){
                int choicePoints = currentChoice.getSubTrajEnd().index-currentChoice.getSubTrajStart().index;
                int cPoints = c.getSubTrajEnd().index-c.getSubTrajStart().index;
                if (cPoints < choicePoints){
                    currentChoice = c;
                    maxCovered = c.amountCovered;
                }
            }
        }
        for (int i = 0; i < nowCovered.length; i++){
            if (currentChoice.getCovered()[i] && !nowCovered[i]){
                nowCovered[i] = true;
            }
        }
        currentChoice.setSelected(true);
        result.add(currentChoice);
        candidates.remove(currentChoice);
        ArrayList<OracleResult> toBeRemoved = new ArrayList<>();
        for (OracleResult c: candidates){
            c.updateAmountCovered(nowCovered);
            if(c.amountCovered == 0){
                toBeRemoved.add(c);
            }
        }
        candidates.removeAll(toBeRemoved);
        result.addAll(doGreedyPart(candidates, nowCovered, totalCovered));
        return result;
    }
}

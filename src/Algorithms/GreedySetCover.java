package Algorithms;

import DataStructures.Querier.OracleResult;
import DataStructures.Querier.SetSystemOracle;
import Objects.TrajPoint;
import Objects.Trajectory;

import java.util.ArrayList;

public class GreedySetCover {

    public ArrayList<OracleResult> doGreedySetCover(ArrayList<Trajectory> trajectories,
                                                    ArrayList<SetSystemOracle> oracles, int lMinValue, int lMaxValue) {
        int maxValue = getMaxValue(trajectories);
        if (lMaxValue == -1 || lMaxValue > maxValue){
            lMaxValue = maxValue;
        }
        lMaxValue = Math.max(lMaxValue, lMinValue);
        ArrayList<OracleResult> candidates = getCandidateTrajectories(trajectories, oracles, lMinValue, lMaxValue);
        boolean[][] nowCovered = new boolean[trajectories.size()][];
        for (Trajectory trajectory: trajectories){
            nowCovered[trajectory.index] = new boolean[trajectory.getPoints().size()];
        }
        ArrayList<OracleResult> result = doGreedyPart(candidates, nowCovered, trajectories);
        return result;
    }

    private int getMaxValue(ArrayList<Trajectory> trajectories) {
        int maxValue = 0;
        for (Trajectory trajectory: trajectories){
            if (trajectory.getPoints().size()-1 > maxValue){
                maxValue = trajectory.getPoints().size()-1;
            }
        }
        return maxValue;
    }

    public ArrayList<OracleResult> getCandidateTrajectories(ArrayList<Trajectory> trajectories,
                                                            ArrayList<SetSystemOracle> oracles,
                                                            int lMin, int lMax){
        ArrayList<OracleResult> result = new ArrayList<>();
        for (Trajectory t: trajectories) {
            for (SetSystemOracle oracle: oracles){
                if (oracle.getFirst() == t){
                    for (int i = 0; i < t.getPoints().size(); i++) {
                        for (int j = i + lMin; j <= Math.min(i + lMax, t.getPoints().size() - 1); j++) {
                            OracleResult candidate = oracle.getCoveredBySub(i, j);
                            candidate.resetAmountCovered();
                            result.add(candidate);
                        }
                    }
                }
            }
        }
        return result;
    }


    private ArrayList<OracleResult> doGreedyPart(ArrayList<OracleResult> candidates, boolean[][] nowCovered,
                                                 ArrayList<Trajectory> trajectories) {
        ArrayList<OracleResult> result = new ArrayList<>();
        if (candidates.isEmpty()){
            return result;
        }
        if (checkCovered(trajectories)){
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
        for (Trajectory t: currentChoice.getSelection()) {
            for (int i = 0; i < nowCovered[t.index].length; i++) {
                if (nowCovered[t.index][i]){
                }
                if (currentChoice.getCovered()[t.index][i]) {
                    nowCovered[t.index][i] = true;
                }
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
            //remove this bit if defining subtrajectories are allowed to overlap
            for (int i = c.getSubTrajStart().index; i<= c.getSubTrajEnd().index; i++){
                if (c.getFirst().getPoints().get(i).isSelected()){
                    toBeRemoved.add(c);
                }
            }
        }
        candidates.removeAll(toBeRemoved);
        result.addAll(doGreedyPart(candidates, nowCovered, trajectories));
        return result;
    }

    private boolean checkCovered(ArrayList<Trajectory> trajectories) {
        for (Trajectory t: trajectories){
            for (TrajPoint p: t.getPoints()){
                if(!p.isCovered()){
                    return false;
                }
            }
        }
        return true;
    }
}

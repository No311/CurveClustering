package Methods;

import DataStructures.FSGMethods.*;
import DataStructures.Reachability.Reachability;
import DataStructures.Reachability.ReachabilityNaive;
import DataStructures.SetSystemQuerier.*;
import Objects.DFDGrid;
import Objects.Trajectory;

import javax.swing.*;
import java.util.ArrayList;

public class SetSystemMethods {
    JTextArea infoText;

    public SetSystemMethods(JTextArea infoText){
        this.infoText = infoText;
    }

    public ArrayList<SetSystemOracle> initSetSystem(int method, int delta,
                                                    int reachInt, int algoInt, int queryInt,
                                                    ArrayList<Trajectory> selection) {
        ArrayList<SetSystemOracle> result = new ArrayList<>();
        switch (method) {
            case 0 -> {
                infoText.append("     Initializing Naive Method...\n");
                infoText.repaint();
                ExtremelyNaiveQuerier NaiveMethod = new ExtremelyNaiveQuerier();
                return NaiveMethod.queryAll(selection, delta);
            }
            case 1 -> {
                infoText.append("     Initializing reachability and queriable information...\n\n");
                infoText.repaint();
                for (int index = 0; index < selection.size(); index++){
                    Trajectory current = selection.get(index);
                    current.index = index;
                }
                for (int one = 0; one < selection.size(); one++) {
                    System.gc();
                    Trajectory first = selection.get(one);
                    infoText.append("\u2794 Initializing Trajectory "+first.getName()+"...\n");
                    infoText.repaint();
                    boolean[][][][] curResults = new boolean[selection.size()][][][];
                    assert first.index == one;
                    for (int two = 0; two < selection.size(); two++) {
                        System.gc();
                        Trajectory second = selection.get(two);
                        infoText.append("     \u2794 with Trajectory "+second.getName()+"...\n");
                        infoText.repaint();
                        assert second.index == two;
                        DFDGrid curGrid = generateDFDGrid(first, second, delta);
                        Reachability reach = initReach(first, second, reachInt, curGrid, false);
                        FSGMethod algo = initAlgo(first, second, algoInt, reach, curGrid);
                        curResults[second.index] = CoverageInfo(queryInt, first, second, reach, algo);
                        infoText.append("     Pair ("+first.getName()+", "+second.getName()+") initialized...\n\n");
                        infoText.repaint();
                    }
                    infoText.append("     Creating Set System Oracle for "+first.getName()+"...\n");
                    infoText.repaint();
                    long starttime = System.currentTimeMillis();
                    result.add(new SetSystemOracle(curResults, first, selection));
                    long endtime = System.currentTimeMillis();
                    double time = ((double) endtime - (double) starttime) / 1000;
                    infoText.append("     Set System Oracle Created in " + time + " seconds.\n" +
                            "     Trajectory "+first.getName()+" initialized...\n\n");
                    infoText.repaint();
                }
            }
        }
        return result;
    }

    private boolean[][][] CoverageInfo(int queryInt, Trajectory first, Trajectory second,
                                         Reachability reach, FSGMethod algo) {
        long endtime;
        double time;
        long starttime;
        infoText.append("     Initializing Coverage Information...\n");
        infoText.repaint();
        starttime = System.currentTimeMillis();
        SetSystemQuerier query = null;
        switch (queryInt) {
            case 1 -> {
                query = new EasyQuerier(reach, algo);
            }
            case 2 -> {
                query = new LongJumpQuerier(reach, algo);
            }
        }
        assert query != null;
        boolean[][][] result = query.queryAll(first, second);
        endtime = System.currentTimeMillis();
        time = ((double) endtime - (double) starttime) / 1000;
        infoText.append("     Coverage Information Initialized in " + time + " seconds.\n");
        infoText.repaint();
        System.gc();
        return result;
    }

    public FSGMethod initAlgo(Trajectory first, Trajectory second, int algoInt, Reachability reach, DFDGrid grid){
        long starttime;
        long endtime;
        double time;
        infoText.append("     Initializing Algorithm (" + first.getName() + ", " + second.getName() + ")...\n");
        infoText.repaint();
        starttime = System.currentTimeMillis();
        FSGMethod algo = null;
        switch (algoInt) {
            case 1 -> {
                algo = new FSGMethodPrepNaive();
            }
            case 2 -> {
                algo = new FSGMethodLog();
            }
            case 3 -> {
                algo = new FSGMethodLogNoOpt();
            }
            case 4 -> {
                algo = new FSGMethodNoPrepNaive();
            }
            case 5 -> {
                algo = new FSGMethodQueryNaive();
            }
        }
        assert algo != null;
        algo.preprocess(grid.getPointsMatrix(),
                reach, first, second);
        endtime = System.currentTimeMillis();
        time = ((double) endtime - (double) starttime) / 1000;
        infoText.append("     Algorithm Initialized in " + time + " seconds.\n");
        infoText.repaint();
        System.gc();
        return algo;
    }

    public Reachability initReach(Trajectory first, Trajectory second, int reachInt, DFDGrid grid, boolean visual) {
        long starttime;
        long endtime;
        double time;
        infoText.append("     Initializing Reachability (" + first.getName() + ", " + second.getName() + ")...\n");
        infoText.repaint();
        starttime = System.currentTimeMillis();
        Reachability reach = null;
        switch (reachInt) {
            case 1 -> {
                reach = new ReachabilityNaive();
            }
        }
        assert reach != null;
        reach.preprocess(grid.getPointsMatrix(), first, second, visual);
        endtime = System.currentTimeMillis();
        time = ((double) endtime - (double) starttime) / 1000;
        infoText.append("     Reachability Initialized in " + time + " seconds.\n");
        System.gc();
        return reach;
    }

    private DFDGrid generateDFDGrid(Trajectory first, Trajectory second, int delta) {
        long starttime;
        long endtime;
        double time;
        infoText.append("     Initializing PointMatrix for DFD Grid," +
                "("+ first.getName()+", "+ second.getName()+")\n");
        infoText.repaint();
        starttime = System.currentTimeMillis();
        DFDGrid grid = null;
        grid = new DFDGrid(first, second, delta, 0, 0);
        endtime = System.currentTimeMillis();
        time = ((double) endtime - (double) starttime) / 1000;
        infoText.append("     PointMatrix initialized in " + time + " seconds.\n");
        infoText.repaint();
        System.gc();
        return grid;
    }
}



package Methods;

import Algorithms.GreedySetCover;
import DataStructures.FSGMethods.*;
import DataStructures.Reachability.Reachability;
import DataStructures.Reachability.ReachabilityNaive;
import DataStructures.Querier.*;
import Objects.FSGrid;
import Objects.Trajectory;

import javax.swing.*;
import java.util.ArrayList;

public class SetSystemMethods {
    JTextArea infoText;
    Runtime rt = Runtime.getRuntime();
    long totalReachTime = (long) 0.0;
    long totalFSGTime = (long) 0.0;
    long totalQueryTime = (long) 0.0;
    long mostReachSpace = (long) 0.0;
    long mostFSGSpace = (long) 0.0;
    long mostQuerySpace = (long) 0.0;

    public SetSystemMethods(JTextArea infoText){
        this.infoText = infoText;
    }

    public ArrayList<SetSystemOracle> initSetSystem(int method, int delta,
                                                    int reachInt, int algoInt, int queryInt,
                                                    ArrayList<Trajectory> selection) {
        System.gc();
        ArrayList<SetSystemOracle> result = new ArrayList<>();
        long totalStartMem = (rt.totalMemory()/1024/1024);
        long freeStartMem = (rt.freeMemory()/1024/1024);
        long curStartUsedMem = totalStartMem-freeStartMem;
        switch (method) {
            case 0 -> {
                infoText.append("     Initializing Naive Method...\n" +
                        "     Total Memory: "+(double) totalStartMem+"MB, Memory in Use: "+(double) curStartUsedMem+"MB.\n");
                infoText.repaint();
                ExtremelyNaiveQuerier NaiveMethod = new ExtremelyNaiveQuerier();
                long curFreeMem = rt.freeMemory()/1024/1024;
                long totalMem = (rt.totalMemory()/1024/1024);
                long curMem = (totalMem-curFreeMem);
                infoText.append("     Naive Method Initialized...\n" +
                        "     Total Memory: "+(double) totalStartMem+", Memory in Use: "+(double) curMem+"MB.\n");
                infoText.repaint();
                return NaiveMethod.queryAll(selection, delta);
            }
            case 1 -> {
                totalReachTime = (long) 0.0;
                totalFSGTime = (long) 0.0;
                totalQueryTime = (long) 0.0;
                mostReachSpace = (long) 0.0;
                mostFSGSpace = (long) 0.0;
                mostQuerySpace = (long) 0.0;
                infoText.append("     Initializing reachability and queriable information...\n" +
                        "     Total Memory: "+(double) totalStartMem+"MB, Memory in Use: "+(double) curStartUsedMem+"MB.\n\n");
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
                        FSGrid curGrid = generateFSGrid(first, second, delta);
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
                    long totalMem = (rt.totalMemory()/1024/1024);
                    long curFreeMem = rt.freeMemory()/1024/1024;
                    long curMem = totalMem-curFreeMem;
                    infoText.append("     Set System Oracle Created in " + time + " seconds,\n" +
                            "     total memory in use: "+ (double) curMem + "MB\n"+
                            "     Trajectory "+first.getName()+" initialized...\n\n");
                    infoText.repaint();
                }
            }
        }
        long totalMem = rt.totalMemory()/1024/1024;
        long curFreeMem = rt.freeMemory()/1024/1024;
        long MethodMem = totalMem-curFreeMem;
        infoText.append("     Reachability and queriable information initialized...\n" +
                "     Total Reachability Time: "+((double) totalReachTime/1000) + ", Max Space Used: "+((double) mostReachSpace)+"MB\n" +
                "     Total FSG Time: "+ ((double) totalFSGTime/1000) + ", Max Space Used: "+((double) mostFSGSpace)+"MB\n" +
                "     Total Query Time: "+ ((double) totalQueryTime/1000) + ", Max Space Used: "+((double) mostQuerySpace)+"MB\n");
        infoText.repaint();
        System.gc();
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
        Querier query = null;
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
        totalQueryTime += (endtime - starttime);
        time = ((double) endtime - (double) starttime) / 1000;
        infoText.append("     Coverage Information Initialized in " + time + " seconds.\n");
        infoText.repaint();
        mostQuerySpace = updateMostMem(mostQuerySpace);
        System.gc();
        return result;
    }



    public FSGMethod initAlgo(Trajectory first, Trajectory second, int algoInt, Reachability reach, FSGrid grid){
        long starttime;
        long endtime;
        double time;
        infoText.append("     Initializing FSG Structure (" + first.getName() + ", " + second.getName() + ")...\n");
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
        totalFSGTime += (endtime - starttime);
        time = ((double) endtime - (double) starttime) / 1000;
        infoText.append("     FSG Structure Initialized in " + time + " seconds.\n");
        infoText.repaint();
        mostFSGSpace = updateMostMem(mostFSGSpace);
        System.gc();
        return algo;
    }

    public Reachability initReach(Trajectory first, Trajectory second, int reachInt, FSGrid grid, boolean visual) {
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
        totalReachTime += (endtime-starttime);
        time = ((double) endtime - (double) starttime) / 1000;
        infoText.append("     Reachability Initialized in " + time + " seconds.\n");
        mostReachSpace = updateMostMem(mostReachSpace);
        System.gc();
        return reach;
    }

    private FSGrid generateFSGrid(Trajectory first, Trajectory second, int delta) {
        long starttime;
        long endtime;
        double time;
        infoText.append("     Initializing PointMatrix for FSGrid," +
                " ("+ first.getName()+", "+ second.getName()+")\n");
        infoText.repaint();
        starttime = System.currentTimeMillis();
        FSGrid grid = new FSGrid(first, second, delta, 0, 0);
        endtime = System.currentTimeMillis();
        time = ((double) endtime - (double) starttime) / 1000;
        infoText.append("     PointMatrix initialized in " + time + " seconds.\n");
        infoText.repaint();
        System.gc();
        return grid;
    }

    public void doGreedySetCover(JTextArea infoText, ArrayList<Trajectory> selection,
                                 ArrayList<SetSystemOracle> oracles, int lMin, int lMax, long totalTime) {
        GreedySetCover g = new GreedySetCover();
        long totalMem = (rt.totalMemory()/1024/1024);
        long curFreeMem = rt.freeMemory()/1024/1024;
        long curMem = totalMem-curFreeMem;
        infoText.append("Greedy Set Cover Started.\n     Total Memory: "+totalMem+"MB, Memory in Use: "+ curMem+"MB.\n");
        long starttime = System.currentTimeMillis();
        ArrayList<OracleResult> GSCResult = g.doGreedySetCover(selection, oracles, lMin, lMax);
        long endtime = System.currentTimeMillis();
        totalTime+= (endtime-starttime);
        double time = ((double) endtime - (double) starttime) / 1000;
        totalMem = (rt.totalMemory()/1024/1024);
        curFreeMem = rt.freeMemory()/1024/1024;
        curMem = totalMem-curFreeMem;
        infoText.append("Greedy Set Cover Completed in " + time + " seconds.\n" +
                "     Total Memory: "+totalMem+"MB, Memory in Use: "+curMem+"MB.\n"+
                "     Total Time used: "+((double) totalTime/1000)+" seconds.\n"+
                "     Results:\n");
        for (OracleResult r: GSCResult){
            infoText.append("     Subtrajectory ("+r.getSubTrajStart().index+", "+r.getSubTrajEnd().index+") from" +
                    " Trajectory "+r.getFirst().getName()+"\n");
        }
        infoText.append("\n");
        System.gc();
    }

    private long updateMostMem(long mem) {
        long curFreeMem = rt.freeMemory()/1024/1024;
        long totalMem = (rt.totalMemory()/1024/1024);
        long curMem = (totalMem-curFreeMem);
        return Math.max(mem, curMem);
    }
}



package Interface.Tabs;

import Algorithms.GreedySetCover;
import DataStructures.Reachability.Reachability;
import DataStructures.Reachability.ReachabilityNaive;
import DataStructures.SetSystemQuerier.*;
import DataStructures.TrajCover.TrajCover;
import DataStructures.TrajCover.TrajCoverLog;
import DataStructures.TrajCover.TrajCoverNaive;
import Interface.ListItem;
import Interface.VisualPanels.SetTrajectoryPanel;
import Interface.WrapLayout;
import Objects.DFDGrid;
import Objects.GridPoint;
import Objects.TrajPoint;
import Objects.Trajectory;
import Interface.GeneralFunctions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class SetTab {
    SetTrajectoryPanel visual;
    JTextArea infoText;
    ArrayList<JComponent> interactables;
    int tabnumber = 0;
    ArrayList<Trajectory> selection = new ArrayList<>();
    DFDGrid[][] GridObject;
    Reachability[][] reachabilitiesPerFirst;
    TrajCover[][] algosPerFirst;
    ArrayList<SetSystemOracle> oracles = new ArrayList<>();
    GeneralFunctions gF = new GeneralFunctions();
    int reachInt = 0;
    int algoInt = 0;
    int queryInt = 0;
    int delta = 0;
    int method = 0;

    public ArrayList<JComponent> init(String delta, JList<ListItem> selectionList,
                                      int framewidth, JTabbedPane mainPane, JTextArea infoText, int method,
                                      String methodString, int reachInt, int algoInt, int queryInt,
                                      String reachString, String algoString, String queryString,
                                      int amount, ArrayList<JComponent> interactables) {
        infoText.append("     Initializing Set System Tab " + amount + "\n     Using the " + methodString + " method...\n");
        if (method == 1) {
            infoText.append("     Using " + reachString + " reachability, \n     the " + algoString + " queriable and \n" +
                    "     the " + queryString + " query method...\n");
        }
        long tabstarttime = System.currentTimeMillis();

        //Initializing some variables
        this.infoText = infoText;
        this.tabnumber = amount;
        for (ListItem item : selectionList.getSelectedValuesList()) {
            Trajectory itemT = item.getT().clone();
            itemT.setSelected(true);
            selection.add(itemT);
        }
        this.reachInt = reachInt;
        this.algoInt = algoInt;
        this.queryInt = queryInt;
        this.delta = Integer.parseInt(delta);
        this.method = method;
        this.GridObject = new DFDGrid[selection.size()][selection.size()];
        this.reachabilitiesPerFirst = new Reachability[selection.size()][selection.size()];
        this.algosPerFirst = new TrajCover[selection.size()][selection.size()];
        this.oracles = initMethod(infoText);

        JPanel setPanel = new JPanel(new BorderLayout());

        //everything coordinatePanel
        JTextField currentField = new JTextField("Current Coordinates: (x: 0, y: 0)");
        currentField.setBackground(UIManager.getColor("Panel.background"));
        currentField.setEditable(false);

        //Initialization of visual
        JCheckBox showGridBox = new JCheckBox("Show Grid", true);
        JLabel gridField = new JLabel("Grid Size = 1");
        visual = new SetTrajectoryPanel(gridField, showGridBox, currentField, oracles);
        visual.addMapListeners(visual);
        visual.updateDrawables(selection);
        visual.setOracleListeners(visual);

        //everything TrajectoryPanel
        JPanel trajectoryPanel = new JPanel();
        trajectoryPanel.setBorder(BorderFactory.createEtchedBorder());
        String trajectoryString = "Trajectories:\n";
        int itemcount = 0;
        for (ListItem item : selectionList.getSelectedValuesList()) {
            trajectoryString += itemcount + ": " + item.toString() + " (clone)\n";
            itemcount++;
        }
        trajectoryString += "Threshold: " + delta;
        JTextArea trajectoryLabel = new JTextArea(trajectoryString);
        trajectoryLabel.setBackground(UIManager.getColor("Panel.background"));
        trajectoryLabel.setColumns(framewidth / 72);
        trajectoryLabel.setEditable(false);
        JScrollPane trajectoryLabelScroll = new JScrollPane(trajectoryLabel);
        trajectoryLabelScroll.setPreferredSize(new Dimension(0, 100));
        trajectoryPanel.add(trajectoryLabelScroll);

        //Everything optionsPanel
        JPanel optionPanel = new JPanel(new WrapLayout());
        optionPanel.setBorder(BorderFactory.createEtchedBorder());
        JSlider sizeSlider = new JSlider(1, 100, 10);
        JLabel sliderLabel = new JLabel("Draw Size: 10");
        visual.sizeSliderConfig(visual, sliderLabel, sizeSlider);
        optionPanel.add(showGridBox);
        optionPanel.add(gridField);
        optionPanel.add(sizeSlider);
        optionPanel.add(sliderLabel);

        //Everything QueryPanel
        JPanel queryPanel = new JPanel(new GridLayout(1, 1));
        queryPanel.setBorder(BorderFactory.createEtchedBorder());
        JButton greedySetButton = new JButton("<html><center>Subtrajectory<br>Covering</center></html>");
        JPanel greedySetLPanel = new JPanel(new FlowLayout());
        greedySetLPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                        "ℓ-values used for Subtrajectory Covering:"), BorderFactory.createEmptyBorder(0, 50, 0, 50)));
        JLabel greedySetLMin = new JLabel("min ℓ:", SwingConstants.CENTER);
        JLabel greedySetLMax = new JLabel("max ℓ:", SwingConstants.CENTER);
        JTextField lMin = new JTextField("", SwingConstants.CENTER);
        lMin.setColumns(3);
        JTextField lMax = new JTextField("", SwingConstants.CENTER);
        lMax.setColumns(3);
        gF.buttonDependency(greedySetButton, lMin, (String s) -> s.matches("^[\\d]*$") || s.equals(""));
        gF.buttonDependency(greedySetButton, lMax, (String s) -> s.matches("^[\\d]*$") || s.equals(""));
        greedySetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                infoText.append("Tab " + tabnumber + ":\n");
                infoText.append("Greedy Set Cover Started.\n");
                long starttime = System.currentTimeMillis();
                String lMinText = lMin.getText();
                String lMaxText = lMax.getText();
                int lMinValue = 0;
                int lMaxValue = -1;
                if (!lMinText.equals("")) {
                    lMinValue = Integer.parseInt(lMinText);
                }
                if (!lMaxText.equals("")) {
                    lMaxValue = Integer.parseInt(lMaxText);
                }
                visual.resetSelectedResults();
                GreedySetCover greedySetCover = new GreedySetCover();
                ArrayList<OracleResult> results = greedySetCover.doGreedySetCover(selection, oracles, lMinValue, lMaxValue);
                visual.addSelectedResults(results);
                visual.setSelection();
                visual.repaint();
                visual.requestFocus();
                long endtime = System.currentTimeMillis();
                double time = ((double) endtime - (double) starttime) / 1000;
                infoText.append("Greedy Set Cover Completed in " + time + " seconds.\n" +
                        "     Results:\n");
                for (OracleResult r: results){
                    infoText.append("     Subtrajectory ("+r.getSubTrajStart().index+", "+r.getSubTrajEnd().index+") from" +
                            " Trajectory "+r.getFirst().getName()+"\n");
                }
                infoText.append("\n");

            }
        });
        greedySetLPanel.add(greedySetLMin);
        greedySetLPanel.add(lMin);
        greedySetLPanel.add(greedySetLMax);
        greedySetLPanel.add(lMax);
        optionPanel.add(greedySetLPanel);
        queryPanel.add(greedySetButton);

        //Everything BottomPanel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(queryPanel, BorderLayout.LINE_START);
        bottomPanel.add(trajectoryPanel, BorderLayout.LINE_END);
        bottomPanel.add(optionPanel, BorderLayout.CENTER);

        //Everything SetPanel
        setPanel.add(currentField, BorderLayout.PAGE_START);
        setPanel.add(visual, BorderLayout.CENTER);
        setPanel.add(bottomPanel, BorderLayout.PAGE_END);
        //dealing with interactables
        this.interactables = interactables;

        mainPane.addTab("Set System " + amount, setPanel);
        long tabendtime = System.currentTimeMillis();
        double time = ((double) tabendtime - (double) tabstarttime) / 1000;
        infoText.append("\u2794 Set System Tab " + amount + " initialized in " + time + " seconds.\n\n");
        infoText.repaint();
        return interactables;
    }

    private SetSystemOracle CoverageInfo(JTextArea infoText, int queryInt, Trajectory first,
                                         Reachability[] reach, TrajCover[] algo) {
        long endtime;
        double time;
        long starttime;
        infoText.append("\u2794 Initializing Coverage Information...\n");
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
        SetSystemOracle result = query.queryAll(first, selection);
        endtime = System.currentTimeMillis();
        time = ((double) endtime - (double) starttime) / 1000;
        infoText.append("     Coverage Information Initialized in " + time + " seconds.\n\n");
        infoText.repaint();
        return result;
    }

    private ArrayList<SetSystemOracle> initMethod(JTextArea infoText) {
        ArrayList<SetSystemOracle> result = new ArrayList<>();
        switch (method) {
            case 0 -> {
                infoText.append("     Initializing Naive Method...\n");
                infoText.repaint();
                ExtremelyNaiveQuerier NaiveMethod = new ExtremelyNaiveQuerier();
                return NaiveMethod.queryAll(selection, delta);
            }
            case 1 -> {
                infoText.append("     Initializing reachability and queriable information...\n");
                infoText.repaint();
                for (int one = 0; one < selection.size(); one++) {
                    Trajectory first = selection.get(one);
                    if (first.index == -1) {
                        first.index = one;
                    } else {assert first.index == one;}
                    for (int two = one; two < selection.size(); two++) {
                        Trajectory second = selection.get(two);
                        if (second.index == -1) {
                            second.index = two;
                        } else {assert second.index == two;}
                        InitReachAlgo(first, second);
                    }
                    result.add(CoverageInfo(infoText, queryInt, first, reachabilitiesPerFirst[first.index],
                            algosPerFirst[first.index]));
                }
            }
        }
        return result;
    }

    private void InitReachAlgo(Trajectory first, Trajectory second) {
        long endtime;
        double time;
        long starttime = System.currentTimeMillis();
        if (first.index != second.index) {
            infoText.append("\u2794 Initializing reach and queriable for the pair " +
                    "(" + first.getName() + ", " + second.getName() + ") both ways...\n");
        } else {
            infoText.append("     Initializing reach and queriable for " + first.getName() + "...\n");
            infoText.repaint();
        }
        generateDFDGrid(first, second);
        if (first.index != second.index){
            generateDFDGrid(second, first);
        }
        initReachability(first, second);
        if (first.index != second.index) {
            initReachability(second, first);
        }
        initAlgo(first, second);
        if (first.index != second.index){
            initAlgo(second, first);
        }
        endtime = System.currentTimeMillis();
        time = ((double) endtime - (double) starttime) / 1000;
        infoText.append("     Pair Initialized in " + time + " seconds.\n\n");
        infoText.repaint();
    }

    private void initAlgo(Trajectory first, Trajectory second){
        long starttime;
        long endtime;
        double time;
        if (algosPerFirst[first.index][second.index] != null) {
            infoText.append("     Algorithm ("+first.getName()+", "+second.getName()+") already initialized\n\n");
            infoText.repaint();
            return;
        }
        infoText.append("     Initializing Algorithm (" + first.getName() + ", " + second.getName() + ")...\n");
        infoText.repaint();
        starttime = System.currentTimeMillis();
        TrajCover algo = null;
        switch (algoInt) {
            case 1 -> {
                algo = new TrajCoverNaive();
            }
            case 2 -> {
                algo = new TrajCoverLog();
            }
        }
        assert algo != null;
        algo.preprocess(GridObject[first.index][second.index].getPointsMatrix(),
                reachabilitiesPerFirst[first.index][second.index], first, second);
        algosPerFirst[first.index][second.index] = algo;
        endtime = System.currentTimeMillis();
        time = ((double) endtime - (double) starttime) / 1000;
        infoText.append("     Algorithm Initialized in " + time + " seconds.\n\n");
        infoText.repaint();
    }

    private void initReachability(Trajectory first, Trajectory second) {
        long starttime;
        long endtime;
        double time;
        if (reachabilitiesPerFirst[first.index][second.index] != null) {
            infoText.append("     Reachability ("+first.getName()+", "+second.getName()+") already initialized\n\n");
            infoText.repaint();
            return;
        }
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
        Reachability swapReach = reachabilitiesPerFirst[second.index][first.index];
        if (swapReach != null){
            reach.set(GridObject[first.index][second.index].getPointsMatrix(), swapReach.getSwapped(),
                    swapReach.getReachMatrix());
        } else {
            reach.preprocess(GridObject[first.index][second.index].getPointsMatrix(), first, second);
        }
        reachabilitiesPerFirst[first.index][second.index] = reach;
        endtime = System.currentTimeMillis();
        time = ((double) endtime - (double) starttime) / 1000;
        infoText.append("     Reachability Initialized in " + time + " seconds.\n\n");
    }

    private void generateDFDGrid(Trajectory first, Trajectory second) {
        long starttime;
        long endtime;
        double time;
        if (GridObject[first.index][second.index] != null) {
            infoText.append("     DFDGrid (" + first.getName() + ", " + second.getName() + ") already initialized\n\n");
            infoText.repaint();
            return;
        }
        infoText.append("     Initializing PointMatrix for DFD Grid," +
                "("+ first.getName()+", "+ second.getName()+")\n");
        infoText.repaint();
        starttime = System.currentTimeMillis();
        DFDGrid swap = GridObject[second.index][first.index];
        DFDGrid grid = null;
        if (swap != null){
            grid = new DFDGrid(first, second, delta, 0, 0, swap.getPointsMatrixSwap(),
                    swap.getPointsMatrix());
        } else {
            grid = new DFDGrid(first, second, delta, 0, 0);
        }
        GridObject[first.index][second.index] = grid;
        endtime = System.currentTimeMillis();
        time = ((double) endtime - (double) starttime) / 1000;
        infoText.append("     PointMatrix initialized in " + time + " seconds.\n\n");
        infoText.repaint();
    }
}

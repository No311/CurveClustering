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
import Interface.VisualPanels.TrajectoryPanel;
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
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.concurrent.Flow;

public class SetTab {
    SetTrajectoryPanel visual;
    JTextArea infoText;
    ArrayList<JComponent> interactables;
    int tabnumber = 0;
    Trajectory first;
    Trajectory second;
    DFDGrid GridObject;
    GridPoint[][] pointmatrix;
    Reachability reach;
    TrajCover algo;
    SetSystemQuerier query;
    SetSystemOracle oracle;
    GeneralFunctions gF = new GeneralFunctions();
    public ArrayList<JComponent> init(String delta, JList<ListItem> firstList, JList<ListItem> secondList,
                                      int framewidth, JTabbedPane mainPane, JTextArea infoText, int method,
                                      String methodString, int reachInt, int algoInt, int queryInt,
                                      String reachString, String algoString, String queryString,
                                      int amount, ArrayList<JComponent> interactables) {
        infoText.append("     Initializing Set System Tab "+amount+"\n     Using the "+methodString+" method...\n");
        if (method == 1){
            infoText.append("     Using "+reachString+" reachability, \n     the "+algoString+" queriable and \n" +
                    "     the "+queryString+" query method...\n");
        }
        long tabstarttime = System.currentTimeMillis();

        //Initializing some variables
        this.infoText = infoText;
        this.tabnumber = amount;
        first = firstList.getSelectedValue().getT().clone();
        second = secondList.getSelectedValue().getT().clone();
        if (first.getName().equals(second.getName())){
            for (TrajPoint p: second.getPoints()){
                p.setClone();
            }
        }
        first.setSelected(true);
        second.setSelected(true);
        initMethod(delta, infoText, method, reachInt, algoInt);
        oracle = CoverageInfo(infoText, queryInt);

        JPanel setPanel = new JPanel(new BorderLayout());

        //everything coordinatePanel
        JTextField currentField = new JTextField("Current Coordinates of Trajectory 1: (x: 0, y: 0)");
        JPanel coordinatePanel = new JPanel(new BorderLayout());
        coordinatePanel.setBorder(BorderFactory.createEtchedBorder());
        currentField.setBackground(UIManager.getColor ( "Panel.background" ));
        currentField.setEditable(false);
        coordinatePanel.add(currentField, BorderLayout.LINE_START);

        //Initialization of visual
        JCheckBox showGridBox = new JCheckBox("Show Grid", true);
        JLabel gridField = new JLabel("Grid Size = 1");
        visual = new SetTrajectoryPanel(gridField, showGridBox, currentField, oracle);
        visual.addMapListeners(visual);
        visual.updateDrawables(first, second);
        visual.setOracleListeners(visual);

        //everything TrajectoryPanel
        JPanel trajectoryPanel = new JPanel();
        trajectoryPanel.setBorder(BorderFactory.createEtchedBorder());
        String firsttraj = firstList.getSelectedValue().toString();
        String secondtraj = secondList.getSelectedValue().toString();
        JTextArea trajectoryLabel = new JTextArea("Trajectories:\n" + "1: " + firsttraj
                + " (clone)\n" + "2: " + secondtraj + " (clone)\nThreshold: " + delta);
        trajectoryLabel.setBackground(UIManager.getColor ( "Panel.background" ));
        trajectoryLabel.setColumns(framewidth/72);
        trajectoryLabel.setEditable(false);
        trajectoryPanel.add(trajectoryLabel);

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
        JPanel queryPanel = new JPanel(new GridLayout(2, 1));
        queryPanel.setBorder(BorderFactory.createEtchedBorder());
        JButton queryTotalCovered = new JButton("<html><center>Total Covered<br>by Trajectory 1</center></html>");
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
        queryTotalCovered.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (TrajPoint point: second.getPoints()){
                    if (oracle.getTotalCoveredByFirst()[point.index]){
                        point.setCovered(true);
                    }
                }
                infoText.append("Tab " + tabnumber +":\n");
                infoText.append("""
                        The points covered by Trajectory 1
                        are shown on the Trajectory Panel as
                        covered.
                        
                        """);
                visual.repaint();
                visual.requestFocus();
            }
        });
        greedySetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                infoText.append("Tab " + tabnumber +":\n");
                infoText.append("Greedy Set Cover Started.\n");
                long starttime = System.currentTimeMillis();
                String lMinText = lMin.getText();
                String lMaxText = lMin.getText();
                int lMinValue = 0;
                int lMaxValue = -1;
                if (!lMinText.equals("")){
                    lMinValue = Integer.parseInt(lMinText);
                }
                if (!lMaxText.equals("")){
                    lMaxValue = Integer.parseInt(lMaxText);
                }
                visual.resetSelectedResults();
                GreedySetCover greedySetCover = new GreedySetCover();
                ArrayList<OracleResult> results = greedySetCover.doGreedySetCover(oracle, lMinValue, lMaxValue);
                visual.addSelectedResults(results);
                visual.setSelection();
                visual.repaint();
                visual.requestFocus();
                long endtime = System.currentTimeMillis();
                double time = ((double) endtime - (double) starttime)/1000;
                infoText.append("Greedy Set Cover Completed in "+time+" seconds.\n" +
                        "Result shown on Trajectory Panel.\n\n");

            }
        });
        greedySetLPanel.add(greedySetLMin);
        greedySetLPanel.add(lMin);
        greedySetLPanel.add(greedySetLMax);
        greedySetLPanel.add(lMax);
        queryPanel.add(queryTotalCovered);
        optionPanel.add(greedySetLPanel);
        queryPanel.add(greedySetButton);

        //Everything BottomPanel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(queryPanel, BorderLayout.LINE_START);
        bottomPanel.add(trajectoryPanel, BorderLayout.LINE_END);
        bottomPanel.add(optionPanel, BorderLayout.CENTER);

        //Everything SetPanel
        setPanel.add(coordinatePanel, BorderLayout.PAGE_START);
        setPanel.add(visual, BorderLayout.CENTER);
        setPanel.add(bottomPanel, BorderLayout.PAGE_END);
        //dealing with interactables
        this.interactables = interactables;

        mainPane.addTab("Set System " + amount, setPanel);
        long tabendtime = System.currentTimeMillis();
        double time = ((double) tabendtime - (double) tabstarttime)/1000;
        infoText.append("     Set System Tab "+ amount + " initialized in "+ time + " seconds.\n\n");
        infoText.repaint();
        return interactables;
    }

    private SetSystemOracle CoverageInfo(JTextArea infoText, int queryInt) {
        long endtime;
        double time;
        long starttime;
        infoText.append("     Initializing Coverage Information...\n");
        infoText.repaint();
        starttime = System.currentTimeMillis();
        switch(queryInt){
            case 1 -> {
                query = new EasyQuerier(reach, algo);
            }
            case 2 -> {
                query = new LongJumpQuerier(reach, algo);
            }
        }

        SetSystemOracle oracle = query.queryAll(first, second);
        endtime = System.currentTimeMillis();
        time = ((double) endtime - (double) starttime)/1000;
        infoText.append("     Coverage Information Initialized in "+time+" seconds.\n");
        infoText.repaint();
        return oracle;
    }

    private void initMethod(String delta, JTextArea infoText, int method, int reachInt, int algoInt) {
        long endtime;
        double time;
        long starttime;
        switch(method){
            case 1 -> {
                infoText.append("     Initializing PointMatrix for DFD Grid...\n");
                infoText.repaint();
                starttime = System.currentTimeMillis();
                GridObject = new DFDGrid(first, second, Integer.parseInt(delta), 0, 0);
                pointmatrix = GridObject.getPointsMatrix();
                endtime = System.currentTimeMillis();
                time = ((double) endtime - (double) starttime)/1000;
                infoText.append("     PointMatrix initialized in "+ time + " seconds.\n" +
                        "     Initializing Reachability and Algorithm...\n");
                infoText.repaint();
                starttime = System.currentTimeMillis();
                switch (reachInt) {
                    case 0 -> reach = null;
                    case 1 -> reach = new ReachabilityNaive();
                }
                switch (algoInt) {
                    case 0 -> algo = null;
                    case 1 -> algo = new TrajCoverNaive();
                    case 2 -> algo = new TrajCoverLog();
                }
                assert reach != null;
                assert algo != null;
                reach.preprocess(pointmatrix);
                endtime = System.currentTimeMillis();
                time = ((double) endtime - (double) starttime)/1000;
                infoText.append("     Reachability Initialized in "+time+" seconds.\n");
                starttime = System.currentTimeMillis();
                algo.preprocess(pointmatrix, reach);
                endtime = System.currentTimeMillis();
                time = ((double) endtime - (double) starttime)/1000;
                infoText.append("     Algorithm Initialized in "+time+" seconds.\n");
                infoText.repaint();
            }
        }
    }

}

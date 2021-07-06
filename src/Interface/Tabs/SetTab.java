package Interface.Tabs;

import Algorithms.GreedySetCover;
import DataStructures.SetSystemQuerier.*;
import Interface.ListItem;
import Interface.VisualPanels.SetTrajectoryPanel;
import Interface.WrapLayout;
import Objects.Trajectory;
import Methods.GeneralFunctions;
import Methods.SetSystemMethods;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class SetTab extends Tab{
    SetTrajectoryPanel visual;
    JTextArea infoText;
    ArrayList<JComponent> interactables;
    int tabnumber = 0;
    ArrayList<Trajectory> selection = new ArrayList<>();
    ArrayList<SetSystemOracle> oracles = new ArrayList<>();
    GeneralFunctions gF = new GeneralFunctions();

    public ArrayList<JComponent> init(String delta, JList<ListItem> selectionList,
                                      int framewidth, JTabbedPane mainPane, JTextArea infoText, int method,
                                      String methodString, int reachInt, int algoInt, int queryInt,
                                      String reachString, String algoString, String queryString,
                                      int amount, ArrayList<JComponent> interactables, SetSystemMethods methods) {
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
        this.oracles = methods.initSetSystem(method, Integer.parseInt(delta),
                reachInt, algoInt, queryInt, selection);

        JPanel setPanel = new JPanel(new BorderLayout());

        //everything coordinatePanel
        JTextField currentField = new JTextField("Current Coordinates: (x: 0, y: 0)");
        currentField.setBackground(UIManager.getColor("Panel.background"));
        currentField.setEditable(false);

        //Initialization of visual
        JCheckBox showGridBox = new JCheckBox("Show Grid", true);
        JLabel gridField = new JLabel("Units Per Grid Block = 1");
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
        setTitle("Set System " + amount);
        mainPane.addTab(title, setPanel);
        long tabendtime = System.currentTimeMillis();
        double time = ((double) tabendtime - (double) tabstarttime) / 1000;
        infoText.append("\u2794 Set System Tab " + amount + " initialized in " + time + " seconds.\n\n");
        infoText.repaint();
        return interactables;
    }
}

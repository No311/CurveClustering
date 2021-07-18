package Interface.Wizards;

import Algorithms.GreedySetCover;
import DataStructures.Querier.OracleResult;
import DataStructures.Querier.SetSystemOracle;
import Interface.ListItem;
import Methods.SetSystemMethods;
import Objects.NamedInt;
import Objects.Trajectory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GreedySetWizard extends OracleWizard {
    @Override
    public void init(JList<ListItem> selection, JTextArea infoText, JTabbedPane mainPane,
                     ArrayList<JComponent> interactables, int setAmount, int framewidth, SetSystemMethods methods){
        name = "Greedy Set Cover Wizard";
        super.init(selection, infoText, mainPane, interactables, setAmount, framewidth, methods);
    }

    @Override
    ArrayList<JTextField> initButtonsPanel(JPanel buttonPanel, JButton confirm, ArrayList choiceBoxes){
        confirm.setText("Do Greedy Set Cover");
        ArrayList<JTextField> result = new ArrayList<>();
        JLabel deltaLabel = new JLabel("thresholds (separated by comma): ");
        JTextField deltaField = new JTextField("");
        deltaField.setEditable(false);
        result.add(deltaField);
        JPanel greedySetLPanel = new JPanel(new FlowLayout());
        greedySetLPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                        "ℓ-values used for Subtrajectory Covering:"), BorderFactory.createEmptyBorder(0, 50, 0, 50)));
        JLabel greedySetLMin = new JLabel("min ℓ:", SwingConstants.CENTER);
        JLabel greedySetLMax = new JLabel("max ℓ:", SwingConstants.CENTER);
        JTextField lMin = new JTextField("", SwingConstants.CENTER);
        JTextField lMax = new JTextField("", SwingConstants.CENTER);
        gF.lButtonDependency(confirm, lMin, deltaField, choiceBoxes, (String s) -> s.matches("^[\\d]*$") || s.equals(""));
        gF.lButtonDependency(confirm, lMax, deltaField, choiceBoxes, (String s) -> s.matches("^[\\d]*$") || s.equals(""));
        result.add(lMin);
        result.add(lMax);
        for (JTextField t: result){
            t.setColumns(3);
        }
        greedySetLPanel.add(greedySetLMin);
        greedySetLPanel.add(lMin);
        greedySetLPanel.add(greedySetLMax);
        greedySetLPanel.add(lMax);
        buttonPanel.add(deltaLabel);
        buttonPanel.add(deltaField);
        buttonPanel.add(greedySetLPanel);
        return result;
    }

    @Override
    void initiateNaive(JList<ListItem> selectionList, JTabbedPane mainPane, JTextArea infoText,
                       int framewidth, SetSystemMethods methods, String methodString, int method, String delta,
                       ArrayList<JTextField> buttonFields) {
        infoText.append("Doing Greedy Set Cover with threshold "+delta+"\n     Using the " + methodString + " method...\n");
        long starttime = System.currentTimeMillis();
        ArrayList<Trajectory> selection = getTrajectories(selectionList);
        ArrayList<SetSystemOracle> oracles = methods.initSetSystem(method, Integer.parseInt(delta),
                -1, -1,-1,selection);
        long endtime = System.currentTimeMillis();
        long totalTime = endtime-starttime;
        double time = ((double) endtime - (double) starttime) / 1000;
        infoText.append("Method Initialized in " + time + " seconds.\n");
        initGreedySetCover(infoText, buttonFields, selection, oracles, methods, totalTime);
    }

    @Override
    void initiateFSG(JList<ListItem> selectionList, JTabbedPane mainPane, JTextArea infoText, int framewidth,
                     SetSystemMethods methods, String methodString, int method, String delta, NamedInt reachInfo,
                     NamedInt algoInfo, NamedInt queryInfo, ArrayList<JTextField> buttonFields) {
        infoText.append("Doing Greedy Set Cover with threshold "+delta+"\n     Using the " + methodString + " method...\n");
        long starttime = System.currentTimeMillis();
        if (method == 1) {
            infoText.append("     Using " + reachInfo.name + " reachability, \n     the " + algoInfo.name + " FSG Method and \n" +
                    "     the " + queryInfo.name + " query method...\n");
        }
        ArrayList<Trajectory> selection = getTrajectories(selectionList);
        ArrayList<SetSystemOracle> oracles = methods.initSetSystem(method, Integer.parseInt(delta),
                reachInfo.number, algoInfo.number, queryInfo.number, selection);
        long endtime = System.currentTimeMillis();
        long totalTime = endtime-starttime;
        double time = ((double) endtime - (double) starttime) / 1000;
        infoText.append("Method Initialized in " + time + " seconds.\n");
        infoText.append("Doing Greedy Set Cover\n     Using the " + methodString + " method...\n");

        initGreedySetCover(infoText, buttonFields, selection, oracles, methods, totalTime);
    }

    private void initGreedySetCover(JTextArea infoText, ArrayList<JTextField> buttonFields,
                                    ArrayList<Trajectory> selection, ArrayList<SetSystemOracle> oracles,
                                    SetSystemMethods methods, long totalTime) {
        int lMin = getLMin(buttonFields);
        int lMax = getLMax(buttonFields);
        methods.doGreedySetCover(infoText, selection, oracles, lMin, lMax, totalTime);
    }


    private ArrayList<Trajectory> getTrajectories(JList<ListItem> selectionList) {
        ArrayList<Trajectory> selection = new ArrayList<>();
        for (ListItem item : selectionList.getSelectedValuesList()) {
            Trajectory itemT = item.getT().clone();
            itemT.setSelected(true);
            selection.add(itemT);
        }
        return selection;
    }

    private int getLMax(ArrayList<JTextField> buttonFields) {
        JTextField lMax = buttonFields.get(2);
        String lMaxText = lMax.getText();
        int lMaxValue = -1;
        if (!lMaxText.equals("")) {
            lMaxValue = Integer.parseInt(lMaxText);
        }
        return lMaxValue;
    }

    private int getLMin(ArrayList<JTextField> buttonFields) {
        JTextField lMin = buttonFields.get(1);
        String lMinText = lMin.getText();
        int lMinValue = 0;
        if (!lMinText.equals("")) {
            lMinValue = Integer.parseInt(lMinText);
        }
        return lMinValue;
    }

}

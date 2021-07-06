package Interface.Wizards;

import Interface.ListItem;
import Interface.Tabs.GridTab;
import Interface.Tabs.Tab;
import Interface.WrapLayout;
import Methods.SetSystemMethods;
import Objects.NamedInt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class DFDGridWizard extends Wizard{


    @Override
    public void init(JList<ListItem> selectionList, JTextArea infoText, JTabbedPane mainPane,
                     ArrayList<JComponent> interactables, int gridAmount, int framewidth, SetSystemMethods methods){
        this.interactables = interactables;
        this.amount = gridAmount;
        ArrayList<JCheckBox> reachBoxes = new ArrayList<>();
        ArrayList<JCheckBox> algoBoxes = new ArrayList<>();
        ArrayList<JCheckBox> reachEnabled = new ArrayList<>();
        ArrayList<JCheckBox> algoEnabled = new ArrayList<>();

        frame = new JFrame("DFD Grid Wizard");
        JPanel backPanel = new JPanel(new BorderLayout());
        JPanel algoPanel = new JPanel(new BorderLayout());
        JPanel reachPanel = new JPanel(new BorderLayout());
        JPanel listsPanel = new JPanel(new GridLayout(1,2));
        JPanel buttonPanel = new JPanel(new WrapLayout());
        JPanel reachCheckBoxPanel = new JPanel(new WrapLayout());
        JPanel algoCheckBoxPanel = new JPanel(new WrapLayout());
        JPanel checkPanels = new JPanel(new BorderLayout());
        JPanel optionsPanel = new JPanel(new BorderLayout());

        frame.setResizable(false);
        frame.setVisible(true);
        frame.setLayout(new BorderLayout());

        //Everything FirstList
        JPanel firstListPanel = new JPanel(new BorderLayout());
        firstListPanel.setBorder(BorderFactory.createEtchedBorder());
        JTextField firstLabel = new JTextField("Choose the 1st trajectory:");
        firstLabel.setEditable(false);
        firstListPanel.add(firstLabel, BorderLayout.PAGE_START);
        JList<ListItem> firstList = new JList<>(selectionList.getModel());
        firstList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane firstScroll = new JScrollPane(firstList);
        firstListPanel.add(firstScroll, BorderLayout.CENTER);

        //Everything SecondList
        JPanel secondListPanel = new JPanel(new BorderLayout());
        secondListPanel.setBorder(BorderFactory.createEtchedBorder());
        JTextField secondLabel = new JTextField("Choose the 2nd trajectory:");
        secondLabel.setEditable(false);
        secondListPanel.add(secondLabel, BorderLayout.PAGE_START);
        JList<ListItem> secondList = new JList<>(selectionList.getModel());
        secondList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane secondScroll = new JScrollPane(secondList);
        secondListPanel.add(secondScroll, BorderLayout.CENTER);


        //Everything ReachPanel and ReachCheckBoxPanel
        JLabel reachLabel = new JLabel("Reachability to prepare:", SwingConstants.CENTER);
        JCheckBox naiveReach = new JCheckBox("Naive", false);
        naiveReach.setActionCommand("reachNaive");
        reachCheckBoxInit(naiveReach, reachBoxes, algoBoxes, reachEnabled);
        reachPanel.add(reachLabel, BorderLayout.PAGE_START);
        reachCheckBoxPanel.add(naiveReach);
        reachPanel.add(reachCheckBoxPanel, BorderLayout.CENTER);

        //Everything AlgoPanel and AlgoCheckBoxPanel
        JLabel algoLabel = new JLabel("FSG Structures to prepare:", SwingConstants.CENTER);
        JCheckBox naivePrepAlgo = new JCheckBox("Naive Prep", false);
        JCheckBox logAlgo = new JCheckBox("Log Query", false);
        JCheckBox noOptAlgo = new JCheckBox("Log Query (No Opt)", false);
        JCheckBox noPrepAlgo = new JCheckBox("Naive No Prep", false);
        JCheckBox naiveQueryAlgo = new JCheckBox("Naive Log Query", false);
        naivePrepAlgo.setActionCommand("algoNaivePrep");
        logAlgo.setActionCommand("algoLog");
        noOptAlgo.setActionCommand("algoNoOpt");
        noPrepAlgo.setActionCommand("algoNoPrep");
        naiveQueryAlgo.setActionCommand("algoNaiveLog");
        algoCheckBoxInit(naivePrepAlgo, algoBoxes, algoEnabled);
        algoCheckBoxInit(logAlgo, algoBoxes, algoEnabled);
        algoCheckBoxInit(noPrepAlgo, algoBoxes, algoEnabled);
        algoCheckBoxInit(naiveQueryAlgo, algoBoxes, algoEnabled);
        algoCheckBoxInit(noOptAlgo, algoBoxes, algoEnabled);
        algoPanel.add(algoLabel, BorderLayout.PAGE_START);
        algoCheckBoxPanel.add(naivePrepAlgo);
        algoCheckBoxPanel.add(noPrepAlgo);
        algoCheckBoxPanel.add(naiveQueryAlgo);
        algoCheckBoxPanel.add(logAlgo);
        algoCheckBoxPanel.add(noOptAlgo);
        algoPanel.add(algoCheckBoxPanel, BorderLayout.CENTER);


        //Everything ButtonPanel
        JButton confirm = new JButton("Create DFDGrid");
        JLabel deltaLabel = new JLabel("thresholds (separated by comma): ");
        JTextField deltaField = new JTextField("");
        deltaField.setEditable(false);
        deltaField.setColumns(3);
        confirm.setEnabled(false);
//        String warning = "Warning: All combinations of Reachability Structures,\n" +
//                "Data Structures and thresholds get processed to their own tab.\n" +
//                "Do not select too many structures or add too many thresholds at once.";
//        JTextArea warningLabel = new JTextArea();
//        warningLabel.setLineWrap(true);
//        warningLabel.setColumns(20);
//        warningLabel.setEditable(false);
//        warningLabel.setText(warning);
        buttonPanel.add(deltaLabel);
        buttonPanel.add(deltaField);
//        buttonPanel.add(warningLabel);
        buttonPanel.add(confirm);

        //Everything CheckPanels
        checkPanels.add(reachPanel, BorderLayout.PAGE_START);
        checkPanels.add(algoPanel, BorderLayout.PAGE_END);

        //Everything OptionsPanel
        optionsPanel.add(checkPanels, BorderLayout.PAGE_START);
        optionsPanel.add(buttonPanel, BorderLayout.PAGE_END);

        //listeners
        firstList.addListSelectionListener(e -> deltaField.setEditable(firstList.getSelectedIndices().length != 0 &&
                secondList.getSelectedIndices().length != 0));
        secondList.addListSelectionListener(e -> deltaField.setEditable(firstList.getSelectedIndices().length != 0 &&
                secondList.getSelectedIndices().length != 0));
        gF.buttonDependency(confirm, deltaField, (String s) -> !s.equals("") && s.matches("^[\\d\\s,]*$"));
        confirm.addActionListener(e -> createGridTabs(deltaField, firstList, secondList, frame, mainPane, infoText,
                reachEnabled, algoEnabled, framewidth, methods));
        listsPanel.add(firstListPanel, 0);
        listsPanel.add(secondListPanel, 1);

        backPanel.add(listsPanel);

        backPanel.add(optionsPanel, BorderLayout.PAGE_END);

        frame.add(backPanel);
        frame.pack();
    }

    private void createGridTabs(JTextField deltaField, JList<ListItem> firstList, JList<ListItem> secondList,
                                JFrame frame, JTabbedPane mainPane, JTextArea infoText,
                                ArrayList<JCheckBox> reachEnabled, ArrayList<JCheckBox> algoEnabled, int framewidth,
                                SetSystemMethods methods){
        String deltatext = deltaField.getText().replaceAll("\\s", "");
        String[] deltas = deltatext.split(",");
        if (!(firstList.getSelectedValue().getT().hasPoints() && secondList.getSelectedValue().getT().hasPoints())){
            infoText.append("     One of the trajectories is empty.\nProcess Cancelled.");
            wizardCancel = false;
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            return;
        }
        for (String delta: deltas){
            if (!reachEnabled.isEmpty()){
                for (JCheckBox reach: reachEnabled) {
                    NamedInt reachInfo = gF.getReachInfo(reach);
                    if (!algoEnabled.isEmpty()) {
                        for (JCheckBox algo: algoEnabled) {
                            NamedInt algoInfo = gF.getAlgoInfo(algo);
                            GridTab newGridTab = new GridTab();
                            interactables = newGridTab.init(delta, firstList, secondList, framewidth, mainPane,
                                    infoText, reachInfo.number, algoInfo.number, reachInfo.name, algoInfo.name, amount, interactables,
                                    methods);
                            amount++;
                            tabs.add(newGridTab);
                        }
                    } else {
                        GridTab newGridTab = new GridTab();
                        interactables = newGridTab.init(delta, firstList, secondList, framewidth, mainPane, infoText,
                                reachInfo.number, 0, reachInfo.name, "no", amount, interactables, methods);
                        amount++;
                        tabs.add(newGridTab);
                    }
                }
            } else {
                GridTab newGridTab = new GridTab();
                interactables = newGridTab.init(delta, firstList, secondList, framewidth, mainPane, infoText,
                        0, 0, "no", "no", amount, interactables, methods);
                amount++;
                tabs.add(newGridTab);
            }
        }
        for (Tab g : tabs) {
            GridTab ag = (GridTab) g;
            ag.updateInteractables(interactables);
        }
        wizardCancel = false;
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }
    private void reachCheckBoxInit(JCheckBox checkBox, ArrayList<JCheckBox> reachBoxes,
                                   ArrayList<JCheckBox> algoBoxes, ArrayList<JCheckBox> reachEnabled){
        reachBoxes.add(checkBox);
        checkBox.addActionListener(e -> {
            if (checkBox.isSelected()){
                reachEnabled.add(checkBox);
            } else {
                reachEnabled.remove(checkBox);
            }

            if (reachEnabled.isEmpty()){
                for (JCheckBox algoBox: algoBoxes){
                    algoBox.setSelected(false);
                    algoBox.setEnabled(false);
                }
            } else {
                for (JCheckBox algoBox: algoBoxes){
                    algoBox.setEnabled(true);
                }
            }
        });
    }

    private void algoCheckBoxInit(JCheckBox checkBox, ArrayList<JCheckBox> algoBoxes, ArrayList<JCheckBox> algoEnabled){
        algoBoxes.add(checkBox);
        checkBox.setEnabled(false);
        checkBox.addActionListener(e -> {
            if (checkBox.isSelected()){
                algoEnabled.add(checkBox);
            } else {
                algoEnabled.remove(checkBox);
            }
        });
    }


}

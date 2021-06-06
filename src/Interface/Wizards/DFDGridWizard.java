package Interface.Wizards;

import Interface.GeneralFunctions;
import Interface.ListItem;
import Interface.Tabs.GridTab;
import Interface.WrapLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class DFDGridWizard {
    ArrayList<JComponent> interactables;
    int amount;
    ArrayList<GridTab> tabs = new ArrayList<>();
    boolean wizardCancel = true;
    GeneralFunctions gF = new GeneralFunctions();
    JFrame frame;
    public void init(JList<ListItem> selectionList, JTextArea infoText, JTabbedPane mainPane,
                     ArrayList<JComponent> interactables, int gridAmount, int framewidth){
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
        JLabel algoLabel = new JLabel("Data Structures to prepare:", SwingConstants.CENTER);
        JCheckBox naiveAlgo = new JCheckBox("Naive", false);
        JCheckBox logAlgo = new JCheckBox("Log Query", false);
        naiveAlgo.setActionCommand("algoNaive");
        logAlgo.setActionCommand("algoLog");
        algoCheckBoxInit(naiveAlgo, algoBoxes, algoEnabled);
        algoCheckBoxInit(logAlgo, algoBoxes, algoEnabled);
        algoPanel.add(algoLabel, BorderLayout.PAGE_START);
        algoCheckBoxPanel.add(naiveAlgo);
        algoCheckBoxPanel.add(logAlgo);
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
                reachEnabled, algoEnabled, framewidth));
        listsPanel.add(firstListPanel, 0);
        listsPanel.add(secondListPanel, 1);

        backPanel.add(listsPanel);

        backPanel.add(optionsPanel, BorderLayout.PAGE_END);

        frame.add(backPanel);
        frame.pack();
    }

    private void createGridTabs(JTextField deltaField, JList<ListItem> firstList, JList<ListItem> secondList,
                                JFrame frame, JTabbedPane mainPane, JTextArea infoText,
                                ArrayList<JCheckBox> reachEnabled, ArrayList<JCheckBox> algoEnabled, int framewidth){
        String deltatext = deltaField.getText().replaceAll("\\s", "");
        String[] deltas = deltatext.split(",");
        if (!(firstList.getSelectedValue().getT().hasPoints() && secondList.getSelectedValue().getT().hasPoints())){
            infoText.append("     One of the trajectories is empty.\nProcess Cancelled.");
            wizardCancel = false;
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            return;
        }

        for (String delta: deltas){
            String reachString = "no";
            String algoString = "no";
            if (!reachEnabled.isEmpty()){
                for (JCheckBox reach: reachEnabled) {
                    int reachInt = 0;
                    switch (reach.getActionCommand()) {
                        case "reachNaive" -> {
                            reachInt = 1;
                            reachString = "naive";
                        }
                    }
                    if (!algoEnabled.isEmpty()) {
                        for (JCheckBox algo: algoEnabled) {
                            int algoInt = 0;
                            switch (algo.getActionCommand()) {
                                case "algoNaive" -> {
                                    algoInt = 1;
                                    algoString = "naive";
                                }
                                case "algoLog" -> {
                                    algoInt = 2;
                                    algoString = "Log Query";
                                }
                            }
                            GridTab newGridTab = new GridTab();
                            interactables = newGridTab.init(delta, firstList, secondList, framewidth, mainPane,
                                    infoText, reachInt, algoInt, reachString, algoString, amount, interactables);
                            amount++;
                            tabs.add(newGridTab);
                        }
                    } else {
                        GridTab newGridTab = new GridTab();
                        interactables = newGridTab.init(delta, firstList, secondList, framewidth, mainPane, infoText,
                                reachInt, 0, reachString, algoString, amount, interactables);
                        amount++;
                        tabs.add(newGridTab);
                    }
                }
            } else {
                GridTab newGridTab = new GridTab();
                interactables = newGridTab.init(delta, firstList, secondList, framewidth, mainPane, infoText,
                        0, 0, reachString, algoString, amount, interactables);
                amount++;
                tabs.add(newGridTab);
            }
        }
        for (GridTab g : tabs) {
            g.updateInteractables(interactables);
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

    public ArrayList<JComponent> getInteractables() {
        return interactables;
    }

    public int getAmount() {
        return amount;
    }

    public ArrayList<GridTab> getTabs() {
        return tabs;
    }

    public boolean isWizardCancel() {
        return wizardCancel;
    }

    public JFrame getFrame() {
        return frame;
    }
}

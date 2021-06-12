package Interface.Wizards;

import Interface.GeneralFunctions;
import Interface.ListItem;
import Interface.Tabs.GridTab;
import Interface.Tabs.SetTab;
import Interface.VisualPanels.TrajectoryPanel;
import Interface.WrapLayout;
import Objects.Trajectory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class SetSystemWizard {
    ArrayList<JComponent> interactables;
    int amount;
    ArrayList<SetTab> tabs = new ArrayList<>();
    boolean wizardCancel = true;
    GeneralFunctions gF = new GeneralFunctions();
    JFrame frame;

    public void init(JList<ListItem> selection, JTextArea infoText, JTabbedPane mainPane,
                     ArrayList<JComponent> interactables, int setAmount, int framewidth){
        this.interactables = interactables;
        this.amount = setAmount;
        ArrayList<JCheckBox> reachBoxes = new ArrayList<>();
        ArrayList<JCheckBox> algoBoxes = new ArrayList<>();
        ArrayList<JCheckBox> queryBoxes = new ArrayList<>();
        ArrayList<JCheckBox> reachEnabled = new ArrayList<>();
        ArrayList<JCheckBox> algoEnabled = new ArrayList<>();
        ArrayList<JCheckBox> queryEnabled = new ArrayList<>();
        ArrayList<JCheckBox> NaiveBoxes = new ArrayList<>();
        ArrayList<JCheckBox> choiceBoxes = new ArrayList<>();

        frame = new JFrame("Set System Wizard");
        JPanel backPanel = new JPanel(new BorderLayout());
        JPanel algoPanel = new JPanel(new BorderLayout());
        JPanel reachPanel = new JPanel(new BorderLayout());
        JPanel queryPanel = new JPanel(new BorderLayout());
        JPanel listsPanel = new JPanel(new GridLayout(1,1));
        JPanel methodWrapper = new JPanel(new BorderLayout());
        JPanel methodPanel = new JPanel(new GridLayout(2, 1));
        JPanel NaivePanel = new JPanel(new BorderLayout());
        JPanel DFDPanel = new JPanel(new GridLayout(2, 1));
        JPanel DFDMainPanel = new JPanel(new BorderLayout());
        JPanel DFDOptionsPanel = new JPanel(new GridLayout(1, 3));
        JPanel reachCheckBoxPanel = new JPanel(new FlowLayout());
        JPanel algoCheckBoxPanel = new JPanel(new FlowLayout());
        JPanel queryCheckBoxPanel = new JPanel(new FlowLayout());

        JPanel buttonPanel = new JPanel(new WrapLayout());
        JPanel optionsPanel = new JPanel(new BorderLayout());

        frame.setResizable(false);
        frame.setVisible(true);
        frame.setLayout(new BorderLayout());

        //Everything SelectionList
        JPanel selectionListPanel = new JPanel(new BorderLayout());
        selectionListPanel.setBorder(BorderFactory.createEtchedBorder());
        JTextField selectionLabel = new JTextField("Choose the trajectories:");
        selectionLabel.setEditable(false);
        selectionListPanel.add(selectionLabel, BorderLayout.PAGE_START);
        JList<ListItem> selectionList = new JList<>(selection.getModel());
        selectionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane selectionScroll = new JScrollPane(selectionList);
        selectionListPanel.add(selectionScroll, BorderLayout.CENTER);

        //Everything NaiveMethod
        NaivePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK),"Naive Method"));
        JCheckBox nMethodBox = new JCheckBox("Use the Naive Method");
        nMethodBox.setHorizontalAlignment(SwingConstants.CENTER);
        choiceBoxes.add(nMethodBox);
        NaivePanel.add(nMethodBox);

        //Everything ReachPanel and ReachCheckBoxPanel
        JLabel reachLabel = new JLabel("Reachability to prepare:", SwingConstants.CENTER);
        JCheckBox naiveReach = new JCheckBox("Naive", false);
        naiveReach.setEnabled(false);
        naiveReach.setActionCommand("reachNaive");
        reachCheckBoxInit(naiveReach, reachBoxes, algoBoxes, queryBoxes, reachEnabled, algoEnabled, queryEnabled);
        reachPanel.add(reachLabel, BorderLayout.PAGE_START);
        reachCheckBoxPanel.add(naiveReach);
        reachPanel.add(reachCheckBoxPanel, BorderLayout.CENTER);
        reachPanel.setBorder(BorderFactory.createEtchedBorder());

        //Everything AlgoPanel and AlgoCheckBoxPanel
        JLabel algoLabel = new JLabel("Data Structure to prepare:", SwingConstants.CENTER);
        JCheckBox naiveAlgo = new JCheckBox("Naive", false);
        JCheckBox logAlgo = new JCheckBox("Log Query", false);
        naiveAlgo.setActionCommand("algoNaive");
        logAlgo.setActionCommand("algoLog");
        algoCheckBoxInit(naiveAlgo, algoBoxes, queryBoxes, algoEnabled, queryEnabled);
        algoCheckBoxInit(logAlgo, algoBoxes, queryBoxes, algoEnabled, queryEnabled);
        algoPanel.add(algoLabel, BorderLayout.PAGE_START);
        algoCheckBoxPanel.add(naiveAlgo);
        algoCheckBoxPanel.add(logAlgo);
        algoPanel.add(algoCheckBoxPanel, BorderLayout.CENTER);
        algoPanel.setBorder(BorderFactory.createEtchedBorder());

        //Everything QueryPanel and QueryCheckBoxPanel
        JLabel queryLabel = new JLabel("Query Strategy to Use:", SwingConstants.CENTER);
        JCheckBox naiveQuery = new JCheckBox("Naive", false);
        JCheckBox longjumpQuery = new JCheckBox("Long Jump", false);
        naiveQuery.setActionCommand("queryNaive");
        longjumpQuery.setActionCommand("queryLongjump");
        queryCheckBoxInit(naiveQuery, queryBoxes, queryEnabled);
        queryCheckBoxInit(longjumpQuery, queryBoxes, queryEnabled);
        queryPanel.add(queryLabel, BorderLayout.PAGE_START);
        queryCheckBoxPanel.add(naiveQuery);
        queryCheckBoxPanel.add(longjumpQuery);
        queryPanel.add(queryCheckBoxPanel, BorderLayout.CENTER);
        queryPanel.setBorder(BorderFactory.createEtchedBorder());

        //Everything DFDOptionsPanel
        DFDOptionsPanel.add(reachPanel);
        DFDOptionsPanel.add(algoPanel);
        DFDOptionsPanel.add(queryPanel);
        DFDOptionsPanel.setBorder(BorderFactory.createEtchedBorder());

        //Everything DFDMainPanel
        JCheckBox DFDMethodBox = new JCheckBox("Use the DFD Method");
        ArrayList<ArrayList<JCheckBox>> dependlist = new ArrayList<>();
        dependlist.add(reachBoxes);
        dependlist.add(algoBoxes);
        dependlist.add(queryBoxes);
        ArrayList<ArrayList<JCheckBox>> enablelist = new ArrayList<>();
        enablelist.add(reachBoxes);
        methodCheckBoxInit(DFDMethodBox, enablelist, dependlist);
        choiceBoxes.addAll(queryBoxes);
        DFDMethodBox.setHorizontalAlignment(SwingConstants.CENTER);
        DFDMainPanel.add(DFDMethodBox);
        DFDMainPanel.setBorder(BorderFactory.createEtchedBorder());

        //Everything DFDMethod
        DFDPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK),"The Discrete Fréchet Distance Method:"));
        DFDPanel.add(DFDMainPanel, 0);
        DFDPanel.add(DFDOptionsPanel, 1);

        //Everything MethodPanel
        methodPanel.add(NaivePanel);
        methodPanel.add(DFDPanel);

        //Everything methodWrapper
        JLabel methodLabel = new JLabel("Choose the Method");
        methodWrapper.add(methodLabel, BorderLayout.PAGE_START);
        methodWrapper.add(methodPanel);

        //Everything ButtonPanel
        JButton confirm = new JButton("Initialize Set System");
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

        optionsPanel.add(methodPanel, BorderLayout.PAGE_START);
        optionsPanel.add(buttonPanel, BorderLayout.PAGE_END);

        //listeners
        selectionList.addListSelectionListener(e -> deltaField.setEditable(selectionList.getSelectedIndices().length != 0));
        gF.buttonChoiceDependency(confirm, deltaField, (String s) -> !s.equals("") && s.matches("^[\\d\\s,]*$"),
                choiceBoxes);
        confirm.addActionListener(e -> createSetTabs(deltaField, selectionList,
                nMethodBox, NaiveBoxes, DFDMethodBox, frame, mainPane, infoText,
                reachEnabled, algoEnabled, queryEnabled, framewidth));
        listsPanel.add(selectionListPanel);

        backPanel.add(listsPanel);

        backPanel.add(optionsPanel, BorderLayout.PAGE_END);

        frame.add(backPanel);
        frame.pack();
    }

    private void createSetTabs(JTextField deltaField, JList<ListItem> selectionList,
                               JCheckBox nMethodBox, ArrayList<JCheckBox> naiveBoxes, JCheckBox DFDMethodBox,
                               JFrame frame, JTabbedPane mainPane, JTextArea infoText,
                               ArrayList<JCheckBox> reachEnabled, ArrayList<JCheckBox> algoEnabled,
                               ArrayList<JCheckBox> queryEnabled, int framewidth) {
        String deltatext = deltaField.getText().replaceAll("\\s", "");
        String[] deltas = deltatext.split(",");
        if (!(selectionList.getSelectedValue().getT().hasPoints())){
            infoText.append("     No selected trajectories.\nProcess Cancelled.");
            wizardCancel = false;
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            return;
        }
        String methodString = "";
        int method = 0;
        if (nMethodBox.isSelected()){
            methodString = "Naive";
            for (String delta: deltas){
                SetTab newSetTab = new SetTab();
                interactables = newSetTab.init(delta, selectionList, framewidth, mainPane,
                        infoText, method, methodString, -1, -1, -1, "",
                        "", "", amount, interactables);
                amount++;
                tabs.add(newSetTab);
            }
        }
        if (DFDMethodBox.isSelected()) {
            method = 1;
            methodString = "DFD";
            for (String delta : deltas) {
                String reachString = "no";
                String algoString = "no";
                String queryString = "no";
                if (!reachEnabled.isEmpty()) {
                    for (JCheckBox reach : reachEnabled) {
                        int reachInt = 0;
                        switch (reach.getActionCommand()) {
                            case "reachNaive" -> {
                                reachInt = 1;
                                reachString = "naive";
                            }
                        }
                        if (!algoEnabled.isEmpty()) {
                            for (JCheckBox algo : algoEnabled) {
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
                                if (!queryEnabled.isEmpty()) {
                                    for (JCheckBox query : queryEnabled) {
                                        int queryInt = 0;
                                        switch (query.getActionCommand()) {
                                            case "queryNaive" -> {
                                                queryInt = 1;
                                                queryString = "naive";
                                            }
                                            case "queryLongjump" -> {
                                                queryInt = 2;
                                                queryString = "Long Jump";
                                            }
                                        }
                                        SetTab newSetTab = new SetTab();
                                        interactables = newSetTab.init(delta, selectionList, framewidth, mainPane,
                                                infoText, method, methodString, reachInt, algoInt, queryInt, reachString,
                                                algoString, queryString, amount, interactables);
                                        amount++;
                                        tabs.add(newSetTab);
                                    }
                                }
                            }
                        } else {
                            infoText.append("Set System could not be created:\n no algorithm selected.\n\n");
                        }
                    }
                } else {
                    infoText.append("Set System could not be created:\n no reachability selected.\n\n");
                }
            }
        }
        wizardCancel = false;
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

    private void methodCheckBoxInit(JCheckBox methodEnableBox, ArrayList<ArrayList<JCheckBox>> enableBoxes,
                                 ArrayList<ArrayList<JCheckBox>> dependBoxes) {
        methodEnableBox.addActionListener(e -> {
            if (methodEnableBox.isSelected()){
                for (ArrayList<JCheckBox> enable: enableBoxes) {
                    for (JCheckBox box : enable) {
                        box.setEnabled(true);
                    }
                }
            } else {
                for (ArrayList<JCheckBox> depend: dependBoxes) {
                    for (JCheckBox box : depend) {
                        box.setEnabled(false);
                        box.setSelected(false);
                    }
                }
            }
        });
    }

    private void reachCheckBoxInit(JCheckBox checkBox, ArrayList<JCheckBox> reachBoxes,
                                   ArrayList<JCheckBox> algoBoxes,
                                   ArrayList<JCheckBox> queryBoxes,
                                   ArrayList<JCheckBox> reachEnabled,
                                   ArrayList<JCheckBox> algoEnabled,
                                   ArrayList<JCheckBox> queryEnabled){
        reachBoxes.add(checkBox);
        checkBox.addActionListener(e -> {
            if (checkBox.isSelected()){
                reachEnabled.add(checkBox);
                for (JCheckBox algoBox: algoBoxes){
                    algoBox.setEnabled(true);
                }
            } else {
                reachEnabled.remove(checkBox);
                if (reachEnabled.isEmpty()){
                    algoEnabled.removeAll(algoBoxes);
                    queryEnabled.removeAll(queryBoxes);
                    for (JCheckBox algoBox: algoBoxes){
                        algoBox.setSelected(false);
                        algoBox.setEnabled(false);
                    }
                    for (JCheckBox queryBox: queryBoxes){
                        queryBox.setSelected(false);
                        queryBox.setEnabled(false);
                    }
                }
            }
        });
    }

    private void algoCheckBoxInit(JCheckBox checkBox, ArrayList<JCheckBox> algoBoxes, ArrayList<JCheckBox> queryBoxes,
                                  ArrayList<JCheckBox> algoEnabled, ArrayList<JCheckBox> queryEnabled){
        algoBoxes.add(checkBox);
        checkBox.setEnabled(false);
        String actionCommand = checkBox.getActionCommand();
        checkBox.addActionListener(e -> {
            if (checkBox.isSelected()){
                algoEnabled.add(checkBox);
                for (JCheckBox queryBox: queryBoxes){
                    queryBox.setEnabled(true);
                }
            } else {
                algoEnabled.remove(checkBox);
                if (algoEnabled.isEmpty()){
                    queryEnabled.removeAll(queryBoxes);
                    for (JCheckBox queryBox: queryBoxes){
                        queryBox.setSelected(false);
                        queryBox.setEnabled(false);
                    }
                }
            }
        });

    }

    private void queryCheckBoxInit(JCheckBox checkBox, ArrayList<JCheckBox> queryBoxes,
                                   ArrayList<JCheckBox> queryEnabled){
        queryBoxes.add(checkBox);
        checkBox.setEnabled(false);
        checkBox.addActionListener(e -> {
            if (checkBox.isSelected()){
                queryEnabled.add(checkBox);
            } else {
                queryEnabled.remove(checkBox);
            }
        });
    }

    public ArrayList<JComponent> getInteractables() {
        return interactables;
    }

    public int getAmount() {
        return amount;
    }

    public ArrayList<SetTab> getTabs() {
        return tabs;
    }

    public boolean isWizardCancel() {
        return wizardCancel;
    }

    public JFrame getFrame() {
        return frame;
    }

}

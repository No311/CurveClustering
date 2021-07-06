package Interface.Wizards;

import Interface.GeneralFunctions;
import Interface.ListItem;
import Interface.Tabs.SetTab;
import Interface.Tabs.Tab;
import Interface.WrapLayout;
import Methods.SetSystemMethods;
import Objects.NamedInt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class SetSystemWizard extends Wizard{
    @Override
    public void init(JList<ListItem> selection, JTextArea infoText, JTabbedPane mainPane,
                     ArrayList<JComponent> interactables, int setAmount, int framewidth, SetSystemMethods methods){
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
        JPanel methodPanel = new JPanel(new BorderLayout());
        JPanel NaivePanel = new JPanel(new BorderLayout());
        JPanel DFDPanel = new JPanel(new BorderLayout());
        JPanel DFDMainPanel = new JPanel(new BorderLayout());
        JPanel DFDOptionsPanel = new JPanel(new GridLayout(1, 3));
        JPanel reachCheckBoxPanel = new JPanel(new FlowLayout());
        JPanel algoCheckBoxPanel = new JPanel(new GridLayout(3,1));
        JPanel algoCheckBoxPanel1 = new JPanel(new FlowLayout());
        JPanel algoCheckBoxPanel2 = new JPanel(new FlowLayout());
        JPanel algoCheckBoxPanel3 = new JPanel(new FlowLayout());
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
        algoCheckBoxInit(naivePrepAlgo, algoBoxes, queryBoxes, algoEnabled, queryEnabled);
        algoCheckBoxInit(logAlgo, algoBoxes, queryBoxes, algoEnabled, queryEnabled);
        algoCheckBoxInit(noOptAlgo, algoBoxes, queryBoxes, algoEnabled, queryEnabled);
        algoCheckBoxInit(noPrepAlgo, algoBoxes, queryBoxes, algoEnabled, queryEnabled);
        algoCheckBoxInit(naiveQueryAlgo, algoBoxes, queryBoxes, algoEnabled, queryEnabled);
        algoPanel.add(algoLabel, BorderLayout.PAGE_START);
        algoCheckBoxPanel1.add(naivePrepAlgo);
        algoCheckBoxPanel1.add(noPrepAlgo);
        algoCheckBoxPanel2.add(naiveQueryAlgo);
        algoCheckBoxPanel2.add(logAlgo);
        algoCheckBoxPanel3.add(noOptAlgo);
        algoCheckBoxPanel.add(algoCheckBoxPanel1);
        algoCheckBoxPanel.add(algoCheckBoxPanel2);
        algoCheckBoxPanel.add(algoCheckBoxPanel3);
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
                BorderFactory.createLineBorder(Color.BLACK),"The Free Space Grid Method:"));
        DFDPanel.add(DFDMainPanel, BorderLayout.PAGE_START);
        DFDPanel.add(DFDOptionsPanel, BorderLayout.CENTER);

        //Everything MethodPanel
        methodPanel.add(NaivePanel, BorderLayout.PAGE_START);
        methodPanel.add(DFDPanel, BorderLayout.CENTER);

        //Everything methodWrapper
        JLabel methodLabel = new JLabel("Choose the Method");
        methodWrapper.add(methodLabel, BorderLayout.PAGE_START);
        methodWrapper.add(methodPanel);

        //Everything ButtonPanel
        JButton confirm = new JButton();
        ArrayList<JTextField> buttonFields = initButtonsPanel(buttonPanel, confirm, choiceBoxes);
        confirm.setEnabled(false);
        buttonPanel.add(confirm);
        gF.buttonChoiceDependency(confirm, buttonFields.get(0), (String s) -> !s.equals("") && s.matches("^[\\d\\s,]*$"),
                choiceBoxes);
        ConfirmActionListener(infoText, mainPane, framewidth,
                methods, reachEnabled, algoEnabled, queryEnabled,
                NaiveBoxes, selectionList, nMethodBox, DFDMethodBox,
                buttonFields, confirm);


        optionsPanel.add(methodPanel, BorderLayout.PAGE_START);
        optionsPanel.add(buttonPanel, BorderLayout.PAGE_END);

        //listeners
        selectionList.addListSelectionListener(e -> buttonFields.get(0).
                setEditable(selectionList.getSelectedIndices().length != 0));

        listsPanel.add(selectionListPanel);

        backPanel.add(listsPanel);

        backPanel.add(optionsPanel, BorderLayout.PAGE_END);

        frame.add(backPanel);
        frame.pack();
    }

    void ConfirmActionListener(JTextArea infoText, JTabbedPane mainPane, int framewidth,
                                       SetSystemMethods methods, ArrayList<JCheckBox> reachEnabled,
                                       ArrayList<JCheckBox> algoEnabled, ArrayList<JCheckBox> queryEnabled,
                                       ArrayList<JCheckBox> NaiveBoxes, JList<ListItem> selectionList,
                                       JCheckBox nMethodBox, JCheckBox DFDMethodBox, ArrayList<JTextField> buttonFields,
                                       JButton confirm) {
        confirm.addActionListener(e -> initiate(selectionList,
                nMethodBox, NaiveBoxes, DFDMethodBox, frame, mainPane, infoText,
                reachEnabled, algoEnabled, queryEnabled, framewidth, methods, buttonFields));
    }

    ArrayList<JTextField> initButtonsPanel(JPanel buttonPanel, JButton confirm, ArrayList<JCheckBox> choiceBoxes){
        confirm.setText("Initialize Set System");
        ArrayList<JTextField> result = new ArrayList<>();
        JLabel deltaLabel = new JLabel("thresholds (separated by comma): ");
        JTextField deltaField = new JTextField("");
        deltaField.setEditable(false);
        deltaField.setColumns(3);
        result.add(deltaField);
        buttonPanel.add(deltaLabel);
        buttonPanel.add(deltaField);
        return result;
    }


    private void initiate(JList<ListItem> selectionList, JCheckBox nMethodBox, ArrayList<JCheckBox> naiveBoxes,
                          JCheckBox DFDMethodBox, JFrame frame, JTabbedPane mainPane, JTextArea infoText,
                          ArrayList<JCheckBox> reachEnabled, ArrayList<JCheckBox> algoEnabled,
                          ArrayList<JCheckBox> queryEnabled, int framewidth, SetSystemMethods methods,
                          ArrayList<JTextField> buttonFields) {
        String deltatext = buttonFields.get(0).getText().replaceAll("\\s", "");
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
                initiateNaive(selectionList, mainPane, infoText, framewidth, methods, methodString, method, delta, buttonFields);
            }
        }
        if (DFDMethodBox.isSelected()) {
            method = 1;
            methodString = "DFD";
            for (String delta : deltas) {
                if (!reachEnabled.isEmpty()) {
                    for (JCheckBox reach : reachEnabled) {
                        NamedInt reachInfo = gF.getReachInfo(reach);
                        if (!algoEnabled.isEmpty()) {
                            for (JCheckBox algo : algoEnabled) {
                                NamedInt algoInfo = gF.getAlgoInfo(algo);
                                if (!queryEnabled.isEmpty()) {
                                    for (JCheckBox query : queryEnabled) {
                                        NamedInt queryInfo = gF.getQueryInfo(query);
                                        initiateFSG(selectionList, mainPane, infoText, framewidth, methods, methodString, method, delta, reachInfo, algoInfo, queryInfo, buttonFields);
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
        System.gc();
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

    void initiateFSG(JList<ListItem> selectionList, JTabbedPane mainPane, JTextArea infoText, int framewidth,
                     SetSystemMethods methods, String methodString, int method, String delta, NamedInt reachInfo,
                     NamedInt algoInfo, NamedInt queryInfo, ArrayList<JTextField> buttonFields) {
        SetTab newSetTab = new SetTab();
        interactables = newSetTab.init(delta, selectionList, framewidth, mainPane,
                infoText, method, methodString, reachInfo.number, algoInfo.number, queryInfo.number,
                reachInfo.name, algoInfo.name, queryInfo.name, amount, interactables, methods);
        amount++;
        tabs.add(newSetTab);
    }

    void initiateNaive(JList<ListItem> selectionList, JTabbedPane mainPane, JTextArea infoText,
                       int framewidth, SetSystemMethods methods, String methodString, int method, String delta,
                       ArrayList<JTextField> buttonFields) {
        SetTab newSetTab = new SetTab();
        interactables = newSetTab.init(delta, selectionList, framewidth, mainPane,
                infoText, method, methodString, -1, -1, -1, "",
                "", "", amount, interactables, methods);
        amount++;
        tabs.add(newSetTab);
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

}

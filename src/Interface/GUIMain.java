package Interface;
import Algorithms.Simplification;
import Interface.Tabs.GridTab;
import Interface.Tabs.SetTab;
import Interface.VisualPanels.TrajectoryPanel;
import Interface.Wizards.DFDGridWizard;
import Interface.GeneralFunctions;
import Interface.Wizards.SetSystemWizard;
import Objects.Trajectory;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GUIMain {
    boolean wizardCancel = false;
    ArrayList<Trajectory> loadedTrajectories = new ArrayList<>();
    ArrayList<Trajectory> selectedTrajectories = new ArrayList<>();
    Trajectory editable = null;
    ArrayList<ListItem> ListData = new ArrayList<>();
    ArrayList<GridTab> gridTabs = new ArrayList<>();
    ArrayList<SetTab> setTabs = new ArrayList<>();
    ArrayList<JComponent> interactables = new ArrayList<>();
    GeneralFunctions gF = new GeneralFunctions();
    Simplification simple = new Simplification();
    private int gridAmount = 0;
    private int setAmount = 0;
    private int framewidth = 800;
    private int frameheight = 800;

    public void init(){
        //initialization
        JFrame frame = new JFrame("CurveClustering");
        JPanel backPanel = new JPanel(new BorderLayout());
        JTabbedPane mainPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        JPanel mapPanel = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel buttonsPanel = new JPanel(new GridLayout(2,1));
        JPanel mapButtonsPanel = new JPanel(new WrapLayout());
        JPanel funcButtonsPanel = new JPanel(new WrapLayout());
        JPanel trajButtonsPanel = new JPanel(new WrapLayout());
        JPanel muButtonsPanel = new JPanel(new WrapLayout());
        JPanel panelPanel = new JPanel(new BorderLayout());
        JPanel selectionPanel = new JPanel(new BorderLayout());
        JPanel editPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel infoPanel = new JPanel(new BorderLayout());

        //initialization of Map
        JCheckBox editBox = new JCheckBox("Editing");
        editBox.setEnabled(false);
        JLabel gridField = new JLabel("Grid Size = 1");
        JCheckBox showGridBox = new JCheckBox("Show Grid", true);
        JTextField currentField = new JTextField("Current Coordinates: (x: 0, y: 0)");
        TrajectoryPanel map = new TrajectoryPanel(gridField, showGridBox, currentField, editBox);

        //everything frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setVisible(true);
        frame.setLayout(new BorderLayout());
        frame.setMinimumSize(new Dimension(framewidth,frameheight));

        //everything to do with selections
        selectionPanel.setBorder(BorderFactory.createEtchedBorder());
        JTextField selectionLabel = new JTextField("Selected Trajectories:");
        selectionLabel.setEditable(false);
        selectionPanel.add(selectionLabel, BorderLayout.PAGE_START);
        JList<ListItem> selectionList = new JList<>(new ListItem[]{});
        selectionListInit(map, selectionList);
        JScrollPane selectionScroll = new JScrollPane(selectionList);
        selectionScroll.setPreferredSize(new Dimension(0, 100));
        selectionPanel.add(selectionScroll, BorderLayout.PAGE_END);

        //everything to do with editables
        editPanel.setBorder(BorderFactory.createEtchedBorder());
        JTextField editLabel = new JTextField("Editable Trajectory:");
        editLabel.setEditable(false);
        editPanel.add(editLabel, BorderLayout.PAGE_START);
        JList<ListItem> editList = new JList<>(new ListItem[]{});
        editListInit(map, editList, editBox);
        JScrollPane editScroll = new JScrollPane(editList);
        editScroll.setPreferredSize(new Dimension(0, 100));
        editPanel.add(editScroll, BorderLayout.PAGE_END);

        //everything panelPanel
        panelPanel.add(selectionPanel, BorderLayout.LINE_START);
        panelPanel.add(editPanel, BorderLayout.LINE_END);

        //everything infoPanel
        infoPanel.setBorder(BorderFactory.createEtchedBorder());
        JTextArea infoText = new JTextArea();
        JScrollPane infoPane = new JScrollPane(infoText);
        infoPane.getVerticalScrollBar().setUnitIncrement(32);
        infoPane.getVerticalScrollBar().setPreferredSize(new Dimension(0,0));
        infoPane.setWheelScrollingEnabled(true);
        infoText.setEditable(false);
        infoPanel.add(infoPane);

        //everything buttons and bottom panel not done during initialization of Map
        JLabel muLabel = new JLabel("mu");
        JTextField muField = new JTextField("1");
        muField.setColumns(3);
        JButton simplifyButton = new JButton("Simplify");
        JSlider sizeSlider = new JSlider(1, 100, 10);
        JLabel sliderLabel = new JLabel("Draw Size: 10");


        //everything topPanel
        topPanel.setBorder(BorderFactory.createEtchedBorder());
        JMenuBar menu = MenuInit(infoText, frame, mainPane, map, selectionList, editList, muField, simplifyButton,
                sizeSlider, sliderLabel);
        topPanel.add(menu, BorderLayout.LINE_START);

        //everything coordinatePanel
        JPanel coordinatePanel = new JPanel(new BorderLayout());
        coordinatePanel.setBorder(BorderFactory.createEtchedBorder());
        currentField.setBackground(UIManager.getColor ( "Panel.background" ));
        currentField.setEditable(false);
        coordinatePanel.add(currentField, BorderLayout.LINE_START);

        //listeners
        map.addMapListeners(map);
        addButtonListeners(frame, map, sliderLabel, selectionList, editList,
                infoText, muField, simplifyButton, sizeSlider);

        //adding components
        mapButtonsPanel.setBorder(BorderFactory.createEtchedBorder());
        funcButtonsPanel.setBorder(BorderFactory.createEtchedBorder());
        trajButtonsPanel.setBorder(BorderFactory.createEtchedBorder());
        muButtonsPanel.setBorder(BorderFactory.createEtchedBorder());
        buttonsPanel.setBorder(BorderFactory.createEtchedBorder());
        mapButtonsPanel.add(sliderLabel);
        mapButtonsPanel.add(sizeSlider);
        mapButtonsPanel.add(gridField);
        mapButtonsPanel.add(showGridBox);
        muButtonsPanel.add(muLabel);
        muButtonsPanel.add(muField);
        muButtonsPanel.add(simplifyButton);
        trajButtonsPanel.add(editBox);
        funcButtonsPanel.add(muButtonsPanel);
        funcButtonsPanel.add(trajButtonsPanel);
        buttonsPanel.add(mapButtonsPanel);
        buttonsPanel.add(funcButtonsPanel);
        bottomPanel.add(panelPanel, BorderLayout.LINE_END);
        bottomPanel.add(buttonsPanel);

        //everything mapPanel
        mapPanel.add(currentField, BorderLayout.PAGE_START);
        mapPanel.add(map, BorderLayout.CENTER);
        mapPanel.add(bottomPanel, BorderLayout.PAGE_END);

        //everything mainPane
        mainPane.addTab("Trajectories", mapPanel);

        //everything backPanel
        backPanel.add(mainPane, BorderLayout.CENTER);
        backPanel.add(topPanel, BorderLayout.PAGE_START);
        backPanel.add(infoPanel, BorderLayout.LINE_END);
        backPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                updateSize(frame, infoText, selectionLabel, editLabel, backPanel);
            }
        });

        frame.addWindowStateListener(e -> {
            updateSize(frame, infoText, selectionLabel, editLabel, backPanel);
        });
        frame.add(backPanel);
        frame.pack();

        //everything interactables
        interactables.add(menu);
        interactables.add(muField);
        interactables.add(simplifyButton);
        interactables.add(selectionList);
        interactables.add(editList);
        interactables.add(editBox);
    }

    private void updateSize(JFrame frame, JTextArea infoText, JTextField selectionLabel, JTextField editLabel, JPanel backPanel) {
        framewidth = frame.getWidth();
        frameheight = frame.getHeight();
        infoText.setColumns(framewidth / 36);
        selectionLabel.setColumns(framewidth / 72);
        editLabel.setColumns(framewidth / 72);
        backPanel.repaint();
        frame.requestFocus();
    }

    private void selectionListInit(TrajectoryPanel map, JList<ListItem> selectionList) {
        setCellRenderer(selectionList);
        selectionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        selectionList.addListSelectionListener(e -> {
            ArrayList<Trajectory> newselected = new ArrayList<>();
            List<ListItem> selectedValues = selectionList.getSelectedValuesList();
            for (ListItem i: ListData){
                boolean selected = selectedValues.contains(i);
                i.getT().setSelected(selected);
                if (selected){
                    newselected.add(i.getT());
                }
            }
            selectedTrajectories = newselected;
            map.repaint();
        });
    }

    private void editListInit(TrajectoryPanel map, JList<ListItem> editList, JCheckBox editBox) {
        setCellRenderer(editList);
        editList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        editList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()){
                return;
            } else{
                ListItem selected = editList.getSelectedValue();
                if (selected != null){
                    for (ListItem i: ListData){
                        i.getT().setEditable(false);
                    }
                    Trajectory selT = selected.getT();
                    selT.setEditable(true);
                    editable = selT;
                    map.setCurrentEdit(editable);
                    editBox.setEnabled(true);
                } else {
                    editable = null;
                    map.setCurrentEdit(editable);
                    editBox.setSelected(false);
                    editBox.setEnabled(false);
                }
            }
            map.repaint();
        });
        editList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                JList<ListItem> editList = (JList<ListItem>) e.getSource();
                int index = editList.locationToIndex(e.getPoint());
                if (index != -1) {
                    ListItem selected = editList.getModel().getElementAt(index);
                    if (selected.getT().getEditable()){
                        selected.getT().setEditable(false);
                        editList.clearSelection();
                        editable = null;
                        map.setCurrentEdit(editable);
                        editBox.setSelected(false);
                        editBox.setEnabled(false);
                    }
                }
            }
        });
    }

    private void setCellRenderer(JList<ListItem> list){
        list.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<ListItem> list, ListItem value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value.toString());
                return this;
            }
        });
    }

    private void addButtonListeners(JFrame frame, TrajectoryPanel map, JLabel sliderLabel,
                                    JList<ListItem> selectionList, JList<ListItem> editList,
                                    JTextArea infoText, JTextField muField,
                                    JButton simplifyButton, JSlider sizeSlider) {
        map.sizeSliderConfig(map, sliderLabel, sizeSlider);
        gF.buttonDependency(simplifyButton, muField, (String s) -> {
            try{
                Integer.parseInt(s);
                return true;
            } catch (NumberFormatException ne){
                return false;
            }
        });
        simplifyButton.addActionListener(e -> {
            infoText.append("Simplifying\n");
            if (selectedTrajectories.isEmpty()){
                infoText.append("No trajectories selected.\n");
            }
            double mu = Double.parseDouble(muField.getText());

            int[] oldSelected = selectionList.getSelectedIndices();
            int oldlength = ListData.size();
            int lengthcount = 0;
            ArrayList<Trajectory> simplified = new ArrayList<>();
            for (Trajectory t: selectedTrajectories){
                infoText.append("    "+t.getName()+"...\n");
                Trajectory simpleT = simple.SimplifyTrajectory(t, mu);
                loadedTrajectories.add(simpleT);
                simplified.add(simpleT);
                ListData.add(new ListItem(simpleT.getName(), simpleT));
                lengthcount++;
            }
            selectedTrajectories.addAll(simplified);
            updateSelected(oldSelected, oldlength, lengthcount, selectionList, editList);
            infoText.append("Simplified Trajectories.\n\n");
            map.updateDrawables(loadedTrajectories);
            frame.repaint();
            frame.requestFocus();

        });
    }

    public void updateList(JList<ListItem> selectionList) {
        ListItem[] newData = new ListItem[ListData.size()];
        for (int count = 0; count < ListData.size(); count++) {
            newData[count] = ListData.get(count);
        }
        selectionList.setListData(newData);
    }

    private void updateSelected(int[] oldSelected, int oldlength, int lengthcount, JList<ListItem> selectionList,
                                JList<ListItem> editList) {
        int oldEdit = editList.getSelectedIndex();
        updateList(selectionList);
        updateList(editList);
        int[] newSelection = new int[oldSelected.length + lengthcount];
        System.arraycopy(oldSelected, 0, newSelection, 0, oldSelected.length);
        for (int i = 0; i < newSelection.length - oldSelected.length; i++) {
            newSelection[oldSelected.length + i] = oldlength + i;
        }
        selectionList.setSelectedIndices(newSelection);
        editList.setSelectedIndex(oldEdit);
    }

    public JMenuBar MenuInit(JTextArea infoText, JFrame frame, JTabbedPane mainPane, TrajectoryPanel map,
                             JList<ListItem> selectionList, JList<ListItem> editList, JTextField muField,
                             JButton simplifyButton, JSlider sizeSlider, JLabel sliderLabel){
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem startnew = new JMenuItem("New Trajectory...");
        JMenuItem open = new JMenuItem("Open Trajectories...");
        JMenuItem save = new JMenuItem("Save Trajectories...");
        JMenuItem close = new JMenuItem("Close Trajectories");
        JMenuItem DFDGrid = new JMenuItem("Create DFD Grid");
        JMenuItem SetSys = new JMenuItem("Initialize Set System");
        JMenuItem closeTab = new JMenuItem("Close Tab");
        startnew.addActionListener(e -> {
            infoText.append("Creating New Trajectory...\n");
            initNewTrajWizard(infoText, map, frame, selectionList, editList);
        });
        open.addActionListener(e -> {
            FileDialog fd = new FileDialog(new JFrame(), "Open Trajectories...", FileDialog.LOAD);
            fd.setMultipleMode(true);
            fd.setFilenameFilter((dir, name) -> name.matches(".+\\.txt") || name.matches(".+\\.gpx"));
            fd.setVisible(true);
            File[] files = fd.getFiles();
            if (!(files.length == 0)) {
                int[] oldSelected = selectionList.getSelectedIndices();
                int oldlength = ListData.size();
                int lengthcount = 0;
                infoText.append("Loading\n");
                for (File f : files) {
                    infoText.append("    " + f.getName() + "...\n");
                    Trajectory result = TrajectoryInitializer.loadFile(f, infoText);
                    if (result != null) {
                        loadedTrajectories.add(result);
                        selectedTrajectories.add(result);
                        ListData.add(new ListItem(result.getName(), result));
                        lengthcount++;
                    }
                }
                updateSelected(oldSelected, oldlength, lengthcount, selectionList, editList);
                infoText.append("Opened files.\n\n");
                map.updateDrawables(loadedTrajectories);
                frame.repaint();
                frame.requestFocus();
            }
        });
        save.addActionListener(e -> {
            if(!selectedTrajectories.isEmpty()) {
                infoText.append("Saving\n");
                for (Trajectory t: selectedTrajectories){
                    infoText.append("    "+t.getName()+"...\n");
                    TrajectoryInitializer.saveFile(t, infoText);
                }
                infoText.append("Saved files.\n\n");
                frame.repaint();
                frame.requestFocus();
            }
        });
        close.addActionListener(e -> {
            if(!selectedTrajectories.isEmpty()) {
                infoText.append("Closing\n");
                for (Trajectory t: selectedTrajectories){
                    infoText.append("    "+t.getName()+"...\n");
                }
                loadedTrajectories.removeAll(selectedTrajectories);
                map.updateDrawables(loadedTrajectories);
                ListData.removeAll(selectionList.getSelectedValuesList());
                updateList(selectionList);
                updateList(editList);
                infoText.append("Closed files.\n\n");
                frame.repaint();
                frame.requestFocus();
            }
        });
        DFDGrid.addActionListener(e -> {
            infoText.append("Creating DFD Grid...\n");
            if (selectionList.getModel().getSize() == 0){
                infoText.append("No Trajectories open to create \nDFD Grid from.\n\n");
            } else{
                initDFDGridWizard(selectionList, infoText, mainPane);
            }
        });
        SetSys.addActionListener(e -> {
            infoText.append("Creating Set System...\n");
            if (selectionList.getModel().getSize() == 0){
                infoText.append("No Trajectories open to create \nDFD Grid from.\n\n");
            } else{
                initSetWizard(selectionList, infoText, mainPane);
            }
        });
        mainPane.addChangeListener(e -> closeTab.setEnabled(mainPane.getSelectedIndex() != 0));
        closeTab.addActionListener(e -> {
            infoText.append("Closed Tab: " + mainPane.getTitleAt(mainPane.getSelectedIndex())+"\n\n");
            mainPane.remove(mainPane.getSelectedIndex());

        });
        menu.add(startnew);
        menu.add(open);
        menu.add(save);
        menu.add(close);
        menu.add(DFDGrid);
        menu.add(SetSys);
        menu.add(closeTab);
        menuBar.add(menu);
        interactables.add(menu);
        return menuBar;
    }

    private void initDFDGridWizard(JList<ListItem> selectionList, JTextArea infoText, JTabbedPane mainPane) {
        gF.disable(interactables);
        DFDGridWizard gridWizard = new DFDGridWizard();
        gridWizard.init(selectionList, infoText, mainPane, interactables, gridAmount, framewidth);
        gridWizard.getFrame().addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                if (gridWizard.isWizardCancel()) {
                    infoText.append("Process cancelled.\n\n");
                }
                gF.enable(interactables);
                gridAmount = gridWizard.getAmount();
                interactables = gridWizard.getInteractables();
                gridTabs.addAll(gridWizard.getTabs());
                e.getWindow().dispose();
            }
        });
    }

    private void initSetWizard(JList<ListItem> selectionList, JTextArea infoText, JTabbedPane mainPane) {
        gF.disable(interactables);
        SetSystemWizard setWizard = new SetSystemWizard();
        setWizard.init(selectionList, infoText, mainPane, interactables, gridAmount, framewidth);
        setWizard.getFrame().addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                if (setWizard.isWizardCancel()) {
                    infoText.append("Process cancelled.\n\n");
                }
                gF.enable(interactables);
                setAmount = setWizard.getAmount();
                interactables = setWizard.getInteractables();
                setTabs.addAll(setWizard.getTabs());
                e.getWindow().dispose();
            }
        });
    }

    private void initNewTrajWizard(JTextArea infoText, TrajectoryPanel map, JFrame frame,
                                   JList<ListItem> selectionList, JList<ListItem> editList) {
        wizardCancel = true;
        gF.disable(interactables);

        JFrame wFrame = new JFrame("New Trajectory Wizard");
        JPanel backPanel = new JPanel(new BorderLayout());
        JPanel namePanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new BorderLayout());

        wFrame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                if (wizardCancel) {
                    infoText.append("Process cancelled.\n\n");
                }
                gF.enable(interactables);
                e.getWindow().dispose();
            }
        });
        wFrame.setResizable(false);
        wFrame.setVisible(true);
        wFrame.setMinimumSize(new Dimension(400, 0));
        wFrame.setLayout(new BorderLayout());

        JLabel nameLabel = new JLabel("Name:", SwingConstants.CENTER);
        JTextField nameField = new JTextField();
        JButton confirmButton = new JButton("Confirm Name");
        confirmButton.setEnabled(false);
        gF.buttonDependency(confirmButton, nameField, (String s) -> !s.equals("") && s.matches("^[\\w]*$"));
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                Trajectory newTrajectory = new Trajectory(name);
                int[] oldSelected = selectionList.getSelectedIndices();
                int oldlength = ListData.size();
                int lengthcount = 0;
                loadedTrajectories.add(newTrajectory);
                selectedTrajectories.add(newTrajectory);
                ListData.add(new ListItem(newTrajectory.getName(), newTrajectory));
                updateSelected(oldSelected, oldlength, 1, selectionList, editList);
                editList.setSelectedIndex(oldlength);
                infoText.append("New Trajectory "+name+" created.\n\n");
                map.updateDrawables(loadedTrajectories);
                frame.repaint();
                gF.enable(interactables);
                wizardCancel = false;
                wFrame.dispatchEvent(new WindowEvent(wFrame, WindowEvent.WINDOW_CLOSING));
                frame.requestFocus();
            }
        });

        namePanel.add(nameLabel, BorderLayout.PAGE_START);
        namePanel.add(nameField, BorderLayout.CENTER);
        buttonPanel.add(confirmButton, BorderLayout.CENTER);
        backPanel.add(namePanel, BorderLayout.CENTER);
        backPanel.add(buttonPanel, BorderLayout.PAGE_END);

        wFrame.add(backPanel);
        wFrame.pack();
    }


}


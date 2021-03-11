package Interface;
import Algorithms.Simplification;
import Objects.TrajPoint;
import Objects.Trajectory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Array;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class GUIMain {
    boolean gridWizardCancel = false;
    ArrayList<Trajectory> loadedTrajectories = new ArrayList<>();
    ArrayList<Trajectory> selectedTrajectories = new ArrayList<>();
    ArrayList<ListItem> ListData = new ArrayList<>();
    Simplification simple = new Simplification();
    private int gridAmount = 0;
    public void init(){
        //initialization
        JFrame frame = new JFrame("CurveClustering");
        JPanel backPanel = new JPanel(new BorderLayout());
        JTabbedPane mainPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        TrajectoryPanel map = new TrajectoryPanel();
        JPanel mapPanel = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel buttonsPanel = new JPanel(new WrapLayout());
        JPanel selectionPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel infoPanel = new JPanel(new BorderLayout());
        JLabel gridField = new JLabel("Grid Size = 1");

        //everything frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setVisible(true);
        frame.setLayout(new BorderLayout());
        frame.setMinimumSize(new Dimension(800,800));



        //everything to do with selections
        selectionPanel.setBorder(BorderFactory.createEtchedBorder());
        JTextField selectionLabel = new JTextField("Trajectories:");
        selectionLabel.setEditable(false);
        selectionPanel.add(selectionLabel, BorderLayout.PAGE_START);
        JList<ListItem> selectionList = new JList<>(new ListItem[]{});
        selectionListInit(map, selectionList);
        JScrollPane selectionScroll = new JScrollPane(selectionList);
        selectionScroll.setPreferredSize(new Dimension(0, 100));
        selectionPanel.add(selectionScroll, BorderLayout.PAGE_END);

        //everything infoPanel
        infoPanel.setBorder(BorderFactory.createEtchedBorder());
        JTextArea infoText = new JTextArea();
        JScrollPane infoPane = new JScrollPane(infoText);
        infoPane.getVerticalScrollBar().setUnitIncrement(32);
        infoPane.getVerticalScrollBar().setPreferredSize(new Dimension(0,0));
        infoPane.setWheelScrollingEnabled(true);
        infoText.setEditable(false);
        infoPanel.add(infoPane);

        //everything buttons and bottom panel
        JLabel muLabel = new JLabel("mu");
        JTextField muField = new JTextField("1");
        muField.setColumns(3);
        JButton simplifyButton = new JButton("Simplify");
        JSlider sizeSlider = new JSlider(1, 100, 10);
        JLabel sliderLabel = new JLabel("Draw Size: 10");
        JCheckBox showGridBox = new JCheckBox("Show Grid", true);

        //everything topPanel
        topPanel.setBorder(BorderFactory.createEtchedBorder());
        JMenuBar menu = MenuInit(infoText, frame, mainPane, map, selectionList, muField, simplifyButton, sizeSlider,
                showGridBox, sliderLabel);
        topPanel.add(menu, BorderLayout.LINE_START);

        //listeners
        addMapListeners(map, gridField, showGridBox, frame);
        addButtonListeners(frame, map, sliderLabel, selectionList,
                infoText, muField, simplifyButton, sizeSlider);
        //adding components
        buttonsPanel.setBorder(BorderFactory.createEtchedBorder());
        buttonsPanel.add(sliderLabel);
        buttonsPanel.add(sizeSlider);
        buttonsPanel.add(gridField);
        buttonsPanel.add(showGridBox);
        buttonsPanel.add(muLabel);
        buttonsPanel.add(muField);
        buttonsPanel.add(simplifyButton);
        bottomPanel.add(selectionPanel, BorderLayout.LINE_END);
        bottomPanel.add(buttonsPanel);

        //everything mapPanel
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
                infoText.setColumns(frame.getWidth()/36);
                selectionLabel.setColumns(frame.getWidth()/72);
                backPanel.repaint();
                frame.requestFocus();
            }
        });

        frame.addWindowStateListener(e -> {
            infoText.setColumns(frame.getWidth()/36);
            selectionLabel.setColumns(frame.getWidth()/36);
            backPanel.repaint();
            frame.requestFocus();
        });
        frame.add(backPanel);
        frame.pack();

        //solving a size bug
        infoText.setColumns(frame.getWidth()/36);
        selectionLabel.setColumns(frame.getWidth()/36);
        bottomPanel.revalidate();

    }

    private void selectionListInit(TrajectoryPanel map, JList<ListItem> selectionList) {
        selectionList.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<ListItem> list, ListItem value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    setText(value.toString());
                    return this;
                }
        });
        selectionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        selectionList.addListSelectionListener(e -> {
            ArrayList<Trajectory> newselected = new ArrayList<>();
            for (ListItem i: ListData){
                boolean selected = selectionList.getSelectedValuesList().contains(i);
                i.getT().setSelected(selected);
                if (selected){
                    newselected.add(i.getT());
                }
            }
            selectedTrajectories = newselected;
            map.repaint();
        });
    }

    private void addButtonListeners(JFrame frame, TrajectoryPanel map, JLabel sliderLabel,
                                    JList<ListItem> selectionList, JTextArea infoText, JTextField muField,
                                    JButton simplifyButton, JSlider sizeSlider) {
        sizeSliderConfig(map, sliderLabel, sizeSlider);
        buttonNumberDependency(simplifyButton, muField);
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
            updateSelected(oldSelected, oldlength, lengthcount, selectionList);
            infoText.append("Simplified Trajectories.\n\n");
            map.updateDrawables(loadedTrajectories);
            frame.repaint();
            frame.requestFocus();

        });

    }

    private void sizeSliderConfig(VisualPanel panel, JLabel sliderLabel, JSlider sizeSlider) {
        sizeSlider.addChangeListener(e -> {
            panel.setDrawsize(sizeSlider.getValue());
            sliderLabel.setText("Draw Size: "+sizeSlider.getValue());
            panel.repaint();
        });
    }

    private void addMapListeners(VisualPanel map, JLabel gridField, JCheckBox showGridBox, JFrame frame) {
        showGridBox.addItemListener(e -> {
            map.setShowGrid(e.getStateChange() == ItemEvent.SELECTED);
            map.repaint();
        });
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == 'r'){
                    map.resetOrigins(0, 0);
                    map.repaint();
                    frame.requestFocus();
                }
            }
        });
        map.addMouseWheelListener((MouseWheelEvent e) -> {
            int notches = e.getWheelRotation();
            int UtG = map.calculateValues(notches, e.getX(), e.getY());
            gridField.setText("Grid Size = "+UtG);
            gridField.repaint();
            map.repaint();
        });

        map.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                map.setLastPress(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
            }
        });

        map.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                Point last = map.getLastPress();
                int movedX = e.getX() - last.x;
                int movedY = e.getY() - last.y;
                map.setOrigins(movedX, movedY);
                map.setLastPress(e.getX(), e.getY());
                map.repaint();
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);

            }
        });
    }

    public void updateSelectionList(JList<ListItem> selectionList) {
        ListItem[] newData = new ListItem[ListData.size()];
        int count = 0;
        for (ListItem i : ListData) {
            newData[count] = ListData.get(count);
            count++;
        }
        selectionList.setListData(newData);
    }

    private void updateSelected(int[] oldSelected, int oldlength, int lengthcount, JList<ListItem> selectionList) {
        updateSelectionList(selectionList);
        int[] newSelection = new int[oldSelected.length + lengthcount];
        System.arraycopy(oldSelected, 0, newSelection, 0, oldSelected.length);
        for (int i = 0; i < newSelection.length - oldSelected.length; i++) {
            newSelection[oldSelected.length + i] = oldlength + i;
        }
        selectionList.setSelectedIndices(newSelection);
    }

    public JMenuBar MenuInit(JTextArea infoText, JFrame frame, JTabbedPane mainPane, TrajectoryPanel map,
                             JList<ListItem> selectionList, JTextField muField, JButton simplifyButton,
                             JSlider sizeSlider, JCheckBox showGridBox, JLabel sliderLabel){
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem open = new JMenuItem("Open Trajectories...");
        JMenuItem save = new JMenuItem("Save Trajectories...");
        JMenuItem close = new JMenuItem("Close Trajectories");
        JMenuItem DFDGrid = new JMenuItem("Create DFD Grid");
        JMenuItem closeTab = new JMenuItem("Close Tab");
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
                updateSelected(oldSelected, oldlength, lengthcount, selectionList);
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
                updateSelectionList(selectionList);
                infoText.append("Closed files.\n\n");
                frame.repaint();
                frame.requestFocus();
            }
        });
        DFDGrid.addActionListener(e -> {
            infoText.append("Creating DFD Grid...\n");
            if (selectionList.getModel().getSize() == 0){
                infoText.append("No Trajectories open to create DFD Grid from.\n");
            } else{
                initDFDGridWizard(selectionList, infoText, frame, mainPane, menu, muField, simplifyButton, sizeSlider,
                        showGridBox, sliderLabel);
            }
        });
        mainPane.addChangeListener(e -> closeTab.setEnabled(mainPane.getSelectedIndex() != 0));
        closeTab.addActionListener(e -> {
            infoText.append("Closed Tab: " + mainPane.getTitleAt(mainPane.getSelectedIndex())+"\n\n");
            mainPane.remove(mainPane.getSelectedIndex());

        });
        menu.add(open);
        menu.add(save);
        menu.add(close);
        menu.add(DFDGrid);
        menu.add(closeTab);
        menuBar.add(menu);
        return menuBar;
    }

    private void initDFDGridWizard(JList<ListItem> selectionList, JTextArea infoText, JFrame mainFrame, JTabbedPane mainPane,
                                   JMenu menu, JTextField muField, JButton simplifyButton, JSlider sizeSlider,
                                   JCheckBox showGridBox, JLabel sliderLabel) {
        gridWizardCancel = true;
        menu.setEnabled(false);
        muField.setEnabled(false);
        simplifyButton.setEnabled(false);
        sizeSlider.setEnabled(false);
        sliderLabel.setEnabled(false);
        selectionList.setEnabled(false);

        JFrame frame = new JFrame("DFD Grid Wizard");
        JPanel backPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new WrapLayout());

        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                if (gridWizardCancel) {
                    infoText.append("Process cancelled.\n\n");
                }
                menu.setEnabled(true);
                muField.setEnabled(true);
                simplifyButton.setEnabled(true);
                sizeSlider.setEnabled(true);
                sliderLabel.setEnabled(true);
                selectionList.setEnabled(true);
                e.getWindow().dispose();
            }
        });
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

        //Everything ButtonPanel
        JButton confirm = new JButton("Create DFDGrid");
        JLabel deltaLabel = new JLabel("threshold: ");
        JTextField deltaField = new JTextField("");
        deltaField.setEditable(false);
        deltaField.setColumns(3);
        confirm.setEnabled(false);
        buttonPanel.add(deltaLabel);
        buttonPanel.add(deltaField);
        buttonPanel.add(confirm);

        //listeners
        firstList.addListSelectionListener(e -> deltaField.setEditable(firstList.getSelectedIndices().length != 0 &&
                secondList.getSelectedIndices().length != 0));
        secondList.addListSelectionListener(e -> deltaField.setEditable(firstList.getSelectedIndices().length != 0 &&
                secondList.getSelectedIndices().length != 0));
        buttonNumberDependency(confirm, deltaField);
        confirm.addActionListener(e -> createGridTab(deltaField, firstList, secondList, frame, mainPane, infoText));
        JPanel listsPanel = new JPanel(new BorderLayout());
        listsPanel.add(firstListPanel, BorderLayout.LINE_START);
        listsPanel.add(secondListPanel, BorderLayout.LINE_END);

        backPanel.add(listsPanel);
        backPanel.add(buttonPanel, BorderLayout.PAGE_END);

        frame.add(backPanel);
        frame.pack();
    }

    private void createGridTab(JTextField deltaField, JList<ListItem> firstList, JList<ListItem> secondList,
                               JFrame frame, JTabbedPane mainPane, JTextArea infoText) {
        JPanel gridPanel = new JPanel(new BorderLayout());
        DFDGridPanel grid = new DFDGridPanel(Integer.parseInt(deltaField.getText()),
                firstList.getSelectedValue().getT(), secondList.getSelectedValue().getT());

        //everything TrajectoryPanel
        JPanel trajectoryPanel = new JPanel();
        trajectoryPanel.setBorder(BorderFactory.createEtchedBorder());
        String firsttraj = firstList.getSelectedValue().toString();
        String secondtraj = secondList.getSelectedValue().toString();
        JTextArea trajectoryLabel = new JTextArea("Trajectories:\n" + "1: " + firsttraj
                + "\n" + "2: " + secondtraj + "\nThreshold: " + deltaField.getText());
        trajectoryLabel.setBackground(UIManager.getColor ( "Panel.background" ));
        trajectoryLabel.setColumns(frame.getWidth()/72);
        trajectoryLabel.setEditable(false);
        trajectoryPanel.add(trajectoryLabel);

        //everything BottomPanel
        JPanel infoPanel = new JPanel(new WrapLayout());
        infoPanel.setBorder(BorderFactory.createEtchedBorder());
        JCheckBox showGridBox = new JCheckBox("Show Grid", false);
        JLabel gridField = new JLabel("Grid Size = 1");
        JLabel expansionLabel = new JLabel("Grid Expansion: 10");
        JSlider expansionSlider = new JSlider(1, 20, 10);
        expansionSlider.addChangeListener(e -> {
            expansionLabel.setText("Grid Expansion: "+expansionSlider.getValue());
            grid.setGridmul(expansionSlider.getValue());
            grid.repaint();
        });

        JSlider sizeSlider = new JSlider(1, 20, 1);
        JLabel sliderLabel = new JLabel("Draw Size: 1");
        sizeSliderConfig(grid, sliderLabel, sizeSlider);

        addMapListeners(grid, gridField, showGridBox, frame);

        infoPanel.add(sliderLabel);
        infoPanel.add(sizeSlider);
        infoPanel.add(expansionLabel);
        infoPanel.add(expansionSlider);
        infoPanel.add(gridField);
        infoPanel.add(showGridBox);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(trajectoryPanel, BorderLayout.LINE_END);
        bottomPanel.add(infoPanel);

        gridPanel.add(grid, BorderLayout.CENTER);
        gridPanel.add(bottomPanel, BorderLayout.PAGE_END);

        mainPane.addTab("DFD Grid " + gridAmount, gridPanel);
        gridAmount++;
        infoText.append("DFD Grid created between\n     " + firsttraj + " and " + secondtraj + ".\n\n");
        gridWizardCancel = false;
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

    private void buttonNumberDependency(JButton button, JTextField field) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    Integer.parseInt(field.getText());
                    button.setEnabled(true);
                }
                catch (NumberFormatException ne) {
                    button.setEnabled(false);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    Integer.parseInt(field.getText());
                    button.setEnabled(true);
                }
                catch (NumberFormatException ne) {
                    button.setEnabled(false);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                try {
                    Integer.parseInt(field.getText());
                    button.setEnabled(true);
                }
                catch (NumberFormatException ne) {
                    button.setEnabled(false);
                }
            }
        });
    }


    static class ListItem {
        private final String label;
        private final Trajectory t;
        private boolean isSelected = true;

        public ListItem(String label, Trajectory t) {
            this.label = label;
            this.t = t;
        }

        public String toString() {
            return label;
        }

        public Trajectory getT() {
            return t;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }
    }

}


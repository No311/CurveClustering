package Interface;
import Algorithms.Simplification;
import Objects.TrajPoint;
import Objects.Trajectory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.lang.reflect.Array;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class GUIMain {
    ArrayList<Trajectory> loadedTrajectories = new ArrayList<>();
    ArrayList<Trajectory> selectedTrajectories = new ArrayList<>();
    ArrayList<ListItem> ListData = new ArrayList<>();
    Simplification simple = new Simplification();
    public void init(){
        //initialization
        JFrame frame = new JFrame("CurveClustering");
        JPanel backPanel = new JPanel(new BorderLayout());
        TrajectoryPanel map = new TrajectoryPanel();
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel buttonsPanel = new JPanel(new WrapLayout());
        JPanel selectionPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel infoPanel = new JPanel(new BorderLayout());
        JLabel gridField = new JLabel("Grid Size = 1");
        JCheckBox showGridBox = new JCheckBox("Show Grid", true);
        JLabel sliderLabel = new JLabel("Draw Size: 10");

        //frame initialization
        //everything frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800, 800));
        frame.setResizable(true);
        frame.setVisible(true);
        frame.setLayout(new BorderLayout());

        //everything map
        addMapListeners(map, gridField);

        //everything to do with selections
        selectionPanel.setBorder(BorderFactory.createEtchedBorder());
        JTextField selectionLabel = new JTextField("Trajectories:");
        selectionLabel.setColumns(frame.getWidth()/36);
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
        infoText.setColumns(frame.getWidth()/36);
        infoText.setEditable(false);
        infoPanel.add(infoPane);

        //everything topPanel
        topPanel.setBorder(BorderFactory.createEtchedBorder());
        JMenuBar menu = MenuInit(infoText, frame, map, selectionList);
        topPanel.add(menu, BorderLayout.LINE_START);

        //everything buttons and bottom panel
        //initialization
        JLabel muLabel = new JLabel("mu");
        JTextField muField = new JTextField("1");
        muField.setColumns(3);
        JButton simplifyButton = new JButton("Simplify");
        JSlider sizeSlider = new JSlider(1, 100, 10);

        //listeners
        addButtonListeners(frame, map, showGridBox, sliderLabel, selectionList,
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
        bottomPanel.add(buttonsPanel);
        bottomPanel.add(selectionPanel, BorderLayout.LINE_END);

        //everything backPanel
        backPanel.add(map, BorderLayout.CENTER);
        backPanel.add(topPanel, BorderLayout.PAGE_START);
        backPanel.add(bottomPanel, BorderLayout.PAGE_END);
        backPanel.add(infoPanel, BorderLayout.LINE_END);
        backPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                infoText.setColumns(frame.getWidth()/36);
                selectionLabel.setColumns(frame.getWidth()/36);
                backPanel.repaint();
                frame.requestFocus();
            }
        });

        //frame roundup
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
        frame.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                infoText.setColumns(frame.getWidth()/36);
                selectionLabel.setColumns(frame.getWidth()/36);
                backPanel.repaint();
                frame.requestFocus();
            }
        });
        frame.add(backPanel);
        frame.pack();
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
        selectionList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
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
            }
        });
    }

    private void addButtonListeners(JFrame frame, TrajectoryPanel map, JCheckBox showGridBox, JLabel sliderLabel,
                                    JList<ListItem> selectionList, JTextArea infoText, JTextField muField,
                                    JButton simplifyButton, JSlider sizeSlider) {
        showGridBox.addItemListener(e -> {
            map.setShowGrid(e.getStateChange() == ItemEvent.SELECTED);
            map.repaint();
        });
        sizeSlider.	addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                map.setDrawsize(sizeSlider.getValue());
                sliderLabel.setText("Draw Size: "+String.valueOf(sizeSlider.getValue()));
                map.repaint();
            }
        });
        simplifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                infoText.append("Simplifying\n");
                if (selectedTrajectories.isEmpty()){
                    infoText.append("No trajectories selected.\n");
                }
                double mu = 1;
                try {
                    mu = Double.parseDouble(muField.getText());
                } catch (Exception exception){
                    infoText.append("The mu value given is not a number.\n");
                    System.out.println(exception);
                    return;
                }
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

            }
        });
    }

    private void addMapListeners(TrajectoryPanel map, JLabel gridField) {
        map.addMouseWheelListener((MouseWheelEvent e) -> {
            int notches = e.getWheelRotation();
            int UtG = map.calculateValues(notches, e.getX(), e.getY());
            gridField.setText("Grid Size = "+String.valueOf(UtG));
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
        for (int i = 0; i < oldSelected.length; i++) {
            newSelection[i] = oldSelected[i];
        }
        for (int i = 0; i < newSelection.length - oldSelected.length; i++) {
            newSelection[oldSelected.length + i] = oldlength + i;
        }
        selectionList.setSelectedIndices(newSelection);
    }

    public JMenuBar MenuInit(JTextArea infoText, JFrame frame, TrajectoryPanel map, JList<ListItem> selectionList){
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem open = new JMenuItem("Open...");
        JMenuItem save = new JMenuItem("Save...");
        JMenuItem close = new JMenuItem("Close");
        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileDialog fd = new FileDialog(new JFrame(), "Open Trajectories...", FileDialog.LOAD);
                fd.setMultipleMode(true);
                fd.setFile("*.txt");
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
                    updateSelected(oldSelected, oldlength, lengthcount, (JList<ListItem>) selectionList);
                    infoText.append("Opened files.\n\n");
                    map.updateDrawables(loadedTrajectories);
                    frame.repaint();
                    frame.requestFocus();
                }
            }
        });
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
            }
        });
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
            }
        });
        menu.add(open);
        menu.add(save);
        menu.add(close);
        menuBar.add(menu);
        return menuBar;
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


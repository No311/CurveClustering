package Interface.Tabs;

import Interface.ListItem;
import Interface.VisualPanels.DFDGridPanel;
import Interface.WrapLayout;
import Methods.SetSystemMethods;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GridTab extends Tab{
    DFDGridPanel grid;
    JTextArea infoText;
    ArrayList<JComponent> interactables;
    ArrayList<JComponent> localinteractables = new ArrayList<>();
    int tabnumber = 0;

    public ArrayList<JComponent> init(String delta, JList<ListItem> firstList, JList<ListItem> secondList,
                                      int framewidth, JTabbedPane mainPane, JTextArea infoText, int reach, int algo,
                                      String reachString, String algoString, int gridAmount,
                                      ArrayList<JComponent> interactables, SetSystemMethods methods) {
        long starttime = System.currentTimeMillis();
        this.infoText = infoText;
        this.tabnumber = gridAmount;
        JPanel gridPanel = new JPanel(new BorderLayout());

        //everything coordinatePanel
        JPanel coordinatePanel = new JPanel(new BorderLayout());
        coordinatePanel.setBorder(BorderFactory.createEtchedBorder());
        String currentCoord = "Nearest Vertex: (row: 0, column: 0)";
        String lastSelected = "Selected Coordinates: (row: 0, column: 0) ";
        JLabel currentField = new JLabel(currentCoord);
        JLabel selectedField = new JLabel(lastSelected);
        Font f = currentField.getFont();
        currentField.setBackground(UIManager.getColor ( "Panel.background" ));
        selectedField.setBackground(UIManager.getColor ( "Panel.background" ));
        currentField.setFont(f.deriveFont(f.getStyle() & ~Font.BOLD));
        selectedField.setFont(f.deriveFont(f.getStyle() & ~Font.BOLD));
        coordinatePanel.add(currentField, BorderLayout.LINE_START);
        coordinatePanel.add(selectedField, BorderLayout.LINE_END);

        //Initialization of grid
        JCheckBox showGridBox = new JCheckBox("Show Grid", false);
        JLabel gridField = new JLabel("Grid Size = 1");
        grid = new DFDGridPanel(Integer.parseInt(delta),
                firstList.getSelectedValue().getT(), secondList.getSelectedValue().getT(), reach, algo, gridField,
                showGridBox, currentField, selectedField, infoText, methods);

        //everything TrajectoryPanel
        JPanel trajectoryPanel = new JPanel();
        trajectoryPanel.setBorder(BorderFactory.createEtchedBorder());
        String firsttraj = firstList.getSelectedValue().toString();
        String secondtraj = secondList.getSelectedValue().toString();
        JTextArea trajectoryLabel = new JTextArea("Trajectories:\n" + "1: " + firsttraj
                + " (row)\n" + "2: " + secondtraj + " (column)\nThreshold: " + delta);
        trajectoryLabel.setBackground(UIManager.getColor ( "Panel.background" ));
        trajectoryLabel.setColumns(framewidth/72);
        trajectoryLabel.setEditable(false);
        trajectoryPanel.add(trajectoryLabel);

        //everything BottomPanel
        JPanel queryPanel = new JPanel(new GridLayout(2, 1));
        queryPanel.setBorder(BorderFactory.createEtchedBorder());
        JButton queryReach = new JButton("<html><center>Query<br>Reachability<br>Oracle</center></html>");
        JButton queryAlgo = new JButton("<html><center>Query<br>Trajectory Coverage<br>Oracle</center></html>");
        if (reach == 0){
            queryReach.setEnabled(false);
            queryReach.setText("<html><center>Reachability<br>Not Prepared</center></html>");
        } else {
            interactables.add(queryReach);
            localinteractables.add(queryReach);
        }
        if (algo == 0){
            queryAlgo.setEnabled(false);
            queryAlgo.setText("<html><center>Trajectory Coverage<br>Not Prepared</center></html>");
        } else {
            interactables.add(queryAlgo);
            localinteractables.add(queryAlgo);
        }
        queryReach.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (grid.getQuerymode() > 0){
                    infoText.append("Query Canceled.\n\n");
                }
                grid.setQuerymode(1);
                infoText.append("Tab " + tabnumber +":\n");
                infoText.append("""
                        Query Reachability Started:
                        (To cancel, press the right mouse button.)
                             Please select start vertex...
                        """);
            }
        });
        queryAlgo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (grid.getQuerymode() > 0){
                    infoText.append("Query Canceled.\n\n");
                }
                grid.setQuerymode(2);
                infoText.append("Tab " + tabnumber +":\n");
                infoText.append("""
                        Query Trajectory Cover Started:
                        (To cancel, press the right mouse button.)
                             Please select a vertex from 
                                  the first row...
                        """);
            }
        });
        queryPanel.add(queryReach);
        queryPanel.add(queryAlgo);

        JPanel optionPanel = new JPanel(new WrapLayout());
        optionPanel.setBorder(BorderFactory.createEtchedBorder());
        JLabel expansionLabel = new JLabel("Grid Expansion: 10");
        JSlider expansionSlider = new JSlider(1, 20, 10);
        expansionSlider.addChangeListener(e -> {
            expansionLabel.setText("Grid Expansion: "+expansionSlider.getValue());
            grid.setGridmul(expansionSlider.getValue());
            grid.repaint();
        });

        JSlider sizeSlider = new JSlider(1, 20, 1);
        JLabel sliderLabel = new JLabel("Draw Size: 1");
        grid.sizeSliderConfig(grid, sliderLabel, sizeSlider);

        grid.addMapListeners(grid);

        optionPanel.add(sliderLabel);
        optionPanel.add(sizeSlider);
        optionPanel.add(expansionLabel);
        optionPanel.add(expansionSlider);
        optionPanel.add(gridField);
        optionPanel.add(showGridBox);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(trajectoryPanel, BorderLayout.LINE_END);
        bottomPanel.add(optionPanel, BorderLayout.CENTER);
        bottomPanel.add(queryPanel, BorderLayout.LINE_START);

        gridPanel.add(coordinatePanel, BorderLayout.PAGE_START);
        gridPanel.add(grid, BorderLayout.CENTER);
        gridPanel.add(bottomPanel, BorderLayout.PAGE_END);

        //dealing with interactables
        this.interactables = interactables;

        setTitle("DFD Grid " + gridAmount);
        mainPane.addTab(title, gridPanel);
        long endtime = System.currentTimeMillis();
        double time = ((double) endtime - (double) starttime)/1000;
        infoText.append("DFD Grid "+gridAmount+" created between\n     " + firsttraj + " and " + secondtraj + ",\n" +
                "     with threshold "+delta+",\n     "+reachString+" reachability and\n     "
                +algoString+" data structure.\n     Time Total: "+time+" seconds.\n     " +
                "Time Reachability Init: "+grid.reachinittime+"\n     Time Data Structure init: "+grid.algoinittime+"\n\n");

        return interactables;
    }

    public void updateInteractables(ArrayList<JComponent> interactables) {
        this.interactables = interactables;
        for (JComponent i: interactables){
            i.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    if (grid.getQuerymode() > 0){
                        infoText.append("Query Canceled: other function started.\n\n");
                        grid.setQuerymode(0);
                    }
                }
            });
        }
    }
}

package Interface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class GridTab {

    public void init(String delta, JList<ListItem> firstList, JList<ListItem> secondList,
                               int framewidth, JTabbedPane mainPane, JTextArea infoText, int reach, int algo,
                               String reachString, String algoString, int gridAmount) {
        long starttime = System.currentTimeMillis();
        JPanel gridPanel = new JPanel(new BorderLayout());
        DFDGridPanel grid = new DFDGridPanel(Integer.parseInt(delta),
                firstList.getSelectedValue().getT(), secondList.getSelectedValue().getT(), reach, algo);

        //everything TrajectoryPanel
        JPanel trajectoryPanel = new JPanel();
        trajectoryPanel.setBorder(BorderFactory.createEtchedBorder());
        String firsttraj = firstList.getSelectedValue().toString();
        String secondtraj = secondList.getSelectedValue().toString();
        JTextArea trajectoryLabel = new JTextArea("Trajectories:\n" + "1: " + firsttraj
                + "\n" + "2: " + secondtraj + "\nThreshold: " + delta);
        trajectoryLabel.setBackground(UIManager.getColor ( "Panel.background" ));
        trajectoryLabel.setColumns(framewidth/72);
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
        grid.sizeSliderConfig(grid, sliderLabel, sizeSlider);

        grid.addMapListeners(grid, gridField, showGridBox);

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
        long endtime = System.currentTimeMillis();
        double time = ((double) endtime - (double) starttime)/1000;
        infoText.append("DFD Grid "+gridAmount+" created between\n     " + firsttraj + " and " + secondtraj + ",\n" +
                "     with threshold "+delta+",\n     "+reachString+" reachability and\n     "
                +algoString+" data structure.\n     Time: "+time+" seconds.\n\n");

    }
}

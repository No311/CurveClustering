package Interface.VisualPanels;

import DataStructures.Querier.OracleResult;
import DataStructures.Querier.SetSystemOracle;
import Objects.Arrow;
import Objects.TrajPoint;
import Objects.Trajectory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class SetTrajectoryPanel extends TrajectoryPanel {
    private ArrayList<SetSystemOracle> oracles;
    ArrayList<OracleResult> selectedResults = new ArrayList<>();
    OracleResult lastResult = null;
    final Color selectedColor = Color.RED;
    //final Color selectedColor = new Color(196, 82, 0);
    final Color selectedPColor = new Color(196, 177, 0);
    final Color selectedEColor = new Color(250, 231, 60);
    final Color coveredPColor = new Color(16, 107, 47);
    final Color coveredEColor = new Color(80, 191, 118);
    int[] amountSelected;

    public SetTrajectoryPanel(JLabel gridField, JCheckBox showGridBox, JTextField currentField,
                              ArrayList<SetSystemOracle> oracles){
        super(gridField, showGridBox, currentField, new JCheckBox());
        this.oracles = oracles;
        amountSelected = new int[drawables.size()];
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        TrajPoint firstpoint = drawables.get(0).getPoints().get(0);
        startx = firstpoint.drawOrigX();
        starty = firstpoint.drawOrigY();
        drawables.sort((c1, c2) -> {
            if (c1.amountSelected > c2.amountSelected) return 1;
            if (c1.amountSelected < c2.amountSelected) return -1;
            return 0;
        });
        for (Trajectory t: drawables){
            drawTrajectory(g, t, pointsColor, trajectoryColor);
        }
    }

    @Override
    void drawTrajectory(Graphics g, Trajectory t, Color pointsColor, Color trajectoryColor) {
        Graphics2D g2 = (Graphics2D) g;
        size = (drawsize*step)/UtG;
        Stroke oldStroke = g2.getStroke();
        Stroke lineStroke = new BasicStroke((int) Math.ceil(size/2.0));
        g2.setStroke(lineStroke);
        TrajPoint lastPoint = null;
        g.setColor(pointsColor);
        for (int i = t.getPoints().size()-1; i >= 0; i--){
            TrajPoint p = t.getPoints().get(i);
            p.x = updateX(startx, p.drawOrigX());
            p.y = updateY(starty, p.drawOrigY());
            if (lastPoint != null){
                Color edgeColor;
                if (lastPoint.isSelected() && p.isSelected()){
                    edgeColor = selectedEColor;
                } else if (lastPoint.isCovered() && p.isCovered()){
                    edgeColor = coveredEColor;
                } else {
                    edgeColor = trajectoryColor;
                }
                Arrow arrow = new Arrow(getStandardDist(), size);
                arrow.updateColors(edgeColor, edgeColor.darker());
                arrow.updateCoordinates(p.x, p.y, lastPoint.x, lastPoint.y, getStandardDist(), size);
                arrow.drawArrow(g2);
                lastPoint.paint(g2, size);
            }
            g2.setColor(getColor(p, pointsColor));
            p.paint(g2, size);
            lastPoint = p;
        }
        g2.setStroke(oldStroke);
        g.setColor(Color.BLACK);
    }

    public Color getColor(TrajPoint p, Color pointsColor){
        boolean isSelected = false;
        for (OracleResult result: selectedResults){
            if (result.getSubTrajStart() == p || result.getSubTrajEnd() == p){
                isSelected = true;
            }
        }
        if (isSelected){
            return selectedColor;
        } else if (p.isSelected()){
            return selectedPColor;
        } else if (p.isCovered()){
            return coveredPColor;
        }
        return pointsColor;
    }

    public void setOracleListeners(VisualPanel map){
        map.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                TrajPoint selected = null;
                Trajectory selectedT = null;
                for (Trajectory t: drawables) {
                    for (TrajPoint p : t.getPoints()) {
                        if (p.getDrawable().contains(e.getPoint())) {
                            selected = p;
                            selectedT = t;
                            break;
                        }
                    }
                }
                if (selected != null) {
                    if (shiftPressed){
                        if (lastResult == null || lastResult.getFirst() != selectedT) {
                            doFirstSelection(selected, selectedT);
                        } else {
                            SetSystemOracle oracle = oracles.get(0);
                            for (SetSystemOracle o: oracles){
                                if (selectedT == o.getFirst()){
                                    oracle = o;
                                }
                            }
                            OracleResult result;
                            if (lastResult.getSubTrajStart().index > selected.index) {
                                result = oracle.getCoveredBySub(selected.index, lastResult.getSubTrajEnd().index);
                            } else {
                                result = oracle.getCoveredBySub(lastResult.getSubTrajStart().index, selected.index);
                            }
                            selectedResults.remove(lastResult);
                            selectedResults.add(result);
                            lastResult.setSelected(false);
                            result.setSelected(true);
                            lastResult = result;
                        }
                    } else if (altPressed) {
                        if (lastResult == null || lastResult.getFirst() != selectedT) {
                            doFirstSelection(selected, selectedT);
                        } else {
                            SetSystemOracle oracle = oracles.get(0);
                            for (SetSystemOracle o: oracles){
                                if (selectedT == o.getFirst()){
                                    oracle = o;
                                }
                            }
                            OracleResult result;
                            if (lastResult.getSubTrajEnd().index < selected.index) {
                                result = oracle.getCoveredBySub(lastResult.getSubTrajStart().index, selected.index);
                            } else {
                                result = oracle.getCoveredBySub(selected.index, lastResult.getSubTrajEnd().index);
                            }
                            selectedResults.remove(lastResult);
                            selectedResults.add(result);
                            lastResult.setSelected(false);
                            result.setSelected(true);
                            lastResult = result;
                        }
                    } else {
                        doFirstSelection(selected, selectedT);
                    }
                    setSelection();
                    map.repaint();
                    map.requestFocus();
                }
            }

            private void doFirstSelection(TrajPoint selected, Trajectory selectedT) {
                SetSystemOracle oracle = oracles.get(0);
                for (SetSystemOracle o: oracles){
                    if (selectedT.index == o.getFirst().index){
                        oracle = o;
                    }
                }
                OracleResult result = oracle.getCoveredBySub(selected.index, selected.index);
                if (lastResult != null) {
                    lastResult.setSelected(false);
                }
                if (!controlPressed) {
                    selectedResults = new ArrayList<>();
                    lastResult = null;
                }
                selectedResults.add(result);
                lastResult = result;
                result.setSelected(true);
            }
        });
    }

    public void setSelection(){
        for (Trajectory t: drawables){
            t.amountSelected = 0;
            for (TrajPoint p: t.getPoints()){
                p.setSelected(false);
                p.setCovered(false);
            }
        }
        for (OracleResult result : selectedResults) {
            Trajectory first = result.getFirst();
            for (int i = 0; i < first.getPoints().size(); i++) {
                TrajPoint current = first.getPoints().get(i);
                if (i >= result.getSubTrajStart().index && i <= result.getSubTrajEnd().index) {
                    current.setSelected(true);
                    first.amountSelected++;
                    current.setCovered(true);
                }
            }
            boolean[][] covered = result.getCovered();
            for (Trajectory t: result.getSelection()) {
                for (int i = 0; i < t.getPoints().size(); i++) {
                    TrajPoint current = t.getPoints().get(i);
                    if (covered[t.index][i]) {
                        current.setCovered(true);
                    }
                }
            }
        }
    }

    @Override
    void setCoordinateText(double mouseX, double mouseY) {
        currentCoord.setText("Current Coordinates: (x: "+mouseX+", y: "+mouseY+")");
    }

    public void resetSelectedResults(){
        selectedResults = new ArrayList<>();
    }

    public void addSelectedResults(OracleResult newResult){
        selectedResults.add(newResult);
    }

    public void addSelectedResults(ArrayList<OracleResult> newResults){
        selectedResults.addAll(newResults);
    }
}

package Interface.VisualPanels;

import DataStructures.SetSystemQuerier.OracleResult;
import DataStructures.SetSystemQuerier.SetSystemOracle;
import Objects.TrajPoint;
import Objects.Trajectory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class SetTrajectoryPanel extends TrajectoryPanel {
    private SetSystemOracle oracle;
    private Trajectory first = null;
    private Trajectory second = null;
    ArrayList<TrajPoint> firstPoints = new ArrayList<>();
    ArrayList<TrajPoint> secondPoints = new ArrayList<>();
    ArrayList<OracleResult> selectedResults = new ArrayList<>();
    OracleResult lastResult = null;
    final Color selectedColor = new Color(196, 82, 0);
    final Color selectedPColor = new Color(196, 177, 0);
    final Color selectedEColor = new Color(250, 231, 60);
    final Color coveredPColor = new Color(16, 107, 47);
    final Color coveredEColor = new Color(80, 191, 118);

    public SetTrajectoryPanel(JLabel gridField, JCheckBox showGridBox, JTextField currentField,
                              SetSystemOracle oracle){
        super(gridField, showGridBox, currentField, new JCheckBox());
        this.oracle = oracle;
    }

    public void updateDrawables(Trajectory first, Trajectory second){
        this.first = first;
        this.second = second;
        firstPoints = first.getPoints();
        secondPoints = second.getPoints();
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        TrajPoint firstpoint = first.getPoints().get(0);
        startx = firstpoint.drawOrigX();
        starty = firstpoint.drawOrigY();
        drawTrajectory(g, second, Color.BLACK, Color.GRAY);
        drawTrajectory(g, first, pointsColor, trajectoryColor);
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
        for (TrajPoint p: t.getPoints()){
            p.x = updateX(startx, p.drawOrigX());
            p.y = updateY(starty, p.drawOrigY());
            if (lastPoint != null){
                if (lastPoint.isSelected() && p.isSelected()){
                    g2.setColor(selectedEColor);
                } else if (lastPoint.isCovered() && p.isCovered()){
                    g2.setColor(coveredEColor);
                } else {
                    g2.setColor(trajectoryColor);
                }
                g2.draw(new Line2D.Double(lastPoint.x, lastPoint.y, p.x, p.y));
                g2.setColor(getColor(lastPoint, pointsColor));
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
                for (TrajPoint p : firstPoints) {
                    if (p.getDrawable().contains(e.getPoint())) {
                        selected = p;
                        break;
                    }
                }
                if (selected != null) {
                    if (!shiftPressed) {
                        doFirstSelection(selected);
                    } else {
                        if (lastResult == null) {
                            doFirstSelection(selected);
                        } else {
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
                    }
                    setSelection();
                    map.repaint();
                    map.requestFocus();
                }
            }

            private void doFirstSelection(TrajPoint selected) {
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
        for (TrajPoint current: firstPoints){
            current.setSelected(false);
            current.setCovered(false);
        }
        for (TrajPoint current: secondPoints){
            current.setSelected(false);
            current.setCovered(false);
        }
        for (OracleResult result: selectedResults) {
            for (int i = 0; i < firstPoints.size(); i++) {
                TrajPoint current = firstPoints.get(i);
                if (i >= result.getSubTrajStart().index && i <= result.getSubTrajEnd().index) {
                    current.setSelected(true);
                }
            }
            boolean[] covered = result.getCovered();
            for (int i = 0; i < secondPoints.size(); i++) {
                TrajPoint current = secondPoints.get(i);
                if (covered[i]) {
                    current.setCovered(true);
                }
            }
        }
    }

    @Override
    void setCoordinateText(double mouseX, double mouseY) {
        currentCoord.setText("Current Coordinates of Trajectory 1: (x: "+mouseX+", y: "+mouseY+")");
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

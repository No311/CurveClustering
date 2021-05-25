package Interface;

import Objects.TrajPoint;
import Objects.Trajectory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class TrajectoryPanel extends VisualPanel{
    private double startx = 0;
    private double starty = 0;
    private boolean chosen = false;
    ArrayList<Trajectory> drawables = new ArrayList<>();
    JLabel gridField;
    JCheckBox showGridBox;
    JTextField currentCoord;
    JCheckBox editBox;
    boolean controlPressed = false;
    Trajectory currentEdit;

    public TrajectoryPanel(JLabel gridField, JCheckBox showGridBox, JTextField currentField, JCheckBox editBox){
        this.gridField = gridField;
        this.showGridBox = showGridBox;
        this.currentCoord = currentField;
        this.editBox = editBox;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!chosen && !drawables.isEmpty()) {
            for (Trajectory t: drawables){
                if (t.getPoints().size() != 0){
                    TrajPoint firstpoint = t.getPoints().get(0);
                    startx = firstpoint.origx;
                    starty = firstpoint.origy;
                    break;
                }
            }
        }
        for (Trajectory t: drawables){
            if (t.getSelected()) {
                drawTrajectory(g, t);
            }
        }
    }

    private double updateX(double startx, double origx){return (((origx-startx)*step)/UtG) + origin.x;}

    private double updateY(double starty, double origy){return -(((origy-starty)*step)/UtG) + origin.y;}

    private double originalX(double startx, double updatedx) { return (((updatedx - origin.x)*UtG)/step) + startx;}

    private double originalY(double starty, double updatedy) { return (((-updatedy + origin.y)*UtG)/step) + starty;}

    private void drawTrajectory(Graphics g, Trajectory t) {
        Graphics2D g2 = (Graphics2D) g;
        size = (drawsize*step)/UtG;
        Stroke oldStroke = g2.getStroke();
        Stroke lineStroke = new BasicStroke((int) Math.ceil(size/2.0));
        g2.setStroke(lineStroke);
        TrajPoint lastPoint = null;
        g.setColor(pointsColor);
        for (TrajPoint p: t.getPoints()){
            p.x = updateX(startx, p.origx);
            p.y = updateY(starty, p.origy);
            if (lastPoint != null){
                g2.setColor(trajectoryColor);
                g2.draw(new Line2D.Double(lastPoint.x, lastPoint.y, p.x, p.y));
                g2.setColor(pointsColor);
                lastPoint.paint(g2, size);
            }
            p.paint(g2, size);
            lastPoint = p;
        }
        g2.setStroke(oldStroke);
        g.setColor(Color.BLACK);
    }
    public void updateDrawables(ArrayList<Trajectory> newlist){ drawables = newlist; }

    @Override
    public void addMapListeners(VisualPanel map) {
        showGridBox.addItemListener(e -> {
            map.setShowGrid(e.getStateChange() == ItemEvent.SELECTED);
            map.repaint();
        });
        map.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == 'r'){
                    map.resetOrigins(0, 0);
                    map.repaint();
                    map.requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_CONTROL){
                    controlPressed = true;
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL){
                    controlPressed = false;
                }
            }
        });
        map.addMouseWheelListener((MouseWheelEvent e) -> {
            int notches = e.getWheelRotation();
            int UtG = map.calculateValues(notches, e.getX(), e.getY());
            gridField.setText("Grid Size = "+UtG);
            gridField.repaint();
            map.repaint();
            map.requestFocusInWindow();
        });

        map.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                map.setLastPress(e.getX(), e.getY());
                map.requestFocusInWindow();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (editBox.isSelected() && !controlPressed && currentEdit != null){
                    double mouseX = originalX(startx, e.getX());
                    double mouseY = originalY(starty, e.getY());
                    double time = currentEdit.getLastTime() + 1;
                    currentEdit.addPoint(new TrajPoint(mouseX, mouseY, time));
                    map.repaint();
                    map.requestFocusInWindow();
                }
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
                map.requestFocusInWindow();
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                double mouseX = originalX(startx, e.getX());
                double mouseY = originalY(starty, e.getY());
                currentCoord.setText("Current Coordinates: (x: "+mouseX+", y: "+mouseY+")");
            }
        });
    }
    public Trajectory getCurrentEdit() {
        return currentEdit;
    }

    public void setCurrentEdit(Trajectory currentEdit) {
        this.currentEdit = currentEdit;
    }

}

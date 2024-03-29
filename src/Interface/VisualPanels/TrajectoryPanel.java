package Interface.VisualPanels;

import Interface.ListItem;
import Objects.Arrow;
import Objects.TrajPoint;
import Objects.Trajectory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class TrajectoryPanel extends VisualPanel{
    double startx = 0;
    double starty = 0;
    ArrayList<Trajectory> drawables = new ArrayList<>();
    JLabel gridField;
    JCheckBox showGridBox;
    JTextField currentCoord;
    JCheckBox editBox;
    boolean shiftPressed = false;
    boolean altPressed = false;
    boolean controlPressed = false;
    Trajectory currentEdit;
    ArrayList<JList<ListItem>> editRedrawables = new ArrayList<>();

    public TrajectoryPanel(JLabel gridField, JCheckBox showGridBox, JTextField currentField, JCheckBox editBox){
        this.gridField = gridField;
        this.showGridBox = showGridBox;
        this.currentCoord = currentField;
        this.editBox = editBox;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!drawables.isEmpty()) {
            for (Trajectory t: drawables){
                if (t.getPoints().size() != 0){
                    TrajPoint firstpoint = t.getPoints().get(0);
                    startx = firstpoint.drawOrigX();
                    starty = firstpoint.drawOrigY();
                    break;
                }
            }
        }
        for (Trajectory t : drawables) {
            if (t.getSelected()) {
                drawTrajectory(g, t, pointsColor, trajectoryColor);
            }
        }

    }

    double updateX(double startx, double origx){return (((origx-startx)*step)/UtG) + origin.x;}

    double updateY(double starty, double origy){return -(((origy-starty)*step)/UtG) + origin.y;}

    double originalX(double startx, double updatedx) { return (((updatedx - origin.x)*UtG)/step) + startx;}

    double originalY(double starty, double updatedy) { return (((-updatedy + origin.y)*UtG)/step) + starty;}

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
                Arrow arrow = new Arrow(getStandardDist(), size);
                arrow.updateColors(trajectoryColor, trajectoryColor.darker());
                arrow.updateCoordinates(p.x, p.y, lastPoint.x, lastPoint.y, getStandardDist(), size);
                arrow.drawArrow(g2);
                lastPoint.paint(g2, size);
            }
            p.paint(g2, size);
            lastPoint = p;
        }
        g2.setStroke(oldStroke);
        g.setColor(Color.BLACK);
    }
    public void updateDrawables(ArrayList<Trajectory> newlist){
        drawables = newlist;
    }

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
                if (e.getKeyCode() == KeyEvent.VK_SHIFT){
                    shiftPressed = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_ALT){
                    altPressed = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_CONTROL){
                    controlPressed = true;
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT){
                    shiftPressed = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_ALT){
                    altPressed = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_CONTROL){
                    controlPressed = false;
                }
            }
        });
        map.addMouseWheelListener((MouseWheelEvent e) -> {
            int notches = e.getWheelRotation();
            int UtG = map.calculateValues(notches, e.getX(), e.getY());
            gridField.setText("Units Per Grid Block = "+UtG);
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
                if (editBox.isSelected() && altPressed){
                    currentEdit.removeLast();
                    if (drawables.get(0) == currentEdit){
                        if (!currentEdit.hasPoints()){
                            startx = 0;
                            starty = 0;
                        }
                    }
                    for (JList<ListItem> l: editRedrawables){
                        l.repaint();
                    }
                    map.repaint();
                    map.requestFocusInWindow();
                }
                else if (editBox.isSelected() && !shiftPressed && currentEdit != null){
                    double mouseX = originalX(startx, e.getX());
                    double mouseY = originalY(starty, e.getY());
                    if (drawables.get(0) == currentEdit){
                        if (!currentEdit.hasPoints()){
                            startx = mouseX;
                            starty = mouseY;
                        }
                    }
                    double time = currentEdit.getLastTime() + 1;
                    //System.out.println("Making a point at "+mouseX+", "+mouseY+".");
                    TrajPoint newPoint = new TrajPoint(mouseX, mouseY, time);
                    currentEdit.addPoint(newPoint);
                    for (JList<ListItem> l: editRedrawables){
                        l.repaint();
                    }
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
                setCoordinateText(mouseX, mouseY);
            }
        });
    }

    void setCoordinateText(double mouseX, double mouseY) {
        currentCoord.setText("Current Coordinates: (x: "+mouseX+", y: "+mouseY+")");
    }

    public void setEditRedrawables(JList<ListItem> selectionList, JList<ListItem> editList){
        editRedrawables = new ArrayList<>();
        editRedrawables.add(selectionList);
        editRedrawables.add(editList);
    }

    public Trajectory getCurrentEdit() {
        return currentEdit;
    }

    public void setCurrentEdit(Trajectory currentEdit) {
        this.currentEdit = currentEdit;
    }

}

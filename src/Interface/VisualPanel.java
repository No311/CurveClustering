package Interface;

import Objects.TrajPoint;
import Objects.Trajectory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

class VisualPanel extends JPanel {
    public final Point origin = new Point(0, 0);
    private final Point lastPress = new Point(0, 0);
    public int drawsize = 10;
    public int UtG = 1; //units per gridblock
    private final int PtU = 24; // if zoom = 1, 24 pixels = 1 unit. Chosen for the convenience of 24.
    private int zoom = 1;
    private boolean showGrid = true;
    public int step = 24; //the size of a gridbox.
    final private Color secondaryGridColor = new Color(230, 230, 230);

    public VisualPanel() {
        setBorder(BorderFactory.createEtchedBorder());
        setBackground(Color.white);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (showGrid) {
            paintGrid(g);
        }
        //insertOval(g, 20, 20, 20); //Test method that inserts a red circle into the grid, zoomable and pannable.
    }



    private void insertOval(Graphics g, int x, int y, int size) {
        int actX = ((x*step)/UtG) + origin.x;
        int actY = ((y*step)/UtG) + origin.y;
        int actSize = (size*step)/UtG;

        g.setColor(Color.red);
        g.fillOval(actX, actY, actSize, actSize);
        g.setColor(Color.black);
    }

    private void insertGridLine(Graphics g, int x1, int y1, int x2, int y2){
        int actX1 = x1 + origin.x;
        int actY1 = y1 + origin.y;
        int actX2 = x2 + origin.x;
        int actY2 = y2 + origin.y;

        g.drawLine(actX1, actY1, actX2, actY2);
    }

    public void paintSingleGrid(Graphics g, int step){

        for (int x = -origin.x; x <= getWidth()-origin.x; x += 1){
            if (x%step == 0) {
                insertGridLine(g, x, -origin.y, x, getHeight()-origin.y);
            }
        }

        for (int y = -origin.y; y <= getHeight()-origin.y; y += 1){
            if (y%step == 0) {
                insertGridLine(g, -origin.x, y, getWidth()-origin.x, y);
            }
        }
    }

    public void paintGrid(Graphics g) {
        g.setColor(secondaryGridColor);
        paintSingleGrid(g, step);
        g.setColor(Color.lightGray);
        paintSingleGrid(g, 2*step);
        g.setColor(Color.black);
    }

    public void setShowGrid(boolean newValue) { showGrid = newValue; }

    public int getZoom() {
        return zoom;
    }

    public void setZoom (int newZoom) {
        zoom = newZoom;
    }

    public int calculateValues(int notches, int xPos, int yPos) {
        int newZoom = zoom + notches;
        if (newZoom < 1){
            return UtG;
        }
        int oldX = ((xPos*UtG)/step) - ((origin.x*UtG)/step);
        int oldY = ((yPos*UtG)/step) - ((origin.y*UtG)/step);
        int zoomCopy = newZoom;
        int newUtG = 1;
        while (newZoom > 3){
            newUtG = newUtG * 4;
            newZoom -= 3;

        }
        int newstep = (int) PtU /newZoom;
        if (newstep <= 6){
            newstep = 24;
            newUtG = newUtG * 4;
        }
        if (newUtG == 0){return UtG;}
        UtG = newUtG;
        zoom = zoomCopy;
        step = newstep;
        double newX = (int) ((oldX*step)/UtG) + origin.x;
        double newY = (int) ((oldY*step)/UtG) + origin.y;
        int difX = (int) (xPos - newX);
        int difY = (int) (yPos - newY);
        setOrigins(difX, difY);
        return UtG;

    }

    public void setOrigins(int x, int y) {
        origin.x += x;
        origin.y += y;
        repaint();
    }

    public void resetOrigins(int x, int y) {
        origin.x = x;
        origin.y = y;
    }

    public Point getLastPress() {
        return lastPress;
    }

    public void setLastPress(int x, int y) {
        lastPress.x = x;
        lastPress.y = y;
    }

    public int getDrawsize() {
        return drawsize;
    }

    public void setDrawsize(int drawsize) {
        this.drawsize = drawsize;
    }
}

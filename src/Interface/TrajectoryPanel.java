package Interface;

import Objects.TrajPoint;
import Objects.Trajectory;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class TrajectoryPanel extends VisualPanel{
    private double startx = 0;
    private double starty = 0;
    private boolean chosen = false;
    ArrayList<Trajectory> drawables = new ArrayList<>();

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!chosen && !drawables.isEmpty()) {
            TrajPoint firstpoint = drawables.get(0).getPoints().get(0);
            startx = firstpoint.origx;
            starty = firstpoint.origy;
        }
        for (Trajectory t: drawables){
            if (t.getSelected()) {
                drawTrajectory(g, t, startx, starty);
            }
        }
    }

    private double updateX(double startx, TrajPoint p){
        return (((p.origx-startx)*step)/UtG) + origin.x;
    }

    private double updateY(double starty, TrajPoint p){
        return -(((p.origy-starty)*step)/UtG) + origin.y;
    }


    private void drawTrajectory(Graphics g, Trajectory t, double startx, double starty) {
        Graphics2D g2 = (Graphics2D) g;
        size = (drawsize*step)/UtG;
        Stroke oldStroke = g2.getStroke();
        Stroke lineStroke = new BasicStroke((int) Math.ceil(size/2.0));
        g2.setStroke(lineStroke);
        TrajPoint lastPoint = null;
        g.setColor(pointsColor);
        for (TrajPoint p: t.getPoints()){
            p.x = updateX(startx, p);
            p.y = updateY(starty, p);
            if (lastPoint != null){
                g2.setColor(trajectoryColor);
                g2.draw(new Line2D.Double(lastPoint.x, lastPoint.y, p.x, p.y));
                g2.setColor(pointsColor);
                g.fillOval((int) lastPoint.x-(size/2), (int) lastPoint.y-(size/2), size, size);
            }
            g.fillOval((int) p.x-(size/2), (int) p.y-(size/2), size, size);
            lastPoint = p;
        }
        g2.setStroke(oldStroke);
        g.setColor(Color.BLACK);
    }
    public void updateDrawables(ArrayList<Trajectory> newlist){ drawables = newlist; }
}

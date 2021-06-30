package Objects;

import java.awt.*;
import java.awt.geom.Line2D;

public class Arrow {
    Line2D shaft;
    Polygon arrowhead;

    double origx = 0;
    double origy = 0;
    double tarx = 0;
    double tary = 0;
    double head;
    double size;
    Color shaftColor = new Color(67, 104, 163);
    Color headColor = new Color(67, 104, 163);


    public Arrow(double head, double size) {
        this.head = head;
        this.size = size;
    }

    public void updateCoordinates(double ox, double oy, double tx, double ty, double h, double s){
        origx = ox;
        origy = oy;
        tarx = tx;
        tary = ty;
        head = h;
        size = s;
    }

    public void updateColors(Color sC, Color hC){
        shaftColor = sC;
        headColor = hC;
    }

    public void drawArrow(Graphics2D g2){
        shaft = new Line2D.Double(origx, origy, tarx, tary);
        double dy = tary - origy;
        double dx = tarx - origx;
        double degrees = Math.atan2(dy, dx);
        double phi = Math.toRadians(40);
        double firstangle = degrees - (phi/3);
        double secondangle = degrees + (phi/3);
        double ahl = (head/10 + size);
        int[] x = {(int) tarx, (int) (tarx - ahl * Math.cos(firstangle)), (int) (tarx - ahl * Math.cos(secondangle))};
        int[] y = {(int) tary, (int) (tary - ahl * Math.sin(firstangle)), (int) (tary - ahl * Math.sin(secondangle))};
        arrowhead = new Polygon(x, y, 3);
        Color old = g2.getColor();
        g2.setColor(shaftColor);
        g2.draw(shaft);
        g2.setColor(headColor);
        g2.fillPolygon(arrowhead);
        g2.setColor(old);
    }
}

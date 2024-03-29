package Interface.VisualPanels;

import DataStructures.Reachability.Reachability;
import DataStructures.FSGMethods.QueryResult;
import DataStructures.FSGMethods.FSGMethod;
import Methods.SetSystemMethods;
import Objects.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;

public class FSGridPanel extends VisualPanel {
    private int threshold;
    private Trajectory first;
    private Trajectory second;
    private boolean queriedTC = false;
    JLabel gridField;
    JCheckBox showGridBox;
    JLabel currentCoord;
    JLabel selectedCoord;
    JTextArea infoText;
    FSGrid GridObject;
    private GridPoint[][] pointmatrix;
    private int rowmax;
    private ArrayList<GridEdge> edges = new ArrayList<>();
    private ArrayList<GridPoint> pointlist = new ArrayList<>();
    private int gridmul = 10;
    private Reachability reachdata;
    public double reachinittime;
    private FSGMethod algodata;
    public double algoinittime;
    private int querymode = 0; //0 = none, 1 = reach, 2 = algo
    private GridPoint[] reachquery = new GridPoint[2];
    private int[] algoquery = new int[3];
    final Color beforePColor = new Color(113, 20, 219);
    final Color beforeEColor = new Color(164, 84, 255);
    final Color afterPColor = new Color(16, 107, 47);
    final Color afterEColor = new Color(80, 191, 118);
    final Color selectedColor = new Color(196, 177, 0);
    final Color targetColor = new Color(196, 82, 0);
    final Color queryColumnColor = new Color(8, 2, 64);
    final Color queryRow1Color = new Color(119, 97, 133);
    final Color queryRow2Color = new Color(100, 133, 97);


    public FSGridPanel(int threshold, Trajectory first, Trajectory second, int reach, int algo,
                       JLabel gridField, JCheckBox showGridBox, JLabel currentCoord, JLabel selectedCoord,
                       JTextArea infoText, SetSystemMethods methods) {
        super();
        super.setZoomable(true);
        super.setShowGrid(false);
        super.drawsize = 1;
        this.first = first;
        this.second = second;
        this.threshold = threshold;
        this.gridField = gridField;
        this.showGridBox = showGridBox;
        this.currentCoord = currentCoord;
        this.selectedCoord = selectedCoord;
        this.infoText = infoText;
        initDFDGrid(methods, reach, algo);
    }

    private void initDFDGrid(SetSystemMethods methods, int reachInt, int algoInt) {
        GridObject = new FSGrid(first, second, threshold, getStandardDist(), size);
        pointmatrix = GridObject.getPointsMatrix();
        pointlist = GridObject.getPointList();
        edges = GridObject.getEdgesList();
        rowmax = GridObject.getRowmax();
        if (reachInt != 0) {
            reachdata = methods.initReach(first, second, reachInt, GridObject, true);
        }
        if (reachdata != null && algoInt != 0) {
            algodata = methods.initAlgo(first, second, algoInt, reachdata, GridObject);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (GridPoint p: pointlist){
            p.x = updateX(p.column);
            p.y = updateY(p.actualrow);
        }
        //draw background
        g.setColor(backGroundColor);
        double fx = origin.x;
        double fy = gridmul*((((double)-rowmax)*step)/UtG) + origin.y;
        double width = gridmul*((((double) pointmatrix[0].length-1)*step)/UtG);
        double height = gridmul*((((double) rowmax)*step)/UtG);
        g.fillRect((int) fx, (int) fy, (int) width, (int) height);
        //draw the actual DFD Grid
        if (queriedTC){
            drawQueried(g);
        }
        drawEdges(g);
        drawGrid(g);
    }

    private void drawQueried(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Stroke oldStroke = g2.getStroke();
        Stroke lineStroke = new BasicStroke((int) Math.ceil(size));
        g2.setStroke(lineStroke);
        double rowstart = updateX(0);
        double rowend = updateX(pointmatrix[0].length-1);
        double row1coord = updateY(pointmatrix.length - 1 - (algoquery[0]));
        double row2coord = updateY(pointmatrix.length - 1 - (algoquery[1]));
        double colstart = updateY(pointmatrix.length-1);
        double colend = updateY(0);
        double pcoord = updateX(algoquery[2]);
        g.setColor(queryRow1Color);
        Line2D row1Line= new Line2D.Double(rowstart, row1coord, rowend, row1coord);
        g2.draw(row1Line);
        g.setColor(queryRow2Color);
        Line2D row2Line= new Line2D.Double(rowstart, row2coord, rowend, row2coord);
        g2.draw(row2Line);
        g.setColor(queryColumnColor);
        Line2D columnLine= new Line2D.Double(pcoord, colstart, pcoord, colend);
        g2.draw(columnLine);
        g2.setStroke(oldStroke);
        g.setColor(Color.BLACK);
    }

    private void drawEdges(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g.setColor(trajectoryColor);
        Stroke oldStroke = g2.getStroke();
        Stroke lineStroke = new BasicStroke((int) Math.ceil(size/4.0));
        g2.setStroke(lineStroke);
        for (GridEdge edge: edges){
            Color edgeColor;
            if (edge.getOrigin().getSelected() == 0 || edge.getOrigin().getSelected() == 2){
                edgeColor = afterEColor;
            } else if (edge.getTarget().getSelected() == 0 || edge.getTarget().getSelected() == 1){
                edgeColor = beforeEColor;
            } else {
                edgeColor = trajectoryColor;
            }
            GridPoint o = edge.getOrigin();
            GridPoint t = edge.getTarget();
            edge.getRepresentation().updateCoordinates(o.x, o.y, t.x, t.y, getStandardDist(), size);
            edge.getRepresentation().updateColors(edgeColor, edgeColor);
            edge.getRepresentation().drawArrow(g2);
        }
        g2.setStroke(oldStroke);
        g.setColor(Color.BLACK);
    }

    public void drawGrid(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g.setColor(pointsColor);
        for (int i = 0; i <= rowmax; i++){
            for (int j = 0; j < pointmatrix[0].length; j++){
                GridPoint p = pointmatrix[i][j];
                if (p != null) {
                    switch (p.getSelected()) {
                        case 0 -> g.setColor(selectedColor);
                        case 1 -> g.setColor(beforePColor);
                        case 2 -> g.setColor(afterPColor);
                        default -> g.setColor(pointsColor);
                    }
                    if (p.isTarget()){
                        g.setColor(targetColor);
                    }
                    p.paint(g2, size);
                } else {
                    int vx = gridmul*((j*step)/UtG) + origin.x;
                    int vy = gridmul*(((i - rowmax)*step)/UtG) + origin.y;
                    g.setColor(voidPointColor);
                    g.fillOval(vx - (size / 2), vy - (size / 2), size, size);
                }
                g.setColor(pointsColor);
            }
        }
        g.setColor(Color.BLACK);
    }

    private double updateX(int column){ return gridmul*((((double) column)*step)/UtG) + origin.x; }

    private double updateY(int actualrow){ return gridmul*((((double) actualrow - rowmax)*step)/UtG) + origin.y; }

    private int originalColumn(double x) {
        return Math.min(Math.max((int) Math.round((((x-origin.x)/gridmul)*UtG)/step),0), pointmatrix[0].length-1);}
    private int originalRow(double y) {
        return Math.min(Math.max((int) -Math.round((((y-origin.y)/gridmul)*UtG)/step), 0), pointmatrix.length-1);
    }

    @Override
    public double getStandardDist(){
        return (gridmul*((double) (step)/UtG));
    }

    public int getGridmul() {
        return gridmul;
    }

    public void setGridmul(int gridmul) {
        this.gridmul = gridmul;
    }

    public void printMatrixBool(){
        for (int i = 0; i < pointmatrix.length; i++) {
            System.out.print("Row "+i+":");
            for (int j = 0; j < pointmatrix[0].length; j++) {
                if (pointmatrix[i][j] != null) {
                    System.out.print("1, ");
                } else {
                    System.out.print(" , ");
                }
            }
            System.out.println();
        }
    }

    public void printEdgesInPoints(){
        for (GridPoint p: pointlist){
            p.printGridEdges();
        }
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
                if (SwingUtilities.isRightMouseButton(e) && querymode != 0){
                    infoText.append("Query cancelled.\n\n");
                    querymode = 0;
                    return;
                }
                GridPoint selected = null;
                for (GridPoint p: pointlist){
                    if (p.getDrawable().contains(e.getPoint())){
                        selected = p;
                        break;
                    }
                }
                if (selected != null){
                    if (querymode == 0){
                        doSelection(selected);
                        queriedTC = false;
                    }
                    else if (querymode == 1){
                        queriedTC = false;
                        if (reachquery[0] == null){
                            reachquery[0] = selected;
                            infoText.append("     Start vertex ("+selected.row+", "+selected.column+") selected. \n" +
                                    "     Please select goal vertex...\n");
                        }
                        else if (reachquery[1] == null){
                            reachquery[1] = selected;
                            infoText.append("     Goal vertex ("+selected.row+", "+selected.column+") selected. \n" +
                                    "     Querying Reachability Oracle...\n");
                            long starttime = System.currentTimeMillis();
                            boolean result = reachdata.query(reachquery[0], reachquery[1]);
                            long endtime = System.currentTimeMillis();
                            double time = ((double) endtime - (double) starttime)/1000;
                            infoText.append("The answer to the query is "+result+".\n" +
                                    "Time: " + time + "s\n\n");
                            doSelection(reachquery[0]);
                            reachquery[1].setTarget(true);
                            querymode = 0;
                        }
                    }

                    else if (querymode == 2){
                        queriedTC = false;
                        if (algoquery[0] == -1){
                            algoquery[0] = selected.row;
                            infoText.append("     Row "+algoquery[0]+" selected as Row 1. \n" +
                                    "     Please select a vertex from\n     the second row...\n");
                        }
                        else if (algoquery[1] == -1){
                            algoquery[1] = selected.row;
                            infoText.append("     Row "+algoquery[1]+" selected as Row 2. \n" +
                                    "     Please select a vertex from\n     the column P...\n");
                        }
                        else if (algoquery[2] == -1) {
                            algoquery[2] = selected.column;
                            infoText.append("     Column " + algoquery[2] + " selected as Column P. \n" +
                                    "     Querying Free Space Grid Method Oracle...\n");
                            long starttime = System.currentTimeMillis();
                            QueryResult result = algodata.query(algoquery[0], algoquery[1], algoquery[2]);
                            long endtime = System.currentTimeMillis();
                            double time = ((double) endtime - (double) starttime)/1000;
                            if (result == null) {
                                infoText.append("Answer: With subtrajectory Q \n" +
                                        "     from point " + algoquery[0] + " to point " + algoquery[1] + " of "+first.getName()+",\n" +
                                        "     a subtrajectory from "+second.getName()+"\n" +
                                        "     covered by Q including point "+algoquery[2]+"\n" +
                                        "     from "+second.getName()+"\n" +
                                        "     does not exist.\n" +
                                        "     Time: " + time + "s\n\n");
                                querymode = 0;
                            } else {
                                doSelection(result.start);
                                result.goal.setTarget(true);
                                infoText.append("Answer: With subtrajectory Q \n" +
                                        "     from point " + algoquery[0] + " to point " + algoquery[1] + " of "+first.getName()+",\n" +
                                        "     a subtrajectory from "+second.getName()+"\n" +
                                        "     covered by Q including point "+algoquery[2]+"\n" +
                                        "     from "+second.getName()+"\n" +
                                        "     starts at point " + result.start.column + " and ends at point " +
                                        result.goal.column + "\n     of "+second.getName()+".\n" +
                                        "     Time: " + time + "s\n\n");
                                querymode = 0;
                                queriedTC = true;
                            }
                        }
                    }


                }
                map.repaint();
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
                int mouseX = originalColumn(e.getX());
                int mouseY = originalRow(e.getY());
                currentCoord.setText("Nearest Vertex: (row: "+mouseY+", column: "+mouseX+")");
            }
        });
    }

    private void doSelection(GridPoint selected) {
        selected.setSelected(true);
        selectedCoord.setText("Selected Coordinates: (row: "+ selected.row+", column: "+
               selected.column+") ");
        for (GridPoint p: pointlist){
            if (p != selected){
                p.reset();
            }
        }
        for (GridPoint b: selected.getReachedFrom()){
            b.setBefore(true);
        }
        for (GridPoint a: selected.getReachable()){
            a.setAfter(true);
        }
    }

    public void setQuerymode(int querymode) {
        this.querymode = querymode;
        if (querymode == 1){
            Arrays.fill(reachquery, null);
        }
        else if (querymode == 2){
            Arrays.fill(algoquery, -1);
        }
    }
    public int getQuerymode(){
        return querymode;
    }
}

package Algorithms;

import Objects.TrajPoint;
import Objects.Trajectory;

public class Sampling {

    public Trajectory sampleTrajectory(Trajectory t, int verticeAmount, boolean time){
        if (time){
            return sampleTrajectoryTime(t, verticeAmount);
        }
        return sampleTrajectoryDist(t, verticeAmount);
    }

    public Trajectory sampleTrajectoryTime(Trajectory t, int verticeAmount) {
        int tVertices = t.getPoints().size()-1;
        double tProgress = (double) (tVertices)/(verticeAmount-1);
        String name = t.getName() + "_sampled" + verticeAmount;
        Trajectory result = new Trajectory(name);
        TrajPoint lastPoint = t.getPoints().get(t.getPoints().size()-1);
        double i = 0;
        for (int time = 0; time < verticeAmount-1; time++){
            System.out.println(i);
            int first = (int) i;
            TrajPoint firstVertex = t.getPoints().get(first);
            TrajPoint secondVertex = t.getPoints().get(first+1);
            double xDist = firstVertex.origx - secondVertex.origx;
            double yDist = firstVertex.origy - secondVertex.origy;
            double xCoord = firstVertex.origx - (xDist*(i-first));
            double yCoord = firstVertex.origy - (yDist*(i-first));
            result.addPoint(new TrajPoint(xCoord, yCoord, time));
            i+=tProgress;
        }
        result.addPoint(new TrajPoint(lastPoint.origx, lastPoint.origy, verticeAmount-1));
        return result;
    }

    private Trajectory sampleTrajectoryDist(Trajectory t, int verticeAmount) {
        int tVertices = t.getPoints().size();
        String name = t.getName() + "_sampled" + verticeAmount;
        Trajectory result = new Trajectory(name);
        TrajPoint lastPoint = t.getPoints().get(t.getPoints().size()-1);
        double totalDistance = 0;
        double[] distances = new double[tVertices];
        for (int i = 0; i < tVertices-1; i++){
            TrajPoint current = t.getPoints().get(i);
            TrajPoint next = t.getPoints().get(i+1);
            double Xdist = Math.abs(current.origx - next.origx);
            double Ydist = Math.abs(current.origy - next.origy);
            double dist = Math.sqrt(Math.pow(Xdist, 2) + Math.pow(Ydist, 2));
            totalDistance += dist;
            distances[i] = dist;
        }
        double distancePerPoint = totalDistance/(verticeAmount-1);
        double currentDistance = 0;
        int currentPoint = 0;
        for (int time = 0; time < verticeAmount-1; time++){
            double currentProgress = currentDistance/distances[currentPoint];
            while (currentProgress > 1){
                currentDistance = currentDistance-distances[currentPoint];
                currentPoint++;
                currentProgress = currentDistance/distances[currentPoint];
            }
            TrajPoint firstVertex = t.getPoints().get(currentPoint);
            TrajPoint secondVertex = t.getPoints().get(currentPoint+1);
            double xDist = firstVertex.origx - secondVertex.origx;
            double yDist = firstVertex.origy - secondVertex.origy;
            double xCoord = firstVertex.origx - (xDist*currentProgress);
            double yCoord = firstVertex.origy - (yDist*currentProgress);
            result.addPoint(new TrajPoint(xCoord, yCoord, time));
            currentDistance+=distancePerPoint;
        }
        result.addPoint(new TrajPoint(lastPoint.origx, lastPoint.origy, verticeAmount-1));
        return result;
    }
}

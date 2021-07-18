package Processable;


import Interface.GUIMain;
import Interface.ListItem;
import Interface.TrajectoryInitializer;
import Objects.TrajPoint;
import Objects.Trajectory;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class GetRunningTimes {
    //Due to the disjointed nature of the FSG methods, this piece of code extracts the total times spent on certain
    //parts from the output in a text file.
    public static void main(String[] args) {
        FileDialog fd = new FileDialog(new JFrame(), "Open Output Data...", FileDialog.LOAD);
        fd.setMultipleMode(true);
        fd.setFilenameFilter((dir, name) -> name.matches(".+\\.txt"));
        fd.setVisible(true);
        File[] files = fd.getFiles();
        if (!(files.length == 0)) {
            for (File f : files) {
                System.out.println(f.getName());
                OutputTimes(f);
            }
        }
    }

    private static void OutputTimes(File file) {
        FileInputStream fin = null;
        DataInputStream din = null;
        try {
            fin = new FileInputStream(file);
            din = new DataInputStream(fin);
            BufferedReader br = new BufferedReader(new InputStreamReader(din));
            String line;
            int totalExecutions = 1;
            double totalReachTime = 0.0;
            double totalFSGTime = 0.0;
            double totalQueryTime = 0.0;
            double totalTime = 0.0;
            while ((line = br.readLine()) != null){
                if (line.matches("Method Initialized in \\d+.\\d+ seconds.")){
                    System.out.println("Execution Number: "+totalExecutions);
                    System.out.println("Total Reachability Time: "+totalReachTime);
                    System.out.println("Total FSG Time: "+totalFSGTime);
                    System.out.println("Total Query Time: "+totalQueryTime);
                    totalReachTime = 0.0;
                    totalFSGTime = 0.0;
                    totalQueryTime = 0.0;
                    totalExecutions++;
                    String[] splitline = line.split("\\s+");
                    totalTime += Double.parseDouble(splitline[3]);
                } else if (line.matches("\\s+Reachability Initialized in \\d+.\\d+ seconds.")){
                    String[] splitline = line.split("\\s+");
                    totalReachTime += Double.parseDouble(splitline[4]);
                } else if (line.matches("\\s+Algorithm Initialized in \\d+.\\d+ seconds.")||
                line.matches("\\s+FSG Structure Initialized in \\d+.\\d+ seconds.")){
                    String[] splitline = line.split("\\s+");
                    totalFSGTime += Double.parseDouble(splitline[4]);
                } else if (line.matches("\\s+Coverage Information Initialized in \\d+.\\d+ seconds.")){
                    String[] splitline = line.split("\\s+");
                    totalQueryTime += Double.parseDouble(splitline[5]);
                }
                else if (line.matches("Greedy Set Cover Completed in \\d+.\\d+ seconds.")){
                    String[] splitline = line.split("\\s+");
                    System.out.println("Greedy Set Time: "+splitline[5]);
                    totalTime += Double.parseDouble(splitline[5]);
                    System.out.println("Total Time: "+totalTime+"\n");
                    totalTime = 0.0;
                }




            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return;
        } catch (IOException e) {
            System.out.println("IOException");
            return;
        } finally {
            try {
                if (fin != null) fin.close();
                if (din != null) din.close();
            } catch (IOException e) {
                System.out.println("IOException 2");
            }
        }
    }
}

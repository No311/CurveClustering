package Interface;

import Objects.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class TrajectoryInitializer {

    public static Trajectory loadFile(File file, JTextArea infoText){
        Trajectory result = new Trajectory(file);
        FileInputStream fin = null;
        DataInputStream din = null;
        try {
            fin = new FileInputStream(file);
            din = new DataInputStream(fin);
            BufferedReader br = new BufferedReader(new InputStreamReader(din));
            String line;
            while ((line = br.readLine()) != null){
                String[] tokens = line.split("[^\\d.-]");
                TrajPoint p = new TrajPoint(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]),
                        Double.parseDouble(tokens[2]));
                result.addPoint(p);
            }
        } catch (FileNotFoundException e) {
            infoText.append("\n File not Found...");
            return null;
        } catch (IOException e) {
            infoText.append("\n File contains I/O Error...");
            return null;
        } finally {
            try {
                if (fin != null) fin.close();
                if (din != null) din.close();
            } catch (IOException e) {
                infoText.append("\n Error closing file...");
            }
        }
        return result;
    }

    public static void saveFile(Trajectory t, JTextArea infoText){
        FileDialog fd = new FileDialog(new JFrame(), "Save Trajectory...", FileDialog.SAVE);
        fd.setVisible(true);
        try {
            if (fd.getFiles().length > 0) {
                String filename = fd.getFiles()[0].getAbsolutePath();
                String extension = filename.substring(filename.length() - 4);
                System.out.println(extension);
                if (!extension.equals(".txt")) {
                    filename += ".txt";
                }
                BufferedWriter bw = new BufferedWriter(new FileWriter(
                        filename, false));
                StringBuilder toWrite = new StringBuilder();
                for (TrajPoint p : t.getPoints()) {
                    toWrite.append(p.toString());
                }
                bw.write(toWrite.toString());
                bw.close();
            } else {
                infoText.append("Could not write to file, save cancelled.\n");
            }
        } catch (Exception e) {
            infoText.append("Could not write to file.\n");
            e.printStackTrace();
        }
    }
}

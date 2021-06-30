package Interface.Tabs;

import javax.swing.*;
import java.util.ArrayList;

public class Tab {
    String title = "";
    ArrayList<JComponent> localinteractables = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String s) {
        title = s;
    }

    public ArrayList<JComponent> getLocalinteractables() {
        return localinteractables;
    }
}

package Interface.Wizards;

import Methods.GeneralFunctions;
import Interface.ListItem;
import Interface.Tabs.Tab;
import Methods.SetSystemMethods;

import javax.swing.*;
import java.util.ArrayList;

public abstract class Wizard {
    ArrayList<JComponent> interactables;
    int amount;
    boolean wizardCancel = true;
    GeneralFunctions gF = new GeneralFunctions();
    JFrame frame;
    ArrayList<Tab> tabs = new ArrayList<>();

    abstract public void init(JList<ListItem> selection, JTextArea infoText, JTabbedPane mainPane,
                     ArrayList<JComponent> interactables, int setAmount, int framewidth, SetSystemMethods methods);

    public ArrayList<JComponent> getInteractables() {
        return interactables;
    }

    public int getAmount() {
        return amount;
    }
    public boolean isWizardCancel() {
        return wizardCancel;
    }
    public JFrame getFrame() {
        return frame;
    }

    public ArrayList<Tab> getTabs(){
        return tabs;
    }
}

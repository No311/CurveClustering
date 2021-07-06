package Interface;

import Objects.Trajectory;

public class ListItem {
    private final String label;
    private final Trajectory t;
    private boolean isSelected = true;

    public ListItem(String label, Trajectory t) {
        this.label = label;
        this.t = t;
    }

    public String toString() {
        String l = label + " ("+t.getPoints().size()+")";
        return l;
    }

    public Trajectory getT() {
        return t;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

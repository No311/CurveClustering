package Interface;

import Interface.VisualPanels.TrajectoryPanel;
import Objects.GridPoint;
import Objects.GridEdge;
import Objects.NamedInt;
import Objects.Trajectory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GeneralFunctions {
    public void buttonDependency(JButton button, JTextField field, Function<String, Boolean> function) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (function.apply(field.getText())){
                    button.setEnabled(true);
                } else {
                    button.setEnabled(false);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (function.apply(field.getText())){
                    button.setEnabled(true);
                } else {
                    button.setEnabled(false);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (function.apply(field.getText())){
                    button.setEnabled(true);
                } else {
                    button.setEnabled(false);
                }
            }
        });
    }

    public void lButtonDependency(JButton button, JTextField field, JTextField controlField,
                                  ArrayList<JCheckBox> choiceBoxes,
                                  Function<String, Boolean> function) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                doCheck();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                doCheck();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                doCheck();
            }

            private void doCheck() {
                boolean choiceEnabled = false;
                for (JCheckBox box: choiceBoxes){
                    if (box.isSelected()){
                        choiceEnabled = true;
                    }
                }
                String s = controlField.getText();
                if (function.apply(field.getText()) && function.apply(s) && !s.equals("") && choiceEnabled){
                    button.setEnabled(true);
                } else {button.setEnabled(false);}
            }
        });
    }

    public void disable(ArrayList<JComponent> toDisable){
        for (JComponent i: toDisable){
            i.setEnabled(false);
        }
    }

    public void enable(ArrayList<JComponent> toEnable){
        for (JComponent i: toEnable){
            i.setEnabled(true);
        }
    }

    public void buttonChoiceDependency(JButton button, JTextField field, Function<String, Boolean> function,
                                       ArrayList<JCheckBox> choiceBoxes) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                doTheActionListener(choiceBoxes, field, function, button);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                doTheActionListener(choiceBoxes, field, function, button);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                doTheActionListener(choiceBoxes, field, function, button);
            }
        });
        for (JCheckBox box: choiceBoxes){
            box.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doTheActionListener(choiceBoxes, field, function, button);
                }
            });
        }

    }

    public void doTheActionListener(ArrayList<JCheckBox> choiceBoxes, JTextField field,
                                    Function<String, Boolean> function, JButton button) {
        boolean choiceEnabled = false;
        for (JCheckBox box: choiceBoxes){
            if (box.isSelected()){
                choiceEnabled = true;
            }
        }
        if (function.apply(field.getText()) && choiceEnabled){
            button.setEnabled(true);
        } else {
            button.setEnabled(false);
        }
    }

    public void setCellRenderer(JList<ListItem> list){
        list.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<ListItem> list, ListItem value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value.toString());
                return this;
            }
        });
    }

    public NamedInt getReachInfo(JCheckBox reach){
        switch (reach.getActionCommand()) {
            case "reachNaive" -> {
                return new NamedInt("naive", 1);
            }
            default -> {
                return new NamedInt("no", 0);
            }
        }
    }

    public NamedInt getAlgoInfo(JCheckBox algo){
        switch (algo.getActionCommand()) {
            case "algoNaivePrep" -> {
                return new NamedInt("Naive Prep", 1);
            }
            case "algoLog" -> {
                return new NamedInt("Log Query", 2);
            }
            case "algoNoOpt" -> {
                return new NamedInt("Log Query (No Optimization)", 3);
            }
            case "algoNoPrep" -> {
                return new NamedInt("Naive No Prep", 4);
            }
            case "algoNaiveLog" -> {
                return new NamedInt("Naive Log Query", 5);
            }
            default -> {
                return new NamedInt("no", 0);
            }
        }
    }

    public NamedInt getQueryInfo(JCheckBox query){
        switch (query.getActionCommand()) {
            case "queryNaive" -> {
                return new NamedInt("naive", 1);
            }
            case "queryLongjump" -> {
                return new NamedInt("Long Jump", 2);
            }
            default -> {
                return new NamedInt("no", 0);
            }
        }
    }




}

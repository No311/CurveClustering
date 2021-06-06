package Interface;

import Objects.GridPoint;
import Objects.GridEdge;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
}

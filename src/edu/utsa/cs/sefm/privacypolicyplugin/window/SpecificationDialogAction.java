package edu.utsa.cs.sefm.privacypolicyplugin.window;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rocky on 10/18/2015.
 */
public class SpecificationDialogAction extends AnAction {
    List<JLabel> methodRows;
    List<JComboBox> actionRows;
    List<JComboBox> phraseRows;
    List<JTextField> purposeRows;
    public void actionPerformed(AnActionEvent e) {
        SpecificationDialog sd = new SpecificationDialog();
        // TODO make the window visible
        System.out.println("hi");
        methodRows = new ArrayList<>();
        actionRows = new ArrayList<>();
        phraseRows = new ArrayList<>();
        purposeRows = new ArrayList<>();
        // add method list. each column will need to be filled
        // actionCol, methodCol, phraseCol, purposeCol
        String [] actions = {"one", "two"};
        String [] phrases = {"three", "four"};
//        addRow(sd, 0, "hello world", actions, phrases);

    }

    private void addRow(SpecificationDialog sd, int index, String method, String [] actions, String [] phrases){
        methodRows.add(
                index, new JLabel());
        methodRows.get(index).setText(method);

        actionRows.add(index, new ComboBox(actions));
        phraseRows.add(index, new ComboBox(phrases));
        purposeRows.add(index, new JTextField());

        sd.getMethodCol().add(methodRows.get(index));
        sd.getActionCol().add(actionRows.get(index));
        sd.getPhraseCol().add(phraseRows.get(index));
        sd.getPurposeCol().add(purposeRows.get(index));

    }
}



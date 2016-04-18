package edu.utsa.cs.sefm.privacypolicyplugin.window;

import javax.swing.*;

/**
 * Created by Rocky on 4/18/2016.
 */
public class SpecificationDialog {
    private JPanel rootPanel;
    private JPanel instructionPanel;
    private JLabel instructionLabel;
    private JScrollPane specificationsPane;
    private JPanel specificationsTablePanel;
    private JPanel actionCol;
    private JPanel methodCol;
    private JPanel phraseCol;
    private JPanel purposeCol;
    private JLabel methodLabel;
    private JLabel actionLabel;
    private JLabel phraseLabel;
    private JLabel purposeLabel;
    private JPanel buttonPanel;
    private JButton doneButton;

    public JPanel getActionCol() {
        return actionCol;
    }

    public JPanel getMethodCol() {
        return methodCol;
    }

    public JPanel getPhraseCol() {
        return phraseCol;
    }

    public JPanel getPurposeCol() {
        return purposeCol;
    }


}

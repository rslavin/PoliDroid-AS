package edu.utsa.cs.sefm.privacypolicyplugin.window;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Rocky on 4/18/2016.
 */
public class SpecificationDialog extends JFrame {
    private JPanel rootPanel;
    private JPanel instructionPanel;
    private JLabel instructionLabel;
    private JScrollPane specificationsPane;
    private JPanel specificationsTablePanel;
    private JPanel actionCol;
    private JPanel methodCol;
    private JPanel phraseCol;
    private JPanel purposeCol;
    private JLabel actionLabel;
    private JLabel phraseLabel;
    private JLabel purposeLabel;
    private JPanel buttonPanel;
    private JButton doneButton;

    public SpecificationDialog() {
        super("Specification Generation");
        setContentPane(rootPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dimension.width / 2 - this.getSize().width / 2, dimension.height / 2 - this.getSize().height / 2);

        // set layouts manually because setting it in the gui designer dosen't seem to work
        methodCol.setLayout(new BoxLayout(methodCol, BoxLayout.PAGE_AXIS));
        actionCol.setLayout(new BoxLayout(actionCol, BoxLayout.PAGE_AXIS));
        phraseCol.setLayout(new BoxLayout(phraseCol, BoxLayout.PAGE_AXIS));
        purposeCol.setLayout(new BoxLayout(purposeCol, BoxLayout.PAGE_AXIS));


        setVisible(true);
    }

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

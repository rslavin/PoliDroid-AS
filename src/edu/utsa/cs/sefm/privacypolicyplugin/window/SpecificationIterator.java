package edu.utsa.cs.sefm.privacypolicyplugin.window;

import com.intellij.openapi.application.ApplicationManager;
import edu.utsa.cs.sefm.privacypolicyplugin.PolicyViolationAppComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Rocky on 4/28/2016.
 */
public class SpecificationIterator extends JFrame{
    private JLabel instructions;
    private JPanel formPanel;
    private JPanel instructionsPanel;
    private JComboBox verbSelect;
    private JPanel rootPanel;
    private JButton nextButton;
    private JLabel phraseLabel;
    private JLabel verbLabel;
    private JLabel necessaryFunctionalityLabel;
    private JLabel necessaryBusinessLabel;
    private JLabel howLabel;
    private JLabel storeLabel;
    private JLabel howLongLabel;
    private JLabel shareLabel;
    private JLabel shareHowLabel;
    private JLabel whoLabel;
    private JComboBox phraseSelect;
    private JComboBox necessaryFunctionalitySelect;
    private JComboBox necessaryBusinessSelect;
    private JTextField howField;
    private JComboBox storeSelect;
    private JTextField howLongField;
    private JComboBox shareSelect;
    private JTextField shareHowField;
    private JTextField whoField;
    private JLabel methodLabel;
    private PolicyViolationAppComponent comp;
    private Object [] apisInCode;
    private int totalApis;
    private int currentApi;

    public SpecificationIterator(){
        super("Specification Generation");
        setContentPane(rootPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dimension.width / 2 - this.getSize().width / 2, dimension.height / 2 - this.getSize().height / 2);
        comp = (PolicyViolationAppComponent) ApplicationManager.getApplication().getComponent("PolicyViolationAppComponent");
        totalApis = comp.apisInCode.size();

        if(totalApis > 0) {
            apisInCode = comp.apisInCode.toArray();
            currentApi = 0; // counter
        }else
        // display some kind of message and close


        populateFields();

        setVisible(true);

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // store things

                // populate fields

            }
        });
    }

    private void populateFields(){
        if(currentApi < totalApis) {
            this.methodLabel.setText((String) apisInCode[currentApi++]);
        }else{
            // render specifications
        }
    }
}

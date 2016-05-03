package edu.utsa.cs.sefm.privacypolicyplugin.window;

import com.intellij.openapi.application.ApplicationManager;
import edu.utsa.cs.sefm.privacypolicyplugin.PolicyViolationAppComponent;
import edu.utsa.cs.sefm.privacypolicyplugin.mappings.Api;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

/**
 * Created by Rocky on 4/28/2016.
 */
public class SpecificationIterator extends JFrame {
    public static final String[] VERBS = {"Collect", "Share", "Used only on device"};
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
    private Object[] apisInCode;
    private int totalApis;
    private int currentApi;

    public SpecificationIterator() {
        super("Specification Generation");
        setContentPane(rootPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dimension.width / 2 - this.getSize().width / 2, dimension.height / 2 - this.getSize().height / 2);
        comp = (PolicyViolationAppComponent) ApplicationManager.getApplication().getComponent("PolicyViolationAppComponent");
        totalApis = comp.apisInCode.size();

        if (totalApis > 0) {
            apisInCode = comp.apisInCode.toArray();
            currentApi = 0; // counter
        } else {
            // display some kind of message and close
        }


        populateFields();

        setVisible(true);

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // store things

                // populate fields
                populateFields();

            }
        });
    }

    private void populateFields() {
        if (currentApi < totalApis) {
            String api = ((String) apisInCode[currentApi++]);

            // method  name
            methodLabel.setText(api);

            // method phrases
            phraseSelect.removeAllItems();
            for (Api existingApi : comp.apis)
                if (existingApi.api.toLowerCase().equals(api.toLowerCase()))
                    for (String apiPhrase : existingApi.phrases)
                        this.phraseSelect.addItem(apiPhrase);

            // verbs
            verbSelect.removeAllItems();
            for (String verb : SpecificationIterator.VERBS)
                verbSelect.addItem(verb);

            // booleans
            setYesNoCombo(necessaryFunctionalitySelect);
            setYesNoCombo(necessaryBusinessSelect);
            setYesNoCombo(storeSelect);
            setYesNoCombo(shareSelect);

        } else {
            // check if there has been any data saved. If not, then no methods were found

            // display specifications
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            SpecificationDisplay sd = new SpecificationDisplay();

            // if no methods found, return a warning

        }
    }

    private void setYesNoCombo(JComboBox box) {
        box.removeAllItems();
        box.addItem("No");
        box.addItem("Yes");
    }
}

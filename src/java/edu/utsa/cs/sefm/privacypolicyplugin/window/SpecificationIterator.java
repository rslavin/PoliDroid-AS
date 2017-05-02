package edu.utsa.cs.sefm.privacypolicyplugin.window;

import com.intellij.openapi.application.ApplicationManager;
import edu.utsa.cs.sefm.privacypolicyplugin.PolicyViolationAppComponent;
import edu.utsa.cs.sefm.privacypolicyplugin.models.ApiMethod;
import edu.utsa.cs.sefm.privacypolicyplugin.models.Specification;
import edu.utsa.cs.sefm.privacypolicyplugin.nlp.ontology.preprocess.ParagraphProcessor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class SpecificationIterator extends JFrame {
    public static final String[] VERBS = {"COLLECTED", "SHARED", "ACCESSED"};
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

    SpecificationIterator() {
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
            // TODO add notification and close
            PolicyViolationAppComponent.logger.warn("No sensitive API invocations detected");
        }

        populateFields();
        setVisible(true);

        nextButton.addActionListener(e -> {
            storeFields(); // store things
            populateFields(); // populate fields
        });
    }

    private void storeFields() {
        if (comp.specifications != null) {
            Specification spec = comp.findSpec(methodLabel.getText());
            if (spec == null)
                spec = new Specification(methodLabel.getText());
            spec.setPhrase((String) phraseSelect.getSelectedItem());
            spec.setVerb((String) verbSelect.getSelectedItem());
            spec.setNecessaryBusiness((String) necessaryBusinessSelect.getSelectedItem());
            spec.setNecessaryFunctionality((String) necessaryFunctionalitySelect.getSelectedItem());
            spec.setStore((String) storeSelect.getSelectedItem());
            spec.setShare((String) shareSelect.getSelectedItem());
            spec.setHow(howField.getText());
            spec.setHowLong(howLongField.getText());
            spec.setShareHow(shareHowField.getText());
            spec.setWho(whoField.getText());

            comp.specifications.add(spec);
        }
    }

    private void populateFields() {
        if (currentApi < totalApis) {
            String api = ((String) apisInCode[currentApi++]);

            // method  name
            methodLabel.setText(api);

            // method phrases
            phraseSelect.removeAllItems();
            for (ApiMethod existingApiMethod : comp.apiMethods)
                if (existingApiMethod.toSimpleString().equals(api.toLowerCase()))
                    for (String apiPhrase : existingApiMethod.phrases)
                        this.phraseSelect.addItem(apiPhrase);

            // verbs
            verbSelect.removeAllItems();
            for (String verb : ParagraphProcessor.VERBS_PAST)
                verbSelect.addItem(verb);

            // booleans
            setYesNoCombo(necessaryFunctionalitySelect);
            setYesNoCombo(necessaryBusinessSelect);
            setYesNoCombo(storeSelect);
            setYesNoCombo(shareSelect);

            // fields
            howField.setText("");
            howLongField.setText("");
            shareHowField.setText("");
            whoField.setText("");

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

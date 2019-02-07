package edu.utsa.cs.sefm.privacypolicyplugin.window;

import com.intellij.openapi.application.ApplicationManager;
import edu.utsa.cs.sefm.privacypolicyplugin.PolicyViolationAppComponent;
import edu.utsa.cs.sefm.privacypolicyplugin.models.Specification;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Rocky on 5/3/2016.
 */
public class SpecificationDisplay extends JFrame{
    private JPanel rootPanel;
    private JLabel instructionsLabel;
    private JTextPane specificationPane;
    private JScrollPane specificationScroll;
    private JTextArea specificationTextArea;
    private JLabel specificationLabel;
    private PolicyViolationAppComponent comp;
    private String specString;

    public SpecificationDisplay(){
        super("Specifications");
        setContentPane(rootPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        comp = ApplicationManager.getApplication().getComponent(PolicyViolationAppComponent.class);
        specString = "";
        for(Specification spec : comp.specifications)
            specString += spec + "\n\n==================\n\n";

        specificationTextArea.setText(specString);
        specificationTextArea.setCaretPosition(0);
        pack();
        setLocation(dimension.width / 2 - this.getSize().width / 2, dimension.height / 2 - this.getSize().height / 2);

        setVisible(true);
    }
}

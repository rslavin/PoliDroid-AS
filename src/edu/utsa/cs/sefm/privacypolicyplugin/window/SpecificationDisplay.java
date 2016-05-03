package edu.utsa.cs.sefm.privacypolicyplugin.window;

import com.intellij.openapi.application.ApplicationManager;
import edu.utsa.cs.sefm.privacypolicyplugin.PolicyViolationAppComponent;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Rocky on 5/3/2016.
 */
public class SpecificationDisplay extends JFrame{
    private JPanel rootPanel;
    private JLabel instructionsLabel;
    private JTextPane specificationPane;
    private PolicyViolationAppComponent comp;

    public SpecificationDisplay(){
        super("Specifications");
        setContentPane(rootPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dimension.width / 2 - this.getSize().width / 2, dimension.height / 2 - this.getSize().height / 2);
        comp = (PolicyViolationAppComponent) ApplicationManager.getApplication().getComponent("PolicyViolationAppComponent");


        setVisible(true);
    }
}

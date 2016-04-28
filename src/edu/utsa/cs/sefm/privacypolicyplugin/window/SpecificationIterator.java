package edu.utsa.cs.sefm.privacypolicyplugin.window;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Rocky on 4/28/2016.
 */
public class SpecificationIterator extends JFrame{
    private JLabel instructions;
    private JPanel formPanel;
    private JPanel instructionsPanel;
    private JCheckBox checkBox1;
    private JComboBox comboBox2;
    private JPanel rootPanel;
    private JButton nextButton;

    public SpecificationIterator(){
        super("Specification Generation");
        setContentPane(rootPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dimension.width / 2 - this.getSize().width / 2, dimension.height / 2 - this.getSize().height / 2);

        setVisible(true);
    }
}

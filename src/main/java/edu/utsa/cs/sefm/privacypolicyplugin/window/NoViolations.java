package edu.utsa.cs.sefm.privacypolicyplugin.window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class NoViolations extends JFrame{
    private JPanel rootPanel;
    private JPanel buttonPanel;
    private JPanel messagePanel;

    NoViolations(){
        super("Specification Generation");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dimension.width / 2 - this.getSize().width / 2, dimension.height / 2 - this.getSize().height / 2);
        setSize(new Dimension(400,200));
        setContentPane(rootPanel);
        setVisible(true);
    }
}

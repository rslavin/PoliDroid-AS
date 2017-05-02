package edu.utsa.cs.sefm.privacypolicyplugin.window;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class SpecificationDialogAction extends AnAction {

    public void actionPerformed(AnActionEvent e) {
        SpecificationIterator si = new SpecificationIterator();
    }

}



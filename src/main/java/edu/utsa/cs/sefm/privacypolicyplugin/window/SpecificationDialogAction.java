package edu.utsa.cs.sefm.privacypolicyplugin.window;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import edu.utsa.cs.sefm.privacypolicyplugin.PolicyViolationAppComponent;

public class SpecificationDialogAction extends AnAction {
    private int totalApis;
    private PolicyViolationAppComponent comp;

    public void actionPerformed(AnActionEvent e) {
        comp = ApplicationManager.getApplication().getComponent(PolicyViolationAppComponent.class);
        totalApis = comp.apisInCode.size();
        if(totalApis <= 0){
            NoViolations ni = new NoViolations();
        }
        else {
            SpecificationIterator si = new SpecificationIterator();
        }
    }

}



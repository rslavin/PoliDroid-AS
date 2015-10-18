package edu.utsa.cs.sefm.privacypolicyplugin.window;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import edu.utsa.cs.sefm.privacypolicyplugin.PolicyViolationAppComponent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Rocky on 10/18/2015.
 */
public class PolicyFileChooser extends AnAction {
    public void actionPerformed(AnActionEvent e) {

        Project project = e.getData(DataKeys.PROJECT);
        VirtualFile file = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor(), project, null);
        PolicyViolationAppComponent comp = (PolicyViolationAppComponent) ApplicationManager.getApplication().getComponent("PolicyViolationAppComponent");

        if (!comp.apis.isEmpty())
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
                String line;

                // TODO check if mappings exist, if not add dialog

                // read in policy
                String fileContents = "";
                while ((line = br.readLine()) != null)
                    fileContents += " " + line;
                parseLine(comp, fileContents);
                br.close();
            } catch (IOException e1) {
                System.err.println("Error parsing policy file");
                e1.printStackTrace();
            }
    }

    private void parseLine(PolicyViolationAppComponent comp, String line) {
        for (String phrase : comp.phrases) {
            if (line.toLowerCase().indexOf(phrase.toLowerCase().trim()) != -1)
                comp.addPolicyPhrase(phrase.toLowerCase().trim());
        }
    }
}

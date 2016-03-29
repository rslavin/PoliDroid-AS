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


import java.io.*;
import java.util.ArrayList;

/**
 * Created by Rocky on 10/18/2015.
 */
public class MapFileChooser extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(DataKeys.PROJECT);
        VirtualFile file = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor(), project, null);
        PolicyViolationAppComponent comp = (PolicyViolationAppComponent) ApplicationManager.getApplication().getComponent("PolicyViolationAppComponent");

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String line;

            // clear existing mappings
            comp.apis = new ArrayList<>();

            // read in new mappings
            while ((line = br.readLine()) != null) {
                parseLine(comp, line);
            }
            br.close();
        } catch (IOException e1) {
            System.err.println("Error parsing mappings");
            e1.printStackTrace();
        }
    }

    private void parseLine(PolicyViolationAppComponent comp, String line) {
        String[] tokens = line.split("\\s*,\\s*", 2);
        if (tokens.length == 2)
            comp.addApi(tokens[1].toLowerCase().trim(), tokens[0].toLowerCase().trim());
    }
}



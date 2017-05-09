package edu.utsa.cs.sefm.privacypolicyplugin.window;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import edu.utsa.cs.sefm.privacypolicyplugin.PolicyViolationAppComponent;
import edu.utsa.cs.sefm.privacypolicyplugin.nlp.ontology.preprocess.HTMLUtils;

public class PolicyFileChooser extends AnAction {
    public void actionPerformed(AnActionEvent e) {

        Project project = e.getData(DataKeys.PROJECT);
        PolicyViolationAppComponent comp = (PolicyViolationAppComponent) ApplicationManager.getApplication().getComponent("PolicyViolationAppComponent");
        VirtualFile file = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor(), project, null);

        if(file != null) {
            file.refresh(false, false);
            String documentPlaintext = HTMLUtils.getText(file);
            PolicyViolationAppComponent.logger.info("Policy plaintext: " + documentPlaintext);

            comp.paragraphProcessor.processParagraphs(documentPlaintext);
            comp.paragraphProcessor.findNounsInOntology();
            comp.paragraphProcessor.findConstituentsInOntology();

            if (comp.paragraphProcessor.ontologyPhrasesInPolicy.size() > 0) {
                PolicyViolationAppComponent.logger.info("### Phrases in the ontology and privacy policy (with verb phrases) are: ###");
                for (String phraseInPolicy : comp.paragraphProcessor.ontologyPhrasesInPolicy) {
                    // add phrase and flag corresponding methods as allowed
                    PolicyViolationAppComponent.logger.info("\t*" + phraseInPolicy);
                    comp.addPolicyPhrase(phraseInPolicy.toLowerCase().trim());
                }
                if (project != null) {
                    PolicyViolationAppComponent.logger.info("Restarting DaemonCodeAnalyzer");
                    DaemonCodeAnalyzer.getInstance(project).restart();
                }
            } else {
                PolicyViolationAppComponent.logger.info("### No phrases from privacy policy present in ontology ###");
            }
        }
    }
}

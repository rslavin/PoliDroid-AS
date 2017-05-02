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
import edu.utsa.cs.sefm.privacypolicyplugin.nlp.ontology.preprocess.HTMLUtils;
import edu.utsa.cs.sefm.privacypolicyplugin.nlp.ontology.preprocess.ParagraphProcessor;

public class PolicyFileChooser extends AnAction {
    public void actionPerformed(AnActionEvent e) {

        final Project project = e.getData(DataKeys.PROJECT);
        PolicyViolationAppComponent comp = (PolicyViolationAppComponent) ApplicationManager.getApplication().getComponent("PolicyViolationAppComponent");
        VirtualFile file = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor(), project, null);
        final String documentPlaintext = HTMLUtils.getText(file);

        // TODO put paragraphProc in component
        ParagraphProcessor paragraphProc = new ParagraphProcessor();
        paragraphProc.processParagraphs(documentPlaintext);
        paragraphProc.findNounsInOntology();
        paragraphProc.findConstituentsInOntology();

        if(ParagraphProcessor.ontologyPhrasesInPolicy.size() > 0) {
            PolicyViolationAppComponent.logger.info("### Phrases in the ontology and privacy policy (with verb phrases) are: ###");
            for (String phraseInPolicy : ParagraphProcessor.ontologyPhrasesInPolicy) {
                // add phrase and flag corresponding methods as allowed
                comp.addPolicyPhrase(phraseInPolicy.toLowerCase().trim());
                PolicyViolationAppComponent.logger.info(phraseInPolicy);
            }
        }else{
            PolicyViolationAppComponent.logger.info("### No phrases from privacy policy present in ontology ###");
        }
/*
        if (!comp.apiMethods.isEmpty() && !OntologyOWLAPI.ontology.isEmpty() && documentPlaintext != null) {
            try {
                BufferedReader br = new BufferedReader(new StringReader(documentPlaintext));
                String line;

                // TODO check if models exist, if not add dialog

                // TODO we SHOULD NOT BE RE-PARSING HERE! Use ParagraphProcessor.ontologyPhrasesInPolicy
                // TODO note that ontologyPhrasesInPolicy is used in PolicyViolationInspector.getViolation
                // read in policy
                PolicyViolationAppComponent.logger.info("### Parsing privacy policy ###");
                StringBuilder fileContents = new StringBuilder();
                while ((line = br.readLine()) != null)
                    fileContents.append(" ").append(line);
                parseLine(comp, fileContents.toString());
                br.close();
                PolicyViolationAppComponent.logger.info("### END Parsing privacy policy ###");
            } catch (IOException e1) {
                PolicyViolationAppComponent.logger.error("Error parsing policy file");
                e1.printStackTrace();
            }
        }
        if (project != null) {
            PolicyViolationAppComponent.logger.info("Restarting DaemonCodeAnalyzer");
            DaemonCodeAnalyzer.getInstance(project).restart();
        }
    }

    private void parseLine(PolicyViolationAppComponent comp, String line) {
        for (String phrase : comp.phrases) {
            if (line.toLowerCase().contains(phrase.toLowerCase().trim()))
                comp.addPolicyPhrase(phrase.toLowerCase().trim());
        }
        */
    }
}

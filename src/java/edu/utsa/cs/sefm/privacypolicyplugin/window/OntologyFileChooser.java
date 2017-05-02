package edu.utsa.cs.sefm.privacypolicyplugin.window;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import edu.utsa.cs.sefm.privacypolicyplugin.PolicyViolationAppComponent;
import edu.utsa.cs.sefm.privacypolicyplugin.nlp.ontology.OntologyOWLAPI;
import edu.utsa.cs.sefm.privacypolicyplugin.nlp.ontology.preprocess.Lemmatizer;


/**
 * Created by Mitra on 11/16/2015.
 */
public class OntologyFileChooser extends AnAction {

    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(DataKeys.PROJECT);
        VirtualFile file = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor(), project, null);
        StringBuilder sb;
        if(file != null) {
            try {
                OntologyOWLAPI.loader(file);
                OntologyOWLAPI.ontologyPhrases = OntologyOWLAPI.ListOfOntologyPhrases(OntologyOWLAPI.ontology);
                sb = new StringBuilder();
                for (int i = 0; i < OntologyOWLAPI.ontologyPhrases.size(); i++) {
                    sb.append(OntologyOWLAPI.ontologyPhrases.get(i)).append(". ");
                }

                OntologyOWLAPI.lemmaOntologyPhrases = Lemmatizer.lemmatizeRList(sb.toString());
                PolicyViolationAppComponent.logger.info(OntologyOWLAPI.lemmaOntologyPhrases.size() + " ontology phrases loaded");

            } catch (Exception exp) {
                PolicyViolationAppComponent.logger.error("Failed to load ontology file");
                exp.printStackTrace();
            }
        }
    }
}

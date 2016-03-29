package edu.utsa.cs.sefm.privacypolicyplugin.window;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import edu.utsa.cs.sefm.privacypolicyplugin.ontology.OntologyOWLAPI;
import edu.utsa.cs.sefm.privacypolicyplugin.preprocess.Lemmatizer;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Created by Mitra on 11/16/2015.
 */
public class OntologyFileChooser extends AnAction {

    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(DataKeys.PROJECT);
        VirtualFile file = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor(), project, null);
        //PolicyViolationAppComponent comp = (PolicyViolationAppComponent) ApplicationManager.getApplication().getComponent("PolicyViolationAppComponent");
        try{
            OntologyOWLAPI.loader(file);
            OntologyOWLAPI.ontologyPhrases = OntologyOWLAPI.ListOfOntologyPhrases(OntologyOWLAPI.ontology);
            String sb = "";
            for(int i = 0; i<OntologyOWLAPI.ontologyPhrases.size(); i++){
                sb += OntologyOWLAPI.ontologyPhrases.get(i);
                sb += ". ";
            }

            OntologyOWLAPI.lemmaOntologyPhrases = Lemmatizer.lemmatizeRList(sb.toString());
            System.out.println(OntologyOWLAPI.lemmaOntologyPhrases.size());

        } catch (Exception exp){
            System.out.println("Failed to load " + file.getName() + ": " + exp.toString());
        }
    }
}

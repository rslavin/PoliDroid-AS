package edu.utsa.cs.sefm.privacypolicyplugin;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethodCallExpression;
import edu.utsa.cs.sefm.privacypolicyplugin.ontology.OntologyOWLAPI;
import edu.utsa.cs.sefm.privacypolicyplugin.preprocess.ParagraphProcessor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rocky on 10/11/2015.
 */
public class PolicyViolationInspection extends LocalInspectionTool{
    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Android Privacy Policy Violation Detector";
    }

    @NotNull
    @Override
    public String getShortName() {
        return "AndroidPrivacyPolicy";
    }

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return GroupNames.BUGS_GROUP_NAME;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(final ProblemsHolder holder, boolean isOnTheFly) {

        return new JavaElementVisitor() {

            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {

                String violation = getViolation(expression);
                if(violation != null) {
                    System.out.println("violation is: " + violation);
                    holder.registerProblem(expression, violation);
                }
            }
        };
    }

    private List<String> isViolation(String api){
        PolicyViolationAppComponent comp = (PolicyViolationAppComponent)ApplicationManager.getApplication().getComponent("PolicyViolationAppComponent");
        return comp.isViolation(api);
    }

    private String isMappedApi(String api){
        PolicyViolationAppComponent comp = (PolicyViolationAppComponent)ApplicationManager.getApplication().getComponent("PolicyViolationAppComponent");
        return comp.hasApi(api);
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    private String getViolation(PsiMethodCallExpression expression){
        String className = expression.resolveMethod().getContainingClass().getQualifiedName();
        String methodName = expression.getMethodExpression().getReferenceName().toString();
        String fullName = className + "." + methodName;

        /*String phrase = isViolation(fullName.toLowerCase());
        if(phrase.length() > 0) {
            System.out.println("Possible privacy policy violation. Consider adding the phrase: \"" + phrase + "\" to your policy.");
            return "Possible privacy policy violation. Consider adding the phrase: \"" + phrase + "\" to your policy.";
        }
        return null;
*/
       List<String> phrasesOfAPI = isViolation(fullName.toLowerCase());
        if(! phrasesOfAPI.isEmpty()){
            List<String> possiblePhrases = new ArrayList<>();
             for(String phraseMappedtoAPI : phrasesOfAPI) {

                 //replace spaces with the underscores.
                 if (OntologyOWLAPI.classDoesExists(phraseMappedtoAPI.toLowerCase().replace(" ", "_"), OntologyOWLAPI.ontology)) {
                     for (String phraseInPolicy : ParagraphProcessor.phrasesInPolicy) {
                         if (OntologyOWLAPI.isEquivalent(phraseInPolicy.toLowerCase(), phraseMappedtoAPI.toLowerCase().replace(" ", "_"), OntologyOWLAPI.ontology)) {
                             return "\"" + phrasesOfAPI + "\" is synonym of \"" + phraseInPolicy + "\" . Consider clarifying the privacy policy.";
                         }
                         if (OntologyOWLAPI.isAncestorOf(phraseInPolicy.toLowerCase(), phraseMappedtoAPI.toLowerCase().replace(" ", "_"), OntologyOWLAPI.ontology)) {
                             if(! possiblePhrases.contains(phraseMappedtoAPI)) {
                                 possiblePhrases.add(phraseMappedtoAPI);
                                 System.out.println("ancestor is :"+ phraseInPolicy);
                             }
                         }
                     }
                 }
             }
            if(!possiblePhrases.isEmpty()){
                String phrases = "";
                for (String possiblePhrase: possiblePhrases){
                    phrases += "\""+ possiblePhrase +"\" ";
                }
                return ("Possible weak privacy policy violation. Consider adding these phrases: "+phrases+" to your policy." );
            }
            if(possiblePhrases.isEmpty()){
                String strongPhrases = "";
                for(String strongPhrase: phrasesOfAPI){
                    strongPhrases += "\""+ strongPhrase +"\" ";
                }
                return ("Possible strong privacy violation. Consider adding these phrases: "+strongPhrases+" to your policy.");
            }

       }
        return null;
    }
}

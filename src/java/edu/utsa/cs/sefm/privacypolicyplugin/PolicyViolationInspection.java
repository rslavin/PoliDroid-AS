package edu.utsa.cs.sefm.privacypolicyplugin;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethodCallExpression;
import edu.utsa.cs.sefm.privacypolicyplugin.nlp.ontology.OntologyOWLAPI;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PolicyViolationInspection extends LocalInspectionTool{

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(final ProblemsHolder holder, boolean isOnTheFly) {

        return new JavaElementVisitor() {

            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {

                String violation = getViolation(expression);
                if(violation != null) {
                    PolicyViolationAppComponent.logger.info("Found misalignment: " + violation);
                    holder.registerProblem(expression, violation);
                }
            }
        };
    }

    private List<String> isViolation(String api){
        PolicyViolationAppComponent comp = (PolicyViolationAppComponent)ApplicationManager.getApplication().getComponent("PolicyViolationAppComponent");
        return comp.isViolation(api);
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    private String getViolation(PsiMethodCallExpression expression){
        PolicyViolationAppComponent comp = (PolicyViolationAppComponent) ApplicationManager.getApplication().getComponent("PolicyViolationAppComponent");
        try {
            String className = expression.resolveMethod().getContainingClass().getQualifiedName();
            String methodName = expression.getMethodExpression().getReferenceName().toString();
            String fullName = className + "." + methodName;

            List<String> phrasesOfAPI = isViolation(fullName.toLowerCase()); // this also adds api invocations to comp
            if (!phrasesOfAPI.isEmpty()) { // if the method has mapped phrases
                for (String phraseMappedToApi : phrasesOfAPI) {
                    PolicyViolationAppComponent.logger.info("Phrase of API: " + phraseMappedToApi);
                    // if the policy/ontology intersection contains the phrase, skip it
                    if (comp.paragraphProcessor.ontologyPhrasesInPolicy.contains(phraseMappedToApi.replaceAll("_", " "))) {
                        phrasesOfAPI.remove(phraseMappedToApi);
                        continue;
                    }
                    for (String phraseInPolicy : comp.paragraphProcessor.ontologyPhrasesInPolicy) {
                        if (OntologyOWLAPI.isEquivalent(phraseInPolicy.toLowerCase().replaceAll(" ", "_"), phraseMappedToApi.toLowerCase().replaceAll(" ", "_"), OntologyOWLAPI.ontology)) {
                            phrasesOfAPI.remove(phraseMappedToApi);
                            break;
                        }
                    }
                }
                if (!phrasesOfAPI.isEmpty()) { // if still has matches
                    List<String> weaklyMappedPhrases = new ArrayList<>();
                    String ancestor = null;
                    // check for weak violation
                    for (String phraseMappedtoAPI : phrasesOfAPI) {
                        // if the phrase exists in the ontology
                        if (OntologyOWLAPI.classDoesExists(phraseMappedtoAPI.toLowerCase().replaceAll(" ", "_"), OntologyOWLAPI.ontology)) {
                            // for each phrase in the policy && ontology
                            for (String phraseInPolicy : comp.paragraphProcessor.ontologyPhrasesInPolicy) {
                                // check if it is an ancestor to phraseMappedToAPI
                                if (OntologyOWLAPI.isAncestorOf(phraseInPolicy.toLowerCase().replaceAll(" ", "_"), phraseMappedtoAPI.toLowerCase().replaceAll(" ", "_"), OntologyOWLAPI.ontology)) {
                                    if (!weaklyMappedPhrases.contains(phraseMappedtoAPI)) {
                                        // mark the descendant (phraseMappedToAPI) to weaklyMappedPhrases
                                        weaklyMappedPhrases.add(phraseMappedtoAPI.replaceAll("_", " "));
                                        PolicyViolationAppComponent.logger.info("Ancestor is:" + phraseInPolicy);
                                        ancestor = phraseInPolicy;
                                    }
                                }
                            }
                        }
                    }
                    if (!weaklyMappedPhrases.isEmpty()) {
                        StringBuilder phrases = new StringBuilder();
                        for (String possiblePhrase : weaklyMappedPhrases) {
                            phrases.append("\"").append(possiblePhrase).append("\" ");
                        }
                        return ("Possible weak privacy policy misalignment (Found \"" + ancestor + "\"). Consider adding these phrases: " + phrases + " to your policy.");
                    } else {
                        StringBuilder strongPhrases = new StringBuilder();
                        for (String strongPhrase : phrasesOfAPI) {
                            strongPhrases.append("\"").append(strongPhrase).append("\" ");
                        }
                        return ("Possible strong privacy misalignment. Consider adding these phrases: " + strongPhrases + " to your policy.");
                    }
                }


            }
        }catch(Exception e){
            return null;
        }
        return null;
    }
}

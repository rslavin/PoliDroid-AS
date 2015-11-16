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

       /* String phrase = isViolation(fullName.toLowerCase());
        if(phrase.length() > 0)
            return "Possible privacy policy violation. Consider adding the phrase: \"" + phrase + "\" to your policy.";
        return null;
        */
        List<String> phrasesOfAPI = isViolation(fullName.toLowerCase());
        if(! phrasesOfAPI.isEmpty()){
             for(String phraseMappedtoAPI : phrasesOfAPI){
                 for(String phraseInPolicy: ParagraphProcessor.phrasesInPolicy){
                     if (OntologyOWLAPI.isEquivalent(phraseMappedtoAPI.toLowerCase(), phraseInPolicy.toLowerCase(), ParagraphProcessor.ontology)){
                         return "Possible weak privacy policy violation. \"" + phraseMappedtoAPI + "\" is synonym of \"" + phraseInPolicy + "\" . Consider clarifying the privacy policy." ;
                     }
                     if(OntologyOWLAPI.isAncestorOf(phraseMappedtoAPI.toLowerCase(), phraseInPolicy.toLowerCase(), ParagraphProcessor.ontology)){
                         return "Possible weak privacy policy violation. \"" + phraseMappedtoAPI + "\" can be subsumed by \"" + phraseInPolicy + "\" . Consider clarifying the privacy policy." ;
                     }
                 }
             }
        }
        return null;
    }
}

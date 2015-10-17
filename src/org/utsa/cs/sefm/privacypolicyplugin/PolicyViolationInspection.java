package org.utsa.cs.sefm.privacypolicyplugin;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

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
                    holder.registerProblem(expression, violation);
                }
            }
        };
    }

    private List getAllowableApis(){
        return new ArrayList();
    }

    private List getAllApis(){
        return new ArrayList();
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    private String getViolation(PsiMethodCallExpression expression){
        String searchFor = "java.io.file.getname";
        String className = expression.resolveMethod().getContainingClass().getQualifiedName();
        String methodName = expression.getMethodExpression().getReferenceName().toString();
        String fullName = className + "." + methodName;
        if(fullName.toLowerCase().equals(searchFor.toLowerCase()))
            return fullName;
        return null;
    }
}

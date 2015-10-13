package org.utsa.cs.sefm.privacypolicyplugin;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethodCallExpression;
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

            //https://devnet.jetbrains.com/message/5506789#5506789
            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                String methodName = "getName";

                if(expression.getMethodExpression().getReferenceName().toString().equals(methodName)) {
//                    String method = expression.getManager().get
                    holder.registerProblem(expression, ((PsiJavaFile) expression.getMethodExpression().getContainingFile()).getPackageName());
                    // package: ((PsiJavaFie) psiFile).getPackageName()
                }
            }

//            @Override
//            public void visitReferenceExpression(PsiReferenceExpression expression) {
//                if(expression.){
//                    holder.registerProblem(expression, "Possible policy violation");
//                }
//            }
        };
    }

    private List getViolationApis(){
        return new ArrayList();
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}

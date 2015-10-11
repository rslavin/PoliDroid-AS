package org.utsa.cs.sefm.privacypolicyplugin;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Rocky on 10/11/2015.
 */
public class PolicyViolationInspection extends LocalInspectionTool{
    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return super.getDisplayName();
    }

    @NotNull
    @Override
    public String getShortName() {
        return super.getShortName();
    }

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return super.getGroupDisplayName();
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(ProblemsHolder holder, boolean isOnTheFly) {
        return super.buildVisitor(holder, isOnTheFly);
    }

    @Override
    public boolean isEnabledByDefault() {
        return super.isEnabledByDefault();
    }
}

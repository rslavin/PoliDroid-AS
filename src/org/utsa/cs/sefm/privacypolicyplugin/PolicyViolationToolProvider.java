package org.utsa.cs.sefm.privacypolicyplugin;

import com.intellij.codeInspection.InspectionToolProvider;

/**
 * Created by Rocky on 10/11/2015.
 */
public class PolicyViolationToolProvider implements InspectionToolProvider{
    @Override
    public Class[] getInspectionClasses() {
        return new Class[]{PolicyViolationInspection.class};
    }
}

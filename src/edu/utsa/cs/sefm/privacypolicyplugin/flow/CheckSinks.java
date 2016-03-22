package edu.utsa.cs.sefm.privacypolicyplugin.flow;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Rocky on 11/1/2015.
 */
public class CheckSinks extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        String apkPath = "";
        String androidSDK = "";
        // TODO: insert action logic here

        Runtime rt = Runtime.getRuntime();
        try {
            final Process pr = rt.exec("java -Xmx4g -cp soot-trunk.jar;soot-infoflow.jar;soot-infoflow-android.jar;slf4j-api-1.7.5.jar;slf4j-simple-1.7.5.jar;axml-2.0.jar soot.jimple.infoflow.android.TestApps.Test " + apkPath + " " + androidSDK);

            new Thread(new Runnable() {
                public void run() {
                    BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                    String line = null;

                    try {
                        while ((line = input.readLine()) != null)
                            System.out.println(line);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }).start();

            pr.waitFor();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}

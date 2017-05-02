package edu.utsa.cs.sefm.privacypolicyplugin.nlp.ontology.preprocess;

import com.intellij.openapi.vfs.VirtualFile;
import edu.utsa.cs.sefm.privacypolicyplugin.PolicyViolationAppComponent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HTMLUtils {
    public static String getText(VirtualFile policy) {
        StringBuilder docString = new StringBuilder();

        // parse into a string
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(policy.getInputStream(), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null)
                docString.append(line).append("\n");
            br.close();
        } catch (Exception e) {
            PolicyViolationAppComponent.logger.error("Failed to read " + policy.getName() + ": " + e.toString());
            e.printStackTrace();
            return null;
        }

        Document htmlDoc = Jsoup.parse(docString.toString());
        PolicyViolationAppComponent.logger.info("Loaded policy file");
        return htmlDoc.text();
    }
}

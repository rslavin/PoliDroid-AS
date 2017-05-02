package edu.utsa.cs.sefm.privacypolicyplugin.nlp.ontology.preprocess;

import com.intellij.openapi.vfs.VirtualFile;
import edu.utsa.cs.sefm.privacypolicyplugin.PolicyViolationAppComponent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HTMLUtils {
    public static String getText(VirtualFile policy) {
        StringBuilder documentHtml = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(policy.getInputStream(), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null)
                documentHtml.append(line).append("\n");
            br.close();
        } catch (Exception e) {
            PolicyViolationAppComponent.logger.error("Failed to read " + policy.getName() + ": " + e.toString());
            e.printStackTrace();
            return null;
        }
        if (!policy.getName().toLowerCase().endsWith(".html")) {
            PolicyViolationAppComponent.logger.info("Loaded text file");
            return documentHtml.toString();
        }
        Document htmlDoc = Jsoup.parse(documentHtml.toString());
        String charset;
        try {
            charset = htmlDoc.select("meta[charset]").first().attr("charset").toUpperCase();
            if (charset.equals("UTF-8")) {
                htmlDoc.select("br").append("\\n");
                htmlDoc.select("p").prepend("\\n\\n");
                PolicyViolationAppComponent.logger.info("Loaded html file");
                return (htmlDoc.text().replace("\\n", "\n")).replace("\n\n", "\n");
            }
        } catch (Exception e) {
            PolicyViolationAppComponent.logger.error("Failed to read " + policy.getName() + ": " + e.toString());
            e.printStackTrace();
            return null;
        }

        PolicyViolationAppComponent.logger.error("Failed to load policy (bad html file?)");
        return null;
    }
}

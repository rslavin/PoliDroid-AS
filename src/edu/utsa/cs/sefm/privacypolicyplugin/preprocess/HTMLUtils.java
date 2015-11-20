package edu.utsa.cs.sefm.privacypolicyplugin.preprocess;

import com.intellij.openapi.vfs.VirtualFile;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Mitra on 11/14/2015.
 */
public class HTMLUtils {
    public static String getText(VirtualFile policy) {
        String documentHtml = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(policy.getInputStream(),"UTF-8"));
            String line;
            while((line = br.readLine()) != null)
                documentHtml +=  line + "\n";
            br.close();
        } catch (Exception e) {
            System.out.println("Failed to read " + policy.getName() + ": " + e.toString());
            return null;
        }
        if (policy.getName().endsWith(".txt")) {
            System.out.println("file ends with txt");
            return documentHtml;
        }
        Document htmlDoc = Jsoup.parse(documentHtml);
        String charset = "UTF-8";
        try {
            charset = htmlDoc.select("meta[charset]").first().attr("charset").toUpperCase();
        } catch (Exception e) {
        }

        if (charset.equals("UTF-8")) {
            htmlDoc.select("br").append("\\n");
            htmlDoc.select("p").prepend("\\n\\n");
            return (htmlDoc.text().replace("\\n", "\n")).replace("\n\n", "\n");
        }

           System.out.println("Failed to read " + policy.getName());
            return null;
    }
}

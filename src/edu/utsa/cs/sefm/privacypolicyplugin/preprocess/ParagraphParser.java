package edu.utsa.cs.sefm.privacypolicyplugin.preprocess;

/**
 * Created by Mitra on 3/21/2016.
 */
import java.io.*;
import java.util.ArrayList;
import java.util.List;
public class ParagraphParser {
    public static List<String> ParagraphParser(String lemmaLine) {
        Runtime rt = Runtime.getRuntime();
        final List<String> parseTreeList = new ArrayList<>();
        //ArrayList<Tree> treeList = new ArrayList<>();

        try {
            final Process pr = rt.exec("java -cp NLP\\stanford-corenlp-3.5.2.jar;NLP\\stanford-corenlp-3.5.2-models.jar;NLP\\utsa-parser.jar;NLP; edu.utsa.cs.sefm.parser.NlpParser ");
            OutputStream outStream = pr.getOutputStream();
            PrintWriter pWriter = new PrintWriter(outStream);
            pWriter.println(lemmaLine);
            pWriter.flush();
            pWriter.close();

            new Thread(new Runnable() {
                public void run() {

                    BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                    String line = null;

                    try {
                        while ((line = input.readLine()) != null){
                            parseTreeList.add(line);
                            /*Tree t = Tree.valueOf(line);
                            if (t.label().value().equals("X"))
                                continue;
                            treeList.add(t);*/
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }).start();
            pr.waitFor();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return parseTreeList;
    }
}

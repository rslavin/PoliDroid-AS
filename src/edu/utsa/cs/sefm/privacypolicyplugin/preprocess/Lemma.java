    package edu.utsa.cs.sefm.privacypolicyplugin.preprocess;

    /**
     * Created by Mitra on 11/14/2015.
     */
    import java.io.*;
    import java.util.LinkedList;
    import java.util.List;

    public class Lemma {

        public static List<String> lemmatize(String currline) {
            Runtime rt = Runtime.getRuntime();
            final List<String> lemmas = new LinkedList<String>();
            try {
                final Process pr = rt.exec("java -cp NLP\\stanford-corenlp-3.5.2.jar;NLP\\stanford-corenlp-3.5.2-models.jar;NLP\\utsa-lemmatizer.jar;NLP; edu.utsa.cs.sefm.lemmatizer.Lemmatize ");
                OutputStream outStream = pr.getOutputStream();
                PrintWriter pWriter = new PrintWriter(outStream);
                pWriter.println(currline);
                pWriter.flush();
                pWriter.close();

                new Thread(new Runnable() {
                    public void run() {

                        BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                        String line = null;

                        try {
                            while ((line = input.readLine()) != null){
                                //System.out.println(line);
                                lemmas.add(line);
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
            return lemmas;
        }
    }





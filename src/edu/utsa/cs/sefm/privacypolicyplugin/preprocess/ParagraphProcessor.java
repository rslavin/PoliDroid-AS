package edu.utsa.cs.sefm.privacypolicyplugin.preprocess;

/**
 * Created by Mitra on 11/14/2015.
 */

import edu.utsa.cs.sefm.privacypolicyplugin.ontology.OntologyOWLAPI;
import org.semanticweb.owlapi.model.OWLOntology;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ParagraphProcessor {
    static ArrayList<String> collectVerbList = new ArrayList<>();
    public static List<String> phrasesInPolicy = new ArrayList<>();
    final static String COLLECT_VERBS_FILENAME = "C:\\Users\\Mitra\\IdeaProjects\\privacy-policy-plugin2\\inputFiles\\collect_verbs.txt";
    static File ontologyFile = new File("C:\\Users\\Mitra\\IdeaProjects\\privacy-policy-plugin2\\inputFiles\\Android_Policy_Ontology.owl");
    public static OWLOntology ontology = OntologyOWLAPI.loader(ontologyFile);
    static
    {
        try
        {
            Scanner s = new Scanner(new File(COLLECT_VERBS_FILENAME));
            while(s.hasNextLine())
                collectVerbList.add(s.nextLine());
        }
        catch (FileNotFoundException e) { e.printStackTrace(); }
    }
    public static File processParagraphs(String text){
        String currLine;

            File file = new File("C:\\Users\\Mitra\\IdeaProjects\\privacy-policy-plugin2\\policy.txt");
        try {
            FileWriter fileWriter = new FileWriter(file);
            Scanner sc = new Scanner(text);
            while (sc.hasNextLine()) {
                currLine = sc.nextLine();
                if(!currLine.isEmpty()){
                    StanfordLemmatizer slem = new StanfordLemmatizer();
                    List<String> lemmaList = new LinkedList<String>();
                    lemmaList = slem.lemmatize(currLine);
                    for (String lemma : lemmaList){
                        //if(PolicySentenceExtractor.containsCollectVerb(lemma)){
                        String collectionVerb = containsWhichCollectVerb(lemma);
                        if(!collectionVerb.equals("VerbNotFound")){
                            System.out.println(currLine);
                            fileWriter.write(currLine);
                            fileWriter.write("\n");
                            findPhrasesInOntology(lemmaList);
                            break;
                        }
                    }
                }

            }
            sc.close();
            fileWriter.flush();
            fileWriter.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }
    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
    public static String containsWhichCollectVerb(String phrase)
    {
        String def = "VerbNotFound";
        for(String verb : collectVerbList)
        {
            if (phrase.equalsIgnoreCase(verb))
            {
                return verb ;
            }
        }
        return def;
    }
    public static void findPhrasesInOntology(List<String> lemmaList){

        List<List<String>> allPhrases = OntologyOWLAPI.ListOfOntologyPhrases(ontology);
        for(List<String> phrase: allPhrases ){
            if(Collections.indexOfSubList(lemmaList, phrase)!= -1 || Collections.lastIndexOfSubList(lemmaList, phrase)!= -1){
                String newPhrase= "";
                for(String word : phrase){
                    if(newPhrase.isEmpty())
                        newPhrase += word;
                    else
                        newPhrase += "_" + word;
                }
                if (!phrasesInPolicy.contains(newPhrase))
                    phrasesInPolicy.add(newPhrase);
            }
        }
    }
}

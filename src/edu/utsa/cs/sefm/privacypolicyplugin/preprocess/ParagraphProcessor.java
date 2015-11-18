package edu.utsa.cs.sefm.privacypolicyplugin.preprocess;

/**
 * Created by Mitra on 11/14/2015.
 */

import edu.utsa.cs.sefm.privacypolicyplugin.ontology.OntologyOWLAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ParagraphProcessor {
    static List<String> collectVerbList = Arrays.asList("store", "collect", "receive", "aggregate", "send", "record", "acquire", "obtain", "use", "transmit", "access", "log", "retain");
    public static List<String> phrasesInPolicy = new ArrayList<>();
    public static String processParagraphs(String text){
        String currLine;
        String content = "";
            Scanner sc = new Scanner(text);
            while (sc.hasNextLine()) {
                currLine = sc.nextLine();
                if(!currLine.isEmpty()){
                    StanfordLemmatizer slem = new StanfordLemmatizer();
                    List<String> lemmaList = new LinkedList<String>();
                    lemmaList = slem.lemmatize(currLine);
                    for (String lemma : lemmaList){
                        String collectionVerb = containsWhichCollectVerb(lemma);
                        if(!collectionVerb.equals("VerbNotFound")){
                            System.out.println(currLine);
                            content += currLine;
                            findPhrasesInOntology(lemmaList);
                            break;
                        }
                    }
                }

            }
        return content;
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
        List<List<String>> allPhrases = OntologyOWLAPI.ListOfOntologyPhrases(OntologyOWLAPI.ontology);
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

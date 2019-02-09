package edu.utsa.cs.sefm.privacypolicyplugin.nlp.ontology.preprocess;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.slf4j.ILoggerFactory;
import edu.stanford.nlp.util.logging.RedwoodConfiguration;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class Lemmatizer {

    public static List<String> lemmatizeRList(String inputText) {
        StanfordLemmatizer stLemm = new StanfordLemmatizer();
        List<String> lemmas;
        lemmas = stLemm.lemmatize(inputText);
        return lemmas;
    }

    static LinkedHashMap<String, String> lemmatizeRMap(String inputText) {
        StanfordLemmatizer stLemm = new StanfordLemmatizer();
        LinkedHashMap<String, String> lemmas;
        lemmas = stLemm.lemmatizePOS(inputText);
        return lemmas;
    }
}

class StanfordLemmatizer {

    private StanfordCoreNLP pipeline;

    StanfordLemmatizer() {
        // Create StanfordCoreNLP object properties, with POS tagging
        // (required for lemmatization), and lemmatization
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");
       // RedwoodConfiguration.current().clear().apply();

            /*
             * This is a pipeline that takes in a string and returns various analyzed linguistic forms.
             * The String is tokenized via a tokenizer (such as PTBTokenizerAnnotator),
             * and then other sequence model style annotation can be used to add things like lemmas,
             * POS tags, and named entities. These are returned as a list of CoreLabels.
             * Other analysis components build and store parse trees, dependency graphs, etc.
             *
             * This class is designed to apply multiple Annotators to an Annotation.
             * The idea is that you first build up the pipeline by adding Annotators,
             * and then you take the objects you wish to annotate and pass them in and
             * get in return a fully annotated object.
             *
             *  StanfordCoreNLP loads a lot of models, so you probably
             *  only want to do this once per execution
             */
        this.pipeline = new StanfordCoreNLP(props);
    }

    List<String> lemmatize(String documentText) {
        LinkedList<String> lemmas = new LinkedList<>();
        //LinkedHashMap<String,String> LemmaPOSMap = new LinkedHashMap<>();
        //Multimap<String, String> myMultimap = LinkedListMultimap.create();
        // Create an empty Annotation just with the given text
        Annotation document = new Annotation(documentText);
        // run all Annotators on this text
        this.pipeline.annotate(document);
        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            // Iterate over all tokens in a sentence
            StringBuilder st = new StringBuilder();
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                if ((token.get(CoreAnnotations.LemmaAnnotation.class)).toLowerCase().contains(".")) {
                    lemmas.add(st.toString());
                    //st = new StringBuilder();
                    break;
                }
                if (!st.toString().isEmpty()) {
                    st.append(" ");
                }
                st.append((token.get(CoreAnnotations.LemmaAnnotation.class)).toLowerCase());
            }
        }
        return lemmas;
    }

    LinkedHashMap<String, String> lemmatizePOS(String text) {
        LinkedHashMap<String, String> lemmaPOSMap = new LinkedHashMap<>();
        Annotation document = new Annotation(text);
        this.pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            // Iterate over all tokens in a sentence
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                lemmaPOSMap.put((token.get(CoreAnnotations.LemmaAnnotation.class)).toLowerCase(), (token.get(CoreAnnotations.PartOfSpeechAnnotation.class)).toLowerCase());
            }
        }
        return lemmaPOSMap;
    }
}







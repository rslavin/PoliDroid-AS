package edu.utsa.cs.sefm.privacypolicyplugin.preprocess;

/**
 * Created by Mitra on 3/21/2016.
 */
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

public class ParagraphParser {

    public static List<String> ParagraphParser(String text) {
        StanfordParser spar = new StanfordParser();
        List<String> parseTrees;
        parseTrees = spar.parse(text);
        return parseTrees;
    }
}



class StanfordParser {

    protected StanfordCoreNLP pipeline;

    public StanfordParser() {
        // Create StanfordCoreNLP object properties, with POS tagging
        // (required for lemmatization), and lemmatization
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, parse ");

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

    public List<String> parse(String documentText)
    {
        List<String> sentenceTreeList = Collections.synchronizedList(new ArrayList<String>());
        // Create an empty Annotation just with the given text
        Annotation document = new Annotation(documentText);
        // run all Annotators on this text
        this.pipeline.annotate(document);
        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            Tree parseTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
            sentenceTreeList.add(parseTree.toString());
        }

        return sentenceTreeList;
    }

}



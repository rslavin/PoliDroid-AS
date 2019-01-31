package edu.utsa.cs.sefm.privacypolicyplugin.nlp.ontology.preprocess;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.utsa.cs.sefm.privacypolicyplugin.PolicyViolationAppComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

class ParagraphParser {
    private static double SENTIMENT_THRESHOLD = 2;

    private StanfordCoreNLP pipeline;

    ParagraphParser() {
        // Create StanfordCoreNLP object properties, with POS tagging
        // (required for lemmatization), and lemmatization
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, parse, sentiment");


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

    List<String> parse(String documentText) {
        List<String> sentenceTreeList = Collections.synchronizedList(new ArrayList<String>());
        Annotation document = new Annotation(documentText);
        // run all Annotators on this text
        this.pipeline.annotate(document);
        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        int sentiment;
        PolicyViolationAppComponent.logger.info("### SENTIMENT ANALYSIS ###");
        for (CoreMap sentence : sentences) {
            Tree parseTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);

            // add sentence to list if sentiment is positive (i.e., they DO collect the information)
            Tree sentimentTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
            sentiment = RNNCoreAnnotations.getPredictedClass(sentimentTree);
            PolicyViolationAppComponent.logger.info("\tSentence: " + sentence);
            PolicyViolationAppComponent.logger.info("\tSentiment Score: " + sentiment +
                    (sentiment < SENTIMENT_THRESHOLD ? " (IGNORING)" : ""));
            if (sentiment >= SENTIMENT_THRESHOLD) // positive sentiment
                sentenceTreeList.add(parseTree.toString());
        }

        return sentenceTreeList;
    }
}
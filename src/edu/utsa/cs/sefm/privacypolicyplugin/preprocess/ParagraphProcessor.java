package edu.utsa.cs.sefm.privacypolicyplugin.preprocess;

/**
 * Created by Mitra on 11/14/2015.
 */

import edu.utsa.cs.sefm.privacypolicyplugin.ontology.OntologyOWLAPI;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.IOException;
import java.util.*;

public class ParagraphProcessor {
    public static final String[] VERBS_PAST = {"accessed", "collected", "obtained", "received", "provided", "gathered",
            "acquired", "combined", "reviewed", "submitted", "logged", "used", "processed", "utilized", "monitored", "stored",
            "retained", "maintained", "kept", "recorded", "saved", "shared", "disclosed", "sent", "transferred", "displayed",
            "psoted", "delivered", "distributed", "notified", "rented"};
    public static List<String> ontologyPhrasesInPolicy = Collections.synchronizedList(new ArrayList<String>());
    public static List<String> paragraphs = Collections.synchronizedList(new ArrayList<String>());

    private static List<String> nounPhrasesPermutations = Collections.synchronizedList(new ArrayList<String>());
    private static HashMap<String, String> constituentsMap = new HashMap<>();
    //private static List<String> collectVerbList = Collections.synchronizedList(Arrays.asList("store", "collect", "receive",
    // "aggregate", "send", "record", "acquire", "obtain", "use", "transmit", "access", "log", "retain"));
    private static List<String> verbList = Collections.synchronizedList(Arrays.asList("collect", "obtain", "receive", "provide",
            "gather", "access", "acquire", "combine", "review", "submit", "log", "use", "process", "utilize", "monitor",
            "store", "retain", "maintain", "keep", "record", "save", "share", "disclose", "send", "transfer", "display",
            "post", "deliver", "distribute", "notify", "rent"));

    /**
     * @param text
     */
    public static void processParagraphs(String text) {

        ParagraphParser parser = new ParagraphParser();
        List<String> parsedParagraphs = new ArrayList<>();
        parsedParagraphs = parser.ParagraphParser(text);
        for (String parseTree : parsedParagraphs) {
            System.out.println(parseTree);
        }
        for (String parseTree : parsedParagraphs) {
            if (parseTree.startsWith("(X"))
                continue;
            PennTreeBankReader treeReader = new PennTreeBankReader(parseTree);
            try {
                DefaultTreeModel tree = treeReader.ptbTreeBuilder();
                if (tree == null) {
                    System.out.println("null");
                    continue;
                }
                DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getRoot();
                getVerbPhrases(rootNode);
                if (!nounPhrasesPermutations.isEmpty()) {
                    System.out.println("Printing the permutation of a noun phrases in the policy:");
                    for (String nounPhrase : nounPhrasesPermutations) {
                        System.out.println(nounPhrase);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method find the ontology phrases that are used in the privacy policy as nouns and combinations of nouns.
     */
    public static void findNounsInOntology() {
        System.out.println(" The ontology phrases are :");
        for (String phrase : nounPhrasesPermutations) {
            if (OntologyOWLAPI.lemmaOntologyPhrases.contains(phrase)) {
                int index = OntologyOWLAPI.lemmaOntologyPhrases.indexOf(phrase);
                String actPhrase = OntologyOWLAPI.ontologyPhrases.get(index);
                if (!ontologyPhrasesInPolicy.contains(actPhrase) && !phrase.equalsIgnoreCase("information")) {
                    ontologyPhrasesInPolicy.add(actPhrase);
                    System.out.print(actPhrase + ", ");
                }
            }
        }
    }

    public static void findConstituentsInOntology(){
        System.out.println("The constituents in the otnology are:");
        for (Map.Entry<String, String> entry: constituentsMap.entrySet()) {
            if (OntologyOWLAPI.lemmaOntologyPhrases.contains(entry.getValue())) {
                System.out.println(entry.getValue() + " found in the ontology as a constituent");
                int index = OntologyOWLAPI.lemmaOntologyPhrases.indexOf(entry.getValue());
                String actPhrase = OntologyOWLAPI.ontologyPhrases.get(index);
                if (!ontologyPhrasesInPolicy.contains(actPhrase) && !entry.getValue().equalsIgnoreCase("information")) {
                    ontologyPhrasesInPolicy.add(actPhrase);
                    System.out.print(actPhrase + ", ");
                }
            }
        }
    }

    /**
     * This recursive method, finds all the VPs in a parse tree recursively
     * and if the VP includes collection VBs, the parse tree is kept for NP analysis.
     *
     * @param rootNode
     */
    private static void getVerbPhrases(DefaultMutableTreeNode rootNode) {
        if (rootNode.getUserObject().equals("VP")) {
            List<String> verbs = new ArrayList<>();
            Enumeration<DefaultMutableTreeNode> children = rootNode.breadthFirstEnumeration();
            while (children.hasMoreElements()) {
                DefaultMutableTreeNode currentNode = children.nextElement();
                if (currentNode.getUserObject().toString().startsWith("VB")) {
                    DefaultMutableTreeNode nextNode = currentNode.getNextNode();
                    if (nextNode.isLeaf()) {
                        verbs.add(nextNode.getUserObject().toString());
                    }
                }
            }
            if (!verbs.isEmpty()) {
                System.out.println("\nThe verbs in the current VP are: ");
                for (String verb : verbs) {
                    System.out.print(verb + ", ");
                }
                List<String> lemmaVerbs = new ArrayList<>();
                List<String> lemmaCollectionVerb = new ArrayList<>();
                for (String verb : verbs) {
                    Map<String, String> lemmaVerbsMap = new HashMap<>();
                    lemmaVerbsMap = Lemmatizer.lemmatizeRMap(verb);
                    for (Map.Entry<String, String> entry : lemmaVerbsMap.entrySet()) {
                        lemmaVerbs.add(entry.getKey());
                    }
                }
                for (String lemmaverb : lemmaVerbs) {
                    if (containsCollectVerb(lemmaverb)) {
                        lemmaCollectionVerb.add(lemmaverb);
                    }
                }
                if (!lemmaCollectionVerb.isEmpty()) {

                    analyzeNounPhrases(rootNode);
                    analyzeConstituents(rootNode);

                }

            }
        }
        Enumeration<DefaultMutableTreeNode> children = rootNode.children();
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode child = children.nextElement();
            getVerbPhrases(child);
        }
    }

    private static void analyzeConstituents(DefaultMutableTreeNode rootNode){
        List<String> constituents = new ArrayList<>();

        getConstituents(constituents, rootNode);
        System.out.println("The constituents for the VP are: ");
        for (String constituent : constituents) {
            System.out.println(constituent);
            Map<String, String> lemmas = Lemmatizer.lemmatizeRMap(constituent);
            String constituentLemma = "";
            for(Map.Entry<String, String> entry: lemmas.entrySet()){
                constituentLemma += entry.getKey() + " ";
            }
            constituentLemma = constituentLemma.trim();
            System.out.println("Constituent: " + constituent + " Lemma: "+ constituentLemma);
            constituentsMap.put(constituent,constituentLemma);
        }
    }

    /**
     * This method finds all the permutations of nouns in the noun phrases of a sentence
     * @param rootNode
     */
    private static void analyzeNounPhrases(DefaultMutableTreeNode rootNode){
        List<String> nounPhrases = new ArrayList<>();
        getNounPhrases(nounPhrases, rootNode);
        System.out.println("\nThe noun phrases for the VP are: ");
        for (String nounPhrase : nounPhrases) {
            System.out.print(nounPhrase + ", ");
        }
        if (!nounPhrases.isEmpty()) {
            //Refine the noun phrases
            //List<String> of a noun phrase permutations
            List<String> refinedNounPhrases = new ArrayList<String>();
            List<String> refinedNouns = new ArrayList<>();
            List<String> temp = new ArrayList<>();
            for (String nounPhrase : nounPhrases) {
                //refining each noun phrase
                refinedNouns = nounPhraseRefinement(nounPhrase);
                if (refinedNouns.size() < 3) {
                    temp = nounPhraseProcess(refinedNouns);
                    for (String tm : temp) {
                        if (!nounPhrasesPermutations.contains(tm)) {
                            nounPhrasesPermutations.add(tm);
                        }
                    }
                    temp.clear();
                }
                refinedNounPhrases.addAll(refinedNouns);
                refinedNouns.clear();
            }

            System.out.println("refined Noun Phrase:" + refinedNounPhrases);
            //The powerSet generates 2^n sets. If the input set is more than a limited number the program cannot handle generating powerSet
            if (refinedNounPhrases.size() < 8) {
                temp = nounPhraseProcess(refinedNounPhrases);
                for (String tm : temp) {
                    if (!nounPhrasesPermutations.contains(tm)) {
                        nounPhrasesPermutations.add(tm);
                    }
                }
                temp.clear();
            }
        }
    }
    /**
     * This method lemmatize a NP and returns a list of all words starting with "NN" POS tag.
     *
     * @param nounPhrase
     * @return
     */
    private static List<String> nounPhraseRefinement(String nounPhrase) {
        List<String> lemmaNounPhrase = new ArrayList<>();
        Map<String, String> lemma = new HashMap<>();
        lemma = Lemmatizer.lemmatizeRMap(nounPhrase);
        System.out.println("\nNoun phrase to be lemmatized: " + nounPhrase);
        for (Map.Entry element : lemma.entrySet()) {
            System.out.println("lemmatized noun phrase :" + element.getKey() + " ,with the tag: " + element.getValue());
            if (element.getValue().toString().startsWith("nn")) {
                System.out.println("The noun phrase tag starts with NN: ");
                lemmaNounPhrase.add(element.getKey().toString());
            }
        }
        return lemmaNounPhrase;
    }




    /**
     * This method gets a noun phrase as the input argument.
     * convert the noun phrases to array list.
     * Generates the power set for the elements of the list.
     * For each element of the power set:
     * IF the element contains only one word then add it to the final list
     * ELSE generate all the permutations of the words in the power set element and add them to the final list
     *
     * @param nounPhrase
     * @return
     */
    private static List<String> nounPhraseProcess(List<String> nounPhrase) {
        List<String> perms = new ArrayList<>();
        Set<String> nounPhraseSet = new HashSet<String>();
        for (String noun : nounPhrase) {
            nounPhraseSet.add(noun);
        }
        for (Set<String> st : powerSet(nounPhraseSet)) {
            if (st.isEmpty()) {
                System.out.print("set is empty");
            }
            System.out.println(st);
            final List<String> ps = new ArrayList<>(st);
            Set<String> branch = new HashSet<>();
            List<Boolean> visited = new ArrayList<>();
            for (int i = 0; i < ps.size(); i++) {
                visited.add(i, false);
            }
            System.out.println(ps.size());
            if (!ps.isEmpty()) {
                if (ps.size() == 1) {
                    perms.add(ps.get(0).toString());
                } else {
                    permute(ps, 0, perms);
                }
            }
        }
        System.out.println("ALL permutations of this NP: ");
        for (String en : perms) {
            System.out.println(en);
        }
        return perms;
    }

    /**
     * This method generates the power set of a set of nouns
     *
     * @param originalSet
     * @param <String>
     * @return
     */
    public static <String> Set<Set<String>> powerSet(Set<String> originalSet) {
        Set<Set<String>> sets = new HashSet<Set<String>>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<String>());
            return sets;
        }
        List<String> list = new ArrayList<String>(originalSet);
        String head = list.get(0);
        Set<String> rest = new HashSet<String>(list.subList(1, list.size()));
        for (Set<String> set : powerSet(rest)) {
            Set<String> newSet = new HashSet<String>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }

    /**
     * This method swap two strings in a list
     *
     * @param input
     * @param i
     * @param j
     */
    public static void swap(List<String> input, int i, int j) {
        String tmp = input.get(i);
        input.set(i, input.get(j));
        input.set(j, tmp);
    }

    /**
     * This method generates the permutation of a set of string
     *
     * @param ps
     * @param i
     * @param perm
     */

    public static void permute(List<String> ps, int i, List<String> perm) {
        if (i == ps.size()) {
            System.out.println("adding the permutation to the final set: ");
            System.out.println(ps);
            StringBuilder st = new StringBuilder();
            for (int j = 0; j < ps.size(); j++) {
                if (j == ps.size() - 1) {
                    st.append(ps.get(j));
                    break;
                }
                st.append(ps.get(j) + " ");
            }
            System.out.println(st.toString().trim());
            perm.add(st.toString());
            return;
        }
        for (int j = i; j < ps.size(); j++) {
            swap(ps, i, j);      // enumerates on position i
            permute(ps, i + 1, perm);  // recurse
            swap(ps, i, j);      // backtracking
        }
    }


    /**
     * This method returns all the Noun Phrases in a Verb Phrase
     *
     * @param nounPhrases
     * @param currentNode
     */
    private static void getNounPhrases(List<String> nounPhrases, DefaultMutableTreeNode currentNode) {
        if (currentNode.getUserObject().toString().equalsIgnoreCase("NP")) {
            StringBuilder np = new StringBuilder();
            Enumeration<DefaultMutableTreeNode> postOrder = currentNode.postorderEnumeration();
            while (postOrder.hasMoreElements()) {
                DefaultMutableTreeNode node = postOrder.nextElement();
                if (node.isLeaf()) {
                    np.append(node.getUserObject().toString() + " ");
                }
            }
            nounPhrases.add(np.toString().trim());
        }
        Enumeration<DefaultMutableTreeNode> children = currentNode.children();
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode child = children.nextElement();
            getNounPhrases(nounPhrases, child);
        }
    }

    private static void getConstituents(List<String> constituents, DefaultMutableTreeNode currentNode){
        StringBuilder cs = new StringBuilder();
        Enumeration<DefaultMutableTreeNode> postOrder = currentNode.postorderEnumeration();
        while (postOrder.hasMoreElements()) {
            DefaultMutableTreeNode node = postOrder.nextElement();
            if (node.isLeaf()) {
                cs.append(node.getUserObject().toString() + " ");
            }
        }
        constituents.add(cs.toString().trim());
        Enumeration<DefaultMutableTreeNode> children = currentNode.children();
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode child = children.nextElement();
            getConstituents(constituents, child);
        }
    }

    /**
     * This method checks if the verb is a collection verb
     *
     * @param phrase
     * @return
     */
    public static boolean containsCollectVerb(String phrase) {
        //String def = "VerbNotFound";
        for (String verb : verbList) {
            if (phrase.equalsIgnoreCase(verb)) {
                /*if verb.equalsIgnoreCase("use"){

            }*/
                return true;
            }
        }
        return false;
    }
}




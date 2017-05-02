package edu.utsa.cs.sefm.privacypolicyplugin.nlp.ontology.preprocess;

import edu.utsa.cs.sefm.privacypolicyplugin.PolicyViolationAppComponent;
import edu.utsa.cs.sefm.privacypolicyplugin.nlp.ontology.OntologyOWLAPI;

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
    public void processParagraphs(String text) {
        PolicyViolationAppComponent.logger.info("### Processing paragraphs ###");

        // annotate paragraphs
        ParagraphParser parser = new ParagraphParser();
        List<String> parsedParagraphs = parser.parse(text);

        for (String parseTree : parsedParagraphs) {
            if (parseTree.startsWith("(X")) {
                PolicyViolationAppComponent.logger.info("Skipping parse tree: " + parseTree);
                continue;
            }
            PennTreeBankReader treeReader = new PennTreeBankReader(parseTree);
            try {
                PolicyViolationAppComponent.logger.info("Processing: " + parseTree);
                DefaultTreeModel tree = treeReader.ptbTreeBuilder();
                if (tree == null) {
                    PolicyViolationAppComponent.logger.warn("Null tree reader (skipping)");
                    continue;
                }

                getVerbPhrases((DefaultMutableTreeNode) tree.getRoot());
                if (!nounPhrasesPermutations.isEmpty()) {
                    PolicyViolationAppComponent.logger.info("Printing permutation of noun phrases in the policy:");
                    for (String nounPhrase : nounPhrasesPermutations) {
                        PolicyViolationAppComponent.logger.info("\t" +nounPhrase);
                    }
                }
            } catch (IOException e) {
                PolicyViolationAppComponent.logger.error("Error processing paragraphs");
                e.printStackTrace();
            }
        }

        PolicyViolationAppComponent.logger.info("### END Processing paragraphs ###");
    }

    /**
     * Finds phrases that also exist in the ontology
     */
    public void findNounsInOntology() {
        PolicyViolationAppComponent.logger.info("### Finding noun matches in ontology (with verb phrase) ###");
        for (String phrase : nounPhrasesPermutations) {
            // if the noun phrase exists in the ontology
            if (OntologyOWLAPI.lemmaOntologyPhrases.contains(phrase)) {
                String match = OntologyOWLAPI.ontologyPhrases.get(OntologyOWLAPI.lemmaOntologyPhrases.indexOf(phrase));
                // add the phrase if it doesn't exist
                if (!ontologyPhrasesInPolicy.contains(match) && !phrase.equalsIgnoreCase("information")) {
                    ontologyPhrasesInPolicy.add(match);
                    PolicyViolationAppComponent.logger.info("\tFound match: " + match);
                }
            }
        }
        PolicyViolationAppComponent.logger.info("### END Finding noun matches in ontology ###");
    }

    /**
     * Finds constituent phrases in ontology
     */
    public void findConstituentsInOntology(){
        PolicyViolationAppComponent.logger.info("### Finding constituents in ontology ###");
        for (Map.Entry<String, String> entry: constituentsMap.entrySet()) {
            if (OntologyOWLAPI.lemmaOntologyPhrases.contains(entry.getValue())) {
                int index = OntologyOWLAPI.lemmaOntologyPhrases.indexOf(entry.getValue());
                String match = OntologyOWLAPI.ontologyPhrases.get(index);
                if (!ontologyPhrasesInPolicy.contains(match) && !entry.getValue().equalsIgnoreCase("information")) {
                    ontologyPhrasesInPolicy.add(match);
                    PolicyViolationAppComponent.logger.info("\tFound match: " + match);
                }
            }
        }
        PolicyViolationAppComponent.logger.info("### END Finding constituents in ontology ###");
    }

    /**
     * Recursively finds verb phrases in a parse tree. If a verb phrase includes a collection
     * of VBs it is kept for NP analysis
     *
     * @param rootNode
     */
    private static void getVerbPhrases(DefaultMutableTreeNode rootNode) {
        PolicyViolationAppComponent.logger.info("### Finding verb phrases for root node \"" + rootNode +"\" ###");
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
                PolicyViolationAppComponent.logger.info("\tThe verbs in the current VP are: ");
                for (String verb : verbs) {
                    PolicyViolationAppComponent.logger.info("\t " + verb);
                }
                List<String> lemmaVerbs = new ArrayList<>();
                List<String> lemmaCollectionVerb = new ArrayList<>();
                for (String verb : verbs) {
                    Map<String, String> lemmaVerbsMap;
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
        PolicyViolationAppComponent.logger.info("### END Finding verb phrases for root node \"" + rootNode +"\" ###");
    }

    private static void analyzeConstituents(DefaultMutableTreeNode rootNode){
        PolicyViolationAppComponent.logger.info("### Finding constituents for root node \"" + rootNode +"\" ###");
        List<String> constituents = new ArrayList<>();

        getConstituents(constituents, rootNode);
        PolicyViolationAppComponent.logger.info("\tThe constituents for the VP are: ");
        for (String constituent : constituents) {
            Map<String, String> lemmas = Lemmatizer.lemmatizeRMap(constituent);
            StringBuilder constituentLemma = new StringBuilder();
            for(Map.Entry<String, String> entry: lemmas.entrySet()){
                constituentLemma.append(entry.getKey()).append(" ");
            }
            constituentLemma = new StringBuilder(constituentLemma.toString().trim());
            PolicyViolationAppComponent.logger.info("\t CONSTITUENT: " + constituent + " | LEMMA: "+ constituentLemma);
            constituentsMap.put(constituent, constituentLemma.toString());
        }
        PolicyViolationAppComponent.logger.info("### END Finding constituents for root node \"" + rootNode +"\" ###");
    }

    /**
     * This method finds all the permutations of nouns in the noun phrases of a sentence
     * @param rootNode
     */
    private static void analyzeNounPhrases(DefaultMutableTreeNode rootNode){
        List<String> nounPhrases = new ArrayList<>();
        getNounPhrases(nounPhrases, rootNode);
        PolicyViolationAppComponent.logger.info("### Finding noun phrases for root node \"" + rootNode +"\" ###");
        PolicyViolationAppComponent.logger.info("\tThe noun phrases for the VP are: ");
        StringBuilder nounPhrasesStr = new StringBuilder();
        for (String nounPhrase : nounPhrases) {
            nounPhrasesStr.append(nounPhrase).append(", ");
        }
        PolicyViolationAppComponent.logger.info("\t " + nounPhrasesStr.toString().replaceAll(", $", ""));
        if (!nounPhrases.isEmpty()) {
            //Refine the noun phrases
            //List<String> of a noun phrase permutations
            List<String> refinedNounPhrases = new ArrayList<>();
            List<String> refinedNouns;
            List<String> temp;
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
     * This method lemmatizes a noune phrase and returns a list of all words starting with "NN" POS tag.
     *
     * @param nounPhrase
     * @return
     */
    private static List<String> nounPhraseRefinement(String nounPhrase) {
        PolicyViolationAppComponent.logger.info("### Refining (lemmatizing) noun phrase: " + nounPhrase + " ###");
        List<String> lemmaNounPhrase = new ArrayList<>();
        Map<String, String> lemma;
        lemma = Lemmatizer.lemmatizeRMap(nounPhrase);
        for (Map.Entry element : lemma.entrySet()) {
            PolicyViolationAppComponent.logger.info("\tLemmatized noun phrase :" + element.getKey() + " ,with the tag: " + element.getValue());
            if (element.getValue().toString().startsWith("nn")) {
                PolicyViolationAppComponent.logger.info("\t The noun phrase tag starts with NN");
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
        PolicyViolationAppComponent.logger.info("### Processing noun phrase: " + nounPhrase + " ###");
        List<String> perms = new ArrayList<>();
        Set<String> nounPhraseSet = new HashSet<String>();
        nounPhraseSet.addAll(nounPhrase);
        for (Set<String> st : powerSet(nounPhraseSet)) {
            if (st.isEmpty()) {
                PolicyViolationAppComponent.logger.info("\tSet is empty");
            }
            List<String> ps = new ArrayList<>(st);
            if (!ps.isEmpty()) {
                if (ps.size() == 1) {
                    perms.add(ps.get(0));
                } else {
                    permute(ps, 0, perms);
                }
            }
        }
        PolicyViolationAppComponent.logger.info("\tPermutations of this NP: ");
        for (String en : perms) {
            PolicyViolationAppComponent.logger.info("\t " + en);
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
    private static <String> Set<Set<String>> powerSet(Set<String> originalSet) {
        Set<Set<String>> sets = new HashSet<>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<>());
            return sets;
        }
        List<String> list = new ArrayList<>(originalSet);
        String head = list.get(0);
        Set<String> rest = new HashSet<>(list.subList(1, list.size()));
        for (Set<String> set : powerSet(rest)) {
            Set<String> newSet = new HashSet<>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }

    /**
     * This method generates the permutation of a set of string
     *
     * @param ps
     * @param i
     * @param perm
     */

    private static void permute(List<String> ps, int i, List<String> perm) {
        if (i == ps.size()) {
            StringBuilder st = new StringBuilder();
            for (int j = 0; j < ps.size(); j++) {
                if (j == ps.size() - 1) {
                    st.append(ps.get(j));
                    break;
                }
                st.append(ps.get(j)).append(" ");
            }
            perm.add(st.toString());
            return;
        }
        for (int j = i; j < ps.size(); j++) {
            Collections.swap(ps, i, j);// enumerates on position i
            permute(ps, i + 1, perm);  // recurse
            Collections.swap(ps, i, j);// backtracking
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
                    np.append(node.getUserObject().toString()).append(" ");
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
                cs.append(node.getUserObject().toString()).append(" ");
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
    private static boolean containsCollectVerb(String phrase) {
        for (String verb : verbList) {
            if (phrase.equalsIgnoreCase(verb)) {
                return true;
            }
        }
        return false;
    }
}




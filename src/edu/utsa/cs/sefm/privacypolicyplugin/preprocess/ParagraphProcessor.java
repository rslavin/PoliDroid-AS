package edu.utsa.cs.sefm.privacypolicyplugin.preprocess;

/**
 * Created by Mitra on 11/14/2015.
 */

import edu.utsa.cs.sefm.privacypolicyplugin.ontology.OntologyOWLAPI;



/**
 * Created by Mitra on 2/26/2016.
 */


import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Semaphore;


public class ParagraphProcessor {
    private static List<String> nounPhrasesPermutations = Collections.synchronizedList(new ArrayList<String>());
    private static List<String> collectVerbList = Collections.synchronizedList(Arrays.asList("store", "collect", "receive", "aggregate", "send", "record", "acquire", "obtain", "use", "transmit", "access", "log", "retain"));
    public static List<String> phrasesInPolicy = Collections.synchronizedList(new ArrayList<String>());
    public static List<String> paragraphs = Collections.synchronizedList(new ArrayList<String>());
    final static int WORKER_THREADS = Runtime.getRuntime().availableProcessors() / 2;
    static Semaphore threadCtr = new Semaphore(WORKER_THREADS, true);
    public static void processParagraphs(String text) {
        String currParagraph;
        String content = "";
        Scanner sc = new Scanner(text);
        while (sc.hasNextLine()) {
            currParagraph = sc.nextLine();
            if (!currParagraph.isEmpty()) {
                paragraphs.add(currParagraph);
            }
        }
        Thread[] threadPool = new Thread[paragraphs.size()];
        for (int i = 0; i < paragraphs.size(); i++) {
            threadPool[i] = new Thread(new ParagraphProcessor().new ParagraphThreadWorker(paragraphs.get(i)));
            threadPool[i].start();
        }
        System.out.println("Printing the permutation of a noun phrases in the policy:");
        for (String nounPhrase : nounPhrasesPermutations) {
            System.out.println(nounPhrase);
        }
    }
    public static void findPhrasesInOntology() {
        System.out.println(" The ontology phrases are :");
        for (String phrase : nounPhrasesPermutations) {
            for(String ontologyPhrase : OntologyOWLAPI.ListOfOntologyPhrases(OntologyOWLAPI.ontology)){
                if (phrase.equalsIgnoreCase(ontologyPhrase) &!phrasesInPolicy.contains(phrase) && !phrase.equalsIgnoreCase("information") ){
                    phrasesInPolicy.add(phrase);
                    System.out.print(phrase + ", ");
                }
            }
        }
    }

    public class ParagraphThreadWorker implements Runnable {
        String paragraph = null;
        public ParagraphThreadWorker(String paragraph) {
            this.paragraph = paragraph;
        }
        public void run(){
            try { threadCtr.acquire(); }
            catch (InterruptedException e1) { return; }
                /*
                 * Make absolutely sure that, whatever happens,
                 * if we acquired the semaphore we will release it.
                 */
            try { work(); }
            catch (Exception e) {
                System.err.println("Failed to parse the current paragraph: " + paragraph + ": " + e.getMessage());
                e.printStackTrace();
            }
            finally { threadCtr.release(); }
        }

        public void work(){

            List<String> paragraphParseTree = ParagraphParser.ParagraphParser(paragraph.replaceAll("\"", "").replaceAll("/", " or "));
            Map<List<String>, List<String>> verbNounPhrases = new HashMap<>();
            for (String parseTree : paragraphParseTree) {
                System.out.println(parseTree);
                PennTreeBankReader treeReader = new PennTreeBankReader(parseTree);
                try {
                    DefaultTreeModel tree = treeReader.ptbTreeBuilder();
                    if (tree == null) {
                        System.out.println("null");
                    }
                    System.out.println(tree.getRoot().toString());
                    System.out.println(tree.toString());
                    DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getRoot();
                    System.out.println("number of nodes in the tree: " + getChildrenCount(rootNode));
                    Enumeration<DefaultMutableTreeNode> en = rootNode.breadthFirstEnumeration();
                    while (en.hasMoreElements()) {
                        DefaultMutableTreeNode node = en.nextElement();
                        TreeNode[] path = node.getPath();
                        System.out.println((node.isLeaf() ? "  - " : "+ ") + path[path.length - 1]);
                    }
                    System.out.println("Roots children");
                    Enumeration<DefaultMutableTreeNode> children = rootNode.children();
                    while (children.hasMoreElements()) {
                        DefaultMutableTreeNode node = children.nextElement();
                        System.out.println(node.getUserObject());
                    }
                    getVerbPhrases(rootNode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        private void getVerbPhrases(DefaultMutableTreeNode rootNode) {
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
                        lemmaVerbsMap = Lemma.lemmatize(verb);
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
                        List<String> nounPhrases = new ArrayList<>();
                        getNounPhrases(nounPhrases, rootNode);
                        System.out.println("\nThe noun phrases for the VP are: ");
                        for (String nounPhrase : nounPhrases) {
                            System.out.print(nounPhrase + ", ");
                        }
                        if (!nounPhrases.isEmpty()) {
                            //lemmatize the nounPhrases
                            //add the verbs and noun phrases to the map

                            //Refine the noun phrases
                            //List<String> of a noun phrase permutations

                            //List<List<String>> permutations = new ArrayList<>();
                            for (String nounPhrase : nounPhrases) {
                                //refining each noun phrase
                                List<String> refinedNounPhrases = nounPhraseRefinement(nounPhrase);
                                List<String> temp = new ArrayList<>();
                                System.out.println("refined Noun Phrase:" + refinedNounPhrases);
                                //The powerSet generates 2^n sets. If the input set is more than a limited number the program cannot handle generating powerSet
                                if (refinedNounPhrases.size() < 7) {
                                    temp = nounPhraseProcess(refinedNounPhrases);
                                    for (String tm : temp) {
                                        if (!nounPhrasesPermutations.contains(tm)) {
                                            nounPhrasesPermutations.add(tm);
                                        }
                                    }
                                    //nounPhrasesPermutations.add(nounPhraseProcess(refinedNounPhrases));
                                }
                            }
                        }
                    }
                }
            }
            Enumeration<DefaultMutableTreeNode> children = rootNode.children();
            while (children.hasMoreElements()) {
                DefaultMutableTreeNode child = children.nextElement();
                getVerbPhrases(child);
            }
        }

        /**
         * This method lemmatize a NP and returns a list of all words starting with "NN" POS tag.
         *
         * @param nounPhrase
         * @return
         */
        private List<String> nounPhraseRefinement(String nounPhrase) {
            List<String> lemmaNounPhrase = new ArrayList<>();
            Map<String, String> lemma = new HashMap<>();
            lemma = Lemma.lemmatize(nounPhrase);
            System.out.println("Noun phrase to be lemmatized: " + nounPhrase);
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
        private List<String> nounPhraseProcess(List<String> nounPhrase) {
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

        public <String> Set<Set<String>> powerSet(Set<String> originalSet) {
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

        public void swap(List<String> input, int i, int j) {
            String tmp = input.get(i);
            input.set(i, input.get(j));
            input.set(j, tmp);
        }

        public void permute(List<String> ps, int i, List<String> perm) {
            if (i == ps.size()) {
                System.out.println("adding the permutation to the final set: ");
                System.out.println(ps);
                StringBuilder st = new StringBuilder();
                for(int j = 0; j<ps.size(); j++){
                    if(j == ps.size()- 1){
                        st.append(ps.get(j));
                        break;
                    }
                    st.append(ps.get(j) + " ");
                }
                System.out.println(st.toString());
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
        private void getNounPhrases(List<String> nounPhrases, DefaultMutableTreeNode currentNode) {
            if (currentNode.getUserObject().toString().equalsIgnoreCase("NP")) {
                StringBuilder np = new StringBuilder();
                Enumeration<DefaultMutableTreeNode> postOrder = currentNode.postorderEnumeration();
                while (postOrder.hasMoreElements()) {
                    DefaultMutableTreeNode node = postOrder.nextElement();
                    if (node.isLeaf()) {
                        np.append(node.getUserObject().toString() + " ");
                    }
                }
                nounPhrases.add(np.toString());
            }
            Enumeration<DefaultMutableTreeNode> children = currentNode.children();
            while (children.hasMoreElements()) {
                DefaultMutableTreeNode child = children.nextElement();
                getNounPhrases(nounPhrases, child);
            }
        }

        /**
         * This method returns the number of all nodes in one subtree with the rootNode as the input argument
         *
         * @param rootNode
         * @return
         */
        private int getChildrenCount(DefaultMutableTreeNode rootNode) {
            Enumeration<DefaultMutableTreeNode> en = rootNode.breadthFirstEnumeration();
            int count = 0;
            while (en.hasMoreElements()) {
                en.nextElement();
                count++;
            }
            return (count - 1);
        }


        public boolean containsCollectVerb(String phrase) {
            //String def = "VerbNotFound";
            for (String verb : collectVerbList) {
                if (phrase.equalsIgnoreCase(verb)) {
                /*if verb.equalsIgnoreCase("use"){

            }*/
                    return true;
                }
            }
            return false;
        }


    }
}



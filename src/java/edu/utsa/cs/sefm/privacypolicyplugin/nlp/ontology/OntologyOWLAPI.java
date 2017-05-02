package edu.utsa.cs.sefm.privacypolicyplugin.nlp.ontology;

import com.intellij.openapi.vfs.VirtualFile;
import edu.utsa.cs.sefm.privacypolicyplugin.PolicyViolationAppComponent;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.util.*;

public class OntologyOWLAPI {
    public static HashMap<String, String> map = new HashMap<>();
    public static OWLOntology ontology;
    public static List<String> ontologyPhrases = Collections.synchronizedList(new ArrayList<String>());
    public static List<String> lemmaOntologyPhrases = Collections.synchronizedList(new LinkedList<String>());
    private static OWLOntologyManager man;
    private static OWLDataFactory fact;
    private static String unvisited = "unvisited";
    private static String visited = "visited";
    private static String visiting = "visiting";

    /**
     * loads the ontology from the specified file
     *
     * @param ontologyFile
     */
    public static void loader(VirtualFile ontologyFile) {
        try {
            man = OWLManager.createOWLOntologyManager();
            fact = man.getOWLDataFactory();
            ontology = man.loadOntologyFromOntologyDocument(ontologyFile.getInputStream());
            PolicyViolationAppComponent.logger.info("Ontology was read successfully. Total Axioms: " + ontology.getAxiomCount());
        } catch (Exception e) {
            PolicyViolationAppComponent.logger.error("Failed to load ontology");
            e.printStackTrace();
        }
    }

    /**
     * returns all the phrases in the ontology
     *
     * @param ontology
     * @return
     */
    public static List<String> ListOfOntologyPhrases(OWLOntology ontology) {
        List<String> allOntologyPhrases = new ArrayList<>();
        for (OWLClass cls : ontology.getClassesInSignature()) {
            String phrase = cls.getIRI().toString().substring(cls.getIRI().toString().indexOf('#') + 1).toLowerCase().replaceAll("_", " ");
            allOntologyPhrases.add(phrase);
        }
        return allOntologyPhrases;
    }

    /**
     * checks the if the class exists in the ontology or not
     *
     * @param className
     * @param ontology
     * @return
     */
    public static boolean classDoesExists(String className, OWLOntology ontology) {
        for (OWLClass cls : ontology.getClassesInSignature()) {
            if (className.compareToIgnoreCase(cls.getIRI().toString().substring(cls.getIRI().toString().indexOf('#') + 1)) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * checks the subClass relationship between two phrase in ontology
     *
     * @param child
     * @param parent
     * @param ontology
     */
    private static boolean isSubclassof(String child, String parent, OWLOntology ontology) {
        for (OWLClass cls : ontology.getClassesInSignature()) {
            if (parent.compareToIgnoreCase(cls.getIRI().toString().substring(cls.getIRI().toString().indexOf('#') + 1)) == 0) {
                for (OWLClassExpression children : cls.getSubClasses(ontology)) {
                    if (child.compareToIgnoreCase(children.asOWLClass().getIRI().toString().substring(children.asOWLClass().getIRI().toString().indexOf('#') + 1)) == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * check if two classes are equivalent in ontology.
     *
     * @param ancestor
     * @param child
     * @param ontology
     */
    public static boolean isEquivalent(String ancestor, String child, OWLOntology ontology) {
        if (isSubclassof(ancestor, child, ontology)) {
            if (isSubclassof(child, ancestor, ontology)) {
                return true;
            }
        }
        for (OWLClass cls : ontology.getClassesInSignature()) {
            if (ancestor.compareToIgnoreCase(cls.getIRI().toString().substring(cls.getIRI().toString().indexOf('#') + 1)) == 0) {
                for (OWLClassExpression synonyms : cls.getEquivalentClasses(ontology)) {
                    if (child.compareToIgnoreCase(synonyms.asOWLClass().getIRI().toString().substring(synonyms.asOWLClass().getIRI().toString().indexOf('#') + 1)) == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * finds the list of parents for a class in ontology.
     *
     * @param className
     * @param ontology
     */
    private static List<String> getParentList(String className, OWLOntology ontology) {
        List<String> parentList = new ArrayList<>();
        for (OWLClass cls : ontology.getClassesInSignature()) {
            if (className.compareToIgnoreCase(cls.getIRI().toString().substring(cls.getIRI().toString().indexOf('#') + 1)) == 0) {
                // Class found
                for (OWLClassExpression parent : cls.getSuperClasses(ontology)) {
                    if (!parentList.contains(parent.asOWLClass().getIRI().toString().substring(parent.asOWLClass().getIRI().toString().indexOf('#') + 1))) {
                        parentList.add(parent.asOWLClass().getIRI().toString().substring(parent.asOWLClass().getIRI().toString().indexOf('#') + 1));
                    }
                }
            }
        }
        return parentList;
    }

    /**
     * Returns a list with all equivalents classes of the input parameter class
     *
     * @param className
     * @param ontology
     * @return
     */
    private static List<String> getEquivalentList(String className, OWLOntology ontology) {
        List<String> equivalentList = new ArrayList<>();
        for (OWLClass cls : ontology.getClassesInSignature()) {
            //System.out.println(cls.getIRI().toString().substring(cls.getIRI().toString().indexOf('#')+1));
            if (className.compareToIgnoreCase(cls.getIRI().toString().substring(cls.getIRI().toString().indexOf('#') + 1)) == 0) {
                //We found the class
                if (!cls.getEquivalentClasses(ontology).isEmpty()) {
                    for (OWLClassExpression equiv : cls.getEquivalentClasses(ontology)) {
                        //System.out.println("This is equiv to " + equiv.toString());
                        if (!equivalentList.contains(equiv.asOWLClass().getIRI().toString().substring(equiv.asOWLClass().getIRI().toString().indexOf('#') + 1)))
                            equivalentList.add(equiv.asOWLClass().getIRI().toString().substring(equiv.asOWLClass().getIRI().toString().indexOf('#') + 1));
                    }
                }
            }
        }
        return equivalentList;
    }

    /**
     * checks if two class(phrase) has direct or indirect relationship in ontology based on DFS
     *
     * @param ancestor
     * @param child
     * @param ontology
     */
    public static boolean isAncestorOf(String ancestor, String child, OWLOntology ontology) {
        if (isSubclassof(child, ancestor, ontology)) {
            return true;
        }
        if (isEquivalent(ancestor, child, ontology)) {
            return false;
        }
        LinkedList<String> q = new LinkedList<String>();
        for (OWLClass u : ontology.getClassesInSignature()) { // method defined in Graph class. not in java APIs
            // set all states as unvisited
            map.put(u.getIRI().toString().toLowerCase().substring(u.getIRI().toString().indexOf('#') + 1), unvisited);
        }

        if (map.containsKey(child.toLowerCase())) {
            map.put(child.toLowerCase(), visiting);
            q.add(child);
            String curr;
            while (!q.isEmpty()) { // while q is not empty
                curr = q.removeFirst(); // i.e., pop(), LinkedList.removeFirst() is in java API
                if (curr != null) { // if there is still something on q
                    List<String> parentList = getParentList(curr, ontology);
                    List<String> equivalentList = getEquivalentList(curr, ontology);
                    List<String> adjacentNodes = new ArrayList<>();
                    adjacentNodes.addAll(parentList);
                    adjacentNodes.addAll(equivalentList);
                    for (String adjacentNode : adjacentNodes)// for each node v adjacent t
                    {
                        if (map.get(adjacentNode.toLowerCase()).contentEquals(unvisited)) {
                            if (adjacentNode.equalsIgnoreCase(ancestor)) {
                                return true; // there is a path
                            } else {
                                map.put(adjacentNode.toLowerCase(), visiting);
                                q.add(adjacentNode.toLowerCase());// add each adjacent node current parent to q
                            }
                        }
                    }
                }
                map.put(curr != null ? curr.toLowerCase() : null, visited);
            }
        }
        return false;
    }

}

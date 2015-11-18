package edu.utsa.cs.sefm.privacypolicyplugin.ontology;

/**
 * Created by Mitra on 11/15/2015.
 */
import java.io.File;
import java.io.IOException;
import java.util.*;

import com.intellij.openapi.vfs.VirtualFile;
import org.apache.log4j.lf5.util.Resource;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OntologyOWLAPI {
    static OWLOntologyManager man;
    static OWLDataFactory fact;
    public static List<List<String>> allOntologyPhrases = new ArrayList<List<String>>();
    static String unvisited = "unvisited" ;
    static String visited = "visited";
    static String visiting = "visiting" ;
    public static ArrayList<ArrayList<String>> relations = new ArrayList<>();
    static List<AddAxiom> myList = new ArrayList<AddAxiom>();
    public static HashMap<String, String> map = new HashMap<String, String>();
    public static OWLOntology ontology;
    /**
     * loads the ontology from the specified file
     * @param ontologyFile
     */
    public static void loader(VirtualFile ontologyFile){
        try {
            man = OWLManager.createOWLOntologyManager();
            fact = man.getOWLDataFactory();
            try {
                ontology = man.loadOntologyFromOntologyDocument(ontologyFile.getInputStream());
                System.out.println("Ontology was read successfully:"+ ontology.getAxiomCount());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch (OWLException e) {
            e.printStackTrace();
        }
        }
        public static List<List<String>> ListOfOntologyPhrases(OWLOntology ontology){
            for (OWLClass cls : ontology.getClassesInSignature()){
                List sublist = Arrays.asList(cls.toString().substring(cls.toString().indexOf('#')+1, cls.toString().indexOf('>')).toLowerCase().split("_"));
              allOntologyPhrases.add(sublist);
                }
            return allOntologyPhrases;
        }
    public static boolean classDoesExists(String className, OWLOntology ontology){
        for (OWLClass cls : ontology.getClassesInSignature()){
            if (className.compareToIgnoreCase(cls.toString().substring(cls.toString().indexOf('#')+1, cls.toString().indexOf('>')))==0){
                return true;
            }
        }
        return false;
    }
    /**
     * checks the subClass relationship between two phrase in ontology
     * @param child
     * @param parent
     * @param ontology
     */
    public static boolean isSubclassof(String child, String parent, OWLOntology ontology){
        for (OWLClass cls : ontology.getClassesInSignature()){
            if (parent.compareToIgnoreCase(cls.toString().substring(cls.toString().indexOf('#')+1, cls.toString().indexOf('>')))==0){
                for (OWLClassExpression children : cls.getSubClasses(ontology)){
                    if (child.compareToIgnoreCase(children.toString().substring(children.toString().indexOf('#')+1, children.toString().indexOf('>')))==0){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
     * check if two classes are equivalent in ontology.
     * @param ancestor
     * @param child
     * @param ontology
     */
    public static boolean isEquivalent(String ancestor,String child, OWLOntology ontology){
        if (isSubclassof(ancestor, child, ontology)){
            if(isSubclassof(child,ancestor, ontology)){
                return true;
            }
        }
        for(OWLClass cls : ontology.getClassesInSignature()){
            if (ancestor.compareToIgnoreCase(cls.toString().substring(cls.toString().indexOf('#')+1, cls.toString().indexOf('>')))==0){
                for(OWLClassExpression synonyms : cls.getEquivalentClasses(ontology)){
                    if (child.compareToIgnoreCase(synonyms.toString().substring(synonyms.toString().indexOf('#')+1, synonyms.toString().indexOf('>')))==0){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
     * finds the list of parents for a class in ontology.
     * @param className
     * @param ontology
     */
    public static List<String> getParentList(String className, OWLOntology ontology){
        List<String> parentList =  new ArrayList<String>();
        for (OWLClass cls : ontology.getClassesInSignature()){
            if (className.compareToIgnoreCase(cls.toString().substring(cls.toString().indexOf('#')+1, cls.toString().indexOf('>')))==0){
                //We found the class
                for (OWLClassExpression parent : cls.getSuperClasses(ontology)){
                    if(!parentList.contains(parent.toString().substring(parent.toString().indexOf('#')+1, parent.toString().indexOf('>')))){
                        parentList.add(parent.toString().substring(parent.toString().indexOf('#')+1, parent.toString().indexOf('>')));
                    }
                }
            }
        }
        return parentList;
    }
    /**
     * checks if two class(phrase) has direct or indirect relationship in ontology based on DFS
     * @param ancestor
     * @param child
     * @param ontology
     */
    public static boolean isAncestorOf(String ancestor, String child, OWLOntology ontology) {
        if(isSubclassof(child,ancestor,ontology)){
            return true;
        }
        if(isEquivalent(ancestor, child, ontology)){
            return false;
        }
        LinkedList<String> q = new LinkedList<String>();
        for (OWLClass u : ontology.getClassesInSignature()) { // method defined in Graph class. not in java APIs
            // set all states as unvisited
            map.put(u.toString().substring(u.toString().indexOf('#')+1, u.toString().indexOf('>')), unvisited);
        }
	    /*
	     * start.state = State.Visiting; // start was passed in
           q.add(start); //add start onto stack.
	     */
        if(map.containsKey(child)){
            map.put(child, visiting);
            q.add(child);
            String curr;
            while(!q.isEmpty()) { // while q is not empty
                curr = q.removeFirst(); // i.e., pop(), LinkedList.removeFirst() is in java API
                if (curr != null) { // if there is still something on q
                    OWLClass currClass = fact.getOWLClass(IRI.create("#" + curr ));
                    List<String> parentList = getParentList(curr, ontology);
                    for(String parent : parentList)// for each node v adjacent t
                    {
                        if(map.get(parent).contentEquals(unvisited)){
                            if (parent.equalsIgnoreCase(ancestor)){
                                return true; // there is a path
                            }
                            else {
                                map.put(parent, visiting);
                                q.add(parent);// add each adjacent node current parent to q
                            }
                        }
                    }
                }
                map.put(curr, visited);
            }
        }
        return false;
    }
}

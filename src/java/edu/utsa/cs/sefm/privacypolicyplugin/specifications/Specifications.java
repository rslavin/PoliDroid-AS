package edu.utsa.cs.sefm.privacypolicyplugin.specifications;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitra on 4/12/2016.
 */
public class Specifications {
    private static List<Specifications> allSpecifications = new ArrayList<>();
    private String api;
    private List<String> phrasesMappedToApi;
    private String purpose;
    private String actionVerbCategory;

    public Specifications(){}

    public static void setSpecification(String api, List<String> phrases){
        Specifications spec = new Specifications();
        spec.api = api;
        spec.phrasesMappedToApi = phrases;
        allSpecifications.add(spec);
    }
    public static void setSpecification(String api, List<String> phrases, String purpose){
        Specifications spec = new Specifications();
        spec.api = api;
        spec.phrasesMappedToApi = phrases;
        spec.purpose = purpose;
        allSpecifications.add(spec);
    }
    public static void setSpecification(String api, List<String> phrases, String purpose, String actionVerb){
        Specifications spec = new Specifications();
        spec.api = api;
        spec.phrasesMappedToApi = phrases;
        spec.purpose = purpose;
        spec.actionVerbCategory = actionVerb;
        allSpecifications.add(spec);
    }
    public static List<Specifications> getAllSpecifications() {
        return allSpecifications;
    }
    public String getApiName(){
        return this.api;
    }
    public List<String> getPhrases(){
        return this.phrasesMappedToApi;
    }
    public String getPurpose(){
        return this.purpose;
    }
    public String getActionVerb(){
        return this.actionVerbCategory;
    }
}

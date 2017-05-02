package edu.utsa.cs.sefm.privacypolicyplugin.models;

import java.util.ArrayList;
import java.util.List;

public class ApiMethod {
    public List<String> phrases;
    public String api;
    public boolean allowed;

    public ApiMethod(String api) {
        this.api = api;
        this.phrases = new ArrayList<>();
        this.allowed = false;
    }

    public ApiMethod(String api, String phrase){
        this.api = api;
        this.phrases = new ArrayList<>();
        this.addPhrase(phrase);
        this.allowed = false;
    }

    public String toSimpleString(){
        return api.toLowerCase().replaceAll("\\(.*$", "").replaceAll("\"", "");
    }

    public void addPhrase(String phrase){
        this.phrases.add(phrase);
    }
}

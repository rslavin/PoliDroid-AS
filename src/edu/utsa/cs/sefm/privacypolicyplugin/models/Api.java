package edu.utsa.cs.sefm.privacypolicyplugin.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rocky on 10/18/2015.
 */
public class Api {
    public List<String> phrases;
    public String api;
    public boolean allowed;

    public Api(String api) {
        this.api = api;
        this.phrases = new ArrayList<>();
        this.allowed = false;
    }

    public Api(String api, String phrase){
        this.api = api;
        this.phrases = new ArrayList<>();
        this.addPhrase(phrase);
        this.allowed = false;
    }

    public void addPhrase(String phrase){
        this.phrases.add(phrase);
    }
}

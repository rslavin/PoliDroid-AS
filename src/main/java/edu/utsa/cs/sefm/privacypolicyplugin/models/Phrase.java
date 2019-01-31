package edu.utsa.cs.sefm.privacypolicyplugin.models;

import java.util.ArrayList;
import java.util.List;

public class Phrase {
    public List<String> apis;
    public String phrase;
    public boolean allowed;

    public Phrase(String phrase) {
        this.phrase = phrase;
        this.apis = new ArrayList<>();
        this.allowed = false;
    }

    public void addApi(String api){
        apis.add(api);
    }

}

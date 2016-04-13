package edu.utsa.cs.sefm.privacypolicyplugin.specifications;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitra on 4/12/2016.
 */
public class Template {
    public List<String> specList = new ArrayList<>();

    private void getAllObjects() {
        for (Specifications object : Specifications.getAllSpecifications()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < object.getPhrases().size(); i++) {
                if (i == 0) {
                    sb.append(object.getPhrases().get(i));
                    continue;
                }
                sb.append(", " + object.getPhrases().get(i));
            }
            if (!object.getPurpose().isEmpty()) {
                specList.add("The application collects information about " + sb.toString() + " for " + object.getPurpose());
            } else {
                specList.add("The application collects information about " + sb.toString());
            }
        }
    }
}
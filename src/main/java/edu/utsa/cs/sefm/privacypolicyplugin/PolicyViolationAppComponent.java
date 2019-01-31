package edu.utsa.cs.sefm.privacypolicyplugin;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import edu.utsa.cs.sefm.privacypolicyplugin.models.ApiMethod;
import edu.utsa.cs.sefm.privacypolicyplugin.models.Specification;
import edu.utsa.cs.sefm.privacypolicyplugin.nlp.ontology.preprocess.ParagraphProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by Rocky on 10/18/2015.
 */
public class PolicyViolationAppComponent implements ApplicationComponent {
    public final static Logger logger = Logger.getInstance("PoliDroid-AS");
    public List<ApiMethod> apiMethods; // all methods from models
    public Set<String> phrases;
    public Set<String> apisInCode; // unique list of api methods that we have models for in the code
    public List<Specification> specifications; // specifications from spec generator
    public ParagraphProcessor paragraphProcessor;


    public PolicyViolationAppComponent() {
        apiMethods = new ArrayList<>();
        phrases = new HashSet<>();
        apisInCode = new HashSet<>();
        specifications = new ArrayList<>();
        paragraphProcessor = new ParagraphProcessor();
    }

    /**
     * Called immediately after constructor
     */
    public void initComponent() {
//        initApis();
//        initPolicyPhrases();
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "PolicyViolationAppComponent";
    }

    /**
     * Adds an API-phrase mapping to the plugin's database. Call this from the mapping
     * file reader.
     * @param api
     * @param phrase
     */
    public void addApi(String api, String phrase) {
        phrases.add(phrase.toLowerCase().trim());
        for (ApiMethod existingApiMethod : apiMethods) {
            if (existingApiMethod.api.equals(api)) {
                existingApiMethod.addPhrase(phrase);
                return;
            }
        }
        apiMethods.add(new ApiMethod(api, phrase));
    }

    /**
     * Checks if the api exists in the set of all mapped APIs.
     * @param api API to search for.
     * @return First phrase associated with the API.
     */
    String hasApi(String api) {
        for (ApiMethod existingApiMethod : apiMethods) {
            if (existingApiMethod.api.toLowerCase().equals(api.toLowerCase())) {
                return existingApiMethod.phrases.get(0);
            }
        }
        return "";
    }

    /**
     * Checks if the api is not allowed based on the privacy policy. If the API
     * exists in the set of all models, but is _not_ represented in the privacy
     * policy, this method returns the first phrase associated with that API.
     * This method is also responsible for detecting any mapped methods in the code.
     * @param api API to check for violation
     * @return Phrase associated with violation _or_ empty String if no violation
     */
    List<String> isViolation(String api){
        for (ApiMethod existingApiMethod : apiMethods) {
            // add it to list of apiMethods found in the code
            if(existingApiMethod.toSimpleString().equals(api.toLowerCase())) {
                PolicyViolationAppComponent.logger.info("API invocation detected: " + api);
                apisInCode.add(api);
                // check if it's a violation
                if(!existingApiMethod.allowed) {
                    PolicyViolationAppComponent.logger.info("Misalignment detected: " + api);
                    return existingApiMethod.phrases;
                }
            }

        }
        return Collections.emptyList();
    }

    /**
     * Marks a specific phrase as being covered by the privacy policy.
     * Marks APIs as allowed if they are associated with the phrase.
     * This detection is based on the mappings file and is used for detecting
     * valid API invocations.
     * @param phrase
     */
    public void addPolicyPhrase(String phrase){
        boolean found = false;
        // check each method in the mapping
        for(ApiMethod apiMethod : apiMethods){
            // if it is mapped to phrase, allow it (non-violation)
            if(apiMethod.phrases.contains(phrase.toLowerCase().trim())) {
                PolicyViolationAppComponent.logger.info("\t Allowing " + apiMethod.toSimpleString());
                found = true;
                apiMethod.allowed = true;
            }
        }
        if(found){
            PolicyViolationAppComponent.logger.info("\t Detected directly-mapped phrase (" + phrase + ") in policy. " +
                    "Associated methods marked as allowed.");
        }
    }

    /**
     * Looks for existing specification. This is useful for when the user starts the specification
     * generator over again and overwrites existing specs.
     * @param methodName
     * @return
     */
    public Specification findSpec(String methodName){
        for(Specification spec : specifications)
            if(methodName.toLowerCase().equals(spec.getMethodName().toLowerCase()))
                return spec;
        return null;
    }
}

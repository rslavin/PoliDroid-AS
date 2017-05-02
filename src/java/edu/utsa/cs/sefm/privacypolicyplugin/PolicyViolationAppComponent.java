package edu.utsa.cs.sefm.privacypolicyplugin;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import edu.utsa.cs.sefm.privacypolicyplugin.models.Api;
import edu.utsa.cs.sefm.privacypolicyplugin.models.Specification;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by Rocky on 10/18/2015.
 */
public class PolicyViolationAppComponent implements ApplicationComponent {
    // test data
    private static final Map<String, String> ALL_MAPPINGS;
    private static final List<String> POLICY_PHRASES;
    public static final boolean DEBUG = true;
    public final static Logger logger = Logger.getInstance("PoliDroid-AS");

    static {
        ALL_MAPPINGS = new HashMap<>();
        // test models
        ALL_MAPPINGS.put("bandwidth", "android.net.wifi.WifiManager.getConfiguredNetworks");
        ALL_MAPPINGS.put("bandwidth", "android.net.wifi.WifiManager.getDhcpInfo");
        ALL_MAPPINGS.put("test", "java.io.file.getname");

        // test phrases from policy
        POLICY_PHRASES = Arrays.asList("another test");
    }

    public List<Api> apis; // all methods from models
    public Set<String> phrases;
    public Set<String> apisInCode; // unique list of api methods that we have models for in the code
    public List<Specification> specifications; // specifications from spec generator


    public PolicyViolationAppComponent() {
        apis = new ArrayList<>();
        phrases = new HashSet<>();
        apisInCode = new HashSet<>();
        specifications = new ArrayList<>();
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
     * Initializes set of all APIs.
     */
    private void initApis() {
        for (Map.Entry<String, String> entry : ALL_MAPPINGS.entrySet()) {
            addApi(entry.getValue(), entry.getKey());
        }
    }

    /**
     * Initializes phrases from the privacy policy. These mark "allowable" APIs.
     */
    private void initPolicyPhrases(){
        for(String policyPhrase : POLICY_PHRASES){
            addPolicyPhrase(policyPhrase);
        }

    }

    /**
     * Adds an API-phrase mapping to the plugin's database. Call this from the mapping
     * file reader.
     * @param api
     * @param phrase
     */
    public void addApi(String api, String phrase) {
        phrases.add(phrase.toLowerCase().trim());
        for (Api existingApi : apis) {
            if (existingApi.api.equals(api)) {
                existingApi.addPhrase(phrase);
                return;
            }
        }
        apis.add(new Api(api, phrase));
    }

    /**
     * Checks if the api exists in the set of all mapped APIs.
     * @param api API to search for.
     * @return First phrase associated with the API.
     */
    public String hasApi(String api) {
        for (Api existingApi : apis) {
            if (existingApi.api.toLowerCase().equals(api.toLowerCase())) {
                return existingApi.phrases.get(0);
            }
        }
        return "";
    }

    /**
     * Checks if the api is not allowed based on the privacy policy. If the API
     * exists in the set of all models, but is _not_ represented in the privacy
     * policy, this method returns the first phrase associated with that API.
     * @param api API to check for violation
     * @return Phrase associated with violation _or_ empty String if no violation
     */
    public List<String> isViolation(String api){
        for (Api existingApi : apis) {
            // add it to list of apis found in the code
            if(existingApi.api.toLowerCase().equals(api.toLowerCase()))
                apisInCode.add(api);

            // check if it's a violation
            if (!existingApi.allowed && existingApi.api.toLowerCase().equals(api.toLowerCase())) {
                return existingApi.phrases;
            }
        }
        List<String> emptyList = Collections.emptyList();
        return emptyList;
    }

    /**
     * Marks a specific phrase as being covered by the privacy policy.
     * Marks APIs as allowed if they are associated with the phrase.
     * @param phrase
     */
    public void addPolicyPhrase(String phrase){
        for(Api api : apis){
            if(api.phrases.contains(phrase.toLowerCase().trim())) {
                System.err.println("added " + phrase);
                api.allowed = true;
            }
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

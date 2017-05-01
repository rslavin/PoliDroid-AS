package edu.utsa.cs.sefm.privacypolicyplugin.models;

/**
 * Created by Rocky on 5/4/2016.
 */
public class Specification {
    public static final String NOT_SPECIFIED = "NOT SPECIFIED";

    private String methodName;
    private String verb;
    private String phrase;
    private String necessaryFunctionality;
    private String necessaryBusiness;
    private String how;
    private String store;
    private String howLong;
    private String share;
    private String shareHow;
    private String who;

    public Specification(String methodName) {
        this.methodName = methodName;
        this.verb = Specification.NOT_SPECIFIED;
        this.phrase = Specification.NOT_SPECIFIED;
        this.necessaryFunctionality = Specification.NOT_SPECIFIED;
        this.necessaryBusiness = Specification.NOT_SPECIFIED;
        this.how = Specification.NOT_SPECIFIED;
        this.store = Specification.NOT_SPECIFIED;
        this.howLong = Specification.NOT_SPECIFIED;
        this.share = Specification.NOT_SPECIFIED;
        this.shareHow = Specification.NOT_SPECIFIED;
        this.who = Specification.NOT_SPECIFIED;
    }

    public Specification(String methodName, String verb, String phrase, String necessaryFunctionality, String necessaryBusiness, String how, String store, String howLong, String share, String shareHow, String who) {
        this.methodName = methodName;
        this.verb = verb;
        this.phrase = phrase;
        this.necessaryFunctionality = necessaryFunctionality;
        this.necessaryBusiness = necessaryBusiness;
        this.how = how;
        this.store = store;
        this.howLong = howLong;
        this.share = share;
        this.shareHow = shareHow;
        this.who = who;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        if (methodName.length() > 0)
            this.methodName = methodName;
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        if (verb.length() > 0)
            this.verb = verb;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        if (phrase.length() > 0)
            this.phrase = phrase;
    }

    public String getNecessaryFunctionality() {
        return necessaryFunctionality;
    }

    public void setNecessaryFunctionality(String necessaryFunctionality) {
        if (necessaryFunctionality.length() > 0)
            this.necessaryFunctionality = necessaryFunctionality;
    }

    public String getNecessaryBusiness() {
        return necessaryBusiness;
    }

    public void setNecessaryBusiness(String necessaryBusiness) {
        if (necessaryBusiness.length() > 0)
            this.necessaryBusiness = necessaryBusiness;
    }

    public String getHow() {
        return how;
    }

    public void setHow(String how) {
        if (how.length() > 0)
            this.how = how;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        if (store.length() > 0)
            this.store = store;
    }

    public String getHowLong() {
        return howLong;
    }

    public void setHowLong(String howLong) {
        if (howLong.length() > 0)
            this.howLong = howLong;
    }

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        if (share.length() > 0)
            this.share = share;
    }

    public String getShareHow() {
        return shareHow;
    }

    public void setShareHow(String shareHow) {
        if (shareHow.length() > 0)
            this.shareHow = shareHow;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        if (who.length() > 0)
            this.who = who;
    }

    public String toString() {
        String dataString = (phrase.equals(Specification.NOT_SPECIFIED) ? "" : phrase + " ");
        return phrase + " is " + verb.toUpperCase() + " with the following specifications:\n" +
                "# The " + dataString + "data used is" + (necessaryFunctionality.toLowerCase().equals("yes") ? " " : " NOT ") + "for the app's basic functionality.\n" +
                "# The " + dataString + "data used is" + (necessaryBusiness.toLowerCase().equals("yes") ? " " : " NOT ") + "for business reasons.\n" +
                "# The " + dataString + "data will be used for: " + how + "\n" +
                "# The " + dataString + "data will" + (store.toLowerCase().equals("yes") ? " " : " NOT ") + "be stored off the device on servers.\n" +
                "# The " + dataString + "data will be stored for " + howLong + " on such servers.\n" +
                "# The " + dataString + "data will" + (share.toLowerCase().equals("yes") ? " " : " NOT ") + "be shared with third parties.\n" +
                "# Such third parties will use the " + dataString + "data for: " + shareHow + "\n" +
                "# The " + dataString + "data will be accessed by " + who + " within the organization.";
    }
}

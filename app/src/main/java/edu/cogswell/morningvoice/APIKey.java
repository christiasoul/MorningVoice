package edu.cogswell.morningvoice;

/**
 * Created by Christian on 4/18/2017.
 */

public class APIKey {
    private static final APIKey ourInstance = new APIKey();

    public static APIKey getInstance() {
        return ourInstance;
    }

    public String getAPIKey(){
        return "3f6a98603f7221a761f6a322aa5790c2";
    }

    private APIKey() {
    }
}

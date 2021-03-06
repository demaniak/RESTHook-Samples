package com.whereismytransport.resthook.client.auth;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Henk on 2016/04/21.
 */
public class ClientCredentials {
    public String grant_type="client_credentials";
    public String scopes;
    public String client_id;
    public String client_secret;
    public String identityUrl;

    public ClientCredentials(String clientId, String clientSecret, String identityUrl, String scopes){
        client_id=clientId;
        client_secret=clientSecret;
        this.identityUrl=identityUrl;
        this.scopes=scopes;
    }

    public Map<String,String> getMap(){
        Map<String,String> map=new HashMap<>();
        map.put("grant_type",grant_type);
        map.put("client_id",client_id);
        map.put("client_secret",client_secret);
        map.put("scope",scopes);
        return map;
    }
}


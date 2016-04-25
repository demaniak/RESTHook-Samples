package com.whereismytransport.resthook.client;

public class RESTHookRequest {
    public String callbackUrl;
    public String description;

    public RESTHookRequest(String callbackUrl, String description) {
        this.callbackUrl = callbackUrl;
        this.description = description;
    }
}

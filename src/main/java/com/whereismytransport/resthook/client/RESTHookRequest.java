package com.whereismytransport.resthook.client;


/**
 * This class is the body which is required to be present in all RESTHook creation
 * requests
 * */

public class RESTHookRequest {

    public String callbackUrl;
    public String description;


    /**
     *
     * @param callbackUrl The callbackUrl represent the address on *THIS* server at which
     *                    post requests must be made for this hook
     * @param description A human readable description of what this hook is for (e.g. "Post to Twitter")
     */
    public RESTHookRequest(String callbackUrl, String description) {
        this.callbackUrl = callbackUrl;
        this.description = description;
    }
}

package com.whereismytransport.resthook.client;

/**
 * Created by ncthbrt on 2016/05/13.
 */
public class ChannelRestHookRetrofitRequest extends RestHookRetrofitRequest {

    public int characterLimit;

    public ChannelRestHookRetrofitRequest(String description, int characterLimit){
        super(description);
        this.characterLimit=characterLimit;
    }
}

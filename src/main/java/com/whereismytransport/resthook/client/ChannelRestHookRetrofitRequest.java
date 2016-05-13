package com.whereismytransport.resthook.client;

/**
 * Created by ncthbrt on 2016/05/13.
 */
public class ChannelRestHookRetrofitRequest extends RestHookRetrofitRequest {
    public String channelName;
    public int characterLimit;

    public ChannelRestHookRetrofitRequest(String callbackUrl, String description, String channelName, int characterLimit){
        super(description);
        this.channelName=channelName;
        this.characterLimit=characterLimit;
    }
}

package com.whereismytransport.resthook.client.azure;

import com.microsoft.azure.storage.table.TableServiceEntity;
import java.net.URLEncoder;

import java.io.UnsupportedEncodingException;

/**
 * Created by Nick Cuthbert on 25/04/2016.
 */
public class RestHookTableEntity extends TableServiceEntity {

    public static final String restHookPartitionKey ="ClientSecrets";
    public String secret;
    public String serverUrl;
    public String relativeCallbackUrl;
    public String relativeServerUrl;

    public RestHookTableEntity(){}

    public RestHookTableEntity(String relativeCallbackUrl, String secret, String serverUrl, String relativeServerUrl){
        this.secret = secret;
        this.serverUrl = serverUrl;
        this.relativeServerUrl = relativeServerUrl;
        this.relativeCallbackUrl=relativeCallbackUrl;
        this.partitionKey=RestHookTableEntity.restHookPartitionKey;

        try {
            this.rowKey= URLEncoder.encode(serverUrl+relativeServerUrl,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }



    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getRelativeServerUrl() {
        return relativeServerUrl;
    }

    public void setRelativeServerUrl(String relativeServerUrl) {
        this.relativeServerUrl = relativeServerUrl;
    }



    public String getRelativeCallbackUrl() {
        return relativeCallbackUrl;
    }

    public void setRelativeCallbackUrl(String relativeCallbackUrl) {
        this.relativeCallbackUrl = relativeCallbackUrl;
    }
}

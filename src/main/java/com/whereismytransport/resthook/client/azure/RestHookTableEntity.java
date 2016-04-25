package com.whereismytransport.resthook.client.azure;

import com.microsoft.azure.storage.table.TableServiceEntity;
import com.sun.deploy.net.URLEncoder;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * Created by Nick Cuthbert on 25/04/2016.
 */
public class RestHookTableEntity extends TableServiceEntity {

    public RestHookTableEntity(String target, String hookType){
        this.partitionKey="TestClientSecrets";
        this.endpoint = Math.abs(new Random().nextInt());
        try {
            this.rowKey= URLEncoder.encode(target+":"+hookType,"UTF-8");
            this.target=target;
            this.hookType=hookType;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public RestHookTableEntity(String relativeCallbackUrl, String secret, String serverUrl, String relativeServerUrl
                               this.partitionKey="TestClientSecrets";
        this.endpoint = endpoint;
        try {
            this.rowKey= URLEncoder.encode(target+":"+hookType,"UTF-8");
            this.hookSecret =hookSecret;
            this.endpoint = endpoint;
            this.target=target;
            this.hookType=hookType;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public int endpoint =-1;
    public String hookSecret="";
    public String target;
    public String hookType;

    public String getHookSecret(){
        return hookSecret;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getHookType() {
        return hookType;
    }

    public void setHookType(String hookType) {
        this.hookType = hookType;
    }

    public void setHookSecret(String hookSecret){
        this.hookSecret =hookSecret;
    }

    public void setEndpoint(int endpoint){
        this.endpoint = endpoint;
    }

    public int getEndpoint(){
        return endpoint;
    }
}

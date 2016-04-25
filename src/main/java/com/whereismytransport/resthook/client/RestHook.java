package com.whereismytransport.resthook.client;

import com.whereismytransport.resthook.client.auth.ClientCredentials;
import com.whereismytransport.resthook.client.auth.Token;
import com.whereismytransport.resthook.client.auth.TokenService;
import com.whereismytransport.resthook.client.azure.ClientRestHookTableEntity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Base64;
import java.util.List;

import static spark.Spark.post;

/**
 * Created by Nick Cuthbert on 25/04/2016.
 */
public class RestHook {
    private List<String> logs;
    private TokenService tokenService = TokenService.retrofit.create(TokenService.class);
    private CaptainHookApiService service;
    private byte[] Secret;

    public RestHook(String baseUrl, List<String> logs, List<String> messages, ClientRestHookTableEntity tableEntity){
        this.logs=logs;

        createWebHook(tableEntity);

        post("hooks/"+tableEntity.endpoint,(req, res) -> {
            if(req.headers().contains("X-Hook-Secret")) {
                // store secret
                tableEntity.setHookSecret(req.headers("X-Hook-Secret"));
                Secret = Base64.getDecoder().decode(tableEntity.getHookSecret());
                res.header("X-HookSecret", req.headers("X-Hook-Secret"));
                res.status(200);
                logs.add("X-Hook-Secret received");
                logs.add(req.headers("X-Hook-Secret"));
            } else if(req.headers().contains("X-Hook-Signature")){
                String body=req.body();
                logs.add(body);
                String bodyHash = "";
                try {
                    bodyHash = encode(Secret, body.getBytes("UTF-8"));
                    logs.add(bodyHash);
                }
                catch (Exception e) {
                    logs.add("Exception occurred encoding hash: " + e.getStackTrace().toString());
                    res.status(500);
                    return res;
                }
                String otherBodyHash=req.headers("X-Hook-Signature");
                logs.add(otherBodyHash);

                if(bodyHash.equals(otherBodyHash)){
                    res.status(200);
                }else{
                    res.status(403);
                }
                System.out.println(bodyHash);
                System.out.println("Message is:\n"+body);
                messages.add(body);
                logs.add("Webhook received.");
            } else{
                res.status(403);
                logs.add("Access denied: X-Hook-Signature or X-Hook-Secret is not present in headers.");
            }
            return res;
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl+"/api/v1/subscriptions/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service= retrofit.create(CaptainHookApiService.class);
    }


    private void createWebHook(ClientRestHookTableEntity entity) {

        // Tell CaptainHook that we want to subscribe to Alerts
        Call getTokenCall=tokenService.createToken(new ClientCredentials(ConfigStrings.clientId, ConfigStrings.clientSecret).getMap());
        getTokenCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                System.out.println(response.code());
                Call createHookCall =  service.createGlobalAlertRESTHook(new RESTHookRequest(ConfigStrings.url +"/hooks/"+entity.endpoint, "Alerts webhook"),"Bearer "+((Token)response.body()).access_token);
                createHookCall.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response2) {
                        if(response2.isSuccessful()) {
                            System.out.println("Created RESTHook Successfully");
                            logs.add("Created RESTHook Successfully");
                        }
                        else {
                            System.out.println(response.message());
                            System.out.println("Something went wrong calling webhook setup. Response code: " + response2.code());
                            logs.add("Something went wrong calling webhook setup. Response code: " + response2.code());
                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        if(t.getCause() == null) {
                            logs.add("CreateHookCall onFailure called.");
                        } else {
                            logs.add(t.getCause().toString());
                            logs.add(t.getCause().getLocalizedMessage());
                            logs.add("Error occurred in calling CaptainHook.");
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call call, Throwable t) {
            }
        });
    }


}

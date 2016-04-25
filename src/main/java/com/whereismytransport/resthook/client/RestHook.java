package com.whereismytransport.resthook.client;

import com.whereismytransport.resthook.client.auth.ClientCredentials;
import com.whereismytransport.resthook.client.auth.Token;
import com.whereismytransport.resthook.client.auth.TokenService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;
import spark.Request;
import spark.*;


import java.util.List;
import java.util.Random;

import static spark.Spark.post;

/**
 * Created by Nick Cuthbert on 25/04/2016.
 */
public class RestHook {
    private TokenService tokenService = TokenService.retrofit.create(TokenService.class);
    private CaptainHookApiService restHookService;
    private List<String> logs;
    private List<String> messages;

    private String serverUrl;
    private String serverRelativeUrl;

    private String relativeCallbackUrl;
    public String secret;

    public RestHook(String serverUrl,
                    String serverRelativeUrl,
                    String clientUrl,
                    ClientCredentials clientCredentials,
                    List<String> logs,
                    List<String> messages,
                    RestHookRepository restHookRepository) {

        this.logs = logs;
        this.serverUrl = serverUrl;

        this.serverRelativeUrl = serverRelativeUrl;
        this.relativeCallbackUrl = "hooks/" + Math.abs(new Random().nextInt());
        this.messages = messages;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl + "/api/v1/subscriptions/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        restHookService = retrofit.create(CaptainHookApiService.class);

        createHook(clientCredentials,clientUrl,() -> {
                post(this.relativeCallbackUrl, (req, res) -> {
                    if (req.headers().contains("X-Hook-Secret")) {
                        // store secret
//                    res.setHookSecret();
                        this.secret = req.headers("X-Hook-Secret");
                        restHookRepository.addRestHook(this.relativeCallbackUrl, this.secret, this.serverUrl, this.serverRelativeUrl);
                        res.header("X-HookSecret", req.headers("X-Hook-Secret"));
                        res.status(200);
                        logs.add("X-Hook-Secret received");
                        logs.add(req.headers("X-Hook-Secret"));
                        return res;
                    } else {
                        return handleHookMessage(req, res);
                    }
                });
        });
    }

    public RestHook(String serverUrl,
                    String serverRelativeUrl,
                    List<String> logs,
                    List<String> messages,
                    String secret)
    {
        this.serverUrl=serverUrl;
        this.serverRelativeUrl=serverRelativeUrl;
        this.secret=secret;
        this.logs=logs;
        this.messages=messages;
        post(this.relativeCallbackUrl, this::handleHookMessage);
    }

    private spark.Response handleHookMessage(Request req, spark.Response res) {
        if (req.headers().contains("X-Hook-Signature")) {
            String body = req.body();
            String xHookSignature = req.headers("X-Hook-Signature");
            messages.add(req.body());
            try {
                if (HmacUtilities.validBody(this, body, xHookSignature)) {
                    res.status(200); //OK
                } else {
                    res.status(403); //Access denied
                    String responseMessage = "Access denied: X-Hook-Signature does not match the secret.";
                    logs.add(responseMessage);
                    res.body(responseMessage);
                }
            } catch (Exception e) {
                String responseMessage = "Exception occurred encoding hash: " + e.getStackTrace().toString();
                logs.add(responseMessage);
                res.status(500); //Internal server error
                return res;
            }
        } else {
            res.status(403);
            String responseMessage = "Access denied: X-Hook-Signature or X-Hook-Secret is not present in headers.";
            logs.add(responseMessage);
            res.body(responseMessage);
        }
        return res;
    }


    private void createHook(ClientCredentials clientCredentials,String clientUrl, Runnable onHookCreate) {
        synchronized (this) {
            // Get token to connect to CaptainHook
            Call<Token> getTokenCall = tokenService.createToken(clientCredentials.getMap());

            getTokenCall.enqueue(new Callback<Token>() {
                @Override
                public void onResponse(Call<Token> call, retrofit2.Response<Token> response) {

                    Call createHookCall = restHookService.createRestHook(serverRelativeUrl, new RESTHookRequest(clientUrl + relativeCallbackUrl, "Test Webhook"),
                            "Bearer " + ((Token) response.body()).access_token);

                    createHookCall.enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, retrofit2.Response response2) {
                            if (response2.isSuccessful()) {
                                onHookCreate.run();

                                logs.add("Created RESTHook Successfully");
                            } else {
                                logs.add("Something went wrong calling webhook setup. Response code: " + response2.code());
                            }
                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {
                            if (t.getCause() == null) {
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

}

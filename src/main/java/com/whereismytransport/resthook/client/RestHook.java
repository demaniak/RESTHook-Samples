package com.whereismytransport.resthook.client;

import com.whereismytransport.resthook.client.auth.ClientCredentials;
import com.whereismytransport.resthook.client.auth.Token;
import com.whereismytransport.resthook.client.auth.TokenService;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import spark.Request;


import java.util.List;
import java.util.Random;
import java.util.UUID;

import static spark.Spark.post;

/**
 * Created by Nick Cuthbert on 25/04/2016.
 */
public class RestHook {
    private TokenService tokenService = TokenService.retrofit.create(TokenService.class);
    private CaptainHookApiService restHookService;
    private String clientUrl;
    public String serverUrl;
    public String serverRelativeUrl;
    public UUID index;

    public String secret;

    public RestHook(String serverUrl,
                    String serverRelativeUrl,
                    String clientUrl) {

        this.serverUrl = serverUrl;
        this.clientUrl=clientUrl;
        this.serverRelativeUrl = serverRelativeUrl;
        this.index=UUID.randomUUID();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        restHookService = retrofit.create(CaptainHookApiService.class);
    }

    public RestHook(String serverUrl,
                    String serverRelativeUrl,
                    String secret,
                    UUID index,
                    String clientUrl)
    {
        this.serverUrl=serverUrl;
        this.serverRelativeUrl=serverRelativeUrl;
        this.secret=secret;
        this.index=index;
        this.clientUrl=clientUrl;
    }

    public spark.Response handleHookMessage(Request req, spark.Response res, List<String> messages,List<String> logs) {
        if(req.headers().contains("X-Hook-Secret")){
            res.status(200);
            secret=req.headers("X-Hook-Secret");
            res.header("X-Hook-Secret",secret);
            return res;
        }
        else if (req.headers().contains("X-Hook-Signature")) {
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

    public boolean createHook(ClientCredentials clientCredentials,List<String> logs) {
        try {
                String relativeCallbackUrl = "hooks/" +index ;
                // Get token to connect to CaptainHook
                Call<Token> getTokenCall = tokenService.createToken(clientCredentials.getMap());

                Response<Token> tokenResponse = getTokenCall.execute();

                if (tokenResponse.isSuccessful()) {
                    Token token = tokenResponse.body();
                    Call createHookCall = restHookService.createRestHook(serverRelativeUrl,
                                                                         new RESTHookRequest(clientUrl + relativeCallbackUrl, "Test Webhook"),
                                                                         "Bearer " + token.access_token);

                    Response createHookCallResponse = createHookCall.execute();
                    if(createHookCallResponse.isSuccessful()) {
                        logs.add("Successfully created web hook");
                        return true;
                    }else {
                        logs.add("Something went wrong calling web hook setup. Response code: " + createHookCallResponse.code()+
                                ", Message"+createHookCallResponse.message()+", Body"+createHookCallResponse.errorBody()
                        );
                    }
                }else {
                    logs.add("Couldn't get token. Response Code:" +tokenResponse.code()+", Message: "+tokenResponse.message());
                }
            }catch (Exception e) {
                e.printStackTrace();
                logs.add(e.getStackTrace().toString());
            }
            return false;
        }
    }


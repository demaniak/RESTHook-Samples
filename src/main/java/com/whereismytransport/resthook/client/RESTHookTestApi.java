package com.whereismytransport.resthook.client;

import com.whereismytransport.resthook.client.auth.ClientCredentials;
import retrofit2.http.Url;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import static spark.Spark.*;

/**
 * This API allows us to easily view logs and webhook bodies received by this client and
 * allows users to create new RESTHooks. This would generally be done at startup. But this
 * sample was created for demonstrative and testing purposes.
 */
public class RESTHookTestApi {

    private List<String> receivedWebhookBodies = new ArrayList<>();
    private List<String> logs = new ArrayList<>();
    private List<String> messages= new ArrayList<>();
    private RestHookRepository restHookRepository;
    private List<RestHook> hooks;
    private ClientCredentials clientCredentials;

    private int port;
    private String baseUrl;

    public RESTHookTestApi(int port, String baseUrl, RestHookRepository restHookRepository,String clientId, String clientSecret){
        if(System.getenv().containsKey(""))
        this.restHookRepository=restHookRepository;
        restHookRepository.initialize(logs,receivedWebhookBodies);
        hooks=restHookRepository.getRestHooks();
        this.port=port;
        this.baseUrl=baseUrl;
        clientCredentials=new ClientCredentials(clientId,clientSecret);
    }

    public void start(){
        //Set the operating port
        port(port);

        // default get
        get("/", (req, res) -> "Test Webhook client api running.");

        // get logs
        get("/logs", (req, res) -> {
            res.status(200);
            return listToMultilineString(logs);
        });

        // get received webhook bodies
        get("/received_hooks", (req, res) -> {
            res.status(200);
            return listToMultilineString(receivedWebhookBodies);
        });

        //Instruct client to create a RESThook at the address given as a UTF-8 encoded string in the body.
        post("/start", (req,res) -> {
            try {
                URL target = new URL(req.body());
                String host=target.getProtocol()+"://"+target.getAuthority();
                String path=target.getFile();
                hooks.add(new RestHook(host, path, baseUrl, clientCredentials, logs, messages, restHookRepository));
                res.status(200);
            }catch(Exception e){
                res.status(500);
            }
            return res;
        });
    }

    private static String listToMultilineString(List<String> list){
        String result="";
        for (String item: list) {
            result+=item+"\n";
        }
        return result;
    }
}

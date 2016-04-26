package com.whereismytransport.resthook.client;

import com.whereismytransport.resthook.client.auth.ClientCredentials;
import okio.Okio;
import retrofit2.http.Url;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private Map<UUID,RestHook> hooks;
    private ClientCredentials clientCredentials;

    private int port;
    private String baseUrl;

    public RESTHookTestApi(int port, String baseUrl, RestHookRepository restHookRepository,String clientId, String clientSecret){

        this.restHookRepository=restHookRepository;
        List<RestHook> restHooks = restHookRepository.getRestHooks();
        hooks= IntStream.range(0,restHooks.size()).boxed().collect(Collectors.toMap(i->restHooks.get(i).index,i->restHooks.get(i)));

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

        post("/hooks/:id", (req, res) -> {
            UUID id=UUID.fromString(req.params(":id"));
            return hooks.get(id).handleHookMessage(req,res,messages,logs);
        });

        //Instruct client to create a RESThook at the address given as a UTF-8 encoded string in the body.
        post("/start", (req,res) -> {
            try {
                URL target = new URL(req.body());
                String host=target.getProtocol()+"://"+target.getAuthority()+"/";
                String path=target.getFile().substring(1);
                RestHook hook=null;
                for (UUID hookKey:hooks.keySet()) {
                    RestHook restHook = hooks.get(hookKey);
                    if(restHook.serverUrl.equals(host) && restHook.serverRelativeUrl.equals(path)){
                        hook=restHook;
                    }
                }

                if(hook==null){
                    hook= new RestHook(host,path,baseUrl);
                    hooks.put(hook.index, hook);
                }

                if(hook.createHook(clientCredentials,logs)){
                    restHookRepository.addOrReplaceRestHook(hook);
                    res.status(200);
                    res.body("Webhook Created with path");
                }else{
                    res.status(500);
                    res.body("Couldn't create webhook");
                }
            }
            catch(Exception e){
                res.status(500);
                res.body("Couldn't create WebHook");
            }
            return res.body();
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

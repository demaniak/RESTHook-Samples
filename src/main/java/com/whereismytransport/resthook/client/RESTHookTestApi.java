package com.whereismytransport.resthook.client;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.port;

/**
 * This API allows us to easily view logs and webhook bodies received by this client and
 * allows users to create new RESTHooks. This would generally be done at startup. But this
 * sample was created for demonstrative and testing purposes.
 */

public class RESTHookTestApi {

    private List<String> alerts = new ArrayList<>();
    private List<String> logs = new ArrayList<>();
    private RestHookRepository restHookRepository;
    private int port;
    private String baseUrl;
    public RESTHookTestApi(int port, String baseUrl, RestHookRepository restHookRepository){
        this.restHookRepository=restHookRepository;
        this.port=port;
        this.baseUrl=baseUrl;
    }

    public void start(){
        //Set the operating port
        port(port);

        // default get
        get("/", (req, res) -> "Test Webhook client api running.");

        // get logs
        get("/logs", (req, res) -> {
            String response="";
            for (String log: logs) {
                response+=log+"\n";
            }
            res.body(response);
            res.status(200);
            return res;
        });

        // get received webhook bodies
        get("/received_hooks", (req, res) -> {
            String response="";
            for (String alert: alerts) {
                response+=alert+"\n";
            }
            res.body(response);
            res.status(200);
            return res;
        });

        //This method is to tell this client to create a RESThook at the given address. Used for integration testing
        post("/start", (req,res) -> {
            try {
                String target = URLDecoder.decode(req.queryMap("target").value(), "UTF-8");

                res.status(200);
            }catch(Exception e){
                res.status(500);
            }
            return res;
        });
    }
}

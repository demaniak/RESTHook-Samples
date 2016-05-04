import com.whereismytransport.resthook.client.RESTHookTestApi;
import com.whereismytransport.resthook.client.RestHookRepository;
import com.whereismytransport.resthook.client.azure.AzureRestHookRepository;
import spark.servlet.SparkApplication;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Startup implements SparkApplication{

    private static RestHookRepository repository;
    private static RESTHookTestApi restHookTestApi;
    private static ArrayList<String> logs= new ArrayList<>();
    private static ArrayList<String> messages=new ArrayList<> ();
    public static void main(String [] args){
<<<<<<< HEAD


        System.out.println("Startup class loaded.");
        logs.add("Startup class loaded.");

        // default get
        get("/", (req, res) -> "Test Webhook client api running.");

        // get logs
        get("/logs", (req, res) -> logs.toString());

        // get alerts
        get("/alerts", (req, res) -> alerts.toString());

        // This is the endpoint that the webhook will connect to
        post("/hooks/alerts", (req, res) -> {
            if(req.headers().contains("X-Hook-Secret")) {
                // store secret
                Secret = DatatypeConverter.parseBase64Binary(req.headers("X-Hook-Secret"));
                res.header("X-Hook-Secret", req.headers("X-Hook-Secret"));
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
                System.out.println("Message is:\n"+req.body());

                alerts.add(bodyHash);
                logs.add("Alert received.");
            } else{
                res.status(403);
                logs.add("Access denied: X-Hook-Signature or X-Hook-Secret does not present in headers.");
            }
            return res;
        });

        // this could be in a post create webhook call
        createAlertWebHook();
=======
        repository=new AzureRestHookRepository(RoleEnvironment.azureStorageConnectionString,RoleEnvironment.url,logs);
        repository.initialize(logs,messages);
        restHookTestApi = new RESTHookTestApi(RoleEnvironment.port, RoleEnvironment.url,repository, RoleEnvironment.clientId,RoleEnvironment.clientSecret,logs,messages);
        restHookTestApi.start();
>>>>>>> 72f95e11aa458af541807d755ec91545a11c16d8
    }



    // Method automatically called by Java Host (e.g. Jetty or Tomcat)
    @Override
    public void init() {

        int port=RoleEnvironment.port;

        if(System.getenv().containsKey("HTTP_PLATFORM_PORT")){
            port = Integer.parseInt(System.getenv("HTTP_PLATFORM_PORT"));
        }else{
            port = RoleEnvironment.port;
        }
        String url=RoleEnvironment.url;
        
        repository=new AzureRestHookRepository(RoleEnvironment.azureStorageConnectionString,url,logs);
        repository.initialize(logs,messages);

        restHookTestApi = new RESTHookTestApi(port, url,repository,RoleEnvironment.clientId,RoleEnvironment.clientSecret,logs,messages);
        restHookTestApi.start();
    }

}

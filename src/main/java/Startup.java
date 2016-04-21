import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.apache.commons.codec.binary.Hex;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import static spark.Spark.*;

public class Startup {

    // Create retrofit rest client for CaptainHook
    public static CaptainHookApiService service = CaptainHookApiService.retrofit.create(CaptainHookApiService.class);
    public static byte[] Secret ={};
    static ArrayList<String> alerts = new ArrayList();
    static ArrayList<String> logs = new ArrayList();

    public static void main(String [] args){
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
                Secret = Base64.decode(req.headers("X-Hook-Secret"));
                res.header("X-Hook-Secret", req.headers("X-Hook-Secret"));
                res.status(200);

                logs.add("X-Hook-Secret received");
            } else if(req.headers().contains("X-Hook-Signature")) {
                String bodyHash = "";
                try {
                    bodyHash = encode(Secret, res.body());
                }
                catch (Exception e) {
                    logs.add("Exception occurred encoding hash: " + e.getStackTrace().toString());
                    res.status(500);
                    return res;
                }
                if(bodyHash.equals(req.headers("X-Hook-Signature"))){
                    res.status(200);
                }else{
                    res.status(403);
                }
                System.out.println(bodyHash);
                System.out.println("Message is:\n"+res.body());

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
    }

    private static void createAlertWebHook() {
        // Tell CaptainHook that we want to subscribe to Alerts
        Call createHookCall =  service.createHook(new Hook("https://testapi.com/hooks/alerts", "Alerts webhook"));
        createHookCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()) {
                    System.out.println("Created Hook Successfully");
                    logs.add("Created Hook Successfully");
                }
                else {
                    System.out.println("Something went wrong calling webhook setup. Response code: " + response.code());
                    logs.add("Something went wrong calling webhook setup. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                System.out.println("Error occurred in calling Captain Hook.");
                logs.add("Error occurred in calling Captain Hook.");
            }
        });
    }

    private static String encode(byte[] key, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key, "HmacSHA256");
        sha256_HMAC.init(secret_key);

        return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
    }
}

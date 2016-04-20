import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.apache.commons.codec.binary.Hex;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static spark.Spark.*;

public class Startup {
    public static byte[] Secret ={};

    public static void main(String [] args){
        System.out.println("Startup class loaded.");

        get("/", (req, res) -> "Hello, World");

        // Create retrofit rest client for CaptainHook
        CaptainHookApiService service =CaptainHookApiService.retrofit.create(CaptainHookApiService.class);

        // Tell CaptainHook that we want to subscribe to Alerts
        Call createHookCall =  service.createHook(new Hook("https://testapi.com/hooks/alerts", "Alerts webhook"));
        createHookCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()) {
                    System.out.println("Created Hook Successfully");
                }
                else {
                    System.out.println("Something went wrong. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                System.out.println("Error occurred in calling Captain Hook.");
            }
        });


        // This is the endpoint that the webhook will connect to
        post("/hooks/alerts", (req, res) -> {
            if(req.headers().contains("X-Hook-Secret")) {
                // store secret
                Secret = Base64.decode(req.headers("X-Hook-Secret"));
                res.header("X-Hook-Secret", req.headers("X-Hook-Secret"));
                res.status(200);
            }
            else if(req.headers().contains("X-Hook-Signature")) {
                String bodyHash = encode(Secret, res.body());
                if(bodyHash.equals(req.headers("X-Hook-Signature"))){
                    res.status(200);
                }else{
                    res.status(403);
                }
                System.out.println(bodyHash);
                System.out.println("Message is:\n"+res.body());
            }else{
                res.status(403);
            }
            return res;
        });
    }

    private static String encode(byte[] key, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key, "HmacSHA256");
        sha256_HMAC.init(secret_key);

        return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
    }
}

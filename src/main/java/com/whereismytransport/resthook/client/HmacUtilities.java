package com.whereismytransport.resthook.client;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;

/**
 * Created by Nick Cuthbert on 25/04/2016.
 */
public class HmacUtilities {

    /**
     * @param key the secret which was supplied by the server upon the intial RESTHook handshake
     * @param data a UTF-8 => base64 string encoded representation of the request body
     * @return
     * @throws Exception
     */
    private static String encode(byte[] key, byte[] data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key, sha256_HMAC.getAlgorithm());
        sha256_HMAC.init(secret_key);
        return DatatypeConverter.printBase64Binary(sha256_HMAC.doFinal(data));
    }

    public static boolean validBody(RestHook hook, String body,String xHookSignature){
        try {
            byte[] bodyBytes = body.getBytes("UTF-8");
            String encodedHash=encode(DatatypeConverter.parseBase64Binary(hook.secret), bodyBytes);
            return encodedHash.equals(xHookSignature);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

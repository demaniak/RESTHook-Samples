package com.whereismytransport.resthook.client;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ncthbrt on 2016/05/13.
 */
public class JsonDeserializer {

    private static final Gson GSON = new Gson();

    public static <T> T convert(String jsonBody, Class<T> type, List<String> logs) {    
        try {
            return GSON.fromJson(jsonBody, type);
        }
        catch (Exception e) {
            logs.add(e.getMessage());
            for (StackTraceElement el: e.getStackTrace()) {
                logs.add(el.toString());
            }
            return null;
        }
    }


}

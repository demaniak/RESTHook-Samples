package com.whereismytransport.resthook.client;

import retrofit2.Call;
import retrofit2.http.*;


/** This class represents some of the available webhooks one may make
 *
 */
public interface CaptainHookApiService {

    /**
     * Note that only users with the scope webhook:global may create global scopes
     */
    @POST("{{path}}")
    Call createRestHook(@Path("path") String creationPath, @Body RESTHookRequest hook, @Header("Authorization") String authorization);
}

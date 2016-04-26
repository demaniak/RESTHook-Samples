package com.whereismytransport.resthook.client;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;


/** This class represents some of the available webhooks one may make
 *
 */
public interface CaptainHookApiService {

    /**
     * Note that only users with the scope webhook:global may create global scopes
     */
    @POST
    Call<ResponseBody> createRestHook(@Url String creationPath, @Body RESTHookRequest hook, @Header("Authorization") String authorization);
}

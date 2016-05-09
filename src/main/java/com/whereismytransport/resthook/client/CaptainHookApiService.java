package com.whereismytransport.resthook.client;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;


/** This class represents some of the available webhooks one may make
 *
 */
public interface CaptainHookApiService {

    /**
     * Note that only users with the scope webhook:global may create global scopes
     */
    @POST
    Call<ResponseBody> createRestHook(@Url String creationPath, @Body RESTHookRequest hook, @Header("Authorization") String authorization);
    
    public static final Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://identity.whereismytransport.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();
}

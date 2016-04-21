import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CaptainHookApiService {
    @POST("hooks/create")
    Call<Hook> createHook(@Body Hook hook);

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://webhooks.whereismytransport.com:443/api/v1/subscriptions/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}

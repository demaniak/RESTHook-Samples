import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface CaptainHookApiService {
    @POST("alerts")
    Call<Hook> createHook(@Body Hook hook, @Header("Authorization") String authorization);

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://localhost:444/api/v1/subscriptions/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import java.util.Map;

/**
 * Created by Henk on 2016/04/21.
 */

public interface TokenService {
    @FormUrlEncoded
    @POST("connect/token")
    Call<Token> createToken(@FieldMap Map<String,String> clientCredentials);

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://identity.whereismytransport.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}

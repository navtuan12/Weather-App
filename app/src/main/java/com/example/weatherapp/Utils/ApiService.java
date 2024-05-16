package com.example.weatherapp.Utils;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ApiService {

    //https://api.openai.com/
    @Headers({ "Content-Type: application/json"})
    @POST("v1/chat/completions")
    Call<ResponseBody> getRecommendation(@Header("Authorization") String token,
                                       @Body RequestBody jsonQuery);

    //http://api.weatherapi.com/
    @GET("v1/forecast.json")
    Call<ResponseBody> getWeatherData(@Query("key") String key,
                                      @Query("q") String q);
}

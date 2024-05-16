package com.example.weatherapp.Fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ekn.gruzer.gaugelibrary.ArcGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.example.weatherapp.Model.Forecast;
import com.example.weatherapp.Model.Weather;
import com.example.weatherapp.R;
import com.example.weatherapp.Utils.ApiService;
import com.example.weatherapp.Utils.JsonHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nitish.typewriterview.TypeWriterView;
import com.squareup.picasso.Picasso;
import com.tencent.mmkv.MMKV;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherDashboardFragment extends Fragment implements View.OnClickListener{
    private final String token = "REPLACE YOUR OPENAI API";
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private TextView tv_location, tv_temp, tv_condition, tv_maxtemp,
                tv_mintemp, tv_uv, tv_uv_reas, tv_sunset, tv_sunrise,
                tv_wind_kph, tv_gust_kph, tv_feelslike, tv_feelslike_reas,
                tv_precip, tv_vis, tv_humidity, tv_dewpoint,
                tv_illumination, tv_moonrise, tv_moonset, tv_averages,
                tv_today_temp, tv_averages_temp, tv_moon_title;

    private List<ImageView> ivList;
    private List<TextView> tvList;
    private ImageView iv_compass_arrow, iv_moon_phase;
    private ArcGauge gauge_pressure;
    private Weather weather;
    private MMKV kv = null;
    private JsonHandler jsonHandler;
    private FloatingActionButton weatherListButton;
    private ScrollView br_layout;
    private TypeWriterView typeWriterView;

    private Retrofit retrofit;
    private ApiService apiService;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            ivList = new ArrayList<>();
            tvList = new ArrayList<>();
            kv = MMKV.defaultMMKV();
            jsonHandler = new JsonHandler();
            String msg = kv.decodeString("weatherJson");
            weather = jsonHandler.getWeather(msg);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_dashboard, container, false);
        tv_location = view.findViewById(R.id.tv_location);
        tv_temp = view.findViewById(R.id.tv_temp);
        tv_condition = view.findViewById(R.id.tv_condition);
        tv_maxtemp = view.findViewById(R.id.tv_max);
        tv_mintemp = view.findViewById(R.id.tv_min);
        tv_uv = view.findViewById(R.id.tv_uv);
        tv_uv_reas = view.findViewById(R.id.tv_uv_reas);
        tv_sunset = view.findViewById(R.id.tv_sunset);
        tv_sunrise = view.findViewById(R.id.tv_sunrise);
        tv_wind_kph = view.findViewById(R.id.tv_windspeed);
        tv_gust_kph = view.findViewById(R.id.tv_gust);
        iv_compass_arrow = view.findViewById(R.id.compass_inside);
        tv_feelslike = view.findViewById(R.id.tv_feelslike);
        tv_feelslike_reas = view.findViewById(R.id.tv_feelslike_reas);
        tv_precip = view.findViewById(R.id.tv_precip_mm);
        tv_vis = view.findViewById(R.id.tv_vis_km);
        tv_humidity = view.findViewById(R.id.tv_humidity);
        tv_dewpoint = view.findViewById(R.id.tv_dewpoint);
        tv_illumination = view.findViewById(R.id.tv_illumination);
        tv_moonrise = view.findViewById(R.id.tv_moonrise);
        tv_moonset = view.findViewById(R.id.tv_moonset);
        tv_averages = view.findViewById(R.id.tv_averages);
        tv_today_temp = view.findViewById(R.id.tv_today_temp);
        tv_averages_temp = view.findViewById(R.id.tv_average_temp);
        tv_moon_title = view.findViewById(R.id.tv_moon_phase);
        iv_moon_phase = view.findViewById(R.id.iv_moonphase);
        gauge_pressure = view.findViewById(R.id.gauge_pressure);

        for(int i = 0; i < weather.getForecast().getConditionImage().size(); i++){
            int ivId = getResources().getIdentifier("iv_"+i, "id", getActivity().getPackageName());
            ImageView iv = view.findViewById(ivId);
            ivList.add(iv);

            int tvId = getResources().getIdentifier("tv_temp_"+i, "id", getActivity().getPackageName());
            TextView tv = view.findViewById(tvId);
            tvList.add(tv);
        }

        weatherListButton = view.findViewById(R.id.ListWeatherButton);
        weatherListButton.setOnClickListener(this);

        br_layout = view.findViewById(R.id.sr_br_layout);
        typeWriterView = view.findViewById(R.id.typeWriterView);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openai.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
        ExecutorService networkExecutor = Executors.newSingleThreadExecutor();
        networkExecutor.execute(() -> {
            String promt = jsonHandler.getPromt(weather.getLocation());
            RequestBody jsonQuery = RequestBody.create(promt, JSON);
            Call<ResponseBody> recommendCall = apiService.getRecommendation("Bearer "+token, jsonQuery);
            try {
                Response<ResponseBody> recommendResponse = recommendCall.execute();
                String recommend = jsonHandler.getRecommend(recommendResponse.body().string());
                typeWriterView.setCharacterDelay(80);
                typeWriterView.animateText(recommend);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        tv_location.setText(weather.getLocation());
        tv_temp.setText(weather.getTemp() + "°");
        tv_condition.setText(weather.getCondition());
        tv_maxtemp.setText("H:" + weather.getMaxTemp());
        tv_mintemp.setText("L:" + weather.getMinTemp());
        tv_uv.setText(weather.getUv());
        tv_uv_reas.setText(uvCheck(weather.getUv()));
        tv_sunset.setText(weather.getForecast().getSunset());
        tv_sunrise.setText("Sun rise: " + weather.getForecast().getSunrise());
        tv_wind_kph.setText(weather.getWindSpeed());
        tv_gust_kph.setText(weather.getGust());

        float rotationDegree = (float) (Float.parseFloat(weather.getWind_degree()) + 1.2);
        iv_compass_arrow.setRotation(rotationDegree);
        tv_feelslike.setText(weather.getFeelslike() + "°");
        tv_feelslike_reas.setText(feelslikeCheck(weather.getFeelslike(), weather.getTemp()));
        tv_precip.setText(weather.getPrecip_mm() + " mm");
        tv_vis.setText(weather.getVis_km() + " km");
        tv_humidity.setText(weather.getHumidity() + "%");

        String dewpoint = getDewpoint(weather.getHumidity(), weather.getTemp());
        tv_dewpoint.setText(String.format("The dew point is %1$s° right now.", dewpoint));
        tv_illumination.setText(weather.getForecast().getIllumination() + "%");
        tv_moonrise.setText(weather.getForecast().getMoonrise());
        tv_moonset.setText(weather.getForecast().getMoonset());
        tv_averages.setText(getAverages(weather.getMaxTemp(),weather.getForecast().getAverage_temp()));
        tv_today_temp.setText(weather.getMaxTemp() + "°");
        tv_averages_temp.setText(weather.getForecast().getAverage_temp() + "°");
        tv_moon_title.setText(weather.getForecast().getMoon_phase());

        Double pressure = Double.parseDouble(weather.getPressure_mb());
        Range range = new Range();
        range.setColor(Color.parseColor("#ce0000"));
        range.setFrom(870);
        range.setTo(1084);
        gauge_pressure.setMinValue(870);
        gauge_pressure.setMaxValue(1084);
        gauge_pressure.setValue(pressure);
        gauge_pressure.addRange(range);

        for(int i=0; i<ivList.size(); i++){
            Log.d("conditionimage", weather.getForecast().getConditionImage().get(i));
            Picasso.get().load("http:"+weather.getForecast().getConditionImage().get(i)).into(ivList.get(i));
            tvList.get(i).setText(weather.getForecast().getForecast_temp().get(i) + "°");
        }

        int ivId = getResources().getIdentifier(weather.getBackground(), "drawable", getContext().getPackageName());
        br_layout.setBackground(getContext().getDrawable(ivId));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ListWeatherButton){
            Fragment fragment = new HomeFragment();
            FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
            fm.replace(R.id.fragment_nav_container, fragment).commit();
        }
    }

    private String uvCheck(String uv){
        Double uv_index = Double.parseDouble(uv);
        if(uv_index < 3) return "Low";
        else if(uv_index < 6) return "Moderate";
        else if(uv_index < 8) return "High";
        else if(uv_index < 11) return "Very High";
        else return "Extreme";
    }
    private String feelslikeCheck(String feelslike, String temp){
        Double f = Double.parseDouble(feelslike);
        Double t = Double.parseDouble(temp);
        if(t > f) return "Wind is making it feel cooler.";
        else if (f > t) return "Humidity is making it feel hotter.";
        return "Similar to the actual temperature.";
    }

    private String getDewpoint(String humidity, String temp){
        int h = Integer.parseInt(humidity);
        int t = Integer.parseInt(temp);
        return Integer.toString(t - (100 - h)/5);
    }

    private String getAverages(String temp, String average){
        int t = Integer.parseInt(temp);
        int a = Integer.parseInt(average);
        return Integer.toString(t - a);
    }
}
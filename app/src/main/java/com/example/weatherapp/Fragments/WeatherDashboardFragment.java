package com.example.weatherapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ekn.gruzer.gaugelibrary.ArcGauge;
import com.example.weatherapp.Model.Forecast;
import com.example.weatherapp.Model.Weather;
import com.example.weatherapp.R;
import com.example.weatherapp.Utils.JsonHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.tencent.mmkv.MMKV;

public class WeatherDashboardFragment extends Fragment {

    private TextView tv_location, tv_temp, tv_condition, tv_maxtemp,
                tv_mintemp, tv_uv, tv_uv_reas, tv_sunset, tv_sunrise,
                tv_wind_kph, tv_gust_kph, tv_feelslike, tv_feelslike_reas,
                tv_precip, tv_vis, tv_humidity, tv_dewpoint,
                tv_illumination, tv_moonrise, tv_moonset, tv_averages,
                tv_today_temp, tv_averages_temp, tv_moon_title;

    private ImageView iv_compass_arrow, iv_moon_phase;
    private ArcGauge gauge_pressure;
    private Weather weather;
    private MMKV kv;
    private JsonHandler jsonHandler;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            kv = MMKV.defaultMMKV();
            jsonHandler = new JsonHandler();
            String msg = kv.decodeString("weatherJson");
            weather = jsonHandler.getWeather(msg);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
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
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_location.setText(weather.getLocation());
        tv_temp.setText(weather.getTemp() + "°");
        tv_condition.setText(weather.getCondition());
        tv_maxtemp.setText("H:" + weather.getMaxTemp());
        tv_mintemp.setText("L:" + weather.getMinTemp());
        tv_uv.setText(weather.getUv());
        tv_uv_reas.setText(uvCheck(weather.getUv()));
        tv_sunset.setText(weather.getForecast().getSunset());
        tv_sunrise.setText(weather.getForecast().getSunrise());
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
        tv_dewpoint.setText(String.format("The dew point is $s right now.", dewpoint));
        tv_illumination.setText(weather.getForecast().getIllumination() + "%");
        tv_moonrise.setText(weather.getForecast().getMoonrise());
        tv_moonset.setText(weather.getForecast().getMoonset());

    }

    private String uvCheck(String uv){
        int uv_index = Integer.parseInt(uv);
        if(uv_index < 3) return "Low";
        else if(uv_index < 6) return "Moderate";
        else if(uv_index < 8) return "High";
        else if(uv_index < 11) return "Very High";
        else return "Extreme";
    }

    private String feelslikeCheck(String feelslike, String temp){
        int f = Integer.parseInt(feelslike);
        int t = Integer.parseInt(temp);
        if(t > f) return "Wind is making it feel cooler.";
        else if (f > t) return "Humidity is making it feel hotter.";
        return "Similar to the actual temperature.";
    }

    private String getDewpoint(String humidity, String temp){
        Double h = Double.parseDouble(humidity);
        Double t = Double.parseDouble(temp);
        return Double.toString(t - (100 - h)/5);
    }
}
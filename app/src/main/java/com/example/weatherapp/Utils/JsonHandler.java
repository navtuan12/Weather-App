package com.example.weatherapp.Utils;

import com.example.weatherapp.Model.Forecast;
import com.example.weatherapp.Model.Weather;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonHandler {
    ObjectMapper objectMapper = new ObjectMapper();
    public Forecast getForeCast(JsonNode weatherJson){
        String sunrise = weatherJson.get("forecast").get("forecastday").get(0).get("astro").get("sunrise").asText();
        String sunset = weatherJson.get("forecast").get("forecastday").get(0).get("astro").get("sunset").asText();
        String moonrise = weatherJson.get("forecast").get("forecastday").get(0).get("astro").get("moonrise").asText();
        String moonset = weatherJson.get("forecast").get("forecastday").get(0).get("astro").get("moonset").asText();
        String illumination = weatherJson.get("forecast").get("forecastday").get(0).get("astro").get("moon_illumination").asText();
        String avgtemp = weatherJson.get("forecast").get("forecastday").get(0).get("day").get("avgtemp_c").asText();
        String moonphase = weatherJson.get("forecast").get("forecastday").get(0).get("astro").get("moon_phase").asText();
        List<String> conditionImage = new ArrayList<>();
        List<String> forecast_temp = new ArrayList<>();
        List<String> forecast_humidity = new ArrayList<>();
        List<String> forecast_uv = new ArrayList<>();
        List<String> forecast_vis = new ArrayList<>();
        List<String> forecast_wind = new ArrayList<>();
        List<String> forecast_feelslike = new ArrayList<>();
        List<String> forecast_pressure = new ArrayList<>();
        List<String> forecast_precip = new ArrayList<>();

        for(int i = 0; i < 24; i++){
            conditionImage.add(weatherJson.get("forecast").get("forecastday").get(0).get("hour").get(i).get("condition").get("icon").asText());
            forecast_temp.add(weatherJson.get("forecast").get("forecastday").get(0).get("hour").get(i).get("temp_c").asText());
            forecast_humidity.add(weatherJson.get("forecast").get("forecastday").get(0).get("hour").get(i).get("humidity").asText());
            forecast_uv.add(weatherJson.get("forecast").get("forecastday").get(0).get("hour").get(i).get("uv").asText());
            forecast_vis.add(weatherJson.get("forecast").get("forecastday").get(0).get("hour").get(i).get("vis_km").asText());
            forecast_wind.add(weatherJson.get("forecast").get("forecastday").get(0).get("hour").get(i).get("wind_kph").asText());
            forecast_feelslike.add(weatherJson.get("forecast").get("forecastday").get(0).get("hour").get(i).get("feelslike_c").asText());
            forecast_pressure.add(weatherJson.get("forecast").get("forecastday").get(0).get("hour").get(i).get("pressure_mb").asText());
            forecast_precip.add(weatherJson.get("forecast").get("forecastday").get(0).get("hour").get(i).get("precip_mm").asText());
        }
        return new Forecast(sunrise,sunset,moonrise,moonset,
                illumination,avgtemp,moonphase,conditionImage,
                forecast_temp,forecast_humidity,forecast_uv,forecast_vis,
                forecast_wind,forecast_feelslike,forecast_pressure,forecast_precip);
    }

    public Weather getWeather(String msg) throws JsonProcessingException {
        JsonNode weatherJson = objectMapper.readTree(msg);
        Forecast forecast = getForeCast(weatherJson);
        return new Weather(
                weatherJson.get("location").get("name").asText(),
                Integer.toString(weatherJson.get("current").get("temp_c").asInt()),
                weatherJson.get("current").get("feelslike_c").asText(),
                Integer.toString(weatherJson.get("current").get("precip_mm").asInt() * 10),
                weatherJson.get("current").get("vis_km").asText(),
                weatherJson.get("current").get("humidity").asText(),
                weatherJson.get("current").get("pressure_mb").asText(),
                Double.toString(weatherJson.get("current").get("wind_degree").asInt() + 1.2),
                weatherJson.get("current").get("wind_kph").asText(),
                weatherJson.get("current").get("gust_kph").asText(),
                weatherJson.get("current").get("condition").get("text").asText(),
                weatherJson.get("current").get("condition").get("icon").asText(),
                Integer.toString( weatherJson.get("forecast").get("forecastday").get(0).get("day").get("maxtemp_c").asInt()),
                Integer.toString(weatherJson.get("forecast").get("forecastday").get(0).get("day").get("mintemp_c").asInt()),
                weatherJson.get("current").get("uv").asText(),
                forecast
        );
    }
}

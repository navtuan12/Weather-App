package com.example.weatherapp.Utils;

import android.graphics.Path;
import android.util.Log;

import com.example.weatherapp.Model.Forecast;
import com.example.weatherapp.Model.Weather;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JsonHandler {
    ObjectMapper objectMapper = new ObjectMapper();

    public String formatDate(String text) throws ParseException {
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat df = new SimpleDateFormat("hh:mm a");

        Date date = df.parse(text);
        return displayFormat.format(date);
    }
    public Forecast getForeCast(JsonNode weatherJson) throws ParseException {

        String sunrise = weatherJson.get("forecast").get("forecastday").get(0).get("astro").get("sunrise").asText();
        String sunset = weatherJson.get("forecast").get("forecastday").get(0).get("astro").get("sunset").asText();
        String moonrise = weatherJson.get("forecast").get("forecastday").get(0).get("astro").get("moonrise").asText();
        String moonset = weatherJson.get("forecast").get("forecastday").get(0).get("astro").get("moonset").asText();

//        sunrise = formatDate(sunrise);
//        sunset = formatDate(sunset);
//        moonrise = formatDate(moonrise);
//        moonset = formatDate(moonset);

        String illumination = weatherJson.get("forecast").get("forecastday").get(0).get("astro").get("moon_illumination").asText();
        String avgtemp = Integer.toString(weatherJson.get("forecast").get("forecastday").get(0).get("day").get("avgtemp_c").asInt());
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
            forecast_temp.add(Integer.toString(weatherJson.get("forecast").get("forecastday").get(0).get("hour").get(i).get("temp_c").asInt()));
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

    public Weather getWeather(String msg) throws JsonProcessingException, ParseException {
        JsonNode weatherJson = objectMapper.readTree(msg);
        Forecast forecast = getForeCast(weatherJson);
        String currentTime = weatherJson.get("location").get("localtime").asText();
        currentTime = currentTime.substring(11, 13);
        if(currentTime.contains(":")){
            currentTime=currentTime.substring(0,1);
        }
        return new Weather(
                setBackground(currentTime, forecast),
                currentTime,
                weatherJson.get("location").get("name").asText(),
                Integer.toString(weatherJson.get("current").get("temp_c").asInt()),
                Integer.toString(weatherJson.get("current").get("feelslike_c").asInt()),
                Integer.toString(weatherJson.get("current").get("precip_mm").asInt() * 10),
                Integer.toString(weatherJson.get("current").get("vis_km").asInt()),
                weatherJson.get("current").get("humidity").asText(),
                weatherJson.get("current").get("pressure_mb").asText(),
                Double.toString(weatherJson.get("current").get("wind_degree").asInt() + 1.2),
                Integer.toString(weatherJson.get("current").get("wind_kph").asInt()),
                Integer.toString(weatherJson.get("current").get("gust_kph").asInt()),
                weatherJson.get("current").get("condition").get("text").asText(),
                weatherJson.get("current").get("condition").get("icon").asText(),
                Integer.toString( weatherJson.get("forecast").get("forecastday").get(0).get("day").get("maxtemp_c").asInt()),
                Integer.toString(weatherJson.get("forecast").get("forecastday").get(0).get("day").get("mintemp_c").asInt()),
                Integer.toString(weatherJson.get("current").get("uv").asInt()),
                forecast
        );
    }

    public String setBackground(String currentTime, Forecast forecast){
        String sunset = forecast.getSunset();
        int isunset = Integer.parseInt(sunset.substring(0,2));
<<<<<<< HEAD
=======
        Log.d("sunset",Integer.toString(isunset));
>>>>>>> 245aa493aca573f924d87cb0819aed9f7d4ae3d0
        int curr = Integer.parseInt(currentTime);
        if(curr < isunset) return "sunny_br";
        else if(curr > isunset) return "night_br";
        else return "sunset_br";
    }

    public String getPromt(String location){
        return String.format(
                "{\n" +
                        "  \"model\": \"gpt-3.5-turbo\",\n" +
                        "  \"messages\": [\n" +
                        "    {\n" +
                        "      \"role\": \"user\",\n" +
                        "      \"content\": \"Recommend what should do with the weather conditions at %1$s at the moment. Response max is 50 words and in JSON. Example { 'recommendation': }, replace single quote in example to double quote.\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}", location
        );
    }

    public String getRecommend(String msg) throws JsonProcessingException {
        JsonNode OpenAiJson = objectMapper.readTree(msg);
        String recommendMsg = OpenAiJson.get("choices").get(0).get("message").get("content").asText();
        JsonNode recommendJson = objectMapper.readTree(recommendMsg);
        return recommendJson.get("recommendation").asText();
    }
}

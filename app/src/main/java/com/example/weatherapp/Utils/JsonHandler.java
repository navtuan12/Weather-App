package com.example.weatherapp.Utils;

import com.example.weatherapp.Model.Weather;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonHandler {
    ObjectMapper objectMapper = new ObjectMapper();
    public Weather getWeather(String msg) throws JsonProcessingException {
        JsonNode weatherJson = objectMapper.readTree(msg);
        return new Weather(
                weatherJson.get("location").get("name").asText(),
                Integer.toString(weatherJson.get("current").get("temp_c").asInt()),
                weatherJson.get("current").get("humidity").asText(),
                weatherJson.get("current").get("wind_kph").asText(),
                weatherJson.get("current").get("condition").get("text").asText(),
                weatherJson.get("current").get("condition").get("icon").asText(),
                Integer.toString( weatherJson.get("forecast").get("forecastday").get(0).get("day").get("maxtemp_c").asInt()),
                Integer.toString(weatherJson.get("forecast").get("forecastday").get(0).get("day").get("mintemp_c").asInt())
        );
    }
}

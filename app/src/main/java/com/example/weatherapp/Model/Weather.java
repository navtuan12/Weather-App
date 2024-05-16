package com.example.weatherapp.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Weather {
    String key;
    String location;
    String temp;
    String feelslike;
    String precip_mm;
    String vis_km;
    String humidity;
    String dew_point;
    String pressure_mb;
    String wind_degree;
    String windSpeed;
    String gust;
    String condition;
    String imageUrl;
    String maxTemp;
    String minTemp;
    String uv;
    Forecast forecast;
    String background;
    String currentTime;

    public Weather(String background, String currentTime, String location, String temp, String feelslike, String precip_mm, String vis_km, String humidity, String pressure_mb,
                   String wind_degree, String windSpeed, String gust, String condition, String imageUrl, String maxTemp, String minTemp, String uv, Forecast forecast) {
        this.background = background;
        this.currentTime = currentTime;
        this.location = location;
        this.temp = temp;
        this.feelslike = feelslike;
        this.precip_mm = precip_mm;
        this.vis_km = vis_km;
        this.humidity = humidity;
        int a = Integer.parseInt(temp);
        int b = Integer.parseInt(humidity);
        this.dew_point = Integer.toString(a - (100 - b)/5);
        this.pressure_mb = pressure_mb;
        this.wind_degree = wind_degree;
        this.windSpeed = windSpeed;
        this.gust = gust;
        this.condition = condition;
        this.imageUrl = imageUrl;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.uv = uv;
        this.forecast = forecast;
    }
}

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
    String humidity;
    String windSpeed;
    String condition;
    String imageUrl;
    String maxTemp;
    String minTemp;

    public Weather(String location, String temp, String humidity, String windSpeed, String condition, String imageUrl, String maxTemp, String minTemp) {
        this.location = location;
        this.temp = temp;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.condition = condition;
        this.imageUrl = imageUrl;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
    }
}

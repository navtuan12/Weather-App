package com.example.weatherapp.Model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Forecast {
    String sunrise, sunset, moonrise, moonset, illumination, average_temp, moon_phase;
    List<String> conditionImage;
    List<String> forecast_temp, forecast_humidity, forecast_uv, forecast_vis, forecast_wind, forecast_feelslike, forecast_pressure, forecast_precip;
}

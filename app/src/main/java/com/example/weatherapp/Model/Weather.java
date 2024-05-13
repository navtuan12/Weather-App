package com.example.weatherapp.Model;


public class Weather {
    String key;
    String location;
    String temp;
    String humidity;
    String windSpeed;
    String condition;
    String maxTemp;
    String minTemp;
    String imageUrl;

    public Weather() {

    }
    public Weather(String location, String temp, String humidity, String windSpeed, String condition, String imageUrl, String maxTemp, String minTemp){
        this.location = location;
        this.humidity = humidity;
        this.temp = temp;
        this.windSpeed = windSpeed;
        this.condition = condition;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.imageUrl = imageUrl;
    }
    public Weather(Weather weather){
        this.key = weather.getKey();
        this.location = weather.getLocation();
        this.humidity = weather.getHumidity();
        this.temp = weather.getTemp();
        this.windSpeed = weather.getWindSpeed();
        this.condition = weather.getCondition();
        this.maxTemp = weather.getMaxTemp();
        this.minTemp = weather.getMinTemp();
        this.imageUrl = weather.getImageUrl();
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }

    public void setMinTemp(String minTemp) {
        this.minTemp = minTemp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLocation() {
        return location;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getTemp() {
        return temp;
    }

    public String getCondition() {
        return condition;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public String getKey() {
        return key;
    }
}

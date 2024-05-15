package com.example.weatherapp.Utils;

;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.Model.Weather;
import com.example.weatherapp.R;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;


import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {
    private List<Weather> weathersList;
    private DatabaseReference weatherRef;
    private static Context context;

    public WeatherAdapter(List<Weather> weathersList, DatabaseReference weatherRef, Context context) {
        this.weathersList = weathersList;
        this.weatherRef = weatherRef;
        this.context = context;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Weather weather = weathersList.get(position);
        try {
            holder.bind(weather);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return weathersList.size();
    }

    public static class WeatherViewHolder extends RecyclerView.ViewHolder {
        TextView locationText, conditionText, temperatureText, humidityText, windSpeedText, maxText, minText;
        ImageView conditionImage;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            locationText = itemView.findViewById(R.id.locationText);
            conditionText = itemView.findViewById(R.id.conditionText);
            temperatureText = itemView.findViewById(R.id.temperatureText);
            humidityText = itemView.findViewById(R.id.humidityText);
            windSpeedText = itemView.findViewById(R.id.windSpeedText);
            maxText = itemView.findViewById(R.id.maxText);
            minText = itemView.findViewById(R.id.minText);
            conditionImage = itemView.findViewById(R.id.conditionImage);
        }
        public void bind(Weather weather) throws IOException {
            String location = weather.getLocation();
            String condition = weather.getCondition();
            String temp = weather.getTemp();
            String maxTemp = weather.getMaxTemp();
            String minTemp = weather.getMinTemp();
            String humidity = weather.getHumidity();
            String windSpeed = weather.getWindSpeed();
            String imageUrl = weather.getImageUrl();

            locationText.setText(location);
            conditionText.setText(condition);
            temperatureText.setText(temp+"°C");
            humidityText.setText(humidity+"%");
            windSpeedText.setText(windSpeed+"km/h");
            maxText.setText("H:"+maxTemp);
            minText.setText("L:"+minTemp);
            Picasso.get().load("http:"+imageUrl).into(conditionImage);
        }
    }
}
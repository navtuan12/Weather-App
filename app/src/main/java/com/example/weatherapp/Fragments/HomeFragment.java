package com.example.weatherapp.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;

import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.weatherapp.Model.Weather;
import com.example.weatherapp.R;
import com.example.weatherapp.Utils.ApiService;
import com.example.weatherapp.Utils.JsonHandler;
import com.example.weatherapp.Utils.RecyclerItemClickListener;
import com.example.weatherapp.Utils.WeatherAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tencent.mmkv.MMKV;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private final String WEATHER_API_KEY = "60fa5cee8d524f52999214906241904";
    private FloatingActionButton addWeatherButton;
    private DatabaseReference weatherRef;
    private ValueEventListener weathersValueEventListener;
    private List<Weather> weathersList;
    private RecyclerView recyclerView;
    private WeatherAdapter weatherAdapter;
    private Retrofit retrofit_weather_api;
    private ApiService weather_api;
    private FirebaseAuth firebaseAuth;
    private Context context;
    private JsonHandler jsonHandler;
    private List<String> listJsonResponse;
    private MMKV kv = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();

        weatherRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId)
                .child("weathers");

        retrofit_weather_api = new Retrofit.Builder()
                .baseUrl("http://api.weatherapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weather_api = retrofit_weather_api.create(ApiService.class);

        kv = MMKV.defaultMMKV();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        addWeatherButton = view.findViewById(R.id.addTaskButton);
        addWeatherButton.setOnClickListener(this);
        context = container.getContext();
        jsonHandler = new JsonHandler();

        weathersList = new ArrayList<>();
        listJsonResponse = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(weathersList, weatherRef, getContext());

        // Set the adapter for the RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(weatherAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        weathersValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ExecutorService networkExecutor = Executors.newSingleThreadExecutor();
                networkExecutor.execute(() -> {
                    weathersList.clear();
                    listJsonResponse.clear();
                    for (DataSnapshot weatherSnapshot : dataSnapshot.getChildren()) {
                        try {
                            Weather weather = weatherSnapshot.getValue(Weather.class);
                            Call<ResponseBody> getWeatherCall = weather_api.getWeatherData(WEATHER_API_KEY, weather.getLocation());
                            Response<ResponseBody> weatherResponse = getWeatherCall.execute();
                            if(weatherResponse.code() == 200) {
                                String weatherMsg = weatherResponse.body().string();
                                weather = jsonHandler.getWeather(weatherMsg);
                                weather.setKey(weatherSnapshot.getKey());
                                listJsonResponse.add(weatherMsg);
                                weathersList.add(weather);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void run() {
                            weatherAdapter.notifyDataSetChanged();
                        }
                    });
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        };
        weatherRef.addValueEventListener(weathersValueEventListener);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        kv.encode("weatherJson",listJsonResponse.get(position));
                        Fragment fragment = new WeatherDashboardFragment();
                        FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                        fm.replace(R.id.fragment_nav_container, fragment).commit();
                    }
                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                int position = viewHolder.getAdapterPosition();
                Weather weather = weathersList.get(position);
                weathersList.remove(position);
                weatherRef.child(weather.getKey()).removeValue();
                weatherAdapter.notifyDataSetChanged();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("onStart", "ONSTART");

    }
    
    @Override
    public void onStop() {
        super.onStop();
        Log.d("onStop","ONSTOP");
        if (weathersValueEventListener != null) {
            weatherRef.removeEventListener(weathersValueEventListener);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addTaskButton) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_weather, null);
            EditText cityNameText = dialogView.findViewById(R.id.cityNameText);
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            dialogView.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String cityName = cityNameText.getText().toString();
                    if (TextUtils.isEmpty(cityName) ) {
                        Toast.makeText(context, "Enter city name", Toast.LENGTH_SHORT).show();
                    }
                    ExecutorService networkExecutor = Executors.newSingleThreadExecutor();
                    networkExecutor.execute(() -> {
                        Call<ResponseBody> getWeatherCall = weather_api.getWeatherData(WEATHER_API_KEY, cityName);
                        try {
                            Response<ResponseBody> weatherResponse = getWeatherCall.execute();
                            if(weatherResponse.code() == 200){
                                Weather weather;
                                weather = jsonHandler.getWeather(weatherResponse.body().string());
                                saveWeatherToFirebase(weather);
                                weathersList.add(weather);
                            } else if (weatherResponse.code() == 400){
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(v.getContext() , "Can't find city name", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    dialog.dismiss();
                }
            });
            dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            dialog.show();
        }
    }

    private void saveWeatherToFirebase(Weather weather) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();

        DatabaseReference userWeatherRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId)
                .child("weathers");

        String weatherId = userWeatherRef.child("weathers").push().getKey();
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            userWeatherRef.child(weatherId).setValue(weather);
        } else {
            // No internet connection, save the task locally and redirect to the homepage
            userWeatherRef.child(weatherId).setValue(weather);
            Toast.makeText(getActivity(), "Task saved locally. No internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

//    private void feedUserData()
}
package com.example.android.sunshine.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;


import java.util.LinkedHashMap;


public class WeatherDataParser {

    public static String location;
    private final String LOG_TAG = ForecastFragment.class.getSimpleName();

    public String[] parseJsonFor3HourWeather(String weatherJsonStr) throws JSONException {

        JSONObject completeJson = new JSONObject(weatherJsonStr);
        JSONArray arrayOfWeatherData3hours = (JSONArray) completeJson.get("list");
        JSONObject city = (JSONObject) completeJson.get("city");
        JSONObject latlong = (JSONObject) city.get("coord");
        location = saveLatLong(latlong);
        String dateOfWeatherData;


        String[] forecast = new String[arrayOfWeatherData3hours.length()];

        for (int i = 0; i < arrayOfWeatherData3hours.length(); i++) {

            JSONObject weatherData3Hour = (JSONObject) arrayOfWeatherData3hours.get(i);

            dateOfWeatherData = returnDate(weatherData3Hour.get("dt_txt").toString());

            JSONObject Main = (JSONObject) weatherData3Hour.get("main");

            Double tempMax = Double.parseDouble(Main.get("temp_max").toString());
            Double tempMin = Double.parseDouble(Main.get("temp_min").toString());

            JSONObject weatherObject = weatherData3Hour.getJSONArray("weather").getJSONObject(0);
            String description = weatherObject.getString("description");

            forecast[i] = dateOfWeatherData + " - " + description + " - " + formatHighLows(tempMax,tempMin);
        }


        return forecast;
    }

    public String saveLatLong(JSONObject latlong){

        String location = "";
        try{

            String latitude = latlong.get("lat").toString();
            String longitude = latlong.get("lon").toString();

            return "geo:" + latitude + "," + longitude;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return location;
    }

    public String returnDate(String date) {
        String str_date = date;
        DateFormat formatter;

        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String day = "";
        Date newdate = null;
        try {

            newdate = formatter.parse(str_date);
            String[] splitDate = (newdate.toString()).split(" ");
            day = (""+splitDate[0]+", "+splitDate[1]+" "+ splitDate[2]+" "+ splitDate[3]+"");

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day;
    }

    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        Context context = MySuperAppApplication.getContext();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);


        String unidadeDasPreferencias = sharedPref.getString(context.getString(R.string.pref_temp_units_key),context.getString(R.string.pref_temp_units_default));

        if (unidadeDasPreferencias.equals(context.getString(R.string.pref_temp_units_imperial))){
            high = (high * 1.8) + 32;
            low = (low * 1.8) + 32;
        }else if (!unidadeDasPreferencias.equals(context.getString(R.string.pref_temp_units_imperial))){

            Log.d(LOG_TAG, "Unit type not found:" + unidadeDasPreferencias);

        }
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }
}

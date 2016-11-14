package com.example.android.sunshine.app;

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


    public String[] parseJsonFor3HourWeather(String weatherJsonStr) throws JSONException {

        JSONObject completeJson = new JSONObject(weatherJsonStr);
        JSONArray arrayOfWeatherData3hours = (JSONArray) completeJson.get("list");

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

    public Object getElementByIndex(LinkedHashMap map, int i){
        return map.get(map.keySet().toArray()[i]);
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
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }
}

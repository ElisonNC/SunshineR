package com.example.android.sunshine.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by elison.coelho on 09/11/2016.
 */

public class WeatherDataParser {


    public double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex) throws JSONException {

        JSONObject o = new JSONObject(weatherJsonStr);
        ArrayList<Integer> days = new ArrayList<>();
        JSONArray arrayOfTests = (JSONArray) o.get("list");
        Map<Integer, Double> map = new HashMap<>();

        int day = 0;

        for (int i = 0; i < arrayOfTests.length(); i++) {

            JSONObject item = (JSONObject) arrayOfTests.get(i);

            day = ReturnDay(item.get("dt_txt").toString());

            JSONObject Main = (JSONObject) item.get("main");
            Double tempMax = (Double) Main.get("temp_max");
            map.put(day, tempMax);
            }

        return 0;
    }

    public Integer ReturnDay(String date) {
        String str_date = date;
        DateFormat formatter;
        int day = 0;
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newdate = null;
        try {
            newdate = formatter.parse(str_date);
            day = newdate.getDay();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day;
    }
}

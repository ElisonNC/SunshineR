package com.example.android.sunshine.app;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.android.sunshine.app.data.WeatherContract;

import com.example.android.sunshine.app.data.WeatherDbHelper;
import com.example.android.sunshine.app.data.WeatherProvider;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;


import java.util.LinkedHashMap;
import java.util.Vector;


public class WeatherDataParser {
    private final Context mContext;

    public static String location;
    private final String LOG_TAG = ForecastFragment.class.getSimpleName();

    WeatherDataParser(Context context){
        mContext = context;
    }


    public Void parseJsonFor3HourWeather(String forecastJsonStr) throws JSONException {

        final String OWM_CITY = "city";
        final String OWM_CITY_NAME = "name";
        final String OWM_COORD = "coord";

        // Location coordinate
        final String OWM_LATITUDE = "lat";
        final String OWM_LONGITUDE = "lon";

        // Weather information.  Each day's forecast info is an element of the "list" array.
        final String OWM_LIST = "list";

        final String OWM_PRESSURE = "pressure";
        final String OWM_HUMIDITY = "humidity";
        final String OWM_WINDSPEED = "speed";
        final String OWM_WIND_DIRECTION = "deg";

        // All temperatures are children of the "temp" object.

        final String OWM_MAX = "temp_max";
        final String OWM_MIN = "temp_min";

        final String OWM_WEATHER = "weather";
        final String OWM_DESCRIPTION = "main";
        final String OWM_WEATHER_ID = "id";

        try {
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);


            JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);
            String cityName = cityJson.getString(OWM_CITY_NAME);

            JSONObject cityCoord = cityJson.getJSONObject(OWM_COORD);
            double cityLatitude = cityCoord.getDouble(OWM_LATITUDE);
            double cityLongitude = cityCoord.getDouble(OWM_LONGITUDE);

            long locationId = addLocation(cityName, cityLatitude, cityLongitude);

            // Insert the new weather information into the database

            Vector<ContentValues> cVVector = new Vector<ContentValues>(weatherArray.length());

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            for(int i = 0; i < weatherArray.length(); i++) {
                // These are the values that will be collected.
                String dateTime;
                double pressure;
                int humidity;
                double windSpeed;
                double windDirection;

                double high;
                double low;

                String description;
                int weatherId;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);
                JSONObject hourForecast = dayForecast.getJSONObject("main");

                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = returnDate(dayForecast.get("dt_txt").toString());
              //  dateTime = String.valueOf(dayTime.setJulianDay(julianStartDay+i));
                pressure = hourForecast.getDouble(OWM_PRESSURE);
                humidity = hourForecast.getInt(OWM_HUMIDITY);

                JSONObject windForecast = dayForecast.getJSONObject("wind");
                windSpeed = windForecast.getDouble(OWM_WINDSPEED);
                windDirection = windForecast.getDouble(OWM_WIND_DIRECTION);

                // Description is in a child array called "weather", which is 1 element long.
                // That element also contains a weather code.
                JSONObject weatherObject =
                        dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);
                weatherId = weatherObject.getInt(OWM_WEATHER_ID);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
               // JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                high = hourForecast.getDouble(OWM_MAX);
                low = hourForecast.getDouble(OWM_MIN);

                ContentValues weatherValues = new ContentValues();

                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationId);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, dateTime);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, windDirection);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, high);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, low);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, description);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherId);

                cVVector.add(weatherValues);
            }
            int inserted = 0;
            // add to database
            if ( cVVector.size() > 0) {
                // Student: call bulkInsert to add the weatherEntries to the databasehere

                ContentValues[] weatherEntries = new ContentValues[cVVector.size()];

                cVVector.toArray(weatherEntries);

                inserted =  mContext.getContentResolver().bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI,weatherEntries);


            }

            Log.d(LOG_TAG, "WeatherDataParser Complete. " + inserted + " Inserted");


        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;

    }

    public String returnDate(String date) {



        String str_date = date;

        String millisec = null;




     //   String day = "";
     //   Date newdate = null;
        try {

         //   newdate = formatter.parse(str_date);
         //   String[] splitDate = (newdate.toString()).split(" ");
          //  day = (""+splitDate[0]+", "+splitDate[1]+" "+ splitDate[2]+" "+ splitDate[3]+"");

            DateFormat formatter;
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = formatter.parse(str_date);
            millisec = ""+d.getTime()+"";



        } catch (ParseException e) {
            e.printStackTrace();
        }

        return millisec;
    }

    long addLocation( String cityName, double lat, double lon) {

        long resultId;

        Cursor cursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                new String[]{ WeatherContract.LocationEntry._ID},
                WeatherContract.LocationEntry.COLUMN_CITY_NAME + " = ?",
                new String[]{cityName},
                null
        );



        if(cursor.moveToFirst()) {
            int cityIndex = cursor.getColumnIndex(WeatherContract.LocationEntry._ID);
            resultId = cursor.getLong(cityIndex);
        }
         else {

            ContentValues values = new ContentValues();
            // testValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, TEST_LOCATION);
            values.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, cityName);
            values.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, lat);
            values.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, lon);

            Uri insertedUri = mContext.getContentResolver().insert(
                                        WeatherContract.LocationEntry.CONTENT_URI,
                    values
            );

            resultId = ContentUris.parseId(insertedUri);

        }
        cursor.close();
        return  resultId;
    }
}

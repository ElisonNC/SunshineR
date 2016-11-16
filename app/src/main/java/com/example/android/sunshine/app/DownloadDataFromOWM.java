package com.example.android.sunshine.app;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by elison.coelho on 08/11/2016.
 */

public class DownloadDataFromOWM extends AsyncTask<URL,Integer,String[]> {

    private final String LOG_TAG = DownloadDataFromOWM.class.getSimpleName();

    public AsyncResponse delegate = null;


    @Override
    protected String[] doInBackground(URL... urls) {

        String forecastJsonStr = getJsonWeatherFromServer(urls);

        if (forecastJsonStr == null) return null;


        WeatherDataParser parse = new WeatherDataParser();

        try {
            return parse.parseJsonFor3HourWeather(forecastJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    private String getJsonWeatherFromServer(URL[] urls) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;


        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast


            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) urls[0].openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            forecastJsonStr = buffer.toString();
            Log.e(LOG_TAG, "Forecast JSON String: " + forecastJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("DetailFragment", "Error closing stream", e);
                }
            }
        }
        return forecastJsonStr;
    }


    protected void onPostExecute(String[] result) {

        delegate.processFinish(result);

    }

}

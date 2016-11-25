package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.sunshine.app.data.WeatherContract;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by elison.coelho on 09/11/2016.
 */

public class ForecastFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {


    private static final int FORECAST_LOADER = 0;



    private final String LOG_TAG = ForecastFragment.class.getSimpleName();

    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,

            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };


    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEATHER_CONDITION_ID = 5;
    static final int COL_COORD_LAT = 6;
    static final int COL_COORD_LONG = 7;

    ForecastAdapter adapter;


    public ForecastFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.d(LOG_TAG, "onCreate");
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
         super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
        inflater.inflate(R.menu.main, menu);
    }


    public URL makeURL (String cidade) {

        // "http://api.openweathermap.org/data/2.5/forecast?q=Lages,br&units=metric&appid=3e49709532f67599b8b2b7cd01d44293"

        Uri.Builder builder = new Uri.Builder();

        builder.scheme("http")
                .authority("api.openweathermap.org")
                .appendPath("data")
                .appendPath("2.5")
                .appendPath("forecast")
                .appendQueryParameter("q", cidade + ",br")
                .appendQueryParameter("units", "metric")
                .appendQueryParameter("appid", "3e49709532f67599b8b2b7cd01d44293");

        String urlstring = builder.build().toString();
        Log.v(LOG_TAG,"URI criada:" + urlstring);

        URL url = null;

        try {

            url = new URL(urlstring);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settingsRefresh) {

            updateWeather();


            return true;
        }
        if (id == R.id.showOnMap){


            // Create the text message with a string
            Intent showOnMapIntent = new Intent();
            showOnMapIntent.setAction(Intent.ACTION_VIEW);
            String location = Utility.getPreferredLocation(getContext());
            showOnMapIntent.setData(Uri.parse(location));

            // Verify that the intent will resolve to an activity
            if (showOnMapIntent.resolveActivity(getContext().getPackageManager()) != null) {
                startActivity(showOnMapIntent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateWeather(){

        DownloadDataFromOWM task = new DownloadDataFromOWM(getActivity());
        String location = Utility.getPreferredLocation(getActivity());

        task.execute(makeURL(location));
    }

//    public void onStart() {
//        super.onStart();
//        Log.d(LOG_TAG, "onStart");
//        updateWeather();
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        adapter = new ForecastAdapter(getActivity(),null,0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(adapter);

        // We'll call our MainActivity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting, cursor.getLong(COL_WEATHER_DATE)
                            ));
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }

    void onLocationChanged(){
        updateWeather();
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
         /*
     * Takes action based on the ID of the Loader that's being created
     */ String locationSetting = Utility.getPreferredLocation(getActivity());

        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());


                return new CursorLoader(getActivity(),
                        weatherForLocationUri,
                        FORECAST_COLUMNS,
                        null,
                        null,
                        sortOrder);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}

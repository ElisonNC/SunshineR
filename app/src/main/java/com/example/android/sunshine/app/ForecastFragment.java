package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import android.support.v4.app.Fragment;
import android.os.Bundle;

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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by elison.coelho on 09/11/2016.
 */

public class ForecastFragment extends Fragment implements AsyncResponse{


    private final String LOG_TAG = ForecastFragment.class.getSimpleName();


    public ForecastFragment() {

    }


    ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
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
            String location = WeatherDataParser.location;
            showOnMapIntent.setData(Uri.parse(location));

            // Verify that the intent will resolve to an activity
            if (showOnMapIntent.resolveActivity(getContext().getPackageManager()) != null) {
                startActivity(showOnMapIntent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateWeather(){

        DownloadDataFromOWM task = new DownloadDataFromOWM();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String cidadeDasPreferencias = sharedPref.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));


        task.delegate = this;
        task.execute(makeURL(cidadeDasPreferencias));
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        adapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                new ArrayList<String>());

        final ListView forecastListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        forecastListView.setAdapter(adapter);
        forecastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String forecast = adapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(intent);
            }
        });

        return rootView;
    }


    @Override
    public void processFinish(String[] output) {
        if ( output != null){
            adapter.clear();
            adapter.addAll(output);
        }
    }
}

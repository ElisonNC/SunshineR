package com.example.android.sunshine.app;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import org.json.JSONException;

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
    DownloadDataFromOWM task = new DownloadDataFromOWM();

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
    }

    public URL makeURL (String cidade) {

        //  "http://api.openweathermap.org/data/2.5/forecast?q=Lages,br&units=metric&appid=3e49709532f67599b8b2b7cd01d44293"

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
        if (id == R.id.action_refresh) {

                 task.delegate = this;
                 task.execute(makeURL("Lages"));

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        String[] forecastArray = {"Hoje - Sol - 14/30",
                "Amanhã - Sol - 14/30",
                "Quinta - Sol - 14/30",
                "Sexta - Sol - 14/30",
                "Sábado - Sol - 14/30",
                "Domingo - Sol - 14/30",
                "Segunda - Sol - 14/30",
                "Terça - Sol - 14/30"};

        List<String> weekForecast = new ArrayList<>(Arrays.asList(forecastArray));

        adapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                weekForecast);

        ListView forecastListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        forecastListView.setAdapter(adapter);





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

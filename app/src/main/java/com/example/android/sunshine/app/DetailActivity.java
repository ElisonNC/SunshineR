package com.example.android.sunshine.app;



import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.android.sunshine.app.data.WeatherContract;



public class DetailActivity extends ActionBarActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detailContainer, new DetailFragment())
                    .commit();


        }


    }






    public static class DetailFragment extends Fragment implements
            LoaderManager.LoaderCallbacks<Cursor> {

        private ShareActionProvider shareActionProvider;

        private static final int URL_LOADER = 0;

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




        private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
        private String forecastStr;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }
        Intent sendIntent;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            return inflater.inflate(R.layout.fragment_detail, container, false);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            getLoaderManager().initLoader(URL_LOADER, null, this);
        }

        public Intent createShareForecastIntent() {

            sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, forecastStr + FORECAST_SHARE_HASHTAG);

            return  sendIntent;

        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.main, menu);
            inflater.inflate(R.menu.shareaction, menu);

            MenuItem item = menu.findItem(R.id.menu_item_share);

            // Fetch and store ShareActionProvider
             shareActionProvider  = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

            if (  forecastStr != null ) {
                shareActionProvider.setShareIntent(createShareForecastIntent());
            }


        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
            }
            if (id == R.id.menu_item_share) {

                // Verify that the intent will resolve to an activity
                if (sendIntent == null){
                    sendIntent = createShareForecastIntent();
                }
                if (sendIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivity(sendIntent);
                }

                //  setShareIntent(sendIntent);
            }

            return super.onOptionsItemSelected(item);
        }


        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Intent intent = getActivity().getIntent();
            if (intent == null){
                return null;
            }



            return new CursorLoader(getActivity(),
                    intent.getData(),
                    FORECAST_COLUMNS,
                    null,
                    null,
                    null);

        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if(!data.moveToFirst()) return;

            String dateString = Utility.formatDate(data.getLong(COL_WEATHER_DATE));

            String weatherDescription = data.getString(COL_WEATHER_DESC);

            boolean isMetric = Utility.isMetric(getActivity());

            String high = data.getString(COL_WEATHER_MAX_TEMP);

            String min = data.getString(COL_WEATHER_MIN_TEMP);

            forecastStr = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, min);

            TextView detailTextView = (TextView)getView().findViewById(R.id.textView);
            detailTextView.setText(forecastStr);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}

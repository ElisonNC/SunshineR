package com.example.android.sunshine.app;

import android.content.Intent;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.util.zip.Inflater;

public class DetailActivity extends ActionBarActivity {



    public String forecast = "";
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


  //  @Override
  //  public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
   //     getMenuInflater().inflate(R.menu.main, menu);
      //  getMenuInflater().inflate(R.menu.shareaction, menu);
        // Locate MenuItem with ShareActionProvider
   //     MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
   //     mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

   //     if (mShareActionProvider  != null ) {
   //         mShareActionProvider.setShareIntent(createShareForecastIntent());
    //    }

   //     return true;
   // }




    private void setShareIntent(Intent shareIntent) {
  //      if (mShareActionProvider != null) {
  //          mShareActionProvider.setShareIntent(shareIntent);
  //      }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {

        private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
        private String forecastStr;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }
        Intent sendIntent;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            Intent intent =  getActivity().getIntent();

            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
                forecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);

                ((TextView) rootView.findViewById(R.id.textView)).setText(forecastStr);
            }




            return rootView;
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
            // Locate MenuItem with ShareActionProvider
        //    MenuItem item = menu.findItem(R.id.menu_item_share);

            // Fetch and store ShareActionProvider
     //       ShareActionProvider mShareActionProvider  = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

       //     if (mShareActionProvider  != null ) {
       //         mShareActionProvider.setShareIntent(createShareForecastIntent());
       //     }

          //  return true;
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


    }
}

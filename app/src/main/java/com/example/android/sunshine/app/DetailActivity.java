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


    private final String DETAILFRAGMENT_TAG = "DTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detailContainer, new DetailFragment(),DETAILFRAGMENT_TAG)
                    .commit();


        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
          //  Intent intent = new Intent(SettingsActivity.class);
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
//        if (id == R.id.menu_item_share) {
//
//            // Verify that the intent will resolve to an activity
//            if (sendIntent == null){
//                sendIntent = createShareForecastIntent();
//            }
//            if (sendIntent.resolveActivity(getContext().getPackageManager()) != null) {
//                startActivity(sendIntent);
//            }
//            //  setShareIntent(sendIntent);
//        }
//
//        return super.onOptionsItemSelected(item);
    }



}

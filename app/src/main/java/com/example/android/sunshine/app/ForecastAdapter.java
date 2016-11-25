package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.android.sunshine.app.Utility.getFormattedMonthDay;
import static com.example.android.sunshine.app.Utility.getHour;


/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */

public class ForecastAdapter extends CursorAdapter {





    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    int position;
    @Override
    public int getItemViewType(int position) {
        this.position = position;
        return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }



    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        // TODO: Determine layoutId from viewType
        switch (viewType){
            case VIEW_TYPE_TODAY:
                layoutId = R.layout.list_item_forecast_today;
                break;
            case  VIEW_TYPE_FUTURE_DAY:
                layoutId = R.layout.list_item_forecast;
                break;
        }
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    public String dateFilter(Context context, Long dateInMillis) {

    Calendar calendar = Calendar.getInstance();
    int currentJulianDay = calendar.get(Calendar.DAY_OF_YEAR);
    calendar.setTimeInMillis(dateInMillis);
    int julianDay = calendar.get(Calendar.DAY_OF_YEAR);


    if (julianDay == currentJulianDay) {
       return "1";
    } else if ( julianDay < currentJulianDay + 7 ) {
       return "2";
    } else {
       return "3";
    }
    }


    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        ViewHolder viewHolder = (ViewHolder) view.getTag();


//        if (dateFilter(context,cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) == "1" ){
//
//
//        };

        viewHolder.dateView.setText(Utility.getFriendlyDayString(context,cursor.getLong(ForecastFragment.COL_WEATHER_DATE)));


        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);
        // Use placeholder image for now
        viewHolder.iconView.setImageResource(R.drawable.ic_launcher);


        viewHolder.descriptionView.setText(cursor.getString(ForecastFragment.COL_WEATHER_DESC));

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        double mid = (high+low)/2;

        if (this.position == 0) {
            viewHolder.highTempView.setText(Utility.formatTemperature(context,high, isMetric));
            viewHolder.lowTempView.setText(Utility.formatTemperature(context,low, isMetric));
        }else viewHolder.highTempView.setText(Utility.formatTemperature(context,mid, isMetric));



//        TextView lowView = (TextView) view.findViewById(R.id.list_item_low_textview);
//        lowView.setText(Utility.formatTemperature(high, isMetric));
    }

    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }

}

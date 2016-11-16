package com.example.android.sunshine.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by elison.coelho on 16/11/2016.
 */

public class MySuperAppApplication extends Application {
    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }
}

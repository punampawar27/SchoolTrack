package com.schooltrack;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.schooltrack.map.MapActivity;
import com.schooltrack.utils.Util;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Gracie on 11/1/2015.
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_screen);
        satartTimer();

    }

    public void satartTimer(){
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SharedPreferences prefernce = SplashActivity.this.getSharedPreferences("GEOTRACKDATA", 0);
                if (prefernce.getString(Util.SESSION_ID, "").equalsIgnoreCase("")) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    SplashActivity.this.startActivity(intent);
                } else {
                    startActivity(new Intent(SplashActivity.this, MapActivity.class));
                    SplashActivity.this.finish();
                }
            }
        }, 5 * 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int orientation = display.getOrientation();
        switch(orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
        }

    }
}

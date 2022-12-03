package com.example.kolorkast;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

public class GeneralActivity extends MainActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // If smartphone lock orientation to portrait
        if (!Helper.isTablet(this.getApplicationContext())){
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public static class Helper {
        public static boolean isTablet(Context context){
            Configuration config = context.getResources().getConfiguration();
            return config.smallestScreenWidthDp >= 600;
        }
    }
}

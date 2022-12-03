package com.example.kolorkast;

import android.content.Context;

import java.io.InputStream;

public class Files {
    public static void readCSV(Context mainScreen){
        try{
            InputStream data = mainScreen.getAssets().open("colorsfile.csv");

        } catch (Exception e){

        }
    }
}

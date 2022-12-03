package com.example.kolorkast;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    //Initializing variables
    ImageView imageView;
    ImageView background;
    ImageView menu;
    ImageButton cameraButton;
    TextView ColorValues;
    TextView ColorNames;
    TextView ColorMix;
    View ColorViews;
    private String currentPhoto;

    private static final int MY_CAMERA_REQUEST_CODE = 100;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //First permissions for camera
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }

        //Setup buttons, images, and text onto screen
        setContentView(R.layout.activity_main);
        cameraButton = findViewById(R.id.button);
        imageView = findViewById(R.id.imageView);
        background = findViewById(R.id.imageView2);
        menu = findViewById(R.id.imageView4);
        ColorValues = findViewById(R.id.displayValues);
        ColorViews = findViewById(R.id.displayColors);
        ColorNames = findViewById(R.id.displayColorName);
        ColorMix = findViewById(R.id.displayParentColors);

        //open camera instantly after permissions been granted
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            startCamera();
        }

        //restart app button to clear the image from layer from the previous picture
        cameraButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
                //get rid of animation for closing an app
                overridePendingTransition(0, 0);
                startActivity(getIntent());
            }
        });



    }



    //image sent from camera to application
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //image converted to bitmap to read the pixel values
        final Bitmap[] bitmap = {BitmapFactory.decodeFile(currentPhoto)};
        imageView.setImageBitmap(rotateImage(bitmap[0]));


        imageView = findViewById(R.id.imageView);
        ColorValues = findViewById(R.id.displayValues);


        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache(true);

        ////////////////////////////////////////////////////////////////////////////
        // list that holds strings of a file
        List<String> listOfStrings = new ArrayList<String>();

        // load data from file
        InputStream is = this.getResources().openRawResource(R.raw.colorsfile);

        BufferedReader bf = new BufferedReader(new InputStreamReader(is));

        // read entire line as string
        String line = null;

        try {
            line = bf.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // checking for end of file
        while (line != null) {
            listOfStrings.add(line);

            try {
                line = bf.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // closing buffer reader object
        try {
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // storing the data in arraylist to array
        String[] array = listOfStrings.toArray(new String[0]);
        ////////////////////////////////////////////////////////////////////////////



        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    //when screen is touched then Color values display along with the color at the bottom
                    if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {

                        //exception handling for if user touches outside of image view
                        try {
//                            crosshair = findViewById(R.id.crosshair);
////                            crosshair.setX(event.getX()-85);//negative is left
////                            crosshair.setY(event.getY()-135); //negative is up
//                            crosshair.setX(event.getX());//negative is left
//                            crosshair.setY(event.getY());

                            bitmap[0] = imageView.getDrawingCache();
                            int pixels = bitmap[0].getPixel((int) event.getX(), (int) event.getY());
//                            int pixels = bitmap[0].getPixel((int) event.getX()+70, (int) event.getY()+65);
                            //find rgb values using imported library
                            int r = Color.red(pixels);

                            int g = Color.green(pixels);

                            int b = Color.blue(pixels);

                            String hex = "#" + Integer.toHexString(pixels);
                            ColorViews.setBackgroundColor(Color.rgb(r, g, b));
                            ColorValues.setText("RGB: " + r + ", " + g + ", " + b + "\nHex: " + hex);

                            //get Hue method

                            int Hue = getHue(r, g, b);

//                            ColorNames.setText("Color: " +array[Hue]+ "\nHue: "+ Hue);

                            // Conditions for Exceptions when listing names like if its grey, black, or white
                            if ((r > 130) & (g == 0) & (b == 0)) {
                                ColorNames.setText("Color: Red\nHue: "+ Hue);
                                ColorViews.setBackgroundColor(Color.rgb(255, g, b));
                            }
                            else if (r>0 & r<129 & (g == 0) & (b == 0)){
                                ColorNames.setText("Color: Dark Red\nHue: "+ Hue);
                            }
                            else if (r==0 & g==0 & b==0){
                                ColorNames.setText("Color: Black\nHue: "+ Hue);
                            }else if ((r==g) & (r==b) & (r<50)){
                                ColorNames.setText("Color: Black\nHue: "+ Hue);
                            }else if ((r==g) & (r==b) & (r>200)){
                                ColorNames.setText("Color: White\nHue: "+ Hue);
                            }else if ((r==g) & (r==b)){
                                ColorNames.setText("Color: Grey\nHue: "+ Hue);
                            //Conditions for if the color is a darker or lighter shade
                            }else if ((r<100) & (b<100) & (g<100)) {
                                ColorNames.setText("Color: Dark " +array[Hue]+ "\nHue: "+ Hue);
                            }
                            else if ((r>200) & (b>200) & (g>200)) {
                                ColorNames.setText("Color: Light " +array[Hue]+ "\nHue: "+ Hue);
                            }
                            //Default condition to just name the color and hue value
                            else {
                                ColorNames.setText("Color: " +array[Hue]+ "\nHue: "+ Hue);
                            }

                            if ((r>200) & (b>200) & (g>200)) {
                                ColorNames.setText("Color: Light " +array[Hue]+ "\nHue: "+ Hue);
                            }
                            //Default condition to just name the color and hue value
                            else {
                                ColorNames.setText("Color: " +array[Hue]+ "\nHue: "+ Hue);
                            }
                            //BUG HERE WHERE IT DOESNT READ RED COLORS WHEN THE HUE IS READ
                            //Parent colors that make up the scanned color
                            if (Hue == 0){
                                ColorMix.setText("");
                            } else if(Hue<20 & Hue>0 || Hue == 360){
                                ColorMix.setText("Color Mix: Red");
                            } else if (Hue>20 & Hue<46){
                                ColorMix.setText("Color Mix: Red + Yellow");
                            } else if (Hue>45 & Hue<76){
                                ColorMix.setText("Color Mix: Yellow");
                            }
                            else if (Hue>75 & Hue<131){
                                ColorMix.setText("Color Mix: Yellow + Green");
                            } else if (Hue>130 & Hue<181){
                                ColorMix.setText("Color Mix: Green");
                            } else if (Hue>180 & Hue<217){
                                ColorMix.setText("Color Mix: Green + Blue");
                            } else if (Hue>216 & Hue<253){
                                ColorMix.setText("Color Mix: Blue");
                            } else if (Hue>252 & Hue<289){
                                ColorMix.setText("Color Mix: Blue + Purple");
                            } else if (Hue>288 & Hue<325){
                                ColorMix.setText("Color Mix: Purple");
                            } else if (Hue>324 & Hue<340){
                                ColorMix.setText("Color Mix: Purple + Red");
                            }  else if (Hue>339){
                                ColorMix.setText("Color Mix: Red");
                            }
//                            System.out.println(Hue);

                        } catch (Exception e){
                            int r = 0;

                            int g = 0;

                            int b = 0;
                            //If the exception is caught just make the color display black
                            ColorViews.setBackgroundColor(Color.rgb(r, g, b));
                            ColorValues.setText("RGB: " + r + ", " + g + ", " + b + " \nHex: #ff000000");
                            ColorNames.setText("Color: Black \nHue: 0");

                        }

                    }
                return true;
            }
        });

    }



    //permission request for camera
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera Permission Granted\nPress the x to take a picture", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    //Get hue function calculator
    public int getHue(int red, int green, int blue){

        float min = Math.min(Math.min(red, green), blue);
        float max = Math.max(Math.max(red, green), blue);

        if (min == max) {
            return 0;
        }

        float hue = 0f;
        if (max == red) {
            hue = (green - blue) / (max - min);

        } else if (max == green) {
            hue = 2f + (blue - red) / (max - min);

        } else {
            hue = 4f + (red - green) / (max - min);
        }

        hue = hue * 60;

        if(hue<0) {
            hue = hue + 360;
        }

        return Math.round(hue);
    }

    //Method to start the camera and then set the bitmap as the image taken from the camera
    public void startCamera(){
        String fileName = "photo";
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try{
            File imageFile = File.createTempFile(fileName,".jpg",storageDirectory);
            currentPhoto = imageFile.getAbsolutePath();

            Uri imageUri = FileProvider.getUriForFile(MainActivity.this,"com.example.kolorkast.fileprovider", imageFile);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, 1);
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    //Method that rotates the image from the camera
    private Bitmap rotateImage(Bitmap bitmap) {
        ExifInterface exifInterface = null;
        try{
            exifInterface = new ExifInterface(currentPhoto);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();
        switch (orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            default:
            }
            Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            imageView.setImageBitmap(rotateBitmap);
        return rotateBitmap;
    }
}


package com.example.hp.bookseller;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

public class SplashActivity extends AppCompatActivity
{
    BookSellerUtil util;
    SharedPreferences preferences;



    private static final int PERMISSION_CODE = 123;

    //Requesting permission
    private boolean checkPermission()
    {


        int result =
                ContextCompat.checkSelfPermission(getApplicationContext(),android.Manifest.permission.CALL_PHONE);

        int result1 =
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int result2 =
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);

        return result == PackageManager.PERMISSION_GRANTED
                && result1 == PackageManager.PERMISSION_GRANTED
                && result2== PackageManager.PERMISSION_GRANTED;



    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]
                {android.Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE },
                PERMISSION_CODE);
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode)
        {
            case PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean cameraRequest =
                            grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean callPhoneRequest =
                            grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalStorage =
                            grantResults[2] == PackageManager.PERMISSION_GRANTED;


                    if (cameraRequest && callPhoneRequest && readExternalStorage)
                        Toast.makeText(SplashActivity.this, "Permission Granted, Now you can access camera,phonediretory,external storage.", Toast.LENGTH_LONG).show();
                    else {

                        Toast.makeText(SplashActivity.this, "Permission Denied, You cannot access location data and camera.", Toast.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)

                                                {
                                                    requestPermissions(new String[]
                                                                    {
                                                                    android.Manifest.permission.CALL_PHONE,
                                                                    Manifest.permission.CAMERA,
                                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                                                    },
                                                            PERMISSION_CODE);
                                                }
                                            }

                                        });
                                return;
                            }

                        }
                    }


                }
                break;
        }
    }

        private void showMessageOKCancel (String message, DialogInterface.OnClickListener okListener)
        {
            new AlertDialog.Builder(SplashActivity.this)
                    .setMessage(message)
                    .setPositiveButton("OK", okListener)
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        }


        @Override
        protected void onCreate (Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash);

            if(!checkPermission())
            {
             requestPermission();
            }


            preferences = getSharedPreferences(util.SHAREDPREFS_FILENAME, MODE_PRIVATE);
            handler.sendEmptyMessageDelayed(100, 7000);

        }
        Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if (msg.what == 100) {


                    if (preferences.getBoolean(util.SHAREDPREFS_LOGINFLAG, false) == true) {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);

                        finish();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        };
}

package com.y_ral.rewaudio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.y_ral.mRaid.MraidAds;


import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    MraidAds mraidAds;
    ViewStub ads;
    Button showAds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ads = findViewById(R.id.ads);
        showAds = findViewById(R.id.showAds);
        mraidAds = new MraidAds(this, ads,"98401SDFsdf","sdf324sd",320,50);
        //mraidAds.loadAds();
    }

    public void showAds(View view) {
        Toast.makeText(this, "Loading Ads wait for few seconds", Toast.LENGTH_LONG).show();
        mraidAds.loadGAds();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mraidAds != null)
            mraidAds.resumeAudio();
        isStoragePermissionGranted();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mraidAds != null)
            mraidAds.pauseAudio();
    }
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG,"Permission is granted");
                    showAds.setVisibility(View.VISIBLE);
                }else{
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                }

                return true;
            } else {
                showAds.setVisibility(View.GONE);

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            showAds.setVisibility(View.VISIBLE);
            return true;
        }
    }
}
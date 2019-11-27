package com.radio.resplandecer.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.radio.resplandecer.R;
import com.radio.resplandecer.screens.home.HomeActivity;

public class SplashScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);

    }






}

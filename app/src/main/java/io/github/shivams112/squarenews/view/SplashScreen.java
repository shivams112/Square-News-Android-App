package io.github.shivams112.squarenews.view;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.karan.churi.PermissionManager.PermissionManager;

import java.util.ArrayList;

import io.github.shivams112.squarenews.R;



public class SplashScreen extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        startApp();
    }



    private void startApp(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this,TopSourceScreen.class));
                finish();
            }
        },2000);
    }
}

package com.example.simplechat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class tips extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        getSupportActionBar().setTitle("Help Center");
    }
}

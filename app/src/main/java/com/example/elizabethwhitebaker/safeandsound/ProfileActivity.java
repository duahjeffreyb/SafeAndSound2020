package com.example.elizabethwhitebaker.safeandsound;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class ProfileActivity extends AppCompatActivity {
    private int initID;
    private DBHandler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initID = getIntent().getIntExtra("initID", 0);
        final String name = getIntent().getStringExtra("name");
        final String user = getIntent().getStringExtra("user");
        final String pass = getIntent().getStringExtra("pass");

        Initiator userProfile = handler.findHandler(user, pass);



    }
}
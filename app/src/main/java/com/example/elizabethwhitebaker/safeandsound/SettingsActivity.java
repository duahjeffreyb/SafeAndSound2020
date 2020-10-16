package com.example.elizabethwhitebaker.safeandsound;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {
    private int initID;
    private String name;
    private DBHandler handler = new DBHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initID = getIntent().getIntExtra("initID", 0);
        name = getIntent().getStringExtra("name");

        final String name = getIntent().getStringExtra("name");
        final String user = getIntent().getStringExtra("user");
        final String pass = getIntent().getStringExtra("pass");

        Initiator userProfile = handler.findHandler(user, pass);

        Button goBack = findViewById(R.id.go_back_button);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, HomeScreenActivity.class);
                intent.putExtra("initID", initID);
                intent.putExtra("name", name);
                startActivity(intent);

            }
        });


    }
}
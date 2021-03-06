package com.example.elizabethwhitebaker.safeandsound;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class DeleteGroupActivity extends AppCompatActivity {
    private int initID;
    private String name;
    private DBHandler handler;
    private ArrayList<Group> groups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_group);

        initID = getIntent().getIntExtra("initID", 0);
        name = getIntent().getStringExtra("name");
        handler = new DBHandler(getApplicationContext());
        groups = handler.getAllGroups();
        Button backButton = findViewById(R.id.delete_back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeleteGroupActivity.this, HomeScreenActivity.class);
                intent.putExtra("initID", initID);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });
    }
}
package com.example.elizabethwhitebaker.safeandsound;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

public class CheckEventActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {
//    private static final String TAG = "CheckEventActivity";

    private int initID;
    private Spinner chooseEvent;
    private Button btnStatusReport;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_event);

        initID = getIntent().getIntExtra("initID", 0);

        intent = new Intent(CheckEventActivity.this, StatusReportActivity.class);

        chooseEvent = findViewById(R.id.chooseEventSpinner);

        Button btnGoBack = findViewById(R.id.doneButton);
        btnStatusReport = findViewById(R.id.statusReportButton);

        chooseEvent.setOnItemSelectedListener(this);
        loadSpinnerData();

        btnStatusReport.setEnabled(false);

        btnStatusReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("initID", initID);
                startActivity(intent);
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CheckEventActivity.this, HomeScreenActivity.class);
                i.putExtra("initID", initID);
                startActivity(i);
            }
        });
    }

    private void loadSpinnerData() {
        DBHandler handler = new DBHandler(this);
        ArrayList<Event> es = handler.getAllEvents();
        ArrayList<String> eventNames = new ArrayList<>();
        eventNames.add("Select event");
        for(Event e : es)
            eventNames.add(e.getEventName());
        ArrayAdapter<String> eventAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, eventNames);
        eventAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseEvent.setAdapter(eventAdapter);
        handler.close();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(i != 0) {
            btnStatusReport.setEnabled(true);
            intent.putExtra("event", adapterView.getItemAtPosition(i).toString());
        }
        else
            btnStatusReport.setEnabled(false);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // TODO Auto-generated method stub
    }
}

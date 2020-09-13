package com.example.elizabethwhitebaker.safeandsound;

import android.content.Intent;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

public class CreateEventActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {
//    private static final String TAG = "CreateEventActivity";

    private DBHandler handler;
    private EditText eventNameET, eventDescET;
    private int initID;
    private ArrayList<CheckBox> checkBoxes;
    private ArrayList<String> groupNames;
    private Spinner pickGroup;
    private Button btnDeleteChecked, btnDeleteAll, btnCreate;
    private ConstraintLayout scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        initID = getIntent().getIntExtra("initID", 0);

        checkBoxes = new ArrayList<>();
        groupNames = new ArrayList<>();

        eventNameET = findViewById(R.id.nameEventEditText);
        eventDescET = findViewById(R.id.eventDescriptionEditText);

        pickGroup = findViewById(R.id.pickGroupSpinner);

        btnDeleteChecked = findViewById(R.id.deleteCheckedButton);
        btnDeleteAll = findViewById(R.id.deleteAllButton);
        btnCreate = findViewById(R.id.createButton);
        Button btnGoBack = findViewById(R.id.doneButton);

        scrollView = findViewById(R.id.scrollViewConstraintLayout);

        pickGroup.setOnItemSelectedListener(this);
        loadSpinnerData();

        btnDeleteChecked.setEnabled(false);
        btnDeleteAll.setEnabled(false);
        btnCreate.setEnabled(false);

        btnDeleteChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox[] checks = new CheckBox[checkBoxes.size()];
                for(int i = 0; i < checkBoxes.size(); i++)
                    checks[i] = checkBoxes.get(i);
                for(CheckBox checkBox : checks) {
                    if(checkBox.isChecked()) {
                        ConstraintSet set = new ConstraintSet();
                        reloadSpinnerData(true, checkBox.getText().toString());
                        set.clone(scrollView);
                        if(checkBoxes.size() == 1) {
                            set.connect(R.id.deleteCheckedButton, ConstraintSet.TOP,
                                    R.id.chosenGroupTextView, ConstraintSet.BOTTOM, 16);
                            btnDeleteChecked.setEnabled(false);
                            btnDeleteAll.setEnabled(false);
                            btnCreate.setEnabled(false);
                        }
                        else if(checkBoxes.indexOf(checkBox) != checkBoxes.size() - 1 && checkBoxes.indexOf(checkBox) != 0)
                            set.connect(checkBoxes.get(checkBoxes.indexOf(checkBox) + 1).getId(), ConstraintSet.TOP,
                                    checkBoxes.get(checkBoxes.indexOf(checkBox) - 1).getId(), ConstraintSet.BOTTOM, 16);
                        else if(checkBoxes.indexOf(checkBox) == 0)
                            set.connect(checkBoxes.get(1).getId(), ConstraintSet.TOP,
                                    R.id.chosenGroupTextView, ConstraintSet.BOTTOM, 16);
                        else if(checkBoxes.indexOf(checkBox) == checkBoxes.size() - 1)
                            set.connect(R.id.deleteCheckedButton, ConstraintSet.TOP,
                                    checkBoxes.get(checkBoxes.indexOf(checkBox) - 1).getId(), ConstraintSet.BOTTOM,16);
                        scrollView.removeView(checkBox);
                        set.applyTo(scrollView);
                        checkBoxes.remove(checkBox);
                    }
                }
            }
        });

        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDeleteChecked.setEnabled(false);
                btnDeleteAll.setEnabled(false);
                btnCreate.setEnabled(false);
                for(CheckBox checkBox : checkBoxes)
                    scrollView.removeView(checkBox);
                checkBoxes.clear();
                ConstraintSet set = new ConstraintSet();
                set.clone(scrollView);
                set.connect(R.id.deleteCheckedButton, ConstraintSet.TOP,
                        R.id.chosenGroupTextView, ConstraintSet.BOTTOM, 16);
                set.applyTo(scrollView);
                loadSpinnerData();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Event> es = handler.getAllEvents();
                boolean status = true;
                for(Event e : es)
                    if(eventNameET.getText().toString().equals(e.getEventName()))
                        status = false;
                if(!eventNameET.getText().toString().isEmpty() && status && !eventDescET.getText().toString().isEmpty()) {
                    handler = new DBHandler(getApplicationContext());
                    Group g = new Group(eventNameET.getText().toString() + " Group");
                    handler.addHandler(g);
                    Group group = handler.findHandlerGroup(eventNameET.getText().toString() + " Group");
                    GroupLeader gL = new GroupLeader(initID, group.getGroupID());
                    handler.addHandler(gL);
                    Event e = new Event(eventNameET.getText().toString(), eventDescET.getText().toString());
                    handler.addHandler(e);
                    Event event = handler.findHandlerEvent(eventNameET.getText().toString());
                    EventGroup eventGroup = new EventGroup(event.getEventID(), group.getGroupID());
                    handler.addHandler(eventGroup);
                    ArrayList<Integer> ids = new ArrayList<>();
                    for (CheckBox checkBox : checkBoxes) {
                        String groupName = checkBox.getText().toString();
                        Group grp = handler.findHandlerGroup(groupName);
                        EventGroup eG = new EventGroup(event.getEventID(), grp.getGroupID());
                        handler.addHandler(eG);
                        ArrayList<GroupMember> gMs = handler.findHandlerGroupMembers(grp.getGroupID());
                        for (GroupMember gM : gMs) {
                            int memID = gM.getMemberID();
                            if(!ids.contains(memID)) {
                                ids.add(memID);
                                GroupMember groupM = new GroupMember(group.getGroupID(), memID);
                                handler.addHandler(groupM);
                            }
                        }
                    }
                    handler.close();
                    Intent i = new Intent(CreateEventActivity.this, HomeScreenActivity.class);
                    i.putExtra("initID", initID);
                    startActivity(i);
                }
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateEventActivity.this, HomeScreenActivity.class);
                i.putExtra("initID", initID);
                startActivity(i);
            }
        });
    }

    private void loadSpinnerData() {
        handler = new DBHandler(getApplicationContext());
        ArrayList<Group> gs = handler.getAllGroups();
        groupNames.clear();
        groupNames.add("Select group");
        for(Group g : gs)
            groupNames.add(g.getGroupName());
        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, groupNames);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pickGroup.setAdapter(groupAdapter);
        handler.close();
    }

    private void reloadSpinnerData(boolean add, String name) {
        if(add)
            groupNames.add(name);
        else
            groupNames.remove(name);
        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, groupNames);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pickGroup.setAdapter(groupAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(i != 0) {
            if(checkBoxes.size() == 0) {
                btnDeleteAll.setEnabled(true);
                btnDeleteChecked.setEnabled(true);
                btnCreate.setEnabled(true);
            }
            String groupName = adapterView.getItemAtPosition(i).toString();
            reloadSpinnerData(false, groupName);
            CheckBox checkBox = new CheckBox(getApplicationContext());
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams
                    (ConstraintLayout.LayoutParams.WRAP_CONTENT,
                            ConstraintLayout.LayoutParams.WRAP_CONTENT);
            checkBox.setId(View.generateViewId());
            scrollView.addView(checkBox, params);
            checkBox.setText(groupName);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(scrollView);
            constraintSet.connect(checkBox.getId(), ConstraintSet.LEFT,
                    R.id.scrollViewConstraintLayout, ConstraintSet.LEFT, 32);
            constraintSet.clear(R.id.deleteCheckedButton, ConstraintSet.TOP);
            constraintSet.connect(R.id.deleteCheckedButton, ConstraintSet.TOP,
                    checkBox.getId(), ConstraintSet.BOTTOM, 16);
            if (checkBoxes.size() == 0) {
                constraintSet.connect(checkBox.getId(), ConstraintSet.TOP,
                        R.id.chosenGroupTextView, ConstraintSet.BOTTOM, 16);
            } else {
                constraintSet.connect(checkBox.getId(), ConstraintSet.TOP,
                        checkBoxes.get(checkBoxes.size() - 1).getId(), ConstraintSet.BOTTOM, 16);
            }
            constraintSet.applyTo(scrollView);
            checkBoxes.add(checkBox);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // TODO Auto-generated method stub
    }
}

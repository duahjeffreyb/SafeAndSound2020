package com.example.elizabethwhitebaker.safeandsound;

import android.content.Intent;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.util.ArrayList;

public class AddToGroupActivity extends AppCompatActivity {
//    private static final String TAG = "AddToGroupActivity";

    private Spinner chooseGroup, chooseMember;
    private Button btnDeleteChecked, btnDeleteAll, btnAddToGroup;
    private int initID;
    private ConstraintLayout scrollView;
    private DBHandler handler;
    private ArrayList<CheckBox> checkBoxes;
    private ArrayList<String> groupNames;
    private ArrayList<String> memNames;
    private String groupName;
    private ArrayList<GroupMember> gms;
    private ArrayList<Member> ms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mems_to_group);

        String names[] = {};
        //ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
        initID = getIntent().getIntExtra("initID", 0);
        final String name = getIntent().getStringExtra("name");


        checkBoxes = new ArrayList<>();
        groupNames = new ArrayList<>();
        memNames = new ArrayList<>();
        ms = new ArrayList<>();
        gms = new ArrayList<>();

        btnDeleteChecked = findViewById(R.id.deleteCheckedButton);
        btnDeleteAll = findViewById(R.id.deleteAllButton);
        btnAddToGroup = findViewById(R.id.addToGroupButton);
        Button btnGoBack = findViewById(R.id.goBackButton);

        scrollView = findViewById(R.id.scrollViewConstraintLayout);

        chooseGroup = findViewById(R.id.chooseGroupSpinner);
        chooseMember = findViewById(R.id.chooseMemberSpinner);
        loadGroupSpinnerData();

        chooseMember.setEnabled(false);
        btnDeleteChecked.setEnabled(false);
        btnDeleteAll.setEnabled(false);
        btnAddToGroup.setEnabled(false);

        chooseGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 0) {
                    groupName = adapterView.getItemAtPosition(i).toString();
                    Group g = handler.findHandlerGroup(groupName);
                    loadMemberSpinnerData(g);
                    if(!checkBoxes.isEmpty()) {
                        for(CheckBox checkBox : checkBoxes)
                            scrollView.removeView(checkBox);
                        checkBoxes.clear();
                    }
                    ConstraintSet set = new ConstraintSet();
                    set.clone(scrollView);
                    set.connect(R.id.deleteAllButton, ConstraintSet.TOP,
                            R.id.chosenMemberTextView, ConstraintSet.BOTTOM, 16);
                    set.applyTo(scrollView);
                    chooseMember.setEnabled(true);
                    btnDeleteAll.setEnabled(false);
                    btnDeleteChecked.setEnabled(false);
                    btnAddToGroup.setEnabled(false);
                } else {
                    chooseMember.setEnabled(false);
                    btnDeleteAll.setEnabled(false);
                    btnDeleteChecked.setEnabled(false);
                    btnAddToGroup.setEnabled(false);
                    if(!checkBoxes.isEmpty()) {
                        for(CheckBox checkBox : checkBoxes)
                            scrollView.removeView(checkBox);
                        checkBoxes.clear();
                        ConstraintSet set = new ConstraintSet();
                        set.clone(scrollView);
                        set.connect(R.id.deleteAllButton, ConstraintSet.TOP,
                                R.id.chosenMemberTextView, ConstraintSet.BOTTOM, 16);
                        set.applyTo(scrollView);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});

        chooseMember.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 0) {
                    btnDeleteAll.setEnabled(true);
                    btnDeleteChecked.setEnabled(true);
                    btnAddToGroup.setEnabled(true);
                    String memberName = adapterView.getItemAtPosition(i).toString();
                    reloadSpinnerData(false, memberName);
                    CheckBox checkBox = new CheckBox(getApplicationContext());
                    ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams
                            (ConstraintLayout.LayoutParams.WRAP_CONTENT,
                                    ConstraintLayout.LayoutParams.WRAP_CONTENT);
                    checkBox.setId(View.generateViewId());
                    scrollView.addView(checkBox, params);
                    checkBox.setText(memberName);
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(scrollView);
                    constraintSet.connect(checkBox.getId(), ConstraintSet.LEFT,
                            R.id.scrollViewConstraintLayout, ConstraintSet.LEFT, 32);
                    constraintSet.clear(R.id.deleteAllButton, ConstraintSet.TOP);
                    constraintSet.connect(R.id.deleteAllButton, ConstraintSet.TOP,
                            checkBox.getId(), ConstraintSet.BOTTOM, 16);
                    if (checkBoxes.size() == 0) {
                        constraintSet.connect(checkBox.getId(), ConstraintSet.TOP,
                                R.id.chosenMemberTextView, ConstraintSet.BOTTOM, 16);
                    } else {
                        constraintSet.connect(checkBox.getId(), ConstraintSet.TOP,
                                checkBoxes.get(checkBoxes.size() - 1).getId(), ConstraintSet.BOTTOM, 16);
                    }
                    constraintSet.applyTo(scrollView);
                    checkBoxes.add(checkBox);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});

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
                            set.connect(R.id.deleteAllButton, ConstraintSet.TOP,
                                    R.id.chosenMemberTextView, ConstraintSet.BOTTOM, 16);
                            btnDeleteChecked.setEnabled(false);
                            btnDeleteAll.setEnabled(false);
                            btnAddToGroup.setEnabled(false);
                        }
                        else if(checkBoxes.indexOf(checkBox) != checkBoxes.size() - 1 && checkBoxes.indexOf(checkBox) != 0)
                            set.connect(checkBoxes.get(checkBoxes.indexOf(checkBox) + 1).getId(), ConstraintSet.TOP,
                                    checkBoxes.get(checkBoxes.indexOf(checkBox) - 1).getId(), ConstraintSet.BOTTOM, 16);
                        else if(checkBoxes.indexOf(checkBox) == 0)
                            set.connect(checkBoxes.get(1).getId(), ConstraintSet.TOP,
                                    R.id.chosenMemberTextView, ConstraintSet.BOTTOM, 16);
                        else if(checkBoxes.indexOf(checkBox) == checkBoxes.size() - 1)
                            set.connect(R.id.deleteAllButton, ConstraintSet.TOP,
                                    checkBoxes.get(checkBoxes.indexOf(checkBox) - 1).getId(), ConstraintSet.BOTTOM, 16);
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
                btnAddToGroup.setEnabled(false);
                for(CheckBox checkBox : checkBoxes)
                    scrollView.removeView(checkBox);
                checkBoxes.clear();
                ConstraintSet set = new ConstraintSet();
                set.clone(scrollView);
                set.connect(R.id.deleteAllButton, ConstraintSet.TOP,
                        R.id.chosenMemberTextView, ConstraintSet.BOTTOM, 16);
                set.applyTo(scrollView);
                handler = new DBHandler(getApplicationContext());
                Group g = handler.findHandlerGroup(groupName);
                loadMemberSpinnerData(g);
                handler.close();
            }
        });

        btnAddToGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler = new DBHandler(getApplicationContext());
                Group g = handler.findHandlerGroup(groupName);
                for(CheckBox checkBox : checkBoxes) {
                    Member m = handler.findHandlerMember(checkBox.getText().toString().substring(0, checkBox.getText().toString().indexOf(" ")),
                            checkBox.getText().toString().substring(checkBox.getText().toString().indexOf(" ") + 1));
                    GroupMember gm = new GroupMember(g.getGroupID(), m.getMemberID());
                    handler.addHandler(gm);
                }
                handler.close();
                Intent i = new Intent(AddToGroupActivity.this, HomeScreenActivity.class);
                i.putExtra("initID", initID);
                i.putExtra("name", name);
                startActivity(i);
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddToGroupActivity.this, HomeScreenActivity.class);
                i.putExtra("initID", initID);
                i.putExtra("name", name);
                startActivity(i);
            }
        });
    }

    private void loadGroupSpinnerData() {
        handler = new DBHandler(this);
        ArrayList<Group> groups = handler.getAllGroups();
        groupNames.clear();
        groupNames.add("Select group");
        for (Group g : groups) {
            Log.i("Group.size", String.valueOf(groups.size()));
            groupNames.add(g.getGroupName());
        }
        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_item, groupNames);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseGroup.setAdapter(groupAdapter);
        handler.close();
    }

    private void loadMemberSpinnerData(Group g) {
        handler = new DBHandler(this);
        gms = handler.findHandlerGroupMembers(g.getGroupID());
        ms = handler.getAllMembers();
        Log.i("gms size", String.valueOf(gms.size()));
        Log.i("ms size", String.valueOf(ms.size()));
        Member[] mems = new Member[ms.size()];
        for(int i = 0; i < ms.size(); i++)
            mems[i] = ms.get(i);
        memNames.clear();
        memNames.add("Select member");
        for(GroupMember gm : gms) {
            Log.i("memberID", String.valueOf(gm.getMemberID()));
            Member m = handler.findHandlerMember(gm.getMemberID());
            //Log.i("test" , handler.findHandlerMember(gm.getMemberID()).getPhoneNumber());
            //Log.i("size", ms.get(0).getFirstName());
            for(int i = 0; i < mems.length; i++) {
                //Log.i("mems memberID", String.valueOf(mems[i].getMemberID()));
                if(m != null) {
                    if (mems[i] == null || mems[i].getMemberID() == m.getMemberID())
                        //potentialMembers.add(m);
                        mems[i] = null;
                }
            }
        }
        for(Member mem : mems) {
            if(mem != null)
            memNames.add(mem.getFirstName() + " " + mem.getLastName());
        }
        ArrayAdapter<String> memberAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_item, memNames);
        memberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseMember.setAdapter(memberAdapter);
        handler.close();
    }

    private void reloadSpinnerData(boolean add, String name) {
        handler = new DBHandler(this);
        if(add)
            memNames.add(name);
        else
            memNames.remove(name);
        ArrayAdapter<String> memberAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, memNames);
        memberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseMember.setAdapter(memberAdapter);
        handler.close();
    }
}

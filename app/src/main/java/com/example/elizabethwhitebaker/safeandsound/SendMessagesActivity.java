package com.example.elizabethwhitebaker.safeandsound;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.SEND_SMS;

public class SendMessagesActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {
//    private static final String TAG = "SendMessagesActivity";
    private static final int REQUEST_SMS = 0;
    private BroadcastReceiver sentStatusReceiver, deliveredStatusReceiver;
    private Spinner groupSpinner;
    private ConstraintLayout scrollView;
    private DBHandler handler;
    private ArrayList<CheckBox> checkBoxes;
    private ArrayList<String> groupNames;
    private String[] phones;
//    private Intents.
    private Button btnDeleteAll, btnDeleteChecked, btnSend;
    private EditText message;
//    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_messages);

//        i = new Intent(SendMessagesActivity.this, StatusReportActivity.class);

        checkBoxes = new ArrayList<>();
        groupNames = new ArrayList<>();

        groupSpinner = findViewById(R.id.groupSpinner);

        btnDeleteChecked = findViewById(R.id.deleteCheckedButton);
        btnDeleteAll = findViewById(R.id.deleteAllButton);
        btnSend = findViewById(R.id.sendButton);
        Button btnGoBack = findViewById(R.id.doneButton);

        message = findViewById(R.id.messageEditText);

        scrollView = findViewById(R.id.scrollViewConstraintLayout);

        groupSpinner.setOnItemSelectedListener(this);
        loadSpinnerData();

        btnDeleteChecked.setEnabled(false);
        btnDeleteAll.setEnabled(false);
        btnSend.setEnabled(false);
        message.setEnabled(false);
        message.setText(R.string.idk);

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(SendMessagesActivity.this, HomeScreenActivity.class);
                back.putExtra("initID", getIntent().getIntExtra("initID", 0));
                startActivity(back);
            }
        });

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
                            message.setEnabled(false);
                            message.setText(R.string.idk);
                            btnSend.setEnabled(false);
                        }
                        else if(checkBoxes.indexOf(checkBox) != checkBoxes.size() - 1 && checkBoxes.indexOf(checkBox) != 0)
                            set.connect(checkBoxes.get(checkBoxes.indexOf(checkBox) + 1).getId(), ConstraintSet.TOP,
                                    checkBoxes.get(checkBoxes.indexOf(checkBox) - 1).getId(), ConstraintSet.BOTTOM, 16);
                        else if(checkBoxes.indexOf(checkBox) == 0)
                            set.connect(checkBoxes.get(1).getId(), ConstraintSet.TOP,
                                    R.id.chosenMembersTextView, ConstraintSet.BOTTOM, 16);
                        else if(checkBoxes.indexOf(checkBox) == checkBoxes.size() - 1)
                            set.connect(R.id.deleteCheckedButton, ConstraintSet.TOP,
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
                message.setEnabled(false);
                message.setText(R.string.idk);
                btnSend.setEnabled(false);
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

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler = new DBHandler(getApplicationContext());
                ArrayList<String> phoneNumbers = new ArrayList<>();
                for (CheckBox checkBox : checkBoxes) {
                    String groupName = checkBox.getText().toString();
                    Group g = handler.findHandlerGroup(groupName);
                    ArrayList<GroupMember> gMembers = handler.findHandlerGroupMembers(g.getGroupID());
                    for (GroupMember gM : gMembers) {
                        int memID = gM.getMemberID();
                        Member m = handler.findHandlerMember(memID);
                        if (!phoneNumbers.contains(m.getPhoneNumber()))
                            phoneNumbers.add(m.getPhoneNumber());
                    }
                }
                handler.close();
                phones = new String[phoneNumbers.size()];
                for (int i = 0; i < phoneNumbers.size(); i++)
                    phones[i] = phoneNumbers.get(i);
                sendSMS();
//                startActivity(i);
                Intent home = new Intent(SendMessagesActivity.this, HomeScreenActivity.class);
                home.putExtra("initID", getIntent().getIntExtra("initID", 0));
                startActivity(home);
            }
        });
    }

    private void sendSMS() {
        message.append(" (Answer \"yes\" or \"no\" and then write comments please)");
        SmsManager sms = SmsManager.getDefault();
        List<String> messages = sms.divideMessage(message.getText().toString());
//        Uri sentMail = Uri.parse("content://sms/sent");
//        sentMail.
//        ArrayList<String> times = new ArrayList<>();
        for (String phone : phones) {
            for (String msg : messages) {
                PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
                PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
                sms.sendTextMessage(phone, null, msg, sentIntent, deliveredIntent);
//                String time = android.content.
            }
        }
//        i.putExtra("times", times);
    }

    public void onResume() {
        super.onResume();
        sentStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = "Unknown Error";
                switch(getResultCode()) {
                    case Activity.RESULT_OK:
                        s = "Message Sent Successfully!!";
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        s = "Generic Failure Error";
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        s = "Error : No Service Available";
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        s = "Error : Null PDU";
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        s = "Error : Radio is off";
                        break;
                    default:
                        break;
                }
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
        };
        deliveredStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = "Message Not Delivered";
                switch(getResultCode()) {
                    case Activity.RESULT_OK:
                        s = "Message Delivered Successfully";
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
        };
        registerReceiver(sentStatusReceiver, new IntentFilter("SMS_SENT"));
        registerReceiver(deliveredStatusReceiver, new IntentFilter("SMS_DELIVERED"));
//        data.add(sentStatusReceiver.getResultData());
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(sentStatusReceiver);
        unregisterReceiver(deliveredStatusReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access sms", Toast.LENGTH_SHORT).show();
                sendSMS();
            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and sms", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(SEND_SMS)) {
                        requestPermissions(new String[]{SEND_SMS}, REQUEST_SMS);
                    }
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void loadSpinnerData() {
        handler = new DBHandler(getApplicationContext());
        ArrayList<Group> groups = handler.getAllGroups();
        groupNames.clear();
        groupNames.add("Select group");
        for(Group g : groups)
            groupNames.add(g.getGroupName());
        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, groupNames);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(groupAdapter);
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
        groupSpinner.setAdapter(groupAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(i != 0) {
            if(checkBoxes.size() == 0) {
                btnDeleteAll.setEnabled(true);
                btnDeleteChecked.setEnabled(true);
                message.setEnabled(true);
                message.setText("");
                btnSend.setEnabled(true);
            }
            String groupName = adapterView.getItemAtPosition(i).toString();
            reloadSpinnerData(false, groupName);
            CheckBox checkBox = new CheckBox(this);
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
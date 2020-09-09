package com.example.elizabethwhitebaker.safeandsound;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class StatusReportActivity extends AppCompatActivity {
//    private static final String TAG = "StatusReportActivity";
    private int initID;
    private ArrayList<TextView> names;
    private ArrayList<TextView> statuses;
    protected ArrayList<TextView> responses;
    private ArrayList<ImageView> stopLights;
    private ConstraintLayout scrollView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_report);

        initID = getIntent().getIntExtra("initID", 0);
        String eventName = getIntent().getStringExtra("event");
        String eventGroup = eventName + " Group";

        TextView statusReport = findViewById(R.id.statusReportTextView);
        TextView groupName = findViewById(R.id.groupNameTextView);

        scrollView = findViewById(R.id.scrollViewConstraintLayout);

        Button btnGoBack = findViewById(R.id.doneButton);

        responses = new ArrayList<>();
        statuses = new ArrayList<>();
        stopLights = new ArrayList<>();
        names = new ArrayList<>();

        HashMap<String, String> data = readSMS();

        DBHandler handler = new DBHandler(getApplicationContext());

        Group g = handler.findHandlerGroup(eventGroup);
        ArrayList<GroupMember> gms = handler.findHandlerGroupMembers(g.getGroupID());
        ArrayList<Member> ms = new ArrayList<>();
        ArrayList<String> numbersCompare = new ArrayList<>();

        for(int i = 0; i < gms.size(); i++) {
            Member m = handler.findHandlerMember(gms.get(i).getMemberID());
            numbersCompare.add(m.getPhoneNumber());
            ms.add(m);
        }

        HashMap<String, String> refinedData = comparePhones(data, numbersCompare);

        statusReport.setText("Status Report for " + eventName);
        groupName.setText("Responders from " + eventGroup + ":");

        String[] phones = new String[refinedData.keySet().size()];
        int i = 0;
        for(String phone : refinedData.keySet()) {
            phones[i] = phone;
            i++;
        }
        for(Member m : ms) {
            int count = 0;
            while(count < refinedData.keySet().size()) {
                String phone = phones[count];
                String msg = refinedData.get(phone);
                if (m.getPhoneNumber().equals(phone)) {
                    if (msg.toLowerCase().startsWith("yes")) {
                        m.setReplyStatus("GOOD");
                        m.setResponse(msg);
                        createResponseTextView(m.getResponse().substring(4));
                        createReplyStatusTextView("GOOD");
                        createStopLightImageView("Green");
                    } else if (msg.toLowerCase().startsWith("no")) {
                        m.setReplyStatus("BAD");
                        m.setResponse(msg);
                        createResponseTextView(m.getResponse().substring(3));
                        createReplyStatusTextView("BAD");
                        createStopLightImageView("Red");
                    } else {
                        m.setReplyStatus("UNK");
                        m.setResponse(msg);
                        createResponseTextView(m.getResponse());
                        createReplyStatusTextView("UNK");
                        createStopLightImageView("Yellow");
                    }
                    count = refinedData.keySet().size();
                } else if (count == refinedData.keySet().size() - 1) {
                    createResponseTextView(m.getResponse());
                    createReplyStatusTextView("UNK");
                    createStopLightImageView("Yellow");
                }
                count++;
            }
            createMemberNameTextView(m.getFirstName() + " " + m.getLastName());
        }

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StatusReportActivity.this, HomeScreenActivity.class);
                i.putExtra("initID", initID);
                startActivity(i);
            }
        });
    }

    private HashMap<String, String> readSMS() {
//        HashMap<String, String> phoneMsgData = new HashMap<>();
//        HashMap<String, Integer> phoneTimeData = new HashMap<>();
        HashMap<String, String> data = new HashMap<>();
        Cursor c = getContentResolver().query(Uri.parse("content://sms/inbox"),
                null, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                String phone = "";
                String msg = "";
//                int timeSent = 0;
                boolean isSame = false;
                for (int i = 0; i < c.getColumnCount(); i++) {
                    if (c.getColumnName(i).equalsIgnoreCase("address"))
                        phone = c.getString(i);
                    else if (c.getColumnName(i).equalsIgnoreCase("body"))
                        msg = c.getString(i);
//                    else if (c.getColumnName(i).equalsIgnoreCase("date_sent"))
//                        timeSent = c.getInt(i);
                }
                if (data.containsKey(phone))
                    isSame = true;
                if (!phone.isEmpty() && !msg.isEmpty() && !isSame) {
                    data.put(phone, msg);
//                    if (timeSent > 0)
//                        phoneTimeData.put(phone, timeSent);
                }
            } while(c.moveToNext());
        }
//        HashMap<String, String> data = new HashMap<>();
//        c = getContentResolver().query(Uri.parse("content://sms/sent"),
//                null, null, null, null);
//        if(c != null && c.moveToFirst()) {
//            do {
//                StringBuilder msg = new StringBuilder();
//                for(int i = 0; i < c.getColumnCount(); i++)
//                    msg.append(c.getColumnName(i)).append(":").append(c.getString(i)).append(" ");
//                System.out.println(msg.toString());
//            }while(c.moveToNext());
//        }
//        if(c != null && c.moveToFirst()) {
//            do {
//                Integer timeSent = 0;
//                boolean isSame = false;
//                if (isFirst) {
//                    c.moveToFirst();
//                    isFirst = false;
//                }
//                for (int i = 0; i < c.getColumnCount(); i++) {
//                    if (c.getColumnName(i).equalsIgnoreCase("address"))
//                        phone = c.getString(i);
//                    else if (c.getColumnName(i).equalsIgnoreCase("body"))
//                        msg = c.getString(i);
//                }
//                if (phoneMsgData.containsKey(phone))
//                    isSame = true;
//                if (!phone.isEmpty() && !msg.isEmpty() && !isSame) {
//                    phoneMsgData.put(phone, msg);
//                    phones.add(phone);
//                }
//            } while(c.moveToNext());
//        }
        if(c != null)
            c.close();
        return data;
    }

    private HashMap<String, String> comparePhones(HashMap<String, String> data, ArrayList<String> numCompares) {
        for (String num : data.keySet()) {
            int count = 0;
            while (count < numCompares.size()) {
                if (num.contains(numCompares.get(count)))
                    count = numCompares.size();
                else if (count == numCompares.size() - 1)
                    data.remove(num);
                count++;
            }
        }
        return data;
    }

    private void createResponseTextView(String memberResponse) {
        TextView response = new TextView(getApplicationContext());
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams
                (ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);
        response.setId(View.generateViewId());
        scrollView.addView(response, params);
        response.setText(memberResponse);
        ConstraintSet set = new ConstraintSet();
        set.clone(scrollView);
        set.connect(response.getId(), ConstraintSet.LEFT,
                R.id.statusReportGuideline, ConstraintSet.RIGHT, 8);
        set.connect(response.getId(), ConstraintSet.RIGHT,
                R.id.scrollViewConstraintLayout, ConstraintSet.RIGHT, 8);
        set.setHorizontalBias(response.getId(), 0.0f);
        set.connect(response.getId(), ConstraintSet.BOTTOM,
                R.id.scrollViewConstraintLayout, ConstraintSet.BOTTOM, 16);
        if(responses.size() == 0) {
            set.clear(R.id.groupNameTextView, ConstraintSet.BOTTOM);
            set.connect(response.getId(), ConstraintSet.TOP,
                    R.id.groupNameTextView, ConstraintSet.BOTTOM, 16);
        } else {
            set.clear(responses.get(responses.size() - 1).getId(), ConstraintSet.BOTTOM);
            set.connect(response.getId(), ConstraintSet.TOP,
                    responses.get(responses.size() - 1).getId(), ConstraintSet.BOTTOM, 16);
        }
        set.applyTo(scrollView);
        responses.add(response);
    }

    private void createReplyStatusTextView(String status) {
        TextView replyStat = new TextView(getApplicationContext());
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams
                (ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);
        replyStat.setId(View.generateViewId());
        scrollView.addView(replyStat, params);
        if(status.equalsIgnoreCase("good"))
            replyStat.setText(R.string.g);
        else if(status.equalsIgnoreCase("bad"))
            replyStat.setText(R.string.b);
        else
            replyStat.setText(R.string.u);
        ConstraintSet set = new ConstraintSet();
        set.clone(scrollView);
        set.connect(replyStat.getId(), ConstraintSet.BASELINE,
                responses.get(statuses.size()).getId(), ConstraintSet.BASELINE, 0);
        set.connect(replyStat.getId(), ConstraintSet.RIGHT,
                R.id.statusReportGuideline, ConstraintSet.LEFT, 16);
        set.applyTo(scrollView);
        statuses.add(replyStat);
    }

    private void createStopLightImageView(String color) {
        ImageView stopLight = new ImageView(getApplicationContext());
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams
                (ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);
        stopLight.setId(View.generateViewId());
        scrollView.addView(stopLight, params);
        if(color.equalsIgnoreCase("green"))
            stopLight.setImageResource(R.drawable.ic_green_dot);
        else if (color.equalsIgnoreCase("red"))
            stopLight.setImageResource(R.drawable.ic_red_dot);
        else
            stopLight.setImageResource(R.drawable.ic_yellow_dot);
        ConstraintSet set = new ConstraintSet();
        set.clone(scrollView);
        set.connect(stopLight.getId(), ConstraintSet.TOP,
                statuses.get(stopLights.size()).getId(), ConstraintSet.TOP, 0);
        set.connect(stopLight.getId(), ConstraintSet.BOTTOM,
                statuses.get(stopLights.size()).getId(), ConstraintSet.BOTTOM, 0);
        if(color.equalsIgnoreCase("green"))
            set.connect(stopLight.getId(), ConstraintSet.RIGHT,
                    statuses.get(stopLights.size()).getId(), ConstraintSet.LEFT, 8);
        else
            set.connect(stopLight.getId(), ConstraintSet.RIGHT,
                    statuses.get(stopLights.size()).getId(), ConstraintSet.LEFT, 48);
        set.applyTo(scrollView);
        stopLights.add(stopLight);
    }

    private void createMemberNameTextView(String memberName) {
        TextView name = new TextView(getApplicationContext());
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams
                (ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);
        name.setId(View.generateViewId());
        scrollView.addView(name, params);
        name.setText(memberName);
        ConstraintSet set = new ConstraintSet();
        set.clone(scrollView);
        set.connect(name.getId(), ConstraintSet.LEFT,
                R.id.scrollViewConstraintLayout, ConstraintSet.LEFT, 16);
        set.connect(name.getId(), ConstraintSet.BASELINE,
                statuses.get(names.size()).getId(), ConstraintSet.BASELINE, 0);
        name.setMaxWidth(300);
        set.applyTo(scrollView);
        names.add(name);
    }
}

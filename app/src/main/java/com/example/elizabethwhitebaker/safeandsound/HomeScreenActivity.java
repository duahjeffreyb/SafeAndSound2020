package com.example.elizabethwhitebaker.safeandsound;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import static android.Manifest.permission.READ_CONTACTS;

public class HomeScreenActivity extends AppCompatActivity {
//    private static final String TAG = "HomeScreenActivity";

    private int initID;
    private static final int CONTACTS = 1234;
    private GoogleSignInClient mGoogleSignInClient;
    private ArrayList<Member> contacts = new ArrayList<>();
    private DBHandler handler;
    private ArrayList<Group> groups = new ArrayList<>();
    private ArrayList<Member> currentMembers = new ArrayList<>();
    private ArrayList<GroupMember> gmems = new ArrayList<>();


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org);
        DBHandler handler = new DBHandler(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                getContactList();
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {

                    Toast.makeText(this, "Contact permission is needed to communicate with others through the app", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{READ_CONTACTS}, CONTACTS);

            }
        }



        initID = getIntent().getIntExtra("initID", 0);
        final String name = getIntent().getStringExtra("name");
        final String user = getIntent().getStringExtra("user");
        final String pass = getIntent().getStringExtra("pass");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Button btnBuildGroup = findViewById(R.id.buildGroupButton);
        Button btnAddToGroup = findViewById(R.id.addToGroupButton);
        Button btnRemoveFromGroup = findViewById(R.id.removeFromGroupButton);
        Button btnSendMsgs = findViewById(R.id.sendMsgsButton);
        Button btnSignOut = findViewById(R.id.signOutButton);
        Button btnCheckEvent = findViewById(R.id.checkEventButton);
        Button btnCreateEvent = findViewById(R.id.createEventButton);
        Button btnProfile = findViewById(R.id.profile_button);
        Button btnDeleteGroup = findViewById(R.id.delete_a_group);

        TextView welcome = findViewById(R.id.welcomeTextView);
        btnSendMsgs.setEnabled(false);
        btnCreateEvent.setEnabled(false);
        btnCheckEvent.setEnabled(false);
        btnAddToGroup.setEnabled(false);
        btnRemoveFromGroup.setEnabled(false);

        ArrayList<Group> groups = handler.getAllGroups();
        ArrayList<Event> events = handler.getAllEvents();
        ArrayList<Member> members = handler.getAllMembers();


        if (members.size() == 1) {
            //handler.addHandler(new Member("Elizabeth", "Baker", "+13366181185"));
            //handler.addHandler(new Member("Tyler", "Hall", "+19102741577"));
            //handler.addHandler(new Member("Codie", "Nichols", "+19105201955"));
        }

        welcome.setText(getString(R.string.welcome_org_text) + ", " + name + "!");


        handler.close();

        if (groups.size() > 0) {
            btnCreateEvent.setEnabled(true);
            btnAddToGroup.setEnabled(true);
            btnRemoveFromGroup.setEnabled(true);
            btnSendMsgs.setEnabled(true);
        }

        if (events.size() > 0) {
            btnCheckEvent.setEnabled(true);
        }

        btnBuildGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeScreenActivity.this, BuildGroupActivity.class);
                i.putExtra("initID", initID);
                i.putExtra("name", name);
                startActivity(i);
            }
        });

        btnCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeScreenActivity.this, CreateEventActivity.class);
                i.putExtra("initID", initID);
                i.putExtra("name", name);
                startActivity(i);
            }
        });

        btnAddToGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeScreenActivity.this, AddToGroupActivity.class);
                i.putExtra("initID", initID);
                i.putExtra("name", name);
                startActivity(i);
            }
        });

        btnRemoveFromGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeScreenActivity.this, RemoveFromGroupActivity.class);
                i.putExtra("initID", initID);
                i.putExtra("name", name);
                startActivity(i);
            }
        });

        btnCheckEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeScreenActivity.this, CheckEventActivity.class);
                i.putExtra("initID", initID);
                i.putExtra("name", name);
                startActivity(i);
            }
        });

        btnSendMsgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeScreenActivity.this, SendMessagesActivity.class);
                i.putExtra("initID", initID);
                i.putExtra("name", name);
                startActivity(i);
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AlertDialog.Builder(HomeScreenActivity.this)
                        .setTitle("Sign Out")
                        .setMessage("Are you sure you want to sign out")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (v.getId()) {
                                    case R.id.signOutButton:
                                        signOut();
                                        break;
                                }
                            }
                        })
                        .setNegativeButton("no", null).show();
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeScreenActivity.this, SettingsActivity.class);
                i.putExtra("initID", initID);
                i.putExtra("name", name);
                i.putExtra("user", user);
                i.putExtra("pass", pass);
                startActivity(i);
            }
        });

        btnDeleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeScreenActivity.this, DeleteGroupActivity.class);
                i.putExtra("initID", initID);
                i.putExtra("name", name);
                i.putExtra("user", user);
                i.putExtra("pass", pass);
                startActivity(i);
            }
        });

    }


    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent(HomeScreenActivity.this, MainActivity.class));
            }
        });
    }

    private void getContactList() {
        /*if(contacts.size() > 0){
            return;
        }*/
        //new Thread(new Runnable() {
        //  @Override
        //public void run() {
        Cursor c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        handler = new DBHandler(getApplicationContext());
        ArrayList<Member> mem = handler.getAllMembers();
        groups = handler.getAllGroups();
        Log.i("1memSize", String.valueOf(mem.size()));
        //contacts = mem;
        if (c != null) {
            while (c.moveToNext()) {
                Log.i("count", String.valueOf(c.getCount()));
                String fullName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_ALTERNATIVE));
                //Log.i("name", fullName);
                String phone = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[\\(\\)\\s\\-]", "");
                String last = fullName.substring(0, fullName.indexOf(","));
                String first = fullName.substring(fullName.indexOf(",") + 2);
                Member member = new Member(first, last, phone);
                if (contacts.size() != 0 && !contacts.contains(member)) {
                    Log.i("newname", member.getFirstName());
                    contacts.add(member);

                }
                if (contacts.size() == 0) {
                    contacts.add(member);
                }
            }
            c.close();
            Log.i("contacts length", String.valueOf(contacts.size()));
        }
        if (mem.size() == 0 && contacts.size() != 0) {
            for (Member contact : contacts) {
                handler.addHandler(contact);
            }
            return;
        }

        for (int i = 0; i < contacts.size(); i++) {
            for (int j = 0; j < mem.size(); j++) {
                if (mem.get(j).getPhoneNumber().equals(contacts.get(i).getPhoneNumber())) {
                    break;
                }
                if (j == mem.size() - 1 && mem.get(j).getPhoneNumber() != contacts.get(i).getPhoneNumber()) {
                    handler.addHandler(contacts.get(i));
                    break;
                }
            }
        }

        checkDeleted(mem);

        mem.retainAll(contacts);
        Log.i("groupsize", String.valueOf(groups.size()));
        gmems = handler.getAllGroupMembers();
        for(GroupMember gm: gmems){
            Member member = handler.findHandlerMember(gm.getMemberID());
            if(member == null){
                handler.deleteHandler(gm.getGroupMemberID(), "GroupMembers");
            }
        }

        for (Group group : groups) {
            ArrayList<GroupMember> groupMembers = handler.findHandlerGroupMembers(group.getGroupID());
            Log.i("GMSize", String.valueOf(groupMembers.size()));
                if(groupMembers.size() == 0) {
                    Log.i("messsage", "deleting groups");
                    handler.deleteHandler(group.getGroupID(), "Groups");
                }
        }
        Log.i("memSize", String.valueOf(mem.size()));

        //}
        //}).start();

    }

    private void checkDeleted(ArrayList<Member> mem) {
        if(contacts.size() == 0){
            for(Member member: mem){
                handler.deleteHandler(member.getMemberID(), "Members");
            }
        }
        for (int i = 0; i < mem.size(); i++) {
            for (int j = 0; j < contacts.size(); j++) {
                if (mem.get(i).getPhoneNumber().equals(contacts.get(j).getPhoneNumber())) {
                    break;
                }
                if (j == contacts.size() - 1 && !mem.get(i).getPhoneNumber().equals(contacts.get(j).getPhoneNumber())) {
                    for (Group group : groups) {
                        if (handler.findHandlerGroupMember(group.getGroupID(), mem.get(i).getMemberID()) != null) {
                            GroupMember groupMember = handler.findHandlerGroupMember(group.getGroupID(), mem.get(i).getMemberID());
                            handler.deleteHandler(groupMember.getMemberID(), "GroupMembers");
                        }
                    }
                    handler.deleteHandler(mem.get(i).getMemberID(), "Members");
                    break;
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CONTACTS) {
            if(grantResults.length == 0){
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContactList();
            } else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
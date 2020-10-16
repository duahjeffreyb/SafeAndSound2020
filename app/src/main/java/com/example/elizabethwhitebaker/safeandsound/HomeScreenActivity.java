package com.example.elizabethwhitebaker.safeandsound;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class HomeScreenActivity extends AppCompatActivity {
//    private static final String TAG = "HomeScreenActivity";

    private int initID;
    private GoogleSignInClient mGoogleSignInClient;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org);
        DBHandler handler = new DBHandler(this);

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

        /*if(members.size() == 0) {
            handler.addHandler(new Member("Elizabeth", "Baker", "+13366181185"));
            handler.addHandler(new Member("Tyler", "Hall", "+19102741577"));
            handler.addHandler(new Member("Codie", "Nichols", "+19105201955"));
        }*/

        welcome.setText(getString(R.string.welcome_org_text) + ", " + name + "!");


        handler.close();

        if(groups.size() > 0) {
            btnCreateEvent.setEnabled(true);
            btnAddToGroup.setEnabled(true);
            btnRemoveFromGroup.setEnabled(true);
            btnSendMsgs.setEnabled(true);
        }

        if(events.size() > 0) {
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
                                switch (v.getId()){
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
}

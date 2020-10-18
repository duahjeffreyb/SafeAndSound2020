package com.example.elizabethwhitebaker.safeandsound;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import static android.Manifest.permission.READ_SMS;
//import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.SEND_SMS;
//import static android.Manifest.permission.READ_CONTACTS;
import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {
    //private static final String TAG = "MainActivity";

    private static final int REQUEST_SMS = 1;
    private static final int REQUEST_READ_SMS = 123;
    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton googleSignIn;
    private DBHandler db;
    private Initiator initiator;


    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        db = new DBHandler(getApplicationContext());
        Button btnSignIn = findViewById(R.id.signin_button);
        Button btnSignUp = findViewById(R.id.signup_button);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignIn = findViewById(R.id.google_sign_in);
        googleSignIn.setSize(SignInButton.SIZE_STANDARD);
        googleSignIn.setEnabled(false);

        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId()){
                    case R.id.google_sign_in:
                        signIn();
                        break;
                }
            }
        });


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }
        });

        if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasSendSMSPermission = checkSelfPermission(SEND_SMS);
            int hasReadSMSPermission = checkSelfPermission(READ_SMS);
            if(hasSendSMSPermission != PERMISSION_GRANTED &&
            hasReadSMSPermission != PERMISSION_GRANTED) {
                if(!shouldShowRequestPermissionRationale(SEND_SMS) &&
                !shouldShowRequestPermissionRationale(READ_SMS)) {
                    showMessageOKCancel("You need to allow access to Read and Send SMS",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    requestPermissions(new String[]{SEND_SMS, READ_SMS}, REQUEST_SMS);
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{SEND_SMS, READ_SMS}, REQUEST_SMS);
            } else if(hasSendSMSPermission != PERMISSION_GRANTED) {
                if(!shouldShowRequestPermissionRationale(SEND_SMS)) {
                    showMessageOKCancel("You need to allow access to Send SMS",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    requestPermissions(new String[]{SEND_SMS}, REQUEST_SMS);
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{SEND_SMS}, REQUEST_SMS);
            } else if(hasReadSMSPermission != PERMISSION_GRANTED) {
                if(!shouldShowRequestPermissionRationale(READ_SMS)) {
                    showMessageOKCancel("You need to allow access to Read SMS",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    requestPermissions(new String[]{READ_SMS}, REQUEST_READ_SMS);
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{READ_SMS}, REQUEST_READ_SMS);
            }
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            updateUI(account);
        } catch (ApiException e) {
            e.printStackTrace();
            Log.w("tag", "signInResult: failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_SMS) {
            if (grantResults[0] == PERMISSION_DENIED && grantResults.length == 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    if (shouldShowRequestPermissionRationale(SEND_SMS))
                        showMessageOKCancel("You need to allow access " +
                                        "to the \"send sms\" permission for the " +
                                        "app to function properly",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        requestPermissions(new String[]{SEND_SMS}, REQUEST_SMS);
                                    }
                                });
            } else if (grantResults.length == 2 &&
                    (grantResults[0] == PERMISSION_DENIED ||
                            grantResults[1] == PERMISSION_DENIED)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    if (shouldShowRequestPermissionRationale(SEND_SMS) ||
                            shouldShowRequestPermissionRationale(READ_SMS))
                        showMessageOKCancel("You need to allow access " +
                                        "to both the permissions for the " +
                                        "app to function properly",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        requestPermissions(new String[]{SEND_SMS, READ_SMS}, REQUEST_SMS);
                                    }
                                });
            }
        } else if(requestCode == REQUEST_READ_SMS) {
            if (grantResults[0] == PERMISSION_DENIED)
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    if(shouldShowRequestPermissionRationale(READ_SMS))
                        showMessageOKCancel("You need to allow access " +
                                        "to the \"read sms\" permission for the " +
                                        "app to function properly",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        requestPermissions(new String[]{READ_SMS}, REQUEST_READ_SMS);
                                    }
                                });
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void updateUI(GoogleSignInAccount account) {
        if(account == null){
            googleSignIn.setEnabled(true);
        }else{
            initiator.setFirstName(account.getGivenName());
            initiator.setLastName(account.getFamilyName());
            initiator.setPicturePath(account.getPhotoUrl().getPath());
            db.addHandler(initiator);
            Intent i = new Intent(MainActivity.this, HomeScreenActivity.class);
            i.putExtra("name", account.getGivenName());
            startActivity(i);
        }
    }
}

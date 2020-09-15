// Done
package com.example.elizabethwhitebaker.safeandsound;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignInActivity extends AppCompatActivity {
    DBHandler handler;
    Button btnSignIn;
    EditText Username, Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        handler = new DBHandler(this);

        Username = findViewById(R.id.usernameEditText);
        Password = findViewById(R.id.passwordEditText);

        btnSignIn = findViewById(R.id.signInSignInButton);
        Button btnCancel = findViewById(R.id.cancelSignInButton);

        //Sign In for Testing
        /*int initID = 1;
        Intent i = new Intent(SignInActivity.this, HomeScreenActivity.class);
        i.putExtra("initID", initID);
        startActivity(i);*/

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = Username.getText().toString();
                String pass = Password.getText().toString();
                try {
                    Initiator initiator = handler.findHandler(user, pass);
                    handler.close();
                    Intent i = new Intent(SignInActivity.this, HomeScreenActivity.class);
                    i.putExtra("initID", initiator.getInitiatorID());
                    startActivity(i);
                } catch(Exception e) {
                    AlertDialog a = new AlertDialog.Builder(btnSignIn.getContext()).create();
                    a.setTitle("Incorrect Username or Password");
                    a.setMessage("Please provide the correct account credentials.");
                    a.show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, MainActivity.class));
            }
        });
    }
}

// Done
package com.example.elizabethwhitebaker.safeandsound;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
//    private static final String TAG = "SignUpActivity";
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int TAKE_PHOTO = 2;
    private ImageView initImageView;
    private Button btnSignUp;
    private String first, last, user, path, phone, pass;
    private DBHandler handler;
    private EditText FirstName, LastName, Username, PhoneNumber, Password, ConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        handler = new DBHandler(this);

        //EditTexts
        FirstName = findViewById(R.id.firstNameEditText);
        LastName = findViewById(R.id.lastNameEditText);
        Username = findViewById(R.id.usernameEditText);
        PhoneNumber = findViewById(R.id.phoneEditText);
        Password = findViewById(R.id.passwordEditText);
        ConfirmPassword = findViewById(R.id.confirmPasswordEditText);

        //TextView
//        TextView or = findViewById(R.id.orTextView);

        //ImageView
        initImageView = findViewById(R.id.initImageView);

        //Buttons
        btnSignUp = findViewById(R.id.signUpButton);
        Button btnCancel = findViewById(R.id.cancelButton);
        Button btnPicture = findViewById(R.id.photoButton);
        Button btnTakePhoto = findViewById(R.id.takePhotoButton);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            }
        });
        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.putExtra("Uri", MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(i, "Select Photo"), RESULT_LOAD_IMAGE);
            }
        });
        if(hasCamera() && hasDefaultCameraApp()) {
            btnTakePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(i, TAKE_PHOTO);
                }
            });
        } else {
            btnTakePhoto.setEnabled(false);
            btnTakePhoto.setVisibility(View.GONE);
//            or.setVisibility(View.GONE);
            ConstraintSet set = new ConstraintSet();
            ConstraintLayout layout = findViewById(R.id.scrollViewConstraintLayout);
            set.clone(layout);
            set.clear(R.id.photoButton, ConstraintSet.RIGHT);
            set.connect(R.id.photoButton, ConstraintSet.RIGHT, R.id.scrollViewConstraintLayout, ConstraintSet.RIGHT, 0);
            set.connect(R.id.photoButton, ConstraintSet.LEFT, R.id.scrollViewConstraintLayout, ConstraintSet.LEFT, 0);
            set.applyTo(layout);
        }

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                first = FirstName.getText().toString();
                last = LastName.getText().toString();
                user = Username.getText().toString();
                phone = PhoneNumber.getText().toString();
                pass = Password.getText().toString();
                String confPass = ConfirmPassword.getText().toString();
                if (!first.equals("") && !last.equals("") && !user.equals("")
                        && initImageView.getVisibility() == View.VISIBLE && !path.equals("")
                        && !phone.equals("") && !pass.equals("") && !confPass.equals("") && pass.equals(confPass)) {
                    Initiator initiator = new Initiator(first, last, user, path, phone, pass);
                    handler.addHandler(initiator);
                    initiator = handler.findHandler(user, pass);
                    handler.close();
                    if (initiator != null) {
                        Intent i = new Intent(SignUpActivity.this, HomeScreenActivity.class);
                        i.putExtra("initID", initiator.getInitiatorID());
                        i.putExtra("user", initiator.getUsername());
                        i.putExtra("pass", initiator.getPassword());
                        startActivity(i);
                    } else {
                        AlertDialog a = new AlertDialog.Builder(btnSignUp.getContext()).create();
                        a.setTitle("No");
                        a.setMessage("No");
                        a.show();
                    }
                } else {
                    AlertDialog a = new AlertDialog.Builder(btnSignUp.getContext()).create();
                    a.setTitle("Invalid Information");
                    a.setMessage("Please make sure you've filled out all the fields");
                    a.show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Bitmap b;
            try {
                b = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                    path = Objects.requireNonNull(data.getData()).getPath();
                initImageView.setVisibility(View.VISIBLE);
                initImageView.setImageBitmap(b);
            } catch(IOException e) {
                e.printStackTrace();
            } catch(NullPointerException npe) {
                npe.printStackTrace();
            }

        } else if(requestCode == TAKE_PHOTO && resultCode == RESULT_OK && data != null) {
            if(data.getExtras() != null) {
                Bitmap b = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                if(b != null) {
                    b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String dir = getFilesDir().getAbsolutePath();
                    path = dir + System.currentTimeMillis() + ".jpg";
                    File newFile = new File(path);
                    try {
                        //noinspection ResultOfMethodCallIgnored
                        newFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    FileOutputStream ofile;
                    try {
                        ofile = new FileOutputStream(newFile);
                        ofile.write(bytes.toByteArray());
                        ofile.close();
                    } catch (FileNotFoundException fnfe) {
                        fnfe.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    initImageView.setVisibility(View.VISIBLE);
                    initImageView.setImageBitmap(b);
                }
            }
        }
    }

    private boolean hasCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private boolean hasDefaultCameraApp() {
        final PackageManager packageManager = getPackageManager();
        final Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> list = packageManager.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}

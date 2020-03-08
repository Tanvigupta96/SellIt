package com.example.carolx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginUPActivity extends AppCompatActivity {
    private EditText login_email, login_password;
    private Button sign_in;
    private TextView sign_up, forget;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private static final String TAG = "";
    private FirebaseAuth.AuthStateListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_u_p);


        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginUPActivity.this, ProfileActivity.class));
            finish();
        }


        progressBar = findViewById(R.id.progressbar);
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        sign_in = findViewById(R.id.sign_in);
        sign_up = findViewById(R.id.sign_up);
        forget = findViewById(R.id.forget);


        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginUPActivity.this, ResetPasswordActivity.class));

            }
        });


        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginUPActivity.this, SignUpActivity.class));

            }
        });

        auth = FirebaseAuth.getInstance();


        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = login_email.getText().toString();
                String Password = login_password.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginUPActivity.this, "Please enter email Id", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(Password)) {
                    Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
                    return;

                }
                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, Password).addOnCompleteListener(LoginUPActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            Intent intent = new Intent(LoginUPActivity.this, ProfileActivity.class);
                            // intent.putExtra("emailId",email);
                            startActivity(intent);
                            finish();


                        } else {
                            Log.d(TAG, "signInWithEmail:Failed");
                            Toast.makeText(LoginUPActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });


        listener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };


    }


    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(listener);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (listener != null) {
            auth.removeAuthStateListener(listener);
        }


    }
}

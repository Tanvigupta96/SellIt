package com.example.carolx;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {
    private Button login_userPass;
    private Button login_gmail;
    private Button login_phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialiseFields();

        login_userPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLogin = new Intent(LoginActivity.this,LoginUPActivity.class);
                startActivity(intentLogin);

            }
        });

        login_gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        login_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(LoginActivity.this,CallLoginActivity.class);
                startActivity(callIntent);

            }
        });
    }

    private void initialiseFields() {
        login_userPass = findViewById(R.id.login_userPass);
        login_gmail = findViewById(R.id.login_Google);
        login_phone  = findViewById(R.id.login_Phone);


    }
}

package com.example.carolx;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;

public class MobileLoginActivity extends AppCompatActivity {
    private CountryCodePicker ccp;
    private Button Continue;
    private EditText inputPhone;
    private String PhoneNumber = " ";
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_login);


        inputPhone = findViewById(R.id.phone_number_input);
        ccp = findViewById(R.id.ccp);
        Continue = findViewById(R.id.buttonContinue);



        ccp.registerCarrierNumberEditText(inputPhone);


        Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PhoneNumber = ccp.getFullNumberWithPlus();
                if (TextUtils.isEmpty(PhoneNumber) || PhoneNumber.length() < 10) {
                    inputPhone.setText("Valid number is required");
                    inputPhone.requestFocus();
                    return;
                } else {
                    saveData();
                    Intent intent = new Intent(MobileLoginActivity.this, VerifyPhoneActivity.class);
                    intent.putExtra("phoneNumber", PhoneNumber);
                    startActivity(intent);
                }


            }
        });


    }

    private void saveData() {
        sharedPreferences = this.getSharedPreferences("mySharedPreference",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("PhoneNumber",PhoneNumber);
        editor.apply();


    }

    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}

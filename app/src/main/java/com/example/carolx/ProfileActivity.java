package com.example.carolx;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;
import com.vikktorn.picker.City;
import com.vikktorn.picker.Country;
import com.vikktorn.picker.CountryPicker;
import com.vikktorn.picker.OnCountryPickerListener;
import com.vikktorn.picker.OnStatePickerListener;
import com.vikktorn.picker.State;
import com.vikktorn.picker.StatePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements OnStatePickerListener, OnCountryPickerListener {
    private FirebaseAuth mAuth;
    public static int countryID, stateID;
    private EditText pickCountry, pickStateButton, mobileEditText;
    private TextInputLayout cityInputLayout, address1InputLayout, address2InputLayout, pinInputLayout;

    // Pickers
    private CountryPicker countryPicker;
    private StatePicker statePicker;
    // arrays of state object
    public static List<State> stateObject;
    private FrameLayout Profile_image;
    private static final int CAMERA_REQUEST = 1888, GALLERY = 1;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private CircleImageView group_photo;
    private static final String IMAGE_DIRECTORY = "/YourDirectName";
    private SwitchCompat switchCompat;
    private CollapsingToolbarLayout toolbarLayout;
    private Toolbar toolbar;
    private LinearLayout Parent;

    private Button buttonSave;
    private AppBarLayout appBarLayout;
    private static final String DEFAULT = "";
    private String PhoneNumber;
    private CountryCodePicker ccp;


    private String mobileNumber = " ", AddressLine1 = " ", AddressLine2 = " ", country = " ", state = " ", city = " ", pin = " ", mode = "REGULAR";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);


        SharedPreferences sharedPreferences = getSharedPreferences("mySharedPreference", MODE_PRIVATE);
        PhoneNumber = sharedPreferences.getString("PhoneNumber", DEFAULT);

        //Get all the values in String



        Log.d("mobile", mobileNumber);


        mAuth = FirebaseAuth.getInstance();
        Profile_image = findViewById(R.id.image_frame);
        group_photo = findViewById(R.id.group_image);
        mobileEditText = findViewById(R.id.mobile);
        address1InputLayout = findViewById(R.id.address1);
        address2InputLayout = findViewById(R.id.address2);
        switchCompat = findViewById(R.id.switchButton);
        buttonSave = findViewById(R.id.Buttonsave);
        pinInputLayout = findViewById(R.id.pinCode);
        cityInputLayout = findViewById(R.id.city);
        toolbar = findViewById(R.id.toolbar);
        Parent = findViewById(R.id.Parent);
        ccp = findViewById(R.id.ccp);




        if(PhoneNumber != DEFAULT){
        mobileEditText.setText(PhoneNumber);
        mobileEditText.setEnabled(false);
        ccp.setVisibility(View.GONE);
        }




        final androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarLayout = findViewById(R.id.toolbar_layout);
        getSupportActionBar().setTitle(" ");


        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    toolbarLayout.setTitle(" ");
                    isShow = true;
                } else if (isShow) {
                    toolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work

                    isShow = false;
                }

            }
        });


        initView();

        // get state from assets JSON
        try {
            getStateJson();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // get City from assets JSON
        try {
            getCityJson();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // initialize country picker
        countryPicker = new CountryPicker.Builder().with(this).listener(this).build();

        // initialize listeners
        setListener();
        setCountryListener();

        Profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestProfileImage();
            }
        });

        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchCompat.isChecked()) {

                }
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserDeatils();
            }
        });
    }


    public void initView() {
        //Buttons
        pickStateButton = findViewById(R.id.pickState);
        //set state picker invisible
        pickCountry = findViewById(R.id.pickCountry);
        // set city picker invisible
        // Text Views


        // initiate state object, parser, and arrays
        stateObject = new ArrayList<>();
    }

    // SET STATE LISTENER
    private void setListener() {
        pickStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statePicker.showDialog(getSupportFragmentManager());
            }
        });
    }


    //SET COUNTRY LISTENER
    private void setCountryListener() {
        pickCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryPicker.showDialog(getSupportFragmentManager());
            }
        });
    }


    // ON SELECTED COUNTRY ADD STATES TO PICKER
    @Override
    public void onSelectCountry(Country country) {
        // get country name and country ID
        pickCountry.setText(country.getName());
        countryID = country.getCountryId();
        statePicker.equalStateObject.clear();

        //set state name text view and state pick button invisible
        // set text on main view


        // GET STATES OF SELECTED COUNTRY
        for (int i = 0; i < stateObject.size(); i++) {
            // init state picker
            statePicker = new StatePicker.Builder().with(this).listener(this).build();
            State stateData = new State();
            if (stateObject.get(i).getCountryId() == countryID) {

                stateData.setStateId(stateObject.get(i).getStateId());
                stateData.setStateName(stateObject.get(i).getStateName());
                stateData.setCountryId(stateObject.get(i).getCountryId());
                stateData.setFlag(country.getFlag());
                statePicker.equalStateObject.add(stateData);
            }
        }
    }


    // ON SELECTED STATE ADD CITY TO PICKER
    @Override
    public void onSelectState(State state) {


        pickStateButton.setText(state.getStateName());
        stateID = state.getStateId();

    }


    // GET STATE FROM ASSETS JSON
    public void getStateJson() throws JSONException {
        String json = null;
        try {
            InputStream inputStream = getAssets().open("states.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject(json);
        JSONArray events = jsonObject.getJSONArray("states");
        for (int j = 0; j < events.length(); j++) {
            JSONObject cit = events.getJSONObject(j);
            State stateData = new State();

            stateData.setStateId(Integer.parseInt(cit.getString("id")));
            stateData.setStateName(cit.getString("name"));
            stateData.setCountryId(Integer.parseInt(cit.getString("country_id")));
            stateObject.add(stateData);
        }
    }


    // GET CITY FROM ASSETS JSON
    public void getCityJson() throws JSONException {
        String json = null;
        try {
            InputStream inputStream = getAssets().open("cities.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }


        JSONObject jsonObject = new JSONObject(json);
        JSONArray events = jsonObject.getJSONArray("cities");
        for (int j = 0; j < events.length(); j++) {
            JSONObject cit = events.getJSONObject(j);
            City cityData = new City();

            cityData.setCityId(Integer.parseInt(cit.getString("id")));
            cityData.setCityName(cit.getString("name"));
            cityData.setStateId(Integer.parseInt(cit.getString("state_id")));
        }
    }

    private void RequestProfileImage() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();


    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void takePhotoFromCamera() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }


    private void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            group_photo.setImageBitmap(photo);

        } else if (requestCode == GALLERY && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(getApplicationContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
                    group_photo.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }


        }
    }

    private String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        if (!wallpaperDirectory.exists()) {  // have the object build the directory structure, if needed.
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::---&gt;" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";


    }

    private boolean validateMobile() {
        String val = mobileEditText.getText().toString();
        String numberOnly = "[789]{1}[0-9]{9}";
        if (val.isEmpty()) {
            mobileEditText.setError("Field can't be Empty");
            return false;
        } else if (val.length() < 10 || val.length() > 10) {
            mobileEditText.setError("Mobile number should be of 10 digits only!");
            return false;


        } else if (!val.matches(numberOnly)) {
            mobileEditText.setError("Only Number Input is allowed");
            return false;

        } else {

            mobileEditText.setError(null);
            return true;
        }

    }

    private boolean validateAddress() {
        String val = address1InputLayout.getEditText().getText().toString();
        if (val.isEmpty()) {
            address1InputLayout.setError("Field can't be Empty");
            return false;
        } else {
            address1InputLayout.setError(null);
            return true;
        }

    }

    private boolean validateCountry() {
        String val = pickCountry.getText().toString();
        if (val.isEmpty()) {
            pickCountry.setError("Field can't be empty");
            return false;
        } else {
            pickCountry.setError(null);
            return true;
        }
    }

    private boolean validateState() {
        String val = pickStateButton.getText().toString();
        if (val.isEmpty()) {
            pickStateButton.setError("Field can't be empty");
            return false;
        } else {
            pickStateButton.setError(null);
            return true;
        }
    }

    private boolean validateCity() {
        String val = cityInputLayout.getEditText().getText().toString();
        if (val.isEmpty()) {
            cityInputLayout.setError("Field can't be empty");
            return false;
        } else {
            cityInputLayout.setError(null);
            return true;
        }


    }

    private boolean validatePin() {
        String val = pinInputLayout.getEditText().getText().toString();
        if (val.isEmpty()) {
            pinInputLayout.setError("Field can't be empty");
            return false;
        } else {
            pinInputLayout.setError(null);
            return true;
        }

    }


    private void saveUserDeatils() {
        if (!validateMobile() || !validateAddress() || !validateCountry() || !validateState() || !validateCity() || !validatePin()) {
            return;

        }

        mobileNumber = mobileEditText.getText().toString();
        AddressLine1 = address1InputLayout.getEditText().getText().toString();
        AddressLine2 = address2InputLayout.getEditText().getText().toString();
        country = pickCountry.getText().toString();
        state = pickStateButton.getText().toString();
        Log.d("country", country);
        Log.d("state", state);

        city = cityInputLayout.getEditText().getText().toString();
        pin = pinInputLayout.getEditText().getText().toString();

        if (switchCompat.isChecked())
            mode = "PREMIUM";

        else
            mode = "REGULAR";
        Log.d("Mode", mode);

        Intent MainScreenIntent = new Intent(ProfileActivity.this, MainScreenActivity.class);
        startActivity(MainScreenIntent);

    }

}

package com.example.carolx.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.carolx.Interface.CategoryItemClickListener;
import com.example.carolx.Interface.SelectedCategoryInterface;
import com.example.carolx.R;
import com.example.carolx.adapter.CategoryAdapter;
import com.example.carolx.adapter.CategoryViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements OnStatePickerListener, OnCountryPickerListener {
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    private StorageReference UserProfileImagesRef;
    private String TAG = "profile_activity";
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
    private Switch switchButton;
    private CollapsingToolbarLayout toolbarLayout;
    private Toolbar toolbar;
    private LinearLayout Parent;

    private Button buttonSave;
    private AppBarLayout appBarLayout;
    private static final String DEFAULT = "";
    private String PhoneNumber;
    private CountryCodePicker ccp;
    private String currentUserId;
    private RecyclerView categoryRecycleView;

    private CategoryAdapter categoryAdapter;
    private ProgressDialog loadingBar;





    private ArrayList<String> selectedCategories;
    private TextView chooseCategoryTextView;
    private List<Integer> positionsArray;
    private ArrayList<String> Categories;


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
        currentUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");


        Profile_image = findViewById(R.id.image_frame);
        group_photo = findViewById(R.id.userProfileImage);
        mobileEditText = findViewById(R.id.mobile);
        address1InputLayout = findViewById(R.id.address1);
        address2InputLayout = findViewById(R.id.address2);
        switchButton = findViewById(R.id.switch1);
        buttonSave = findViewById(R.id.Buttonsave);
        pinInputLayout = findViewById(R.id.pinCode);
        cityInputLayout = findViewById(R.id.city);
        toolbar = findViewById(R.id.toolbar);
        Parent = findViewById(R.id.Parent);
        ccp = findViewById(R.id.ccp);
        chooseCategoryTextView = findViewById(R.id.chooseCategoryTextView);
        loadingBar = new ProgressDialog(this,R.style.MyAlertDialogStyle);


        categoryRecycleView = findViewById(R.id.categoryRecycleView);
        ccp.registerCarrierNumberEditText(mobileEditText);


       /* if (PhoneNumber != DEFAULT) {
            mobileEditText.setText(PhoneNumber);
            mobileEditText.setEnabled(false);
            ccp.setVisibility(View.GONE);
        }*/


        final androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarLayout = findViewById(R.id.toolbar_layout);
        getSupportActionBar().setTitle(" ");


        appBarLayout = findViewById(R.id.app_bar);

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
                    toolbarLayout.setTitle(" ");//careful there should a space between double quote otherwise it wont work

                    isShow = false;
                }

            }
        });


        Categories = new ArrayList<String>();
        Categories.add("Cars");
        Categories.add("Furniture");
        Categories.add("Fashion & Beauty");
        Categories.add("Mobiles");
        Categories.add("Bikes");
        Categories.add("Books");
        Categories.add("Sports");
        Categories.add("Electronics & Appliances");

        selectedCategories = new ArrayList<>();
        positionsArray = new ArrayList<>();


        initCategoryAdapter(selectedCategories);

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


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserDeatils();
            }
        });


        retrieveUserInfo();
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
            loadingBar.setTitle("set Profile Image");
            loadingBar.setMessage("please wait, your profile image is being uploaded");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(getApplicationContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
                    group_photo.setImageBitmap(bitmap);



                    final StorageReference filePath = UserProfileImagesRef.child(currentUserId + ".jpg");
                    filePath.putFile(contentURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {


                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this, "Profile picture updated successfully...", Toast.LENGTH_SHORT).show();
                                filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        final String downloadUrl = task.getResult().toString();
                                        rootRef.child("Users").child(currentUserId).child("images")
                                                .setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ProfileActivity.this, "images in database added successfully...", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();

                                                } else {
                                                    String message = task.getException().toString();
                                                    Toast.makeText(ProfileActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();

                                                }
                                            }
                                        });

                                    }
                                });

                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(ProfileActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }

                        }
                    });


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
        String val = ccp.getFullNumberWithPlus();
        Log.d("val", val);
        String numberOnly = "[789]{1}[0-9]{9}";
        if (val.isEmpty()) {
            mobileEditText.setError("Field can't be Empty");
            return false;
        } else if (val.length() < 13) {
            mobileEditText.setError("Mobile number should be of 10 digits only!");
            return false;


        } /*else if (!val.matches(numberOnly)) {
            mobileEditText.setError("Only Number Input is allowed");
            return false;

        } */ else {

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

    private boolean validateCategories() {
        if (selectedCategories.size() == 0) {
            chooseCategoryTextView.setError("Select atleast 1 Category");
            return false;

        } else {
            chooseCategoryTextView.setError(null);
            return true;
        }
    }


    private void saveUserDeatils() {
        if (!validateMobile() || !validateAddress() || !validateCountry() || !validateState() || !validateCity() || !validatePin()) {
            return;

        }

        mobileNumber = ccp.getFullNumberWithPlus();
        AddressLine1 = address1InputLayout.getEditText().getText().toString();
        AddressLine2 = address2InputLayout.getEditText().getText().toString();
        country = pickCountry.getText().toString();
        state = pickStateButton.getText().toString();
        Log.d("country", country);
        Log.d("state", state);
        city = cityInputLayout.getEditText().getText().toString();
        pin = pinInputLayout.getEditText().getText().toString();

        if (switchButton.isChecked())
            mode = "PREMIUM";

        else
            mode = "REGULAR";
        Log.d("Mode", mode);

        Log.d("selected", selectedCategories.toString());
        Log.d("selected pos", String.valueOf(positionsArray));


        HashMap<String, Object> profileMap = new HashMap<>();
        profileMap.put("uid", currentUserId);
        profileMap.put("mobile", mobileNumber);
        profileMap.put("address1", AddressLine1);
        profileMap.put("address2", AddressLine2);
        profileMap.put("country", country);
        profileMap.put("state", state);
        profileMap.put("city", city);
        profileMap.put("pin", pin);
        profileMap.put("mode", mode);
        profileMap.put("selectedCategories", selectedCategories);
        rootRef.child("Users").child(currentUserId).updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    sendUserToMainActivity();
                    Toast.makeText(ProfileActivity.this, "profile updated successfully...", Toast.LENGTH_SHORT).show();
                } else {

                    String message = task.getException().toString();
                    Toast.makeText(ProfileActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();


                }

            }
        });

        Log.d("map", profileMap.toString());


        Intent MainScreenIntent = new Intent(ProfileActivity.this, MainScreenActivity.class);
        startActivity(MainScreenIntent);

    }


    private void initCategoryAdapter(final ArrayList<String> fetchedCatsArrayList) {
        Log.d(TAG, "initCategoryAdapter: ");
        Log.d(TAG, "SelectedCategoryArraySize " + selectedCategories.size());
        categoryAdapter = new CategoryAdapter(this, Categories, new CategoryItemClickListener() {
            @Override
            public void selectedViewHolder(Button categoryButton, int position) {
                if (selectedCategories.contains(Categories.get(position))) {
                    Log.d(TAG, "Category Removed: " + position);
                    selectedCategories.remove(Categories.get(position));
                    categoryButton.setBackgroundResource(R.drawable.btn_category);
                    categoryButton.setTextColor(getResources().getColor(R.color.colorPrimary));

                } else {
                    Log.d(TAG, "Category Added: " + position);
                    selectedCategories.add(Categories.get(position));
                    positionsArray.add(position);
                    categoryButton.setTextColor(getResources().getColor(R.color.white));
                    categoryButton.setBackgroundResource(R.drawable.btn_radius_category);
                }
                Log.d(TAG, "SelectedCategoryArraySize " + selectedCategories.size());
            }

            @Override
            public void fetchedCategoriesIndex(Button categoryButton, int index) {
                if (fetchedCatsArrayList.size() != 0) {
                    for (String item : fetchedCatsArrayList) {
                        int selectedPosition = Categories.indexOf(item);
                        Log.d(TAG, "inIterator!");
                        Log.d(TAG, "index: " + index);
                        if (index == selectedPosition) {
                            categoryButton.setTextColor(getResources().getColor(R.color.white));
                            categoryButton.setBackgroundResource(R.drawable.btn_radius_category);
                        }
                    }
                }
            }
        });


        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL);
        categoryRecycleView.setLayoutManager(staggeredGridLayoutManager);
        categoryRecycleView.setAdapter(categoryAdapter);
        selectedCategories.clear();
    }


    private void retrieveUserInfo() {
        if (PhoneNumber != DEFAULT) {
            mobileEditText.setText(PhoneNumber);
            mobileEditText.setEnabled(false);
            ccp.setVisibility(View.GONE);
        }
        rootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("mobile") && dataSnapshot.hasChild("address1") && dataSnapshot.hasChild("address2") && dataSnapshot.hasChild("country") && dataSnapshot.hasChild("state") && dataSnapshot.hasChild("city") && dataSnapshot.hasChild("pin") && dataSnapshot.hasChild("mode")  && dataSnapshot.hasChild("selectedCategories") || dataSnapshot.hasChild("image"))) {

                    String mobile = dataSnapshot.child("mobile").getValue().toString();
                    String address1 = dataSnapshot.child("address1").getValue().toString();
                    String address2 = dataSnapshot.child("address2").getValue().toString();
                    String country = dataSnapshot.child("country").getValue().toString();
                    String state = dataSnapshot.child("state").getValue().toString();
                    String city = dataSnapshot.child("city").getValue().toString();
                    String pin = dataSnapshot.child("pin").getValue().toString();
                    String mode = dataSnapshot.child("mode").getValue().toString();


                    try {
                        ArrayList<String> fetchedCategories;

                        fetchedCategories = (ArrayList<String>) dataSnapshot.child("selectedCategories").getValue();

                        Log.d(TAG, fetchedCategories.toString());
                        initCategoryAdapter(fetchedCategories);
                    } catch (Exception e) {
                        Log.d(TAG, "Fetched Categories ArrayList: " + e.toString());
                    }


                    if (dataSnapshot.hasChild("images")) {
                        String retrieveProfileImage = dataSnapshot.child("images").getValue().toString();
                        Picasso.get().load(retrieveProfileImage).into(group_photo);

                    }


                    mobileEditText.setText(mobile);
                    address1InputLayout.getEditText().setText(address1);
                    address2InputLayout.getEditText().setText(address2);
                    pickCountry.setText(country);
                    pickStateButton.setText(state);
                    cityInputLayout.getEditText().setText(city);
                    pinInputLayout.getEditText().setText(pin);
                    switchButton.setChecked(mode.equals("PREMIUM"));


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(ProfileActivity.this, MainScreenActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


}
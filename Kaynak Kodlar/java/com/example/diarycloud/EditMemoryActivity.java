package com.example.diarycloud;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditMemoryActivity extends AppCompatActivity {
    double lat, lng;
    private String deviceDate, currentLocation, currentAddress;
    private Uri addedMediaUri;
    private ImageView addedMedia;
    private TextView memLocation;
    private ActivityResultLauncher<Intent> mediaSelectLauncher;
    private ActivityResultLauncher<String[]> getLocLauncher;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        EditText memTitle = (EditText) findViewById(R.id.edit_memory_title);
        EditText memMain = (EditText) findViewById(R.id.edit_main);
        Spinner memFeelingSpinner = (Spinner) findViewById(R.id.edit_feeling);
        TextView memDate = (TextView) findViewById(R.id.edit_date);
        memLocation = (TextView) findViewById(R.id.edit_location);
        Button editMediaButton = (Button) findViewById(R.id.edit_media_button);
        Button getLocButton = (Button) findViewById(R.id.edit_curr_loc_button);
        Button cancelButton = (Button) findViewById(R.id.edit_cancel_button);
        Button saveButton = (Button) findViewById(R.id.save_button);
        addedMedia = (ImageView) findViewById(R.id.edit_added_media);

        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);

        Memory memory = ListDiaryActivity.memList.get(position);
        memTitle.setText(memory.getMemTitle());
        memMain.setText(memory.getMemMain());
        addedMediaUri = Uri.parse(memory.getMemMedia());
        addedMedia.setImageURI(addedMediaUri);
        currentLocation = memory.getMemLocation();
        currentAddress = memory.getMemAddress();
        memLocation.setText(currentAddress);

        deviceDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        memDate.setText(deviceDate);

        mediaSelectLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK){
                            if(result.getData() != null){
                                addedMediaUri = result.getData().getData();
                                addedMedia.setImageURI(addedMediaUri);
                            }
                        }
                    }
                });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                    Boolean coarseLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_COARSE_LOCATION,false);
                    if (coarseLocationGranted != null && coarseLocationGranted) {
                        getLoc();
                    } else {
                        getLocLauncher.launch(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION});
                    }
                });

        // Emoji Selector
        Resources r = getResources();
        int[] feelingsUnicode = r.getIntArray(R.array.feelings);
        List<String> feelings = new ArrayList<>();
        for(int feel : feelingsUnicode){
            feelings.add(new String(Character.toChars(feel)));
        }
        ArrayAdapter<String> feelingsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, feelings);
        feelingsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        memFeelingSpinner.setAdapter(feelingsAdapter);

        int i = 0;
        while((memory.getMemFeeling().equals(feelings.get(i)))&&(i < feelings.size())) i++;
        memFeelingSpinner.setSelection(i);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                addMemory(
                        memTitle.getText().toString(),
                        memFeelingSpinner.getSelectedItem().toString(),
                        memMain.getText().toString(),
                        deviceDate,
                        currentLocation,
                        currentAddress,
                        addedMediaUri.toString());
                deleteMemory(memory);
                Toast.makeText(EditMemoryActivity.this,
                        "Memory '" + memTitle.getText().toString() + "' edited successfully.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        getLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                getLoc();
            }
        });

        editMediaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent mediaSelect = new Intent(Intent.ACTION_OPEN_DOCUMENT,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                mediaSelectLauncher.launch(mediaSelect);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                finish();
            }
        });

    }

    public void addMemory(String memTitle, String memFeeling, String memMain, String memDate, String memLocation, String memAddress, String addedMediaUri){
        DataBaseHelper dbHelper = new DataBaseHelper(this);
        int dbSize = dbHelper.getDatabaseSize();

        Memory memory = new Memory(
                dbSize+1,
                memTitle,
                memFeeling,
                memMain,
                memDate,
                memLocation,
                memAddress,
                addedMediaUri);
        dbHelper.addMemory(memory);
    }

    public void deleteMemory(Memory memory){
        DataBaseHelper dbHelper = new DataBaseHelper(this);
        dbHelper.deleteMemory(memory);
        finish();
    }

    public void getLoc(){
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this,
                            new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        lat = location.getLatitude();
                                        lng = location.getLongitude();
                                        currentLocation = lat + ", " + lng;;
                                        currentAddress = latLngToAddress(lat, lng);
                                        memLocation.setText(currentAddress);
                                    }
                                }
                            });
        } else {
            getLocLauncher.launch(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION});
        }
    }

    public String latLngToAddress(double lat, double lng){
        String address = lat + ", " + lng;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocation(lat, lng, 1);
            address = addressList.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }
}
package com.example.diarycloud;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;

public class DetailMemoryActivity extends AppCompatActivity {
    private ActivityResultLauncher<String[]> readWritePerms;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        Memory memory = ListDiaryActivity.memList.get(position);

        ImageView memMedia = (ImageView) findViewById(R.id.detail_cover);
        TextView memFeeling = (TextView) findViewById(R.id.detail_feeling);
        TextView memDate = (TextView) findViewById(R.id.detail_date);
        TextView memLocation = (TextView) findViewById(R.id.detail_location);
        TextView memMain = (TextView) findViewById(R.id.detail_main);
        Button memDeleteButton = (Button) findViewById(R.id.detail_delete_button);
        Button memEditButton = (Button) findViewById(R.id.detail_edit_button);
        Button memShareButton = (Button) findViewById(R.id.detail_share_button);

        memMedia.setImageURI(Uri.parse(memory.getMemMedia()));
        getSupportActionBar().setTitle(memory.getMemTitle());
        memFeeling.setText(memory.getMemFeeling());
        memDate.setText(memory.getMemDate());
        memLocation.setText(memory.getMemAddress());
        memMain.setText(memory.getMemMain());

        memDeleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(counter % 2 == 0){
                    Toast.makeText(DetailMemoryActivity.this, "Press delete again to delete memory...", Toast.LENGTH_SHORT).show();
                    counter++;
                } else {
                    deleteMemory(memory);
                }
            }
        });

        memEditButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent editPage = new Intent(DetailMemoryActivity.this, EditMemoryActivity.class);
                editPage.putExtra("position", position);
                startActivity(editPage);
                finish();
            }
        });

        memShareButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

            }
        });

        readWritePerms = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                    Boolean readPermGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_COARSE_LOCATION,false);
                    Boolean writePermGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_COARSE_LOCATION,false);

                    if (readPermGranted != null && writePermGranted != null
                            && readPermGranted && writePermGranted) {
                        Toast.makeText(this, "Couldn't implement that yet.", Toast.LENGTH_SHORT).show();
                        // convertPdf();
                    } else {
                        readWritePerms.launch(new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE});
                    }
                });
    }

    public void deleteMemory(Memory memory){
        DataBaseHelper dbHelper = new DataBaseHelper(this);
        dbHelper.deleteMemory(memory);
        Toast.makeText(this, "Memory '" + memory.getMemTitle() + "' deleted successfully.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.convert_to_pdf) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Couldn't implement that yet.", Toast.LENGTH_SHORT).show();
                // convertPdf();
            } else {
                readWritePerms.launch(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE});
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void convertPdf(){
    }
}
package com.example.diarycloud;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ListDiaryActivity extends AppCompatActivity implements ListDiaryAdapter.OnItemListener {
    public RecyclerView diary;
    public static ArrayList<Memory> memList = new ArrayList<>();

    private int pageHeight = 1120, pageWidth = 792;

    private ActivityResultLauncher<String[]> readWritePerms;
    private ListDiaryAdapter diaryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        diary = (RecyclerView) findViewById(R.id.diary);
        Button addMemoryButton = (Button) findViewById(R.id.add_memory_button);

        diaryAdapter = new ListDiaryAdapter(memList, this);
        diary.setLayoutManager(new LinearLayoutManager(this));
        diary.setAdapter(diaryAdapter);

        DataBaseHelper dbHelper = new DataBaseHelper(getApplicationContext());
        dbHelper.readDB();

        readWritePerms = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                    Boolean readPermGranted = result.getOrDefault(
                            Manifest.permission.READ_EXTERNAL_STORAGE,false);
                    Boolean writePermGranted = result.getOrDefault(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,false);

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

        addMemoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent addInt = new Intent(getApplicationContext(), AddMemoryActivity.class);
                startActivity(addInt);
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        diaryAdapter.notifyDataSetChanged();
    }
    @Override
    public void onRestart() {
        super.onRestart();
        diaryAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.show_on_map) {
            Intent showMap = new Intent(this, MapActivity.class);
            startActivity(showMap);
        }
        if (id == R.id.convert_all_to_pdf) {
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
        if (id == R.id.lock) {
            Intent setPassword = new Intent(ListDiaryActivity.this, SetPasswordActivity.class);
            startActivity(setPassword);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position){
        Intent detailInt = new Intent(this, DetailMemoryActivity.class);
        detailInt.putExtra("position", position);
        startActivity(detailInt);
    }

    // Couldn't implement saving .pdf
    public void convertPdf(){
        PdfDocument diaryPdf = new PdfDocument();

        for (Memory memory : memList){
            Uri uri = Uri.parse(memory.getMemMedia());
            String wholeID = DocumentsContract.getDocumentId(uri);
            String id = wholeID.split(":")[1];
            String[] column = { MediaStore.Images.Media.DATA };
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = getContentResolver().
                    query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{ id }, null);

            String filePath = "";
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }

            cursor.close();
            File file = new File(filePath);
            Bitmap bmp = BitmapFactory.decodeFile(file.getPath());
            Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, 140, 140, false);

            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
            PdfDocument.Page page = diaryPdf.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            Paint media = new Paint();
            Paint title = new Paint();
            Paint body = new Paint();

            canvas.drawBitmap(scaledBmp, 56, 40, media);

            title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            title.setTextSize(18);
            title.setColor(ContextCompat.getColor(this, R.color.orange_700));

            body.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            body.setTextSize(16);
            body.setColor(ContextCompat.getColor(this, R.color.black));



            diaryPdf.finishPage(page);
        }
        File pdf = new File(Environment.getExternalStorageDirectory(), "Diary.pdf");
        try {
            diaryPdf.writeTo(new FileOutputStream(pdf));
            Toast.makeText(this, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        diaryPdf.close();

        Intent openFile = new Intent(Intent.ACTION_VIEW);
        openFile.setDataAndType(Uri.fromFile(pdf), "application/pdf");
        openFile.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Intent chooser = Intent.createChooser(openFile, "Open File");

        try {
            startActivity(chooser);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No PDF reader found!", Toast.LENGTH_SHORT).show();
        }
    }
}
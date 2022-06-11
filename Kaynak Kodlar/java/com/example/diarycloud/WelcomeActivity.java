package com.example.diarycloud;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        EditText enterPassword = (EditText) findViewById(R.id.enter_password_field);
        Button submitPassword = (Button) findViewById(R.id.enter_password_button);

        SharedPreferences sharedPreferences = getSharedPreferences("password", 0);
        String password = sharedPreferences.getString("password", "");

        if (password.length() == 0){
            Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show();
            Intent bypassed = new Intent(this, ListDiaryActivity.class);
            startActivity(bypassed);
        }

        submitPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPassword(password, enterPassword.getText().toString());
            }
        });
    }

    public void submitPassword(String password, String passwordFieldText){
        if(password.equals(passwordFieldText)){
            Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show();
            Intent confirmed = new Intent(this, ListDiaryActivity.class);
            startActivity(confirmed);
        } else {
            Toast.makeText(this, "Wrong Password!", Toast.LENGTH_SHORT).show();
        }
    }
}
package com.example.diarycloud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);

        EditText setPasswordField = (EditText) findViewById(R.id.set_password_field);
        Button setPasswordButton = (Button) findViewById(R.id.set_password_button);

        setPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassword(setPasswordField.getText().toString());
                finish();
            }
        });

    }

    public void setPassword(String password){
        SharedPreferences sharedPreferences = getSharedPreferences("password", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("password", password);
        editor.commit();
    }
}
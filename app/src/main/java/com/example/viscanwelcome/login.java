package com.example.viscanwelcome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class login extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText email = findViewById(R.id.EmailAddress);
        EditText pass = findViewById(R.id.Password);
        Button login = findViewById(R.id.buttonLogin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString().equals("admin@gmail.com") && pass.getText().toString().equals("admin"))
                {
                    login.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openHomePage();
                        }
                    });
                }
            }
        });
    }

    public void openHomePage()
    {
     //   Intent intent = new Intent(this, homePage.class);
        Intent intent = new Intent(this, ML.class);
        startActivity(intent);
    }
}
package com.example.viscanwelcome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {
    EditText emailadd,pwd;
    Button login;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailadd = findViewById(R.id.EmailAddress);
        pwd = findViewById(R.id.Password);
        login = findViewById(R.id.buttonLogin);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        String email = emailadd.getText().toString();
        String pass = pwd.getText().toString();

        progressDialog.setMessage("Login up");
        progressDialog.setTitle("Login up");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    openHomePage();
                    Toast.makeText(login.this,"Login Successful",Toast.LENGTH_SHORT);
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(login.this," "+task.getException(),Toast.LENGTH_SHORT);
                }
            }
        });

    }

    public void openHomePage()
    {
     //   Intent intent = new Intent(this, homePage.class);
        Intent intent = new Intent(this, ML.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
package com.example.viscanwelcome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class signUp extends AppCompatActivity {
    private Button loginButton, signup;
    EditText emailadd, pwd, cpwd;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signup = findViewById(R.id.buttonSignUp);
        emailadd = findViewById(R.id.EmailAddress);
        pwd = findViewById(R.id.Password);
        cpwd = findViewById(R.id.confirmPassword);
        loginButton = findViewById(R.id.buttonLogin);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLoginPage();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    perfAuth();
            }
        });

    }

    private void perfAuth() {
    String email = emailadd.getText().toString();
    String pass = pwd.getText().toString();
    String cpass = cpwd.getText().toString();

    if(!(pass.equals(cpass)))
    {
        cpwd.setError("Password does not match");
    }
    else
    {
        progressDialog.setMessage("Signing up");
        progressDialog.setTitle("Sign up");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    openLoginPage();
                    Toast.makeText(signUp.this,"Sign up Successful",Toast.LENGTH_SHORT);
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(signUp.this," "+task.getException(),Toast.LENGTH_SHORT);
                }
            }
        });
    }
    }

//    private void sendUserToNextPage() {
//        Intent intent = new Intent(this, ML.class);
//        startActivity(intent);
//    }

    public void openLoginPage() {
        Intent intent = new Intent(this, login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}


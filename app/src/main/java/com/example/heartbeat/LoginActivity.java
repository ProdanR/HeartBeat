package com.example.heartbeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText email,password;
    private Button logIn,createAccount;

    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();

        email= (EditText) findViewById(R.id.email_field);
        password= (EditText) findViewById(R.id.password_field);

        logIn= (Button) findViewById(R.id.singup_button);
        createAccount= (Button) findViewById(R.id.singupfb_button);
        loadingBar = new ProgressDialog(this);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToRegisterActivity();

            }

        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllowingUserToLogin();

            }
        });

    }
    private void AllowingUserToLogin()
    {
        String email_t= email.getText().toString();
        String password_t= password.getText().toString();

        if (TextUtils.isEmpty(email_t) || TextUtils.isEmpty(password_t) )
        {
            Toast.makeText(this,"Please complete all fields",Toast.LENGTH_SHORT).show();
        }
        else
        {
            mAuth.signInWithEmailAndPassword(email_t, password_t).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(LoginActivity.this, "You are Logged In Successfully", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, "Error occured: "+ message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }

    private void SendUserToRegisterActivity()
    {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);

    }
}
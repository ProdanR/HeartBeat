package com.example.heartbeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText email,password,fullName,username;
    private Button singUp,singUpWithFb;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private String currentUserId;
    private int sw=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth=FirebaseAuth.getInstance();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");

        email= (EditText) findViewById(R.id.email_field);
        password= (EditText) findViewById(R.id.password_field);
        fullName= (EditText) findViewById(R.id.fullname_field);
        username= (EditText) findViewById(R.id.username_field);

        singUp= (Button) findViewById(R.id.singup_button);
        singUpWithFb= (Button) findViewById(R.id.singupfb_button);

        singUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccount();

            }
        });
    }

    private void createNewAccount() {
        final String email_t= email.getText().toString();
        final String password_t= password.getText().toString();
        final String fullName_t= fullName.getText().toString();
        final String username_t= username.getText().toString();


        if (TextUtils.isEmpty(email_t) || TextUtils.isEmpty(password_t) || TextUtils.isEmpty(fullName_t) || TextUtils.isEmpty(username_t))
        {
            Toast.makeText(getApplicationContext(), "Please complete all fields", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mAuth.fetchProvidersForEmail(email.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                            if (!task.getResult().getProviders().isEmpty()) {
                                Toast.makeText(getApplicationContext(), "Email already exists!", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {

                                mAuth.createUserWithEmailAndPassword(email_t,password_t).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()) {

                                            HashMap userMap= new HashMap();
                                            userMap.put("username", username_t);
                                            userMap.put("fullname",fullName_t);

                                            currentUserId=mAuth.getCurrentUser().getUid();
                                            UsersRef.child(currentUserId).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        Toast.makeText(RegisterActivity.this, "Yeyyyy, you have a account", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        }
                                        else {
                                            Toast.makeText(RegisterActivity.this, "error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }
                    });

        }
        }





    }

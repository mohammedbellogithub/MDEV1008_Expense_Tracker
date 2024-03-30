package com.example.mdev1008_expense_tracker;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    EditText emailText, passwordText;
    Button  regBtn;
    TextView loginText;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.edit_text_email);
        passwordText = findViewById(R.id.edit_text_password);
        progressBar = findViewById(R.id.progressBar);
        regBtn = findViewById(R.id.button_register);
        loginText = findViewById(R.id.text_view_login);

        regBtn.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View view){
               progressBar.setVisibility(View.VISIBLE);
               String email, password;
               email = String.valueOf(emailText.getText());
               password = String.valueOf(passwordText.getText());


               if(TextUtils.isEmpty(email)){
                   Toast.makeText(RegisterActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                   return;
               }

               if(TextUtils.isEmpty(password)){
                   Toast.makeText(RegisterActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                   return;
               }

               mAuth.createUserWithEmailAndPassword(email, password)
                       .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               progressBar.setVisibility(View.GONE);
                               if (task.isSuccessful()) {
                                   Toast.makeText(RegisterActivity.this, "Account Created.",
                                           Toast.LENGTH_SHORT).show();
                                   Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                   startActivity(intent);
                                   finish();
                               } else {
                                   Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                           Toast.LENGTH_SHORT).show();
                               }
                           }
                       });
           }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
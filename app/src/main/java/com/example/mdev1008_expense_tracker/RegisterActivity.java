package com.example.mdev1008_expense_tracker;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    EditText emailText, passwordText;
    Button  regBtn;
    TextView loginText;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    // Method executed when the activity starts
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

    // Method executed when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.purple));

        // Set layout for this activity
        setContentView(R.layout.activity_register);
        // Initialize Firebase authentication instance
        mAuth = FirebaseAuth.getInstance();

        // Find views from layout
        emailText = findViewById(R.id.edit_text_email);
        passwordText = findViewById(R.id.edit_text_password);
        progressBar = findViewById(R.id.progressBar);
        regBtn = findViewById(R.id.button_register);
        loginText = findViewById(R.id.text_view_login);

        // Handle click event for register button
        regBtn.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View view){
               // Show progress bar
               progressBar.setVisibility(View.VISIBLE);

               // Get email and password from EditText fields
               String email, password;
               email = String.valueOf(emailText.getText());
               password = String.valueOf(passwordText.getText());

               // Check if email is empty
               if(TextUtils.isEmpty(email)){
                   Toast.makeText(RegisterActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                   return;
               }

               // Check if password is empty
               if(TextUtils.isEmpty(password)){
                   Toast.makeText(RegisterActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                   return;
               }

               // Create user with email and password
               mAuth.createUserWithEmailAndPassword(email, password)
                       .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               // Hide progress bar
                               progressBar.setVisibility(View.GONE);
                               if (task.isSuccessful()) {
                                   // If registration is successful, show a success message and redirect to LoginActivity
                                   Toast.makeText(RegisterActivity.this, "Account Created.",
                                           Toast.LENGTH_SHORT).show();
                                   Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                   startActivity(intent);
                                   finish();
                               } else {
                                   // If registration fails, show an error message
                                   Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                           Toast.LENGTH_SHORT).show();
                               }
                           }
                       });
           }
        });

        // Handle click event for login text (redirect to LoginActivity)
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
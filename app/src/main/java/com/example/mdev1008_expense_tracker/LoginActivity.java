package com.example.mdev1008_expense_tracker;

// Import statements
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

// LoginActivity class
public class LoginActivity extends AppCompatActivity {
    // Member variables
    EditText emailText, passwordText;
    Button loginBtn;
    TextView RegText;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    // onStart method
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

    // onCreate method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.purple));

        // Set content view
        setContentView(R.layout.activity_login);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize Views
        emailText = findViewById(R.id.edit_text_email);
        passwordText = findViewById(R.id.edit_text_password);
        progressBar = findViewById(R.id.progressBar);
        loginBtn = findViewById(R.id.button_login);
        RegText = findViewById(R.id.text_view_signup);

        // Login button click listener
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(emailText.getText());
                password = String.valueOf(passwordText.getText());

                // Validate email and password fields
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(LoginActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Sign in with email and password
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Login Successful.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                   Toast.makeText(LoginActivity.this, "Login failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        // Register text click listener
        RegText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
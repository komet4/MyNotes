package com.topacademy.mynotes;

import android.content.Intent;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    EditText emailEditText, passwordEditText;
    Button loginButton;
    ProgressBar progressBar;
    TextView createAccountButtonTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progress_bar);
        createAccountButtonTextView = findViewById(R.id.create_account_btn_text_view);

        loginButton.setOnClickListener(v -> loginUser());
        createAccountButtonTextView.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this,
                CreateAccountActivity.class)));
    }

    void loginUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        boolean isValidated = validateData(email, password);
        if (!isValidated) return;

        loginAccountInFirebase(email, password);
    }

    void loginAccountInFirebase(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                task -> {
                    changeInProgress(false);
                    if (task.isSuccessful()) {
                        //login is success
                        if (Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified()) {
                            //go to main activity
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                        else {
                            Utility.showToast(LoginActivity.this, "Email not verified. Please verify your email");
                        }
                    } else {
                        //login failed
                        Utility.showToast(LoginActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage());
                    }
                });
    }

    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password) {
        //validate the data that are input by user

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Email is invalid");
            return false;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Password length is invalid");
            return false;
        }
        return true;
    }
}
package com.topacademy.mynotes;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Patterns;
import android.view.View;
import android.widget.*;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class CreateAccountActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText, confirmPasswordEditText;
    Button createAccountButton;
    ProgressBar progressBar;
    TextView loginTextViewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        createAccountButton = findViewById(R.id.create_account_button);
        progressBar = findViewById(R.id.progress_bar);
        loginTextViewButton = findViewById(R.id.login_text_view_button);

        createAccountButton.setOnClickListener(v -> createUser());
        loginTextViewButton.setOnClickListener(v -> finish());
    }

    void createUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        boolean isValidated = validateData(email, password, confirmPassword);
        if (!isValidated) return;

        createAccountInFirebase(email, password);
    }

    void createAccountInFirebase(String email, String password) {
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(CreateAccountActivity.this,
                task -> {
                    changeInProgress(false);
                    if (task.isSuccessful()) {
                        //create user is done
                        Utility.showToast(CreateAccountActivity.this,
                                "Successfully create account. Check email to verify");
                        Objects.requireNonNull(firebaseAuth.getCurrentUser()).sendEmailVerification();
                        firebaseAuth.signOut();
                        finish();
                    } else {
                        // failure
                        Utility.showToast(CreateAccountActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage());
                    }
                });
    }

    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            createAccountButton.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            createAccountButton.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password, String confirmPassword) {
        //validate the data that are input by user

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Email is invalid");
            return false;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Password length is invalid");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Password not matched");
            return false;
        }
        return true;
    }
}

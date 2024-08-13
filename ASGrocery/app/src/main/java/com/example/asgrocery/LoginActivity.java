package com.example.asgrocery;

import static com.example.asgrocery.CommonUtils.PREFERENCES;
import static com.example.asgrocery.CommonUtils.PREF_USERNAME;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asgrocery.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    final static int REGISTER_REQUEST_CODE = 1;
    DataBaseHelper dataBaseHelper;
    private ActivityLoginBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        dataBaseHelper = new DataBaseHelper(LoginActivity.this);

        // Check if the user is already logged in
        if (isLoggedIn()) {
            String userName = CommonUtils.getStringPref(PREF_USERNAME, LoginActivity.this);

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("USERNAME", userName);
            startActivity(intent);
            finish();
        }

        binding.buttonLogin.setOnClickListener(view1 -> {
            CommonUtils.hideKeyboard(this);

            if (isLoginCredentialsValid(view1)) {
                String userName = binding.editTextUsername.getText().toString().trim();
                String password = binding.editTextPassword.getText().toString();
                if (dataBaseHelper.checkUserCredentials(userName, password)) {
                    saveLoginStatus(true);
                    CommonUtils.setStringPref(PREF_USERNAME, userName, LoginActivity.this);
                    Intent homeIntent = new Intent(LoginActivity.this, MainActivity.class);
                    homeIntent.putExtra("USERNAME", userName);
                    startActivity(homeIntent);
                    finish();
                } else {
                    CommonUtils.showMaterialDialog(this, "Username or Password incorrect!", (dialogInterface, i) -> {});
                }
            }
        });

        binding.buttonSignUp.setOnClickListener(view1 -> {
            CommonUtils.hideKeyboard(this);
            Intent registerIntent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivityForResult(registerIntent, REGISTER_REQUEST_CODE);
        });
    }

    protected boolean isLoginCredentialsValid(View view) {
        String userName = binding.editTextUsername.getText().toString();
        String password = binding.editTextPassword.getText().toString();
        if (userName.length() == 0 || password.length() == 0) {
            CommonUtils.showCustomSnackBar(view, getString(R.string.please_enter_username_and_password));
            return false;
        }
        return true;
    }

    // Storing user status in preference.
    private void saveLoginStatus(boolean isLoggedIn) {
        SharedPreferences preferences = this.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply();
    }

    private boolean isLoggedIn() {
        SharedPreferences preferences = this.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        return preferences.getBoolean("isLoggedIn", false);
    }


}
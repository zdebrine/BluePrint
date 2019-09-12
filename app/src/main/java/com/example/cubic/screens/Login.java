package com.example.cubic.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cubic.R;
import com.example.cubic.controllers.AuthController;
import com.google.firebase.auth.FirebaseUser;
import com.segment.analytics.Analytics;
import com.segment.analytics.Traits;

public class Login extends AppCompatActivity {
    private EditText mEmail, mPass;
    private Button mSubmit;

    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Casting
        mEmail = findViewById(R.id.loginEmail);
        mPass = findViewById(R.id.loginPass);
        mSubmit = findViewById(R.id.loginBtn);

        //Initialize
        authController = AuthController.getInstance();
    }

    public void onLoginPressed(View view) {
        final String mail = mEmail.getText().toString().trim();
        final String pass = mPass.getText().toString().trim();

        authController.authenticate(mail, pass, new AuthController.AuthenticationListener() {
            @Override
            public void onAuthenticated(FirebaseUser user) {
                startActivity(new Intent(Login.this, Home.class));
                finish();

                //Send identity to Segment
                Analytics.with(getApplicationContext()).identify(new Traits().putEmail(mail));
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(Login.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}

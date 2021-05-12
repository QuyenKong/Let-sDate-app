package com.example.sem6.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sem6.R;
import com.example.sem6.util.AuthUtil;
import com.example.sem6.util.HttpClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int GG_SIGN_IN = 1;

    GoogleSignInClient mGoogleSignInClient;
    SignInButton signInButton;
    CircleImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        icon= findViewById(R.id.icon);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this,"Let's date  love you <3 !!\nLet's date right\nLet's date now",Toast.LENGTH_LONG).show();
            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton = findViewById(R.id.btn_google_login);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(this);
        findViewById(R.id.btn_basic_login).setOnClickListener(this);
        findViewById(R.id.dont_have_account).setOnClickListener(this);
    }

    private void signInGG() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GG_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == GG_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGGSignInResult(task);
        }
    }

    private void handleGGSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.

            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("email", account.getEmail());
            jsonRequest.put("fullName", account.getDisplayName());
            jsonRequest.put("avatar", account.getPhotoUrl() == null ? "" : account.getPhotoUrl().toString());
            jsonRequest.put("socialId", account.getId());

            HttpClient.getInstance(this).post(
                    "/auth/login/google",
                    jsonRequest,
                    response -> {
                        JSONObject res = response;
                        try {
                            String token = String.valueOf(((JSONObject)res.get("data")).get("token"));
                            AuthUtil.setToken(this, token);
                            Toast.makeText(this,"Login successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(this, MainActivity.class));
                        } catch (JSONException e) {
                            Toast.makeText(this,"Login failed", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        Toast.makeText(this,"Login failed", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    });
        } catch (ApiException | JSONException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(this,"Login failed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_google_login:
                signInGG();
                break;
            case R.id.btn_basic_login:
                basicSignIn();
                break;
            case R.id.dont_have_account:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            default:
                break;
        }
    }

    private void basicSignIn() {
        String email = String.valueOf(((TextView) findViewById(R.id.txt_username)).getText());
        String password = String.valueOf(((TextView) findViewById(R.id.txt_password)).getText());

        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("email", email);
            jsonRequest.put("password", password);

            HttpClient.getInstance(this).post(
                    "/auth/login",
                    jsonRequest,
                    response -> {
                        JSONObject res = response;
                        try {
                            String token = String.valueOf(((JSONObject)res.get("data")).get("token"));
                            AuthUtil.setToken(this, token);
                            Toast.makeText(this,"Login successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(this, MainActivity.class));
                        } catch (JSONException e) {
                            Toast.makeText(this,"Login failed", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        Toast.makeText(this,"Login failed", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    });
        } catch (JSONException e) {
            Toast.makeText(this,"Login failed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
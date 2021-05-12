package com.example.sem6.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sem6.R;
import com.example.sem6.util.AuthUtil;
import com.example.sem6.util.HttpClient;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    private EditText txt_username;
    private EditText txt_full_name;
    private EditText txt_password;
    private EditText txt_re_password;
    private CheckBox cb_visitor;
    private CheckBox cb_collab;
    private Button btn_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txt_username = findViewById(R.id.txt_username);
        txt_full_name = findViewById(R.id.txt_full_name);
        txt_password = findViewById(R.id.txt_password);
        txt_re_password = findViewById(R.id.txt_re_password);
        cb_visitor = findViewById(R.id.cb_visitor);
        cb_collab = findViewById(R.id.cb_collaborator);
        btn_signup = findViewById(R.id.btn_sign_up);

        comboBoxHandle();
        //handle button sign up when clicked
        btn_signup.setOnClickListener(v -> register());
    }

    private void register() {
        if (!isValid())
            return;
        String registerFor = cb_visitor.isChecked() ? "visitor" : "collaborator";
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("fullName", txt_full_name.getText().toString());
            jsonRequest.put("email", txt_username.getText().toString());
            jsonRequest.put("password", txt_password.getText().toString());
            jsonRequest.put("confirmPassword", txt_re_password.getText().toString());
            jsonRequest.put("terms", true);

            HttpClient.getInstance(this).post(
                    "/auth/register/" + registerFor,
                    jsonRequest,
                    response -> {
                        JSONObject res = response;
                        try {
                            String token = String.valueOf(((JSONObject) res.get("data")).get("token"));
                            AuthUtil.setToken(this, token);
                            Toast.makeText(this, "Register successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(this, MainActivity.class));
                            overridePendingTransition(0, 0);
                        } catch (JSONException e) {
                            Toast.makeText(this, "Register failed", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        Toast.makeText(this, "Register failed", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    });
        } catch (JSONException e) {
            Toast.makeText(this, "Register failed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void comboBoxHandle() {
        cb_collab.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!cb_collab.isChecked() && !cb_visitor.isChecked()) {
                cb_visitor.setChecked(true);
            }
        });
        cb_visitor.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!cb_collab.isChecked() && !cb_visitor.isChecked()) {
                cb_collab.setChecked(true);
            }
        });
    }

    private boolean isValid() {
        boolean flag = true;
        String username = txt_username.getText().toString().trim();
        String fullName = txt_full_name.getText().toString();
        String password = txt_password.getText().toString().trim();
        String rePassword = txt_re_password.getText().toString().trim();

        if (TextUtils.isEmpty(rePassword)) {
            txt_re_password.requestFocus();
            txt_re_password.setError("Mật khẩu không được để trống");
            flag = false;
        }

        if (TextUtils.isEmpty(password)) {
            txt_password.requestFocus();
            txt_password.setError("Mật khẩu không được để trống");
            flag = false;
        }

        if (TextUtils.isEmpty(fullName)) {
            txt_full_name.requestFocus();
            txt_full_name.setError("Tên đầy đủ không được để trống");
            flag = false;
        }

        if (TextUtils.isEmpty(username)) {
            txt_username.requestFocus();
            txt_username.setError("Email không được để trống");
            flag = false;
        }

        if (!(cb_collab.isChecked() || cb_visitor.isChecked())) {
            Toast.makeText(this, "Vui lòng chọn vai trò của bạn", Toast.LENGTH_SHORT).show();
            flag = false;
        }

        return flag;
    }
}
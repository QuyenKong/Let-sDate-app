package com.example.sem6.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sem6.R;
import com.example.sem6.models.User;
import com.example.sem6.util.AuthUtil;
import com.example.sem6.util.HttpClient;
import com.example.sem6.util.ImageUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.StorageMetadata;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity  {
    private static final int PICK_AVATAR = 1;
    EditText et_name;
    TextView et_date_of_birth;
    EditText et_phone_number;
    EditText et_bio;
    EditText et_price_per_hour;
    TextView tv_email;
    TextView tv_coin;
    TextView tv_decreption;
    TextView tv_tlcd;
    TextView tv_tltk;
    TextView txt_logout;
    TextView tv_setting_gender;
    TextView tv_vta;
    Spinner spinner_gender;
    TextView tv_setting_fee;
    ImageView iv_avatar;
    NumberFormat formatter = new DecimalFormat("###,###,##0");
    Calendar myCalendar =Calendar.getInstance();
    String txt_gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //Datepick time dialog
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }
        };
        et_date_of_birth = findViewById(R.id.et_date_of_birth);
        et_date_of_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    new DatePickerDialog(SettingsActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    updateLabel(et_date_of_birth);

            }
        });
        // logout text
        findViewById(R.id.txt_logout).setOnClickListener(v -> AuthUtil.logout(this));

        //handle spinner for  gender
        spinner_gender = findViewById(R.id.spinner_gender);
        //setting adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_gender.setAdapter(adapter);
        spinner_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parent.getItemAtPosition(position);
                spinner_gender.setSelection(position);
                txt_gender=spinner_gender.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // bind ui here
        AuthUtil.getAuthUser(this, user -> bindUI(user));

        //NavigationBottom handle
        BottomNavigationView navView = findViewById(R.id.bottom_navigation_view);
        navView.setSelectedItemId(R.id.ic_settings);
        navView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.ic_home:
                    startActivity(new Intent(getApplicationContext(),
                            MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.ic_settings:
                    return true;
                case R.id.ic_calendars:
                    startActivity(new Intent(getApplicationContext(),
                            DatingActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });
        //button add coin clicklítener
        findViewById(R.id.img_add).setOnClickListener(v -> {
            View chargeCoinView = LayoutInflater.from(this).inflate(R.layout.input_coins, null);
            new AlertDialog.Builder(this)
                    .setView(chargeCoinView)
                    .setCancelable(false)
                    .setPositiveButton("Nạp tiền", (dialog, which) -> {
                        int coin = Integer.valueOf(((EditText) chargeCoinView.findViewById(R.id.edt_input_coins)).getText().toString());
                        HttpClient.getInstance(this).put(
                                "/user/coin/charge?amount=" + coin,
                                null,
                                response -> {
                                    Toast.makeText(this, "Nạp tiền thành công", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(this, SettingsActivity.class));
                                },
                                error -> {
                                    Toast.makeText(this, "Nạp tiền thất bại", Toast.LENGTH_LONG).show();
                                    error.printStackTrace();
                                });
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> dialog.cancel())
                    .create()
                    .show();
        });
        //chon topic button clicklistener

        findViewById(R.id.txt_chon).setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),
                    TopicActivity.class));
            overridePendingTransition(0, 0);
        });

        findViewById(R.id.btn_save).setOnClickListener(v -> putAuthMe());
    }

    @Override
    protected void onStart() {
        super.onStart();
        //bind Id ;)))
        tv_setting_fee=findViewById(R.id.tv_setting_fee);
        textGradiant(tv_setting_fee);
        tv_decreption=findViewById(R.id.tv_decreption);
        textGradiant(tv_decreption);
        tv_tlcd=findViewById(R.id.tv_thiet_lap_topic);
        textGradiant(tv_tlcd);
        tv_tltk=findViewById(R.id.thiet_lap_tk);
        textGradiant(tv_tltk);
        tv_vta=findViewById(R.id.tv_vi_tien);
        textGradiant(tv_vta);
        txt_logout=findViewById(R.id.txt_logout);
        textGradiant(txt_logout);
        tv_setting_gender= findViewById(R.id.tv_setting_gender);
        textGradiant(tv_setting_gender);
        et_name = findViewById(R.id.et_name);
        et_bio = findViewById(R.id.et_bio);
        et_phone_number = findViewById(R.id.et_phone_number);
        et_price_per_hour = findViewById(R.id.et_price_per_hour);
        tv_email = findViewById(R.id.tv_email);
        tv_coin = findViewById(R.id.tv_coin);
        if (AuthUtil.isVisitor(this)) {
            tv_setting_fee.setVisibility(View.GONE);
            findViewById(R.id.container_price_per_hour).setVisibility(View.GONE);
        }
        iv_avatar = findViewById(R.id.iv_avatar);
        iv_avatar.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, PICK_AVATAR);
        });

    }

    public void bindUI(User authUser) {
        et_price_per_hour.setText(formatter.format(authUser.getPricePerHour()));
        et_name.setText(authUser.getFullName());
        et_phone_number.setText(authUser.getPhone());
        et_bio.setText(authUser.getBio());
        et_date_of_birth.setText(new SimpleDateFormat("dd/MM/yyyy").format(authUser.getBirthDate()));
        spinner_gender.setSelection(setGender(authUser));
        tv_email.setText(authUser.getEmail());
        tv_coin.setText(String.valueOf(formatter.format(authUser.getBalance())));
        Glide.with(this).load(authUser.getAvatar()).into(iv_avatar);

    }

    public void putAuthMe() {
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("fullName", et_name.getText().toString());
            jsonRequest.put("bio", et_bio.getText().toString());
            jsonRequest.put("pricePerHour", Double.valueOf(et_price_per_hour.getText().toString().replace(",","")));
            jsonRequest.put("address", " ");
            Date birthday = new SimpleDateFormat("dd/MM/yyyy").parse(et_date_of_birth.getText().toString());
            jsonRequest.put("birthDate", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(birthday));
            jsonRequest.put("gender", txt_gender);
            jsonRequest.put("phone", et_phone_number.getText().toString());

            HttpClient.getInstance(this).put(
                    "/auth/me",
                    jsonRequest,
                    response -> {
                        Toast.makeText(this, "Cập nhật hồ sơ thành công", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(this, SettingsActivity.class));
                    },
                    error -> {
                        Toast.makeText(this, "Cập nhật hồ sơ thất bại", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    });
        } catch (Exception e) {
            Toast.makeText(this, "Cập nhật hồ sơ thất bại", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    public int setGender(User user) {
        if (user.getGender() == null) {
            return 0;
        } else if (user.getGender().equals("Nam")) {
            return 0;
        } else if (user.getGender().equals("Nữ")) {
            return 1;
        } else return 2;
    }
    public void textGradiant(TextView a){
        Shader shader = new LinearGradient(0,0,0,a.getTextSize(),
                new int[]{Color.parseColor("#FF9671"),
                        Color.parseColor("#FF6F91"),
//                        Color.parseColor("#64B678"),
//                        Color.parseColor("#478AEA"),
//                        Color.parseColor("#8446CC"),
                },null, Shader.TileMode.CLAMP);
        a.getPaint().setShader(shader);
    }

    private void updateLabel( TextView editText) {

        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editText.setText(sdf.format(myCalendar.getTime()));
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_AVATAR:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();
                    try {
                        InputStream is = this.getContentResolver().openInputStream(uri);
                        ImageUtil.upload(is, taskSnapshot -> {
                            StorageMetadata storageMetadata = taskSnapshot.getMetadata();
                            String url = getString(R.string.base_firebase_storage) + storageMetadata.getPath() + "?alt=media";
                            Glide.with(this).load(url).into(iv_avatar);
                            //TODO: gọi API update profile ở đây
                            HttpClient.getInstance(this).put(
                                    "/auth/me/avatar?url=" + url,
                                    null,
                                    response -> {
                                        Toast.makeText(this, "Cập nhật ảnh đại diện thành công", Toast.LENGTH_LONG).show();
                                    },
                                    error -> {
                                        Toast.makeText(this, "Cập nhật ảnh đại diện thất bại", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(this, SettingsActivity.class));
                                        error.printStackTrace();
                                    });
                        }, e -> e.printStackTrace());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }
}
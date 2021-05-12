package com.example.sem6.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.sem6.R;
import com.example.sem6.adapters.RatingAdapter;
import com.example.sem6.dto.PagedResponse;
import com.example.sem6.dto.RestResponse;
import com.example.sem6.models.DatingSchedule;
import com.example.sem6.models.User;
import com.example.sem6.util.AuthUtil;
import com.example.sem6.util.HttpClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DetailsCollabActivity extends AppCompatActivity {
    NumberFormat formatter = new DecimalFormat("###,###,##0");
    DatePicker datePicker;
    TimePicker timePicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_collab);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStart() {
        super.onStart();
        DatePicker datePicker = findViewById(R.id.date_picker1);
        TimePicker timePicker = findViewById(R.id.time_picker1);
        timePicker.setIs24HourView(true);
        if (AuthUtil.getRole(this).equals("COLLABORATOR")) {
            findViewById(R.id.request_container).setVisibility(View.GONE);
        }
        long uid = getIntent().getLongExtra("uid", 1);
        HttpClient.getInstance(this).get(
                "/user/" + uid,
                null,
                response -> {
                    try {
                        RestResponse<User> data = new ObjectMapper()
                                .readValue(
                                        response.toString(),
                                        new TypeReference<RestResponse<User>>() {
                                        });
                        bindUI(data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                },
                null);

        HttpClient.getInstance(this).get(
                "/dating/uid/" + uid+"?limit=1000&page=1",
                null,
                response -> {
                    try {
                        PagedResponse<List<DatingSchedule>> data = new ObjectMapper()
                                .readValue(
                                        response.toString(),
                                        new TypeReference<PagedResponse<List<DatingSchedule>>>() {});
                        bindRating(data.getData().stream().filter(s -> s.getStatus() == 3).collect(Collectors.toList()), uid);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                },
                null);

        findViewById(R.id.btn_request).setOnClickListener(v -> onRequested(uid));
    }

    private void bindUI(User user) {
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            Glide.with(this)
                    .load(user.getAvatar())
                    .into((ImageView) findViewById(R.id.iv_avatar_collab));
        }
        ((TextView) findViewById(R.id.txt_name)).setText(user.getFullName());
        ((TextView) findViewById(R.id.txt_gender)).setText(user.getGender() == null ? "LGBT" : user.getGender());
        ((TextView) findViewById(R.id.txt_birthday))
                .setText(new SimpleDateFormat("dd/MM/yyyy").format(user.getBirthDate()));
        ((TextView) findViewById(R.id.txt_bio)).setText(user.getBio());
        ((TextView) findViewById(R.id.tv_fee)).setText(formatter.format(user.getPricePerHour()) + " đ/h");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void bindRating(List<DatingSchedule> schedules, long collabUID) {
        if (schedules.isEmpty()) {
            findViewById(R.id.lv_rating).setVisibility(View.GONE);
            findViewById(R.id.tv_no_rating).setVisibility(View.VISIBLE);
            return;
        }
        ((ListView) findViewById(R.id.lv_rating))
                .setAdapter(new RatingAdapter(this, R.layout.lv_rating, schedules, collabUID));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void onRequested(long uid) {
        DatePicker datePicker = findViewById(R.id.date_picker1);
        TimePicker timePicker = findViewById(R.id.time_picker1);
        Date startDate = new Date(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getHour(), timePicker.getMinute());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.HOUR_OF_DAY, 3);
        Date endDate = calendar.getTime();

        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("collaboratorId", uid);
            jsonRequest.put("startTime", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(startDate));
            jsonRequest.put("endTime", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(endDate));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpClient.getInstance(this).post(
                "/dating/request",
                jsonRequest,
                response -> {
                    try {
                        RestResponse<Object> data = new ObjectMapper()
                                .readValue(
                                        response.toString(),
                                        new TypeReference<RestResponse<Object>>() {
                                        });
                        Toast.makeText(this, data.getMessage(), Toast.LENGTH_LONG).show();
                        if (data.isStatus()) {
                            startActivity(new Intent(this, DatingActivity.class));
                        } else {
                            Toast.makeText(this, String.valueOf(data.getData()), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Đặt lịch thất bại", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Đặt lịch thất bại", Toast.LENGTH_SHORT).show());
    }
}
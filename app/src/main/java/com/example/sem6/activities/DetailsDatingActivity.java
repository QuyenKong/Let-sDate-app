package com.example.sem6.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.RequiresApi;

import com.example.sem6.R;
import com.example.sem6.adapters.DatingScheduleAdapter;
import com.example.sem6.dto.PagedResponse;
import com.example.sem6.dto.RestResponse;
import com.example.sem6.models.DatingSchedule;
import com.example.sem6.models.User;
import com.example.sem6.util.AuthUtil;
import com.example.sem6.util.HttpClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DetailsDatingActivity extends AppCompatActivity {
    TextView start_time;
    TextView end_time;
    TextView full_name;
    TextView phone_number;
    TextView coin;
    TextView status;
    TextView coin_owned;
    Button btn_succcess;
    Context context;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm dd/MM/yyyy");
    private String myRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_dating);
        start_time = findViewById(R.id.txt_start_time);
        end_time = findViewById(R.id.txt_end_time);
        full_name = findViewById(R.id.txt_full_name);
        phone_number = findViewById(R.id.txt_phone_number);
        coin = findViewById(R.id.txt_coin);
        status = findViewById(R.id.txt_status);
        coin_owned = findViewById(R.id.txt_coin_owned);
        btn_succcess = findViewById(R.id.btn_successful);
        Context context = this;
      if (!btn_succcess.getText().toString().equals("X??c nh???n")){
          btn_succcess.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  startActivity(new Intent(getApplicationContext(), DatingActivity.class));
                  overridePendingTransition(0, 0);
              }
          });
      }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStart() {
        super.onStart();
//        long id = getIntent().getLongExtra("id", 1);
        try {
            DatingSchedule datingSchedule = new ObjectMapper().readValue(getIntent().getStringExtra("dating"), DatingSchedule.class);
            myRole = AuthUtil.getRole(this);
            bindUI(datingSchedule);
            if (btn_succcess.getText().toString().equals("X??c nh???n")){
                btn_succcess.setOnClickListener(v -> onAccepted(datingSchedule));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void bindUI(DatingSchedule schedule) {
        start_time.setText(simpleDateFormat.format(schedule.getStartBookingTime()));
        end_time.setText(simpleDateFormat.format(schedule.getEndBookingTime()));
        User opponent = myRole.equals("VISITOR")
                ? schedule.getUserDatingSchedules().stream().map(u -> u.getUser()).filter(u -> u.getUserRole().equals("COLLABORATOR")).findFirst().get()
                : schedule.getUserDatingSchedules().stream().map(u -> u.getUser()).filter(u -> u.getUserRole().equals("VISITOR")).findFirst().get();
        full_name.setText(opponent.getFullName());
        phone_number.setText(opponent.getPhone());
        coin.setText(schedule.getTransaction().getAmount() * 3 + " ??");
        status.setText(handleStatus(schedule));
        if (myRole.equals("VISITOR")) {
            switch (schedule.getStatus()) {
                case 1:
                    //             return "Ch??? x??c nh???n";
                    phone_number.setText("Ch??? x??c nh???n");
                    Toast.makeText(DetailsDatingActivity.this, "L???ch h???n ph???i ???????c x??c nh???n ????? xem s??? ??i???n tho???i ", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    switch (schedule.getTransaction().getStatus()) {
                        case 1:
                            phone_number.setText("ch??? thanh to??n");
                            Toast.makeText(DetailsDatingActivity.this, "B???n ph???i thanh to??n ????? ???????c xem s??? ??i???n tho???i ", Toast.LENGTH_LONG).show();
                            break;
                        //                   return "Ch??? thanh to??n";
                        case 2:
                            //                    return "???? thanh to??n";
                            phone_number.setText(opponent.getPhone());
                            break;
                    }
                    break;
                case 3:
                    //             return "???? ho??n th??nh";
                    phone_number.setText(opponent.getPhone());
                    break;
                case 4:
                    phone_number.setText("Cu???c h???n ???? b??? h???y");
                    break;
                //              return "???? h???y";
            }

        } else {
            phone_number.setText(opponent.getPhone());
        }
        if (myRole.equals("VISITOR")) {
            coin_owned.setText("S??? ti???n ph???i thanh to??n");
        } else {
            coin_owned.setText("S??? ti???n nh???n ???????c ");
        }
        switch (schedule.getStatus()) {
            case 1:
                //    return "Ch??? x??c nh???n";
                status.setTextColor(Color.parseColor("#FF6F91"));
                btn_succcess.setText("X??c nh???n");
                break;
            case 2:
                switch (schedule.getTransaction().getStatus()) {
                    case 1:
                        //  return "Ch??? thanh to??n";
                        status.setTextColor(Color.parseColor("#8E65B7"));
                        break;
                    case 2:
                        //    return "???? thanh to??n";
                        status.setTextColor(Color.parseColor("#2FD3B4"));
                        Toast.makeText(this, "H??y li??n l???c v???i kh??ch h??ng ????? ti???n h??nh l???ch h???n", Toast.LENGTH_LONG).show();
                        btn_succcess.setText("Ho??n th??nh");
                        break;
                }
                break;
            case 3:
                //  return "???? ho??n th??nh";
                status.setTextColor(Color.parseColor("#E3EB2B"));
                btn_succcess.setText("???? Ho??n th??nh");
                break;
            case 4:
                //   return "???? h???y";
                status.setTextColor(Color.parseColor("#615F5F"));
                btn_succcess.setText("Quay l???i");
                break;

        }
    }


    private String handleStatus(DatingSchedule schedule) {
        switch (schedule.getStatus()) {
            case 1:
                return "Ch??? x??c nh???n";
            case 2:
                switch (schedule.getTransaction().getStatus()) {
                    case 1:
                        return "Ch??? thanh to??n";
                    case 2:
                        return "???? thanh to??n";
                }
                break;
            case 3:
                return "???? ho??n th??nh";
            case 4:
                return "???? h???y";
        }
        return "Kh??ng x??c ?????nh";
    }


    private long countingTime(String start, String end) throws ParseException {
        Date start1 = simpleDateFormat.parse(start);
        Date end1 = simpleDateFormat.parse(end);
        long difference = end1.getTime() - start1.getTime();
        return difference / (60 * 60 * 1000) % 24;
    }
    private void onAccepted(DatingSchedule schedule) {
        HttpClient.getInstance(context).put(
                "/dating/" + schedule.getId() + "/accept",
                null,
                response -> {
                    try {
                        RestResponse<DatingSchedule> data = new ObjectMapper()
                                .readValue(
                                        response.toString(),
                                        new TypeReference<RestResponse<DatingSchedule>>() {
                                        });
                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                        if (data.isStatus()) {
                            context.startActivity(new Intent(context, DatingActivity.class));
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Kh??ng th??? x??c nh???n l???ch h???n", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(context, "Kh??ng th??? x??c nh???n l???ch h???n", Toast.LENGTH_SHORT).show());

        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        overridePendingTransition(0, 0);
    }

}

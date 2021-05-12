package com.example.sem6.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DatingActivity extends AppCompatActivity {
    DatingScheduleAdapter datingScheduleAdapter;
    ListView lvDating;
    List<DatingSchedule> schedules;
    TextView tv_thongbao;
    TextView ft_trang_thai;
    TextView ft_time;
    LinearLayout ll_thongbao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dating);
        BottomNavigationView navView = findViewById(R.id.bottom_navigation_view);


        navView.setSelectedItemId(R.id.ic_calendars);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_home:
                        startActivity(new Intent(getApplicationContext(),
                                MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.ic_settings:
                        startActivity(new Intent(getApplicationContext(),
                                SettingsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.ic_calendars:
                        return true;
                }
                return false;
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        ll_thongbao = findViewById(R.id.ll_thong_bao);
        tv_thongbao = findViewById(R.id.tv_thong_bao_chua_co_hen);
        ft_time = findViewById(R.id.tv_time_gan_nhat);
        ft_trang_thai = findViewById(R.id.tv_trang_thai);
        textGradiant(tv_thongbao);
        lvDating = findViewById(R.id.lv_schedule);

        HttpClient.getInstance(this).get(
                "/dating?limit=1000&page=1",
                null,
                response -> {
                    try {
                        PagedResponse<List<DatingSchedule>> data = new ObjectMapper()
                                .readValue(
                                        response.toString(),
                                        new TypeReference<PagedResponse<List<DatingSchedule>>>() {
                                        });
                        schedules = data.getData();
                        Collections.sort(schedules, new Comparator<DatingSchedule>() {
                            @Override
                            public int compare(DatingSchedule o1, DatingSchedule o2) {
                                return (o1.getStartBookingTime().toString()).compareTo(o2.getStartBookingTime().toString());
                            }
                        });

                        if (schedules.size() > 0) {
                            ll_thongbao.setVisibility(View.GONE);

                        } else ll_thongbao.setVisibility(View.VISIBLE);
                        datingScheduleAdapter = new DatingScheduleAdapter(this, R.layout.schedule_item, schedules, AuthUtil.getRole(this));
                        lvDating.setAdapter(datingScheduleAdapter);
                        lvDating.setOnItemClickListener((parent, view, position, id) -> {
                            DatingSchedule dating = datingScheduleAdapter.getItem(position);
                            Intent intent = new Intent(this, DetailsDatingActivity.class);
                            try {
                                intent.putExtra("dating", new ObjectMapper().writeValueAsString(dating));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            startActivity(intent);
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                },
                null);

        ft_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(schedules, new Comparator<DatingSchedule>() {
                    @Override
                    public int compare(DatingSchedule o1, DatingSchedule o2) {
                        return (o1.getStartBookingTime().toString()).compareTo(o2.getStartBookingTime().toString());
                    }
                });
                datingScheduleAdapter.notifyDataSetChanged();
            }
        });
        ft_trang_thai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(schedules, new Comparator<DatingSchedule>() {
                    @Override
                    public int compare(DatingSchedule o1, DatingSchedule o2) {
                        if (o1.getStatus() == o2.getStatus())
                            return 1;
                        else if (o1.getStatus() > o2.getStatus())
                            return 0;
                        else
                            return -1;
                    }

                });
                Collections.sort(schedules, new Comparator<DatingSchedule>() {
                    @Override
                    public int compare(DatingSchedule o1, DatingSchedule o2) {
                        if (o1.getStatus() == o2.getStatus())
                            return 1;
                        else if (o1.getStatus() > o2.getStatus())
                            return 0;
                        else
                            return -1;
                    }

                });
                datingScheduleAdapter.notifyDataSetChanged();

            }
        });
    }

    public void textGradiant(TextView a) {
        Shader shader = new LinearGradient(0, 0, 0, a.getTextSize(),
                new int[]{Color.parseColor("#FF9671"),
                        Color.parseColor("#FF6F91"),
//                        Color.parseColor("#64B678"),
//                        Color.parseColor("#478AEA"),
//                        Color.parseColor("#8446CC"),
                }, null, Shader.TileMode.CLAMP);
        a.getPaint().setShader(shader);
    }
}

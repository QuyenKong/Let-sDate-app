package com.example.sem6.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sem6.R;
import com.example.sem6.adapters.TopicAdapter;
import com.example.sem6.adapters.UserAdapter;
import com.example.sem6.dto.PagedResponse;
import com.example.sem6.models.Topic;
import com.example.sem6.models.User;
import com.example.sem6.util.AuthUtil;
import com.example.sem6.util.HttpClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    List<User> arrUser;
    private ListView lvContact;
    private GridView gvContact;
    UserAdapter userAdapter;
    Button btn_search;
    TextView ft_tang;
    TextView ft_giam;
    List<Topic> topics;
    Set<Topic> topicSelected = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_search = findViewById(R.id.btn_search_by_topic);
        ft_tang = findViewById(R.id.btn_tang);
        ft_giam = findViewById(R.id.btn_giam);
        AuthUtil.bindAuthGuard(this);

        //Initialize Bottom Navigation View.
        BottomNavigationView navView = findViewById(R.id.bottom_navigation_view);

        navView.setSelectedItemId(R.id.ic_home);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_home:
                        return true;
                    case R.id.ic_settings:
                        startActivity(new Intent(getApplicationContext(),
                                SettingsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.ic_calendars:
                        startActivity(new Intent(getApplicationContext(),
                                DatingActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        //lvContact = findViewById(R.id.lvcontact);

        gvContact = findViewById(R.id.gvcontact);
        gvContact.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, DetailsCollabActivity.class);
            intent.putExtra("uid", id);
            startActivity(intent);
        });
        HttpClient.getInstance(this).get(
                "/user/collaborators",
                null,
                response -> {
                    try {
                        PagedResponse<List<User>> data = new ObjectMapper().readValue(response.toString(), new TypeReference<PagedResponse<List<User>>>() {
                        });
                        arrUser = data.getData();
//                        Collections.sort(arrUser, new Comparator<User>() {
//                            @Override
//                            public int compare(User o1, User o2) {
//                                if (o1.getPricePerHour() == o2.getPricePerHour())
//                                    return 0;
//                                else if (o1.getPricePerHour() > o2.getPricePerHour())
//                                    return 1;
//                                else
//                                    return -1;
//                            }
//                        });
                        userAdapter = new UserAdapter(this, R.layout.collab, arrUser);
                        gvContact.setAdapter(userAdapter);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                },
                null);
        //ft_giam
        ft_giam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(arrUser, new Comparator<User>() {
                    @Override
                    public int compare(User o1, User o2) {
                        if (o1.getPricePerHour() == o2.getPricePerHour())
                            return 0;
                        else if (o1.getPricePerHour() < o2.getPricePerHour())
                            return 1;
                        else
                            return -1;
                    }
                });
                userAdapter.notifyDataSetChanged();
            }
        });
        //ft_tang
        ft_tang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(arrUser, new Comparator<User>() {
                    @Override
                    public int compare(User o1, User o2) {
                        if (o1.getPricePerHour() == o2.getPricePerHour())
                            return 1;
                        else if (o1.getPricePerHour() > o2.getPricePerHour())
                            return 0;
                        else
                            return -1;
                    }
                });
                userAdapter.notifyDataSetChanged();
            }
        });
        // on item click

        findViewById(R.id.iv_pick_topic).setOnClickListener(v -> openPickTopicDialog());
        findViewById(R.id.btn_search_by_topic).setOnClickListener(v -> onFilteredByTopics());
        findViewById(R.id.et_topic_ids).setOnClickListener(v -> openPickTopicDialog());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void openPickTopicDialog() {
        topicSelected.clear();
        HttpClient.getInstance(this).get(
                "/topic?limit=1000&page=1",
                null,
                response -> {
                    try {
                        PagedResponse<List<Topic>> data = new ObjectMapper().readValue(response.toString(), new TypeReference<PagedResponse<List<Topic>>>() {
                        });
                        topics = data.getData();
                        TopicAdapter topicAdapter = new TopicAdapter(this, R.layout.topic_item, topics);

                        View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_pick_topics, null);
                        GridView gvTopic = dialog.findViewById(R.id.gv_topic_dialog);
                        gvTopic.setAdapter(topicAdapter);
                        ;
                        gvTopic.setOnItemClickListener((parent, view, position, id) -> {
                            if (topicSelected.contains(topicAdapter.getItem(position))) {
//                                gvTopic.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
                                topicAdapter.setSelectedItem(position);
                                topicSelected.remove(topicAdapter.getItem(position));
                            } else {
//                               gvTopic.getChildAt(position).setBackgroundResource(R.drawable.surround_item_click);
                                topicAdapter.setSelectedItem(position);
                                topicSelected.add(topicAdapter.getItem(position));
                            }
                        });

                        new AlertDialog.Builder(this)
                                .setView(dialog)
                                .setPositiveButton("Xác nhận", (dialog1, which) -> {
                                    String topicTitle = String.join(",", topicSelected.stream().map(Topic::getTitle).collect(Collectors.toList()));
                                    ((EditText) findViewById(R.id.et_topic_ids)).setText(topicTitle);
                                })
                                .setNegativeButton("Hủy", (dialog1, which) -> {

                                })
                                .create()
                                .show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                },
                null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onFilteredByTopics() {
        String topicIds = String.join(",", topicSelected.stream().map(Topic::getId).map(String::valueOf).collect(Collectors.toList()));
        String path = topicIds.isEmpty()
                ? "/user/collaborators"
                : "/user/collaborators?topicIds=" + topicIds;
        HttpClient.getInstance(this).get(
                path,
                null,
                response -> {
                    try {
                        PagedResponse<List<User>> data = new ObjectMapper().readValue(response.toString(), new TypeReference<PagedResponse<List<User>>>() {
                        });
                        arrUser = data.getData();
                        userAdapter = new UserAdapter(this, R.layout.collab, arrUser);
                        //  lvContact.setAdapter(userAdapter);
                        gvContact.setAdapter(userAdapter);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                },
                null);
    }

}
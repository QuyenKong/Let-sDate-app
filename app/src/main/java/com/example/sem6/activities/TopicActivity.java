package com.example.sem6.activities;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sem6.R;
import com.example.sem6.adapters.TopicAdapter;
import com.example.sem6.adapters.UserAdapter;
import com.example.sem6.dto.PagedResponse;
import com.example.sem6.dto.RestResponse;
import com.example.sem6.models.Topic;
import com.example.sem6.models.User;
import com.example.sem6.util.AuthUtil;
import com.example.sem6.util.HttpClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TopicActivity extends AppCompatActivity {

    private GridView gv_Topic;
    private List<Topic> arrTopic = new ArrayList<>();
    private EditText edt_show_topic_selected;
    private Button btn_confirm;
    TopicAdapter topicAdapter;
    String stringTopic="";
    Set<Integer> topicSelectedID = new HashSet<>();
    Set<Topic> topicSelected = new HashSet<>();
    Context context;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gv_topic);
        gv_Topic=findViewById(R.id.gv_topic);
        context=this;
        findViewById(R.id.btn_confirm).setOnClickListener(v -> pickTopics(topicSelectedID));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void pickTopics(Set<Integer> topicSelectedID) {
        long uid = AuthUtil.getUID(this);
        String topicIds = String.join(",", topicSelectedID.stream().map(String::valueOf).collect(Collectors.toList()));
   //     String topicTitle = String.join(",", topicSelected.stream().map(Topic::getTitle).collect(Collectors.toList()));
  //      edt_show_topic_selected.setText(stringTopic);
        try {
            HttpClient.getInstance(this).post(
                    "/topic/pick?uid=" + uid + "&topicIds=" + topicIds,
                    null,
                    response -> {
                        Toast.makeText(context, "Cập nhật chủ đề thành công", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(this, SettingsActivity.class));
                    },
                    error -> {
                      Toast.makeText(context, "Cập nhật chủ đề  thất bại", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(this, SettingsActivity.class));
                        error.printStackTrace();
                    });
        } catch (Exception ex) {
            Toast.makeText(context, "Cập nhật chủ đề  thất bại", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, SettingsActivity.class));
            ex.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        HttpClient.getInstance(this).get(
                "/topic?limit=1000&page=1",
                null,
                response -> {
                    try {
                        PagedResponse<List<Topic>> data = new ObjectMapper().readValue(response.toString(), new TypeReference<PagedResponse<List<Topic>>>() {
                        });
                        arrTopic = data.getData();
                        topicAdapter = new TopicAdapter(this, R.layout.gv_topic, (ArrayList<Topic>) arrTopic);
                        gv_Topic = findViewById(R.id.gv_topic);
                        gv_Topic.setAdapter(topicAdapter);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                },
                null);
        edt_show_topic_selected=findViewById(R.id.edt_show_topic_selected);
        // on item click and set change color
        gv_Topic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (topicSelected.contains(topicAdapter.getItem(position))) {
                    //     gvTopic.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
                    topicSelectedID.remove(topicAdapter.getItem(position).getId());
                    topicSelected.remove(topicAdapter.getItem(position));
                    topicAdapter.setSelectedItem(position);

                } else {
                    //  gvTopic.getChildAt(position).setBackgroundColor(Color.BLUE);
                    topicSelected.add(topicAdapter.getItem(position));
                    topicSelectedID.add(topicAdapter.getItem(position).getId());
                    topicAdapter.setSelectedItem(position);
                    if (stringTopic=="") {
                        stringTopic += topicAdapter.getItem(position).getTitle();
                    }else  stringTopic +=", "+topicAdapter.getItem(position).getTitle();
                }
                edt_show_topic_selected.setText(stringTopic);
            }
        });
        // handle btn_confirm clicked and push data to sever

    }
}
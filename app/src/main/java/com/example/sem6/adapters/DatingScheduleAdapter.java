package com.example.sem6.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.sem6.R;
import com.example.sem6.activities.DatingActivity;
import com.example.sem6.dto.RestResponse;
import com.example.sem6.models.DatingSchedule;
import com.example.sem6.models.User;
import com.example.sem6.util.HttpClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class DatingScheduleAdapter extends ArrayAdapter<DatingSchedule> {
    private Context context;
    private List<DatingSchedule> arrDating;
    private String myRole;
    NumberFormat formatter = new DecimalFormat("###,###,##0");
    public DatingScheduleAdapter(
            @NonNull Context context,
            int resource,
            @NonNull List<DatingSchedule> objects,
            String myRole) {
        super(context, resource, objects);
        this.context = context;
        this.arrDating = objects;
        this.myRole = myRole;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm dd/MM/yyyy");
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.schedule_item, parent, false);
            viewHolder.tv_start_booking_time = convertView.findViewById(R.id.tv_start_booking_time);
            viewHolder.tv_end_booking_time = convertView.findViewById(R.id.tv_end_booking_time);
            viewHolder.tv_user = convertView.findViewById(R.id.tv_user);
            viewHolder.tv_fee = convertView.findViewById(R.id.tv_fee);
            viewHolder.tv_status = convertView.findViewById(R.id.tv_status);
            viewHolder.btn_accept = convertView.findViewById(R.id.btn_accept);
            viewHolder.btn_pay = convertView.findViewById(R.id.btn_pay);
            viewHolder.btn_complete = convertView.findViewById(R.id.btn_complete);
            viewHolder.btn_feedback = convertView.findViewById(R.id.btn_feedback);
            viewHolder.btn_cancel = convertView.findViewById(R.id.btn_cancel);
            viewHolder.rating_container = convertView.findViewById(R.id.rating_container);
            viewHolder.rating_bar = convertView.findViewById(R.id.rating_bar);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DatingSchedule schedule = arrDating.get(position);
        viewHolder.tv_start_booking_time.setText(simpleDateFormat.format(schedule.getStartBookingTime()));
        viewHolder.tv_end_booking_time.setText(simpleDateFormat.format(schedule.getEndBookingTime()));
        User opponent = myRole.equals("VISITOR")
                ? schedule.getUserDatingSchedules().stream().map(u -> u.getUser()).filter(u -> u.getUserRole().equals("COLLABORATOR")).findFirst().get()
                : schedule.getUserDatingSchedules().stream().map(u -> u.getUser()).filter(u -> u.getUserRole().equals("VISITOR")).findFirst().get();
        viewHolder.tv_user.setText(opponent.getFullName());
        viewHolder.tv_fee.setText(formatter.format(schedule.getTransaction().getAmount() )+ "đ");

        viewHolder.tv_status.setText(handleStatus(schedule));
        switch (schedule.getStatus()) {
            case 1:
            //    return "Chờ xác nhận";
            viewHolder.tv_status.setTextColor(Color.parseColor("#FF6F91"));
            case 2:
                switch (schedule.getTransaction().getStatus()) {
                    case 1:
                      //  return "Chờ thanh toán";
                        viewHolder.tv_status.setTextColor(Color.parseColor("#8E65B7"));
                        break;
                    case 2:
                    //    return "Đã thanh toán";
                        viewHolder.tv_status.setTextColor(Color.parseColor("#2FD3B4"));
                        break;
                }
                break;
            case 3:
              //  return "Đã hoàn thành";
                viewHolder.tv_status.setTextColor(Color.parseColor("#E3EB2B"));
                break;
            case 4:
             //   return "Đã hủy";
                viewHolder.tv_status.setTextColor(Color.parseColor("#615F5F"));
                break;

        }
        viewHolder.rating_bar.setRating((float) schedule.getTransaction().getRating());
        if (myRole.equals("VISITOR")) {
            handleButtons4Visitor(viewHolder, schedule);
        } else {
            handleButtons4Collab(viewHolder, schedule);
        }
        viewHolder.btn_cancel.setOnClickListener(v -> onCanceled(schedule));
        viewHolder.btn_accept.setOnClickListener(v -> onAccepted(schedule));
        viewHolder.btn_pay.setOnClickListener(v -> onPaid(schedule));
        viewHolder.btn_complete.setOnClickListener(v -> onCompleted(schedule));
        viewHolder.btn_feedback.setOnClickListener(v -> onFeedBacked(schedule));
        return convertView;
    }

    private void onFeedBacked(DatingSchedule schedule) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_feedback, null);
        new AlertDialog.Builder(context)
                .setView(dialogView)
                .setPositiveButton("Gửi đánh giá", (dialog, which) -> {
                    RatingBar rBar = dialogView.findViewById(R.id.rating_bar);
                    EditText etComment = dialogView.findViewById(R.id.et_comment);

                    JSONObject jsonRequest = new JSONObject();
                    try {
                        jsonRequest.put("rating", (int) rBar.getRating());
                        jsonRequest.put("ratingComment", etComment.getText());
                    } catch (JSONException e) {
                        Toast.makeText(context, "Đánh giá thất bại", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    HttpClient.getInstance(context).put(
                            "/dating/" + schedule.getId() + "/feedback",
                            jsonRequest,
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
                                    Toast.makeText(context, "Đánh giá thất bại", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            },
                            error -> Toast.makeText(context, "Đánh giá thất bại", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                })
                .create()
                .show();
    }

    private void onCompleted(DatingSchedule schedule) {
        HttpClient.getInstance(context).put(
                "/dating/" + schedule.getId() + "/complete",
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
                        Toast.makeText(context, "Không thể hoàn thành lịch hẹn", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(context, "Không thể hoàn thành lịch hẹn", Toast.LENGTH_SHORT).show());
    }

    private void onPaid(DatingSchedule schedule) {
        HttpClient.getInstance(context).put(
                "/dating/" + schedule.getId() + "/pay",
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
                        Toast.makeText(context, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(context, "Thanh toán thất bại", Toast.LENGTH_SHORT).show());
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
                        Toast.makeText(context, "Không thể xác nhận lịch hẹn", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(context, "Không thể xác nhận lịch hẹn", Toast.LENGTH_SHORT).show());
    }

    private void onCanceled(DatingSchedule schedule) {
        HttpClient.getInstance(context).put(
                "/dating/" + schedule.getId() + "/cancel",
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
                        Toast.makeText(context, "Không thể hủy lịch hẹn", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(context, "Không thể hủy lịch hẹn", Toast.LENGTH_SHORT).show());
    }

    class ViewHolder {
        TextView tv_start_booking_time;
        TextView tv_end_booking_time;
        TextView tv_user;
        TextView tv_fee;
        TextView tv_status;
        Button btn_accept;
        Button btn_pay;
        Button btn_complete;
        Button btn_feedback;
        Button btn_cancel;
        RatingBar rating_bar;
        LinearLayout rating_container;
    }

    private String handleStatus(DatingSchedule schedule) {
        switch (schedule.getStatus()) {
            case 1:
                return "Chờ xác nhận";
            case 2:
                switch (schedule.getTransaction().getStatus()) {
                    case 1:
                        return "Chờ thanh toán";
                    case 2:
                        return "Đã thanh toán";
                }
                break;
            case 3:
                return "Đã hoàn thành";
            case 4:
                return "Đã hủy";
        }
        return "Không xác định";
    }

    private void handleButtons4Collab(ViewHolder viewHolder, DatingSchedule schedule) {
        switch (schedule.getStatus()) {
            case 1:
                viewHolder.btn_accept.setVisibility(View.VISIBLE);
                viewHolder.btn_cancel.setVisibility(View.VISIBLE);
                break;
            case 2:
                switch (schedule.getTransaction().getStatus()) {
                    case 1:
                        viewHolder.btn_cancel.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        viewHolder.btn_complete.setVisibility(View.VISIBLE);
                        break;
                }
                break;
        }
    }

    private void handleButtons4Visitor(ViewHolder viewHolder, DatingSchedule schedule) {
        switch (schedule.getStatus()) {
            case 1:
                viewHolder.btn_cancel.setVisibility(View.VISIBLE);
                break;
            case 2:
                switch (schedule.getTransaction().getStatus()) {
                    case 1:
                        viewHolder.btn_pay.setVisibility(View.VISIBLE);

                        viewHolder.btn_cancel.setVisibility(View.VISIBLE);
                        break;
                }
                break;
            case 3:
                if (schedule.getTransaction().getStatus() != 4) {
                    viewHolder.btn_feedback.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.rating_container.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

}

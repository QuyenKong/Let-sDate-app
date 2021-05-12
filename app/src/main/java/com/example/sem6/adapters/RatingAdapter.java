package com.example.sem6.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.sem6.R;
import com.example.sem6.models.DatingSchedule;
import com.example.sem6.models.Transaction;
import com.example.sem6.models.User;

import java.util.List;

public class RatingAdapter extends ArrayAdapter<DatingSchedule> {
    List<DatingSchedule> data;
    long collabUID;

    public RatingAdapter(@NonNull Context context, int resource, @NonNull List<DatingSchedule> objects, long collabUID) {
        super(context, resource, objects);
        data = objects;
        this.collabUID = collabUID;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.lv_rating, null);
        Transaction trans = data.get(position).getTransaction();
        User opponent = data.get(position).getUserDatingSchedules()
                .stream()
                .map(u -> u.getUser())
                .filter(u -> u.getId() != collabUID)
                .findFirst().get();
        ((TextView) convertView.findViewById(R.id.tv_name)).setText(opponent.getFullName() + " ");
        ((TextView) convertView.findViewById(R.id.tv_comment)).setText(trans.getRatingComment());
        ((RatingBar) convertView.findViewById(R.id.rating_bar)).setRating((float) trans.getRating());
        return convertView;
    }
}

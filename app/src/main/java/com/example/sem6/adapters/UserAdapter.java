package com.example.sem6.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.sem6.R;
import com.example.sem6.models.User;
import com.example.sem6.util.HttpClient;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {
    private Context context;
    private List<User> arrUser;
    NumberFormat formatter = new DecimalFormat("###,###,##0");

    public UserAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        this.context = context;
        this.arrUser = objects;
    }

    @Nullable
    @Override
    public User getItem(int position) {
        return arrUser.get(position);
    }

    @Override
    public long getItemId(int position) {
        return arrUser.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();

        if (convertView == null) {
           // convertView = LayoutInflater.from(context).inflate(R.layout.collab, parent, false);
            convertView = LayoutInflater.from(context).inflate(R.layout.collab_2, parent, false);
            viewHolder.img = convertView.findViewById(R.id.iv_avatar);
            viewHolder.tv_name = convertView.findViewById(R.id.tv_name);
            viewHolder.tv_bio = convertView.findViewById(R.id.tv_bio);
            viewHolder.tv_price = convertView.findViewById(R.id.tv_price);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        User user = arrUser.get(position);

        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            Glide.with(context)
                    .load(user.getAvatar())
                    .into(viewHolder.img);
        }

        viewHolder.tv_name.setText(user.getFullName());
        viewHolder.tv_bio.setText(user.getBio() == null ? "Không có mô tả" : user.getBio());
        viewHolder.tv_price.setText(formatter.format(user.getPricePerHour()) + " đ/h");

        return convertView;
    }

    class ViewHolder {
        ImageView img;
        TextView tv_name;
        TextView tv_bio;
        TextView tv_price;
    }


}

package com.example.sem6.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sem6.R;
import com.example.sem6.models.Topic;

import java.util.List;

public class TopicAdapter extends ArrayAdapter<Topic> {
    private Context context;
    private int resource;
    private List<Topic> arrTopic;

    //String include topic has been selected
    private String mSelectedTopic = " ";
    //get position to change the text when clicked on Item
    private int sSelectedPosition = -1;
    private int uSelectedPosition = -1;


    public TopicAdapter(@NonNull Context context, int resource, @NonNull List<Topic> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.arrTopic = objects;
    }

    @Nullable
    @Override
    public Topic getItem(int position) {
        return arrTopic.get(position);
    }

    @Override
    public int getCount() {
        return arrTopic.size();
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder = new ViewHolder();

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.topic_item, parent, false);
            viewHolder.tv_topic_title = convertView.findViewById(R.id.tv_topic_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Topic topic = arrTopic.get(position);

        viewHolder.tv_topic_title.setText(topic.getTitle());


        //handle item color when clicked on item
        //đây là trong hàm getview

        if (position == sSelectedPosition) {
            if (viewHolder.tv_topic_title.getCurrentTextColor()==Color.parseColor("#F25757")){
                viewHolder.tv_topic_title.setTextColor(Color.BLACK);
            }else
            viewHolder.tv_topic_title.setTextColor(Color.parseColor("#F25757"));
            mSelectedTopic += viewHolder.tv_topic_title.getText().toString();
        }
        return convertView;
    }


    public void setSelectedItem(int itemPosition) {
        sSelectedPosition = itemPosition;
        notifyDataSetChanged();
    }

    public String getString(String topic) {
        return topic = mSelectedTopic;
    }

    class ViewHolder {
        TextView tv_topic_title;

    }
}

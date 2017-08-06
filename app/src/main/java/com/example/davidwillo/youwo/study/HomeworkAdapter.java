package com.example.davidwillo.youwo.study;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.davidwillo.youwo.R;
import com.example.davidwillo.youwo.network.model.Homework;

import java.util.ArrayList;

public class HomeworkAdapter extends BaseAdapter {
    ArrayList<Homework> homeworks;
    Context context;
    String username, course;
    HomeworkDB db;

    public HomeworkAdapter(Context context, String username, String course) {
        this.context = context;
        db = new HomeworkDB(context);
        homeworks = db.queryHomeworks(course, username);
        this.username = username;
        this.course = course;
    }

    public void AddData(Homework homework) {
        homeworks.add(homework);
        db.insertHomework(course, false, homework.getHwDescription(), username);
        notifyDataSetChanged();
    }

    public void RemoveData(int position) {
        db.deleteHomework(username, course, homeworks.get(position).getHwDescription());
        homeworks.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return homeworks.size();
    }

    @Override
    public Object getItem(int position) {
        return homeworks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.homework_item, null);
        }
        CheckBox isFinished = (CheckBox) convertView.findViewById(R.id.isFinished);
        final TextView homework_description = (TextView) convertView.findViewById(R.id.homework_description);
        isFinished.setChecked(homeworks.get(position).getFinished());
        homework_description.setText(homeworks.get(position).getHwDescription());
        if (homeworks.get(position).getFinished()) {
            homework_description.setTextColor(Color.parseColor("#1ee410"));
        } else {
            homework_description.setTextColor(Color.parseColor("#000000"));
        }
        isFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (homeworks.get(position).getFinished()) {
                    homeworks.get(position).setFinished(false);
                    db.updateHomework(username, course, homeworks.get(position).getHwDescription(), false);
                    homework_description.setTextColor(Color.parseColor("#000000"));
                } else {
                    homeworks.get(position).setFinished(true);
                    db.updateHomework(username, course, homeworks.get(position).getHwDescription(), true);
                    homework_description.setTextColor(Color.parseColor("#1ee410"));
                }
            }
        });
        return convertView;
    }


}

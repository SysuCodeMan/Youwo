package com.example.davidwillo.youwo.study;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.davidwillo.youwo.R;
import com.example.davidwillo.youwo.network.model.Course;

import java.util.ArrayList;

public class TimetableAdapter extends BaseAdapter {
    private ArrayList<Course> courses;
    private Context context;
    private String username;
    CourseDB db;

    public TimetableAdapter(Context context, String username) {
        this.context = context;
        this.username = username;
        db = new CourseDB(context);
        courses = db.queryCourses(username);
    }

    public void UpdateTimeTable(ArrayList<Course> courses) {
        this.courses.clear();
        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            this.courses.add(course);
        }
        db.UpdateRecord(username, courses);
        notifyDataSetChanged();
        notifyDataSetInvalidated();
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }


    @Override
    public int getCount() {
        return courses.size();
    }

    @Override
    public Object getItem(int position) {
        return courses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View convertView;
        ViewHolder viewHolder;
        if (view == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.course_item, null);
            viewHolder = new ViewHolder();
            viewHolder.courseLinear = (LinearLayout) convertView.findViewById(R.id.courseLinear);
            viewHolder.courseName = (TextView) convertView.findViewById(R.id.courseName);
            convertView.setTag(viewHolder);
        } else {
            convertView = view;
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Course course = courses.get(position);
        if (course.getLength() != 0) {
            if (course.getPlace() != -1) {
                if (course.getPlace() == 0) {
                    viewHolder.courseLinear.setBackground(convertView.getResources().getDrawable(R.drawable.tableitem_top));
                    viewHolder.courseLinear.setZ(1.0f);
                } else if (course.getPlace() == (course.getLength() - 1)) {
                    //convertView.setLayoutParams(new ViewGroup.LayoutParams(convertView.getWidth(),0));

                    viewHolder.courseLinear.setBackground(convertView.getResources().getDrawable(R.drawable.tableitem_bottom));
                    //convertView.setVisibility(View.INVISIBLE);
                    viewHolder.courseLinear.setZ(0);
                } else {
                    //convertView.setLayoutParams(new ViewGroup.LayoutParams(mywidth,0));
                    viewHolder.courseLinear.setBackground(convertView.getResources().getDrawable(R.drawable.tableitem_middle));
                    //convertView.setVisibility(View.INVISIBLE);
                    viewHolder.courseLinear.setZ(0);
                }
            }
        } else {
            //convertView.setVisibility(View.VISIBLE);
            //convertView.setLayoutParams(new ViewGroup.LayoutParams(convertView.getWidth(),100));
            viewHolder.courseLinear.setBackground(null);
            viewHolder.courseLinear.setZ(0);
        }

        if (course.getLength() > 1 && course.getPlace() == 0) {
            viewHolder.courseName.setText(course.getName()+'@'+course.getClassroom());
            viewHolder.courseName.setMinLines(5);
        } else {
            viewHolder.courseName.setText(course.getName());
            viewHolder.courseName.setTextSize(11);
        }
        //viewHolder.courseClassroom.setText(course.getClassroom());
        //viewHolder.courseTime.setText(course.getTime());
        //viewHolder.coursePeriod.setText(course.getPeriod());
        //if (course.getLength())
        return convertView;
    }

    private class ViewHolder {
        public LinearLayout courseLinear;
        public TextView courseName;
        //public TextView courseTime;
        //public TextView courseClassroom;
        //public TextView coursePeriod;
    }
}

package com.approxsoft.approxu.Model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import androidx.annotation.NonNull;

import com.approxsoft.approxu.R;

import java.util.List;

public class DataList extends ArrayAdapter<Data> {

    private Activity context;
    private List<Data> dataList;

    public DataList(Activity context, List<Data> dataList) {
        super(context, R.layout.list_layout, dataList);
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public View getView(int position, View covertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        @SuppressLint({"ViewHolder", "InflateParams"}) View listViewItem = inflater.inflate(R.layout.list_layout, null, true);
        TextView subjectTextView = listViewItem.findViewById(R.id.subject_text);
        TextView topicsTextView = listViewItem.findViewById(R.id.topic_text);
        TextView timeTextView = listViewItem.findViewById(R.id.date_text);

        Data data = dataList.get(position);

        subjectTextView.setText(data.getSubjectName());
        topicsTextView.setText(data.getSubjectTopics());
        timeTextView.setText(data.getSubjectDate());

        return listViewItem;
    }
}

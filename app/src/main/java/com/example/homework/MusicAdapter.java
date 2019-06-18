package com.example.homework;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.List;

public class MusicAdapter extends ArrayAdapter<Music> {
    private int resourceId;

    public MusicAdapter(Context context, int resource, List<Music> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // return super.getView(position, convertView, parent);
        Music music = getItem(position);
        View view;
        ViewHolder viewHolder;
        // 这里是为了优化加载
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name_view = view.findViewById(R.id.music_name);
            viewHolder.singer_view = view.findViewById(R.id.music_singer);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.singer_view.setText(music.getAuthor());
        viewHolder.name_view.setText(music.getTitle());
        return view;
    }

}

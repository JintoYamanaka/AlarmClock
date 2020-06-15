package com.example.alarmclock.listcomponent;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmclock.R;

public class ListViewHolder extends RecyclerView.ViewHolder {
    TextView musicName;
    TextView time;

    ListViewHolder(View itemView){
        super(itemView);
        this.musicName = itemView.findViewById(R.id.musicName);
        this.time = itemView.findViewById(R.id.time);
    }
}


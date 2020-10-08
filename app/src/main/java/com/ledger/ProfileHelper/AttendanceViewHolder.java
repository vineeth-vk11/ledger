package com.ledger.ProfileHelper;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ledger.R;

public class AttendanceViewHolder extends RecyclerView.ViewHolder {

    TextView name, date, time;

    public AttendanceViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.nameOfUser);
        date = itemView.findViewById(R.id.date);
        time = itemView.findViewById(R.id.time);

    }
}

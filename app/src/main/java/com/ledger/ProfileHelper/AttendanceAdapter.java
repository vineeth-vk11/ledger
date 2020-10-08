package com.ledger.ProfileHelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ledger.R;

import java.util.ArrayList;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceViewHolder> {

    Context context;
    ArrayList<AttendanceModel> attendanceModelArrayList;
    ArrayList<AttendanceModel> attendanceModelArrayList1;

    public AttendanceAdapter(Context context, ArrayList<AttendanceModel> attendanceModelArrayList) {
        this.context = context;
        this.attendanceModelArrayList = attendanceModelArrayList;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);;
        View view = layoutInflater.inflate(R.layout.list_item_attendance, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        holder.name.setText(attendanceModelArrayList.get(position).getName());
        holder.date.setText(attendanceModelArrayList.get(position).getDate());
        holder.time.setText(attendanceModelArrayList.get(position).getTime());

    }

    @Override
    public int getItemCount() {
        return attendanceModelArrayList.size();
    }
}

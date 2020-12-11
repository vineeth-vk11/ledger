package com.ledger.NavigationHelper.NavigationHelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ledger.R;

import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsViewHolder> {

    Context context;
    ArrayList<NotificationModel> notificationModelArrayList;

    public NotificationsAdapter(Context context, ArrayList<NotificationModel> notificationModelArrayList) {
        this.context = context;
        this.notificationModelArrayList = notificationModelArrayList;
    }

    @NonNull
    @Override
    public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_item_sales_notifications, parent, false);
        return new NotificationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsViewHolder holder, int position) {
        holder.name.setText(notificationModelArrayList.get(position).getName());
        holder.notes.setText(notificationModelArrayList.get(position).getNote());

        if(notificationModelArrayList.get(position).getType().equals("complaint")){
            holder.issueText.setVisibility(View.VISIBLE);
        }
        
    }

    @Override
    public int getItemCount() {
        return notificationModelArrayList.size();
    }
}

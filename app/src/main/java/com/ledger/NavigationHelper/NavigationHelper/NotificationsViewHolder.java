package com.ledger.NavigationHelper.NavigationHelper;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ledger.R;

public class NotificationsViewHolder extends RecyclerView.ViewHolder {

    TextView name, notes, issueText;

    public NotificationsViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.name);
        notes = itemView.findViewById(R.id.note);
        issueText = itemView.findViewById(R.id.issue);

    }
}

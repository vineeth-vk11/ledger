package com.ledger.SalesHelper;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ledger.R;

public class SalesViewHolder extends RecyclerView.ViewHolder {

    TextView name;
    CardView sales;
    ImageView image;
    ImageButton info;

    public SalesViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.name);
        sales = itemView.findViewById(R.id.sales);
        image = itemView.findViewById(R.id.imageView2);
        info = itemView.findViewById(R.id.info);

    }
}

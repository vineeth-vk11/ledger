package com.ledger.CompaniesHelper;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ledger.R;

public class CompaniesViewHolder extends RecyclerView.ViewHolder {

    TextView companyName;
    CardView company;
    ImageView image;

    public CompaniesViewHolder(@NonNull View itemView) {
        super(itemView);

        companyName = itemView.findViewById(R.id.nameOfUser);
        company = itemView.findViewById(R.id.company);
        image = itemView.findViewById(R.id.imageView2);
    }
}

package com.ledger.TransactionsHelper;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ledger.R;

public class TransactionsViewHolder extends RecyclerView.ViewHolder {

    TextView date;
    TextView particular;
    TextView debit;
    TextView credit;

    public TransactionsViewHolder(@NonNull View itemView) {
        super(itemView);

        date = itemView.findViewById(R.id.date);
        particular = itemView.findViewById(R.id.particular);
        debit = itemView.findViewById(R.id.debit);
        credit = itemView.findViewById(R.id.credit);

    }
}

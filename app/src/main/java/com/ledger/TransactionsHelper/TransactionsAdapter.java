package com.ledger.TransactionsHelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ledger.R;
import com.ledger.SalesHelper.SalesModel;
import com.ledger.SalesHelper.SalesViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsViewHolder> implements Filterable {

    Context context;
    ArrayList<TransactionsModel> transactionsModelArrayList;
    ArrayList<TransactionsModel> transactionsModelArrayListAll;

    public TransactionsAdapter(Context context, ArrayList<TransactionsModel> transactionsModelArrayList) {
        this.context = context;
        this.transactionsModelArrayList = transactionsModelArrayList;
        this.transactionsModelArrayListAll = new ArrayList<>(transactionsModelArrayList);
    }

    @NonNull
    @Override
    public TransactionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_transactions, parent, false);
        return new TransactionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsViewHolder holder, int position) {
        holder.date.setText(transactionsModelArrayList.get(position).getDate());
        holder.particular.setText(transactionsModelArrayList.get(position).getParticular());
        holder.debit.setText(transactionsModelArrayList.get(position).getDebit());
        holder.credit.setText(transactionsModelArrayList.get(position).getCredit());
    }

    @Override
    public int getItemCount() {
        return transactionsModelArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<TransactionsModel> filteredList = new ArrayList<>();
            if(constraint.toString().isEmpty()){
                filteredList.addAll(transactionsModelArrayListAll);
            }
            else {
                for(int i = 0; i<transactionsModelArrayListAll.size();i++){
                    if(transactionsModelArrayListAll.get(i).getDate().toLowerCase().contains(constraint.toString().toLowerCase())
                    || transactionsModelArrayListAll.get(i).getCredit().toLowerCase().contains(constraint.toString().toLowerCase())
                    || transactionsModelArrayListAll.get(i).getDebit().toLowerCase().contains(constraint.toString().toLowerCase())
                    || transactionsModelArrayListAll.get(i).getParticular().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(transactionsModelArrayListAll.get(i));
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            transactionsModelArrayList.clear();
            transactionsModelArrayList.addAll((Collection<? extends TransactionsModel>) results.values);
            notifyDataSetChanged();
        }
    };
}

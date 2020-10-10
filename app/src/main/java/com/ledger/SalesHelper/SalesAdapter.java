package com.ledger.SalesHelper;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.ledger.DealersHelper.DealersFragment;
import com.ledger.ProfileHelper.ProfileFragment;
import com.ledger.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SalesAdapter extends RecyclerView.Adapter<SalesViewHolder> implements Filterable {

    Context context;
    ArrayList<SalesModel> salesModelArrayList;
    String company;
    ArrayList<SalesModel> salesModelArrayListAll;

    public SalesAdapter(Context context, ArrayList<SalesModel> salesModelArrayList, String company) {
        this.context = context;
        this.salesModelArrayList = salesModelArrayList;
        this.company = company;
        this.salesModelArrayListAll = new ArrayList<>(salesModelArrayList);
    }

    @NonNull
    @Override
    public SalesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_sales, parent, false);
        return new SalesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalesViewHolder holder, final int position) {
        holder.name.setText(salesModelArrayList.get(position).getName());

        if(salesModelArrayList.get(position).getImage() != null){
            Picasso.get().load(salesModelArrayList.get(position).getImage()).into(holder.image);
        }
        else {
            holder.image.setImageResource(R.drawable.ic_sales);
        }

        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProfileFragment profileFragment = new ProfileFragment();

                Bundle bundle = new Bundle();
                bundle.putString("name", salesModelArrayList.get(position).getName());
                bundle.putString("email", salesModelArrayList.get(position).getEmail());
                bundle.putString("phone", salesModelArrayList.get(position).getPhone());
                bundle.putString("address", salesModelArrayList.get(position).getAddress());
                bundle.putString("company", company);
                bundle.putString("sales", salesModelArrayList.get(position).getId());
                bundle.putString("from", "sales");

                if(salesModelArrayList.get(position).getImage() != null){
                    bundle.putString("pic",salesModelArrayList.get(position).getImage());
                }
                else{
                    bundle.putString("pic","none");
                }

                profileFragment.setArguments(bundle);

                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, profileFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        holder.sales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DealersFragment dealersFragment = new DealersFragment();

                Bundle bundle = new Bundle();
                bundle.putString("userId", salesModelArrayList.get(position).getId());
                bundle.putString("company",company);
                bundle.putString("from","head");
                bundle.putString("name",salesModelArrayList.get(position).getName());
                dealersFragment.setArguments(bundle);

                AppCompatActivity activity = (AppCompatActivity)v.getContext();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame,dealersFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return salesModelArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<SalesModel> filteredList = new ArrayList<>();
            if(constraint.toString().isEmpty()){
                filteredList.addAll(salesModelArrayListAll);
            }
            else {
                for(int i = 0; i<salesModelArrayListAll.size();i++){
                    if(salesModelArrayListAll.get(i).getName().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(salesModelArrayListAll.get(i));
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            salesModelArrayList.clear();
            salesModelArrayList.addAll((Collection<? extends SalesModel>) results.values);
            notifyDataSetChanged();
        }
    };
}

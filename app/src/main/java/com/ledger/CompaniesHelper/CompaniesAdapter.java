package com.ledger.CompaniesHelper;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ledger.R;
import com.ledger.SalesHelper.SalesFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CompaniesAdapter extends RecyclerView.Adapter<CompaniesViewHolder> implements Filterable {

    Context context;
    ArrayList<CompaniesModels> companiesModelsArrayList;
    ArrayList<CompaniesModels> companiesModelsArrayListAll;

    public CompaniesAdapter(Context context, ArrayList<CompaniesModels> companiesModelsArrayList) {
        this.context = context;
        this.companiesModelsArrayList = companiesModelsArrayList;
        this.companiesModelsArrayListAll = new ArrayList<>(companiesModelsArrayList);
    }

    @NonNull
    @Override
    public CompaniesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_company, parent, false);
        return new CompaniesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CompaniesViewHolder holder, final int position) {
        holder.companyName.setText(companiesModelsArrayList.get(position).getCompanyName());

        final FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        db.collection("Companies").document(companiesModelsArrayList.get(position).getCompanyId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();

                if(documentSnapshot.exists()){
                    if(documentSnapshot.getString("pic") != null){
                        Picasso.get().load(documentSnapshot.getString("pic")).into(holder.image);
                    }
                    else {
                        holder.image.setImageResource(R.drawable.ic_building_office);
                    }

                    if(!documentSnapshot.getString("name").equals(companiesModelsArrayList.get(position).getCompanyName())){
                        companiesModelsArrayList.get(position).setCompanyName(documentSnapshot.getString("name"));
                        holder.companyName.setText(documentSnapshot.getString("name"));
                    }
                }
            }
        });


        holder.company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SalesFragment salesFragment = new SalesFragment();

                Bundle bundle = new Bundle();
                bundle.putString("company",companiesModelsArrayList.get(position).getCompanyId());
                bundle.putString("name",companiesModelsArrayList.get(position).getCompanyName());
                salesFragment.setArguments(bundle);

                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame,salesFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return companiesModelsArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<CompaniesModels> filteredList = new ArrayList<>();
            if(constraint.toString().isEmpty()){
                filteredList.addAll(companiesModelsArrayListAll);
            }
            else {
                for(int i = 0; i<companiesModelsArrayListAll.size();i++){
                    if(companiesModelsArrayListAll.get(i).getCompanyName().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(companiesModelsArrayListAll.get(i));
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            companiesModelsArrayList.clear();
            companiesModelsArrayList.addAll((Collection<? extends CompaniesModels>) results.values);
            notifyDataSetChanged();
        }
    };
}

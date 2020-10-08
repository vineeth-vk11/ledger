package com.ledger.DealersHelper;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ledger.ProfileHelper.ProfileFragment;
import com.ledger.R;
import com.ledger.TransactionsHelper.TransactionsFragment;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DealersAdapter extends RecyclerView.Adapter<DealersViewHolder> implements Filterable {

    Context context;
    ArrayList<DealersModel> dealersModelArrayList;
    ArrayList<DealersModel> dealersModelArrayListAll;
    String from;

    String date;

    public DealersAdapter(Context context, ArrayList<DealersModel> dealersModelArrayList, String from) {
        this.context = context;
        this.dealersModelArrayList = dealersModelArrayList;
        this.dealersModelArrayListAll = new ArrayList<>(dealersModelArrayList);
        this.from = from;
    }

    @NonNull
    @Override
    public DealersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_dealer, parent, false);
        return new DealersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DealersViewHolder holder, final int position) {
        holder.name.setText(dealersModelArrayList.get(position).getName());

        holder.image.setImageResource(R.drawable.ic_dealer);


        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment profileFragment = new ProfileFragment();

                Bundle bundle = new Bundle();
                bundle.putString("name", dealersModelArrayList.get(position).getName());
                bundle.putString("email", dealersModelArrayList.get(position).getEmail());
                bundle.putString("phone", dealersModelArrayList.get(position).getPhone());
                bundle.putString("address", dealersModelArrayList.get(position).getAddress());
                bundle.putString("company",dealersModelArrayList.get(position).getCompany());
                bundle.putString("sales",dealersModelArrayList.get(position).getSalesId());
                bundle.putString("from","dealer");

                if(dealersModelArrayList.get(position).getImage() != null){
                    bundle.putString("pic",dealersModelArrayList.get(position).getImage());
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

        date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        final String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        final FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        db.collection("Companies").document(dealersModelArrayList.get(position).getCompany())
                .collection("sales").document(dealersModelArrayList.get(position).getSalesId())
                .collection("attendance").document(dealersModelArrayList.get(position).getId()+" "+date)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();

                if(documentSnapshot.exists()){
                    holder.attendance.setChecked(true);
                    holder.attendance.setEnabled(false);
                }
                else {
                    holder.attendance.setEnabled(true);
                }
            }
        });

        if(from.equals("sales")){
            holder.attendance.setVisibility(View.VISIBLE);
        }
        else {
            holder.attendance.setVisibility(View.GONE);
        }

        holder.attendance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){

                    db.collection("Companies").document(dealersModelArrayList.get(position).getCompany())
                            .collection("sales").document(dealersModelArrayList.get(position).getSalesId())
                            .collection("attendance").document(dealersModelArrayList.get(position).getId()+" "+date)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult();

                            if(documentSnapshot.exists()){
                            }
                            else {
                                new AlertDialog.Builder(context)
                                        .setTitle("Mark attendance")
                                        .setMessage("Are you sure you want to mark the attendance?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                new AlertDialog.Builder(context)
                                                        .setTitle("Confirmation for marking attendance")
                                                        .setMessage("Confirm attendance?")
                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                                HashMap<String, String> attendance = new HashMap<>();
                                                                attendance.put("name", dealersModelArrayList.get(position).getName());
                                                                attendance.put("date", date);
                                                                attendance.put("id",dealersModelArrayList.get(position).getId());
                                                                attendance.put("time", String.valueOf(currentTime));

                                                                FirebaseFirestore db;
                                                                db = FirebaseFirestore.getInstance();

                                                                db.collection("Companies").document(dealersModelArrayList.get(position).getCompany())
                                                                        .collection("sales").document(dealersModelArrayList.get(position).getSalesId())
                                                                        .collection("attendance").document(dealersModelArrayList.get(position).getId()+" "+date)
                                                                        .set(attendance).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        holder.attendance.setEnabled(false);
                                                                        Toast.makeText(context, "Attendance marked",Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                        })
                                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                buttonView.setChecked(false);
                                                            }
                                                        }).show();
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                buttonView.setChecked(false);
                                            }
                                        }).show();
                            }
                        }
                    });

                }
            }

        });

        holder.dealer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TransactionsFragment transactionsFragment = new TransactionsFragment();

                Bundle bundle = new Bundle();
                bundle.putString("userId", dealersModelArrayList.get(position).getId());
                bundle.putString("company",dealersModelArrayList.get(position).getCompany());
                bundle.putString("sales",dealersModelArrayList.get(position).getSalesId());
                bundle.putString("name",dealersModelArrayList.get(position).getName());
                bundle.putString("address",dealersModelArrayList.get(position).getAddress());
                bundle.putString("number",dealersModelArrayList.get(position).getPhone());
                transactionsFragment.setArguments(bundle);

                AppCompatActivity activity = (AppCompatActivity)v.getContext();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame,transactionsFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return dealersModelArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<DealersModel> filteredList = new ArrayList<>();
            if(constraint.toString().isEmpty()){
                filteredList.addAll(dealersModelArrayListAll);
            }
            else {
                for(int i = 0; i<dealersModelArrayListAll.size();i++){
                    if(dealersModelArrayListAll.get(i).getName().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(dealersModelArrayListAll.get(i));
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dealersModelArrayList.clear();
            dealersModelArrayList.addAll((Collection<? extends DealersModel>) results.values);
            notifyDataSetChanged();
        }
    };

}

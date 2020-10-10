package com.ledger.SalesHelper;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ledger.R;

import java.util.ArrayList;

public class SalesFragment extends Fragment {

    RecyclerView sales;
    FirebaseFirestore db;
    ArrayList<SalesModel> salesModelArrayList;
    String company;

    SearchView searchView;
    SalesAdapter salesAdapter;

    ImageButton sort;
    ImageButton download;
    ImageButton share;

    ImageView empty;
    ProgressBar progressBar;

    TextView fragment;

    private FirebaseAnalytics firebaseAnalytics;

    TextView toolbarText;
    String name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sales, container, false);

        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        Bundle bundle = getArguments();
        company = bundle.getString("company");
        name = bundle.getString("name");

        sort = getActivity().findViewById(R.id.sort);
        download = getActivity().findViewById(R.id.download);
        share = getActivity().findViewById(R.id.share);

        sort.setVisibility(View.INVISIBLE);
        download.setVisibility(View.INVISIBLE);
        share.setVisibility(View.INVISIBLE);
        fragment = getActivity().findViewById(R.id.nameOfUser);

        fragment.setText("Sales");

        empty = view.findViewById(R.id.empty);
        progressBar = view.findViewById(R.id.progressBar3);

        sales = view.findViewById(R.id.salesRecycler);
        db = FirebaseFirestore.getInstance();
        salesModelArrayList = new ArrayList<>();

        sales.setLayoutManager(new LinearLayoutManager(getContext()));
        sales.setHasFixedSize(true);

        toolbarText = getActivity().findViewById(R.id.nameOfUser);
        toolbarText.setText(name);
        toolbarText.setTextSize(24);

        db.collection("Companies").document(company).collection("sales").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                getSales();
            }
        });

        searchView = view.findViewById(R.id.salesSearch);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                salesAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return view;
    }

    private void getSales(){
        progressBar.setVisibility(View.VISIBLE);
        db.collection("Companies").document(company).collection("sales").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                salesModelArrayList.clear();
                for(DocumentSnapshot documentSnapshot: task.getResult()){

                    SalesModel salesModel = new SalesModel();
                    salesModel.setName(documentSnapshot.getString("name"));
                    salesModel.setId(documentSnapshot.getId());
                    salesModel.setEmail(documentSnapshot.getString("email"));
                    salesModel.setPhone(documentSnapshot.getString("phoneNumber"));
                    salesModel.setAddress(documentSnapshot.getString("address"));

                    if(documentSnapshot.getString("pic") != null){
                        salesModel.setImage(documentSnapshot.getString("pic"));
                    }

                    Log.i("name",documentSnapshot.getString("name"));

                    salesModelArrayList.add(salesModel);
                }

                salesAdapter = new SalesAdapter(getContext(), salesModelArrayList, company);
                sales.setAdapter(salesAdapter);
                progressBar.setVisibility(View.INVISIBLE);
                if(salesModelArrayList.size() == 0){
                    empty.setVisibility(View.VISIBLE);
                }
                else{
                    empty.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
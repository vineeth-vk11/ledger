package com.ledger.CompaniesHelper;

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


public class CompaniesFragment extends Fragment {

    RecyclerView companies;
    FirebaseFirestore db;
    ArrayList<CompaniesModels> companiesModelsArrayList;
    ArrayList<String> companiesList = new ArrayList<>();

    String userId;

    SearchView searchView;

    CompaniesAdapter companiesAdapter;

    String name;

    ImageButton sort;
    ImageButton download;
    ImageButton share;

    ImageView empty;
    ProgressBar progressBar;

    TextView fragment;

    private FirebaseAnalytics firebaseAnalytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_companies, container, false);

        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        Bundle bundle = getArguments();
        userId = bundle.getString("userId");

        sort = getActivity().findViewById(R.id.sort);
        download = getActivity().findViewById(R.id.download);
        share = getActivity().findViewById(R.id.share);
        fragment = getActivity().findViewById(R.id.nameOfUser);

        fragment.setText("Companies");

        sort.setVisibility(View.INVISIBLE);
        download.setVisibility(View.INVISIBLE);
        share.setVisibility(View.INVISIBLE);

        empty = view.findViewById(R.id.empty);
        progressBar = view.findViewById(R.id.progressBar2);

        companies = view.findViewById(R.id.companiesRecycler);
        db = FirebaseFirestore.getInstance();
        companiesModelsArrayList = new ArrayList<>();

        companies.setLayoutManager(new LinearLayoutManager(getContext()));
        companies.setHasFixedSize(true);

        db.collection("Heads").document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                getCompanies();
            }
        });

        searchView = view.findViewById(R.id.companiesSearch);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                companiesAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return view;
    }


    private void getCompanies(){
        progressBar.setVisibility(View.VISIBLE);
        db.collection("Heads").document(userId).collection("companies").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                companiesModelsArrayList.clear();
                for(final DocumentSnapshot documentSnapshot: task.getResult()){

                    final CompaniesModels companiesModels = new CompaniesModels();
                    companiesModels.setCompanyId(documentSnapshot.getId());

                    db.collection("Companies").document(documentSnapshot.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot1 = task.getResult();

                        }
                    });

                    companiesModels.setCompanyName(documentSnapshot.getString("name"));
                    companiesModelsArrayList.add(companiesModels);
                }

                companiesAdapter = new CompaniesAdapter(getContext(), companiesModelsArrayList);
                companies.setAdapter(companiesAdapter);
                progressBar.setVisibility(View.INVISIBLE);
                if(companiesModelsArrayList.size()==0){
                    empty.setVisibility(View.VISIBLE);
                }
                else {
                    empty.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
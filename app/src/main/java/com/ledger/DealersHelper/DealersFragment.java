package com.ledger.DealersHelper;

import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ledger.R;

import java.util.ArrayList;


public class DealersFragment extends Fragment {

    RecyclerView dealers;
    FirebaseFirestore db;
    ArrayList<DealersModel> dealersModelArrayList;
    String company;
    String salesId;

    SearchView searchView;
    DealersAdapter dealersAdapter;

    ImageButton sort;
    ImageButton download;
    ImageButton share;

    String from;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dealers, container, false);

        Bundle bundle = getArguments();
        salesId = bundle.getString("userId");
        company = bundle.getString("company");
        from = bundle.getString("from");

        Log.i("from",from);

        sort = getActivity().findViewById(R.id.sort);
        download = getActivity().findViewById(R.id.download);
        share = getActivity().findViewById(R.id.share);

        sort.setVisibility(View.INVISIBLE);
        download.setVisibility(View.INVISIBLE);
        share.setVisibility(View.INVISIBLE);

        dealers = view.findViewById(R.id.dealersRecycler);
        db = FirebaseFirestore.getInstance();
        dealersModelArrayList = new ArrayList<>();

        dealers.setLayoutManager(new LinearLayoutManager(getContext()));
        dealers.setHasFixedSize(true);

        db.collection("Companies").document(company).collection("sales").document(salesId).collection("dealers").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                getDealers();
            }
        });

        searchView = view.findViewById(R.id.dealerSearch);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                dealersAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return view;
    }

    private void getDealers(){
        db.collection("Companies").document(company).collection("sales").document(salesId).collection("dealers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                dealersModelArrayList.clear();
                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    DealersModel dealersModel = new DealersModel();
                    dealersModel.setName(documentSnapshot.getString("name"));
                    dealersModel.setId(documentSnapshot.getId());
                    dealersModel.setCompany(company);
                    dealersModel.setSalesId(salesId);
                    dealersModel.setEmail(documentSnapshot.getString("email"));
                    dealersModel.setPhone(documentSnapshot.getString("phoneNumber"));
                    dealersModel.setAddress(documentSnapshot.getString("address"));

                    dealersModelArrayList.add(dealersModel);
                }

                dealersAdapter = new DealersAdapter(getContext(), dealersModelArrayList, from);
                dealers.setAdapter(dealersAdapter);
            }
        });
    }
}
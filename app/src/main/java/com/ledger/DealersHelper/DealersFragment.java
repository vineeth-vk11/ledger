package com.ledger.DealersHelper;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import com.ledger.NavigationHelper.SalesNotificationsFragment;
import com.ledger.R;

import java.util.ArrayList;
import java.util.HashMap;


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

    ImageView empty;
    ProgressBar progressBar;

    TextView fragment;

    private FirebaseAnalytics firebaseAnalytics;

    String name;

    int flag, noFlag, healthOk, healthGood, healthBad, total;

    ImageButton notifications;
    TextView notificationsNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dealers, container, false);
        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        Bundle bundle = getArguments();
        salesId = bundle.getString("userId");
        company = bundle.getString("company");
        from = bundle.getString("from");
        name = bundle.getString("name");

        Log.i("from",from);

        sort = getActivity().findViewById(R.id.sort);
        download = getActivity().findViewById(R.id.download);
        share = getActivity().findViewById(R.id.share);
        notifications = getActivity().findViewById(R.id.notifications);
        notificationsNumber = getActivity().findViewById(R.id.notificationsNumber);

        notifications.setVisibility(View.VISIBLE);
        notificationsNumber.setVisibility(View.VISIBLE);

        fragment = getActivity().findViewById(R.id.nameOfUser);

        fragment.setText("Dealers");
        fragment.setText(name);
        fragment.setTextSize(24);

        sort.setVisibility(View.INVISIBLE);
        download.setVisibility(View.INVISIBLE);
        share.setVisibility(View.INVISIBLE);

        empty = view.findViewById(R.id.empty);
        progressBar = view.findViewById(R.id.progressBar4);

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

        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalesNotificationsFragment salesNotificationsFragment = new SalesNotificationsFragment();

                Bundle bundle1 = new Bundle();
                bundle1.putString("userId", salesId);
                bundle1.putString("company",company);

                salesNotificationsFragment.setArguments(bundle1);

                FragmentManager fragmentManager2 = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                fragmentTransaction2.replace(R.id.main_frame,salesNotificationsFragment);
                fragmentTransaction2.addToBackStack(null);
                fragmentTransaction2.commit();

            }
        });

        return view;
    }

    private void getDealers(){
        progressBar.setVisibility(View.VISIBLE);
        healthBad = 0;
        healthOk = 0;
        healthBad = 0;
        flag = 0;
        noFlag = 0;

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

                    if(documentSnapshot.getString("healthValue") != null){
                        dealersModel.setHealthValue(documentSnapshot.getString("healthValue"));

                        if(documentSnapshot.getString("healthValue").equals("Good")){
                            healthGood+= 1;
                        }
                        else if(documentSnapshot.getString("healthValue").equals("Ok")){
                            healthOk += 1;
                        }
                        else if(documentSnapshot.getString("healthValue").equals("Bad")){
                            healthBad += 1;
                        }
                    }

                    if(documentSnapshot.getString("osLimit") != null){
                        dealersModel.setOsLimit(documentSnapshot.getString("osLimit"));
                    }
                    else {
                        dealersModel.setOsLimit("0");
                    }

                    if(documentSnapshot.getString("pic") != null){
                        dealersModel.setImage(documentSnapshot.getString("pic"));
                    }

                    if(documentSnapshot.getString("outstanding") != null){
                        dealersModel.setOutstanding(documentSnapshot.getString("outstanding"));
                    }
                    else {
                        dealersModel.setOutstanding("0");
                    }

                    if(documentSnapshot.getString("osLimit") != null && documentSnapshot.getString("healthValue") != null){

                        if(Integer.parseInt(documentSnapshot.getString("osLimit")) >= Integer.parseInt(documentSnapshot.getString("outstanding"))){
                            noFlag = noFlag+1;
                        }
                        else {
                            flag = flag+1;
                        }

                        total += 1;
                    }

                    dealersModelArrayList.add(dealersModel);
                }

                dealersAdapter = new DealersAdapter(getContext(), dealersModelArrayList,getActivity().getSupportFragmentManager(), from);
                dealers.setAdapter(dealersAdapter);
                if(dealersModelArrayList.size()==0){
                    empty.setVisibility(View.VISIBLE);
                }
                else {
                    empty.setVisibility(View.INVISIBLE);
                }

                HashMap<String, Object> data = new HashMap<>();
                data.put("flagged",String.valueOf(flag));
                data.put("notFlagged",String.valueOf(noFlag));
                data.put("GoodHealth",String.valueOf(healthGood));
                data.put("OkHealth",String.valueOf(healthOk));
                data.put("BadHealth",String.valueOf(healthBad));
                data.put("total",String.valueOf(total));

                db.collection("Companies").document(company).collection("sales").document(salesId).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
    }
}
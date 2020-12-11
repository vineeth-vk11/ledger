package com.ledger.NavigationHelper;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ledger.DealersHelper.AttendanceNotesDialog;
import com.ledger.NavigationHelper.NavigationHelper.NotificationModel;
import com.ledger.NavigationHelper.NavigationHelper.NotificationsAdapter;
import com.ledger.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class SalesNotificationsFragment extends Fragment {

    String company, salesId;

    RecyclerView notificationsRecycler;
    FirebaseFirestore db;
    ArrayList<NotificationModel> notificationModelArrayList;

    TextView fragment;

    FloatingActionButton floatingActionButton;

    ImageButton notifications;
    TextView notificationsNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sales_notifications, container, false);

        floatingActionButton = view.findViewById(R.id.floating_action_button);
        notifications = getActivity().findViewById(R.id.notifications);
        notificationsNumber = getActivity().findViewById(R.id.notificationsNumber);

        notifications.setVisibility(View.INVISIBLE);
        notificationsNumber.setVisibility(View.INVISIBLE);

        Bundle bundle = getArguments();

        company = bundle.getString("company");
        salesId = bundle.getString("userId");

        fragment = getActivity().findViewById(R.id.nameOfUser);
        fragment.setText("Notifications");

        notificationsRecycler = view.findViewById(R.id.notificationsRecycler);
        notificationsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationsRecycler.setHasFixedSize(true);

        db = FirebaseFirestore.getInstance();

        notificationModelArrayList = new ArrayList<>();

        getNotifications();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCustomNotificationDialog addCustomNotificationDialog = new AddCustomNotificationDialog(company, salesId);
                addCustomNotificationDialog.show(getChildFragmentManager(), "Add Notes");
            }
        });
        return view;
    }

    private void getNotifications(){
        final String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Log.i("date", date);

        db.collection("Companies").document(company).collection("sales").document(salesId)
                .collection("notes").document(date).collection("notes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                notificationModelArrayList.clear();

                for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots.getDocuments()){
                    NotificationModel notificationModel = new NotificationModel();

                    notificationModel.setDate(documentSnapshot.getString("date"));
                    notificationModel.setName(documentSnapshot.getString("name"));
                    notificationModel.setNote(documentSnapshot.getString("note"));
                    notificationModel.setType(documentSnapshot.getString("type"));

                    Log.i("name",documentSnapshot.getString("name"));

                    if(documentSnapshot.getString("date").equals(date)){
                        notificationModelArrayList.add(notificationModel);
                    }
                }

                getComplaints();

            }
        });
    }

    private void getComplaints(){
        db.collection("Companies").document(company).collection("sales").document(salesId)
                .collection("complaints").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot: task.getResult()){

                    NotificationModel notificationModel = new NotificationModel();
                    notificationModel.setNote(documentSnapshot.getString("note"));
                    notificationModel.setName(documentSnapshot.getString("name"));
                    notificationModel.setType(documentSnapshot.getString("type"));

                    notificationModelArrayList.add(notificationModel);
                }

                NotificationsAdapter notificationsAdapter = new NotificationsAdapter(getContext(), notificationModelArrayList);
                notificationsRecycler.setAdapter(notificationsAdapter);
            }
        });
    }
}
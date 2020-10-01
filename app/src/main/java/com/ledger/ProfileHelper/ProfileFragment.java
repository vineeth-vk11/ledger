package com.ledger.ProfileHelper;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ledger.R;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {

    String name, email, phoneNumber, address, company, sales;

    TextView txtName, txtEmail, txtPhone, txtAddress;

    RecyclerView attendance;
    ArrayList<AttendanceModel> attendanceModelArrayList;
    FirebaseFirestore db;

    String from;

    TextView textView;

    TextView fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        Bundle bundle = getArguments();

        fragment = getActivity().findViewById(R.id.name);

        fragment.setText("Profile");

        name = bundle.getString("name");
        email = bundle.getString("email");
        phoneNumber = bundle.getString("phone");
        address = bundle.getString("address");
        company = bundle.getString("company");
        sales = bundle.getString("sales");
        from = bundle.getString("from");

        txtName = view.findViewById(R.id.name);
        txtEmail = view.findViewById(R.id.email);
        txtAddress = view.findViewById(R.id.address);
        txtPhone = view.findViewById(R.id.phone);
        textView = view.findViewById(R.id.textView33);

        txtName.setText(name);
        txtEmail.setText(email);
        txtAddress.setText(address);
        txtPhone.setText(phoneNumber);


        attendance = view.findViewById(R.id.attendanceRecycler);
        attendance.setLayoutManager(new LinearLayoutManager(getContext()));
        attendance.setHasFixedSize(true);

        attendanceModelArrayList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        if(from.equals("sales")){
            textView.setVisibility(View.VISIBLE);
            db.collection("Companies").document(company).collection("sales").document(sales).collection("attendance").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    attendanceModelArrayList.clear();
                    for(DocumentSnapshot documentSnapshot: task.getResult()){

                        AttendanceModel attendanceModel = new AttendanceModel();
                        attendanceModel.setName(documentSnapshot.getString("name"));
                        attendanceModel.setDate(documentSnapshot.getString("date"));
                        attendanceModel.setTime(documentSnapshot.getString("time"));

                        attendanceModelArrayList.add(attendanceModel);
                    }

                    AttendanceAdapter attendanceAdapter = new AttendanceAdapter(getContext(),attendanceModelArrayList);
                    attendance.setAdapter(attendanceAdapter);
                }
            });

        }


        return view;
    }
}
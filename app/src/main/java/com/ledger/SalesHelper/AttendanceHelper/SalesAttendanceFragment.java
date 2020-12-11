package com.ledger.SalesHelper.AttendanceHelper;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ledger.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SalesAttendanceFragment extends Fragment {

    RecyclerView attendance;
    ArrayList<AttendanceModel> attendanceModelArrayList;
    ArrayList<AttendanceModel> attendanceModelArrayList1;
    FirebaseFirestore db;

    String  company, sales;

    Button dateSelector;
    ImageButton download;

    String startDateExisting;
    String endDateExisting;

    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sales_attendance, container, false);

        Bundle bundle = getArguments();

        company = bundle.getString("company");
        sales = bundle.getString("sales");

        attendance = view.findViewById(R.id.attendanceRecycler);
        attendance.setLayoutManager(new LinearLayoutManager(getContext()));
        attendance.setHasFixedSize(true);

        attendanceModelArrayList = new ArrayList<>();
        attendanceModelArrayList1 = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        getAttendance(date);

        dateSelector = view.findViewById(R.id.imageButton4);
        download = view.findViewById(R.id.imageButton5);

        dateSelector.setText(date);

        dateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDateButton();
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    private void getAttendance(final String dateSelected){

        db.collection("Companies").document(company).collection("sales").document(sales).collection("attendance").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                attendanceModelArrayList.clear();
                for(DocumentSnapshot documentSnapshot: task.getResult()){

                    AttendanceModel attendanceModel = new AttendanceModel();
                    attendanceModel.setName(documentSnapshot.getString("name"));
                    attendanceModel.setDate(documentSnapshot.getString("date"));
                    attendanceModel.setTime(documentSnapshot.getString("time"));

                    if(dateSelected.equals(documentSnapshot.getString("date"))){
                        attendanceModelArrayList.add(attendanceModel);
                    }

                    attendanceModelArrayList1.add(attendanceModel);

                }

                Log.i("size", String.valueOf(attendanceModelArrayList.size()));
                Log.i("Size1", String.valueOf(attendanceModelArrayList1.size()));

                AttendanceAdapter attendanceAdapter = new AttendanceAdapter(getContext(),attendanceModelArrayList);
                attendance.setAdapter(attendanceAdapter);
            }
        });
    }

    private void handleDateButton(){

        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int newMonth = month + 1;

                String date = dayOfMonth+ "/" +  newMonth + "/" + year;
                dateSelector.setText(date);
                getAttendance(date);
            }
        }, year, month, date);

        datePickerDialog.show();
    }
}
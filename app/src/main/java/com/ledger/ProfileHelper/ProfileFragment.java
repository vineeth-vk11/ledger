package com.ledger.ProfileHelper;

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
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ledger.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    String name, email, phoneNumber, address, company, sales, pic;

    TextView txtName, txtEmail, txtPhone, txtAddress;

    RecyclerView attendance;
    ArrayList<AttendanceModel> attendanceModelArrayList;
    ArrayList<AttendanceModel> attendanceModelArrayList1;
    FirebaseFirestore db;

    String from;

    TextView textView;

    TextView fragment;
    ImageButton dateSel;

    private FirebaseAnalytics firebaseAnalytics;

    TextView textView1;

    CircleImageView circleImageView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        Bundle bundle = getArguments();

        fragment = getActivity().findViewById(R.id.nameOfUser);

        fragment.setText("Profile");

        name = bundle.getString("name");
        email = bundle.getString("email");
        phoneNumber = bundle.getString("phone");
        address = bundle.getString("address");
        company = bundle.getString("company");
        sales = bundle.getString("sales");
        from = bundle.getString("from");
        pic = bundle.getString("pic");


        textView1 = view.findViewById(R.id.textView34);
        dateSel = view.findViewById(R.id.imageButton2);
        txtName = view.findViewById(R.id.nameOfUser);
        txtEmail = view.findViewById(R.id.email);
        txtAddress = view.findViewById(R.id.address);
        txtPhone = view.findViewById(R.id.phone);
        textView = view.findViewById(R.id.textView33);
        circleImageView = view.findViewById(R.id.profilePic);

        if(pic.equals("none")){
            circleImageView.setImageResource(R.drawable.ic_sales);
        }
        else {
            Picasso.get().load(pic).into(circleImageView);
        }

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
            dateSel.setVisibility(View.VISIBLE);
            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            getAttendance(date);
        }

        dateSel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        return view;
    }

    private void getAttendance(final String date){
        db.collection("Companies").document(company).collection("sales").document(sales).collection("attendance").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                attendanceModelArrayList.clear();
                for(DocumentSnapshot documentSnapshot: task.getResult()){

                    AttendanceModel attendanceModel = new AttendanceModel();
                    attendanceModel.setName(documentSnapshot.getString("name"));
                    attendanceModel.setDate(documentSnapshot.getString("date"));
                    attendanceModel.setTime(documentSnapshot.getString("time"));

                    if(documentSnapshot.getString("date").equals(date)){
                        attendanceModelArrayList.add(attendanceModel);
                    }
                }

                if(attendanceModelArrayList.size() == 0){
                    textView1.setText("No attendance on " + date);
                    textView1.setVisibility(View.VISIBLE);
                }
                else {
                    textView1.setVisibility(View.INVISIBLE);
                }

                AttendanceAdapter attendanceAdapter = new AttendanceAdapter(getContext(),attendanceModelArrayList);
                attendance.setAdapter(attendanceAdapter);
            }
        });
    }

    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONDAY),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        int correctedMonth = month+1;
        String day = String.valueOf(dayOfMonth);
        String sMonth = String.valueOf(correctedMonth);

        if(dayOfMonth<10){
            day = "0" + day;
        }

        if(correctedMonth<10){
            sMonth = "0"+sMonth;
        }

        String selectedDate = day + "-" + sMonth + "-" + year;
        Log.i("date",selectedDate);
        getAttendance(selectedDate);

    }
}
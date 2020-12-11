package com.ledger.ProfileHelper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ledger.DealersHelper.AttendanceNotesDialog;
import com.ledger.R;
import com.ledger.SalesHelper.AttendanceHelper.AttendanceModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    String name, email, phoneNumber, address, company, sales, pic, salesTarget, collectionTarget, from;

    TextView txtName, txtEmail, txtPhone, txtAddress;

    TextView fragment;
    ImageButton callButton;
    Button salesAndCollectionTargets;

    CircleImageView circleImageView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        txtName = view.findViewById(R.id.nameOfUser);
        txtEmail = view.findViewById(R.id.email);
        txtAddress = view.findViewById(R.id.address);
        txtPhone = view.findViewById(R.id.phone);
        circleImageView = view.findViewById(R.id.profilePic);
        callButton = view.findViewById(R.id.callButton);
        salesAndCollectionTargets = view.findViewById(R.id.salesAndCollectionTargets);

        Bundle bundle = getArguments();

        fragment = getActivity().findViewById(R.id.nameOfUser);
        fragment.setText("Profile");

        name = bundle.getString("name");
        email = bundle.getString("email");
        phoneNumber = bundle.getString("phone");
        address = bundle.getString("address");
        company = bundle.getString("company");
        sales = bundle.getString("sales");
        pic = bundle.getString("pic");

        if(bundle.getString("from") != null){
            from = bundle.getString("from");
        }

        if(from.equals("sales")){
            salesTarget = bundle.getString("salesTarget");
            collectionTarget = bundle.getString("collectionTarget");
        }
        else {
            salesAndCollectionTargets.setVisibility(View.GONE);
        }

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+phoneNumber));
                startActivity(intent);
            }
        });

        txtPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+phoneNumber));
                startActivity(intent);
            }
        });

        if(pic.equals("none")){
            circleImageView.setImageResource(R.drawable.ic_sales);
        }
        else {
            Picasso.get().load(pic).into(circleImageView);
        }

        salesAndCollectionTargets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetSalesCollectionTargetsDialog setSalesCollectionTargetsDialog = new SetSalesCollectionTargetsDialog(salesTarget, collectionTarget, sales, company);

                setSalesCollectionTargetsDialog.show(getChildFragmentManager(), "Set Targets");
            }
        });

        txtName.setText(name);
        txtEmail.setText(email);
        txtAddress.setText(address);
        txtPhone.setText(phoneNumber);

        return view;
    }
}
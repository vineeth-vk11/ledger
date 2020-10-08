package com.ledger.NavigationHelper;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ledger.R;


public class CompanyProfileFragment extends Fragment {

    TextView fragment;

    private FirebaseAnalytics firebaseAnalytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        fragment = getActivity().findViewById(R.id.nameOfUser);

        fragment.setText("Company Profile");

        return inflater.inflate(R.layout.fragment_company_profile, container, false);
    }
}
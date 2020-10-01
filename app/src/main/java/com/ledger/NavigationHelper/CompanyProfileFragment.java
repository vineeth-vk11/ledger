package com.ledger.NavigationHelper;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ledger.R;


public class CompanyProfileFragment extends Fragment {

    TextView fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragment = getActivity().findViewById(R.id.name);

        fragment.setText("Company Profile");

        return inflater.inflate(R.layout.fragment_company_profile, container, false);
    }
}
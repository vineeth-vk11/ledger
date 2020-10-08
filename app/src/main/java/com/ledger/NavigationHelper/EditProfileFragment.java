package com.ledger.NavigationHelper;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.ledger.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;


public class EditProfileFragment extends Fragment {

    ImageButton edit;
    CircleImageView imageView;

    String userId;

    TextView fragment;

    TextView name, email, phone, address;

    private FirebaseAnalytics firebaseAnalytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        Bundle bundle = getArguments();
        userId = bundle.getString("id");

        fragment = getActivity().findViewById(R.id.nameOfUser);

        fragment.setText("Profile");

        edit = view.findViewById(R.id.editImage);
        imageView = view.findViewById(R.id.profile_image);

        name = view.findViewById(R.id.nameOfUser);
        email = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);
        address = view.findViewById(R.id.address);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(getActivity());
            }
        });

        final FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        db.collection("Users").document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if(documentSnapshot.getString("pic") != null){
                    String image = documentSnapshot.getString("pic");
                    Picasso.get().load(image).into(imageView);
                }

                Log.i("name",documentSnapshot.getString("name"));

                name.setText(documentSnapshot.getString("name"));
                email.setText(documentSnapshot.getString("email"));
                phone.setText(documentSnapshot.getString("phoneNumber"));
                address.setText(documentSnapshot.getString("address"));

            }
        });

        return view;
    }
}
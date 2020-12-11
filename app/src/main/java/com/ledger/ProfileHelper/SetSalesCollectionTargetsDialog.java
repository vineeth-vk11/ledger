package com.ledger.ProfileHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ledger.R;

import org.w3c.dom.Document;

import java.util.HashMap;

public class SetSalesCollectionTargetsDialog extends AppCompatDialogFragment {

    String existingSalesTarget, existingCollectionTarget, salesId, company;

    public SetSalesCollectionTargetsDialog(String existingSalesTarget, String existingCollectionTarget, String salesId, String company) {
        this.existingSalesTarget = existingSalesTarget;
        this.existingCollectionTarget = existingCollectionTarget;
        this.salesId = salesId;
        this.company = company;
    }

    EditText salesTarget, collectionTarget;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_sales_collection_targets, null);

        salesTarget = view.findViewById(R.id.sales_target_edit);
        collectionTarget = view.findViewById(R.id.collection_target_edit);

        final FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        db.collection("Companies").document(company).collection("sales").document(salesId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();

                if(documentSnapshot.getString("salesTarget") != null){
                    salesTarget.setText(documentSnapshot.getString("salesTarget"));
                }

                if(documentSnapshot.getString("collectionTarget") != null){
                    collectionTarget.setText(documentSnapshot.getString("collectionTarget"));
                }
            }
        });

        builder.setView(view)
                .setTitle("Set Targets")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String enteredSalesTarget = salesTarget.getText().toString();
                        String enteredCollectionTarget = collectionTarget.getText().toString();

                        if(TextUtils.isEmpty(enteredSalesTarget)){
                        }
                        else if(TextUtils.isEmpty(enteredCollectionTarget)){
                        }
                        else {
                            HashMap<String, Object> data = new HashMap<>();
                            data.put("salesTarget",enteredSalesTarget);
                            data.put("collectionTarget",enteredCollectionTarget);

                            FirebaseFirestore db;
                            db = FirebaseFirestore.getInstance();

                            db.collection("Companies").document(company).collection("sales").document(salesId)
                                    .update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                        }
                    }
                });

        return builder.create();
    }
}

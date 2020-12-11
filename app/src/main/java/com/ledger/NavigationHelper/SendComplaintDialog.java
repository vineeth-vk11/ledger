package com.ledger.NavigationHelper;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.ledger.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SendComplaintDialog extends AppCompatDialogFragment {

    EditText complaint;

    String company, sales, name;

    public SendComplaintDialog(String company, String sales, String name) {
        this.company = company;
        this.sales = sales;
        this.name = name;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_send_complaint, null);

        complaint = view.findViewById(R.id.complaint_edit);

        final String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        builder.setView(view)
                .setTitle("Send Complaint")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String enteredComplaint = complaint.getText().toString();

                        if(TextUtils.isEmpty(enteredComplaint)){

                        }
                        else {
                            HashMap<String, Object> data = new HashMap<>();
                            data.put("note",enteredComplaint);
                            data.put("date",date);
                            data.put("name",name);
                            data.put("type","complaint");

                            FirebaseFirestore db;
                            db = FirebaseFirestore.getInstance();

                            db.collection("Companies").document(company).collection("sales").document(sales).collection("complaints")
                                    .add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {

                                }
                            });
                        }
                    }
                });

        return builder.create();
    }
}

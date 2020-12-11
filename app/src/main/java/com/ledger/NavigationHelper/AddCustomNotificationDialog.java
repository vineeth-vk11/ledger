package com.ledger.NavigationHelper;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ledger.R;

import java.util.Calendar;
import java.util.HashMap;

public class AddCustomNotificationDialog extends AppCompatDialogFragment implements DatePickerDialog.OnDateSetListener{

    String company, sales;

    public AddCustomNotificationDialog(String company, String sales) {
        this.company = company;
        this.sales = sales;
    }

    EditText reminderName, reminderNote;
    Button dateSelector;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_add_custom_notification, null);

        reminderNote = view.findViewById(R.id.notes_edit);
        reminderName = view.findViewById(R.id.name_edit);
        dateSelector = view.findViewById(R.id.date);

        dateSelector = view.findViewById(R.id.date);
        dateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        builder.setView(view)
                .setTitle("Add Reminder Note")
                .setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String date = dateSelector.getText().toString();
                        String note = reminderNote.getText().toString();
                        String name = reminderName.getText().toString();

                        if(date.equals("Select Remainder Date")){

                        }
                        else if(TextUtils.isEmpty(note)){

                        }
                        else if(TextUtils.isEmpty(name)){

                        }
                        else {
                            HashMap<String, Object> data = new HashMap<>();
                            data.put("date",date);
                            data.put("note",note);
                            data.put("name",name);
                            data.put("type", "notification");

                            FirebaseFirestore db;
                            db = FirebaseFirestore.getInstance();

                            db.collection("Companies").document(company).collection("sales").document(sales)
                                    .collection("notes").document(date).collection("notes").add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {

                                }
                            });
                        }
                    }
                });

        return builder.create();
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
        String selectedDate = dayOfMonth + "-" + correctedMonth + "-" + year;

        if(dayOfMonth < 9){
            selectedDate = "0"+selectedDate;
        }

        dateSelector.setText(selectedDate);
    }
}

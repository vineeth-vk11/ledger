package com.ledger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class TermsActivity extends AppCompatActivity {

    String name, address;

    String userId, type, company, sales, number;

    CheckBox checkBox;
    Button next;

    TextView txtName;
    TextView txtAddress;

    TextView textView, timeStamp, device;

    String dirpath;

    AppBarLayout appBarLayout;

    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        checkBox = findViewById(R.id.checkBox);
        next = findViewById(R.id.next);

        txtName = findViewById(R.id.nameOfUser);
        txtAddress = findViewById(R.id.address);
        textView = findViewById(R.id.textView36);
        timeStamp = findViewById(R.id.timeStamp);
        device = findViewById(R.id.Device);
        appBarLayout = findViewById(R.id.appBarLayout);
        progressBar = findViewById(R.id.progressBar6);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        address = intent.getStringExtra("address");
        userId = intent.getStringExtra("userId");
        type = intent.getStringExtra("type");

        if(type.equals("sales")){
            company = intent.getStringExtra("company");
        }
        else if(type.equals("dealer")){
            company = intent.getStringExtra("company");
            sales = intent.getStringExtra("sales");
            number = intent.getStringExtra("number");
        }

        txtName.setText("Name: "+name);
        txtAddress.setText("Address: "+address);

        textView.setText("I, We running the dealership in the name of "+ name +
                " having my/our registered showroom / shop located at " + address+
                " for the purpose of trading with 21st Century Business Syndicate Company, having its registered office at 120, Ashiana Towers Exhibition Road, Patna, Bihar, 800001, India (hereinafter referred to as “21st” which expression shall mean and include unless repugnant to the context its successors and assigns). Therefore, I/We hereby undertake to comply with the following terms and conditions:");

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){
                    timeStamp.setText(String.valueOf(Calendar.getInstance().getTime()));
                    device.setText(Build.MODEL);
                    next.setEnabled(true);
                }
                else {
                    next.setEnabled(false);
                    timeStamp.setText("");
                    device.setText("");
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                next.setVisibility(View.GONE);
                layoutToImage();
                try {
                    imageToPDF();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Date currentTime = Calendar.getInstance().getTime();

                HashMap<String, Object> terms = new HashMap<>();
                terms.put("T&C",true);
                terms.put("timeOfAcceptingTerms",currentTime);
                terms.put("Product", Build.PRODUCT);
                terms.put("Device",Build.DEVICE);
                terms.put("Model",Build.MODEL);

                FirebaseFirestore db;
                db = FirebaseFirestore.getInstance();

                db.collection("Users").document(userId).update(terms).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(TermsActivity.this, "Terms and Contitions Accepted",Toast.LENGTH_SHORT).show();

                        Uri file = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + File.separator + "NewPDF.pdf"));
                        StorageReference storageReference;
                        storageReference = FirebaseStorage.getInstance().getReference();;

                        final StorageReference storageReference1 = storageReference.child(userId + " T&C pdf");
                        storageReference1.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                if(type.equals("head")){
                                    Intent intent = new Intent(TermsActivity.this, MainActivity.class);
                                    intent.putExtra("userId", userId);
                                    intent.putExtra("type","head");
                                    intent.putExtra("name",name);
                                    intent.putExtra("address",address);

                                    startActivity(intent);
                                    finish();
                                }
                                else if(type.equals("sales")){
                                    Intent intent = new Intent(TermsActivity.this, MainActivity.class);
                                    intent.putExtra("userId", userId);
                                    intent.putExtra("company", company);
                                    intent.putExtra("type","sales");
                                    intent.putExtra("name",name);
                                    intent.putExtra("address",address);

                                    startActivity(intent);
                                    finish();
                                }
                                else if(type.equals("dealer")){
                                    Intent intent = new Intent(TermsActivity.this, MainActivity.class);
                                    intent.putExtra("userId", userId);
                                    intent.putExtra("company", company);
                                    intent.putExtra("sales",sales);
                                    intent.putExtra("type","dealer");
                                    intent.putExtra("name",name);
                                    intent.putExtra("address",address);
                                    intent.putExtra("number",number);

                                    startActivity(intent);
                                    finish();
                                }

                                progressBar.setVisibility(View.INVISIBLE);

                            }
                        });
                    }
                });
            }
        });
    }

    public void layoutToImage(){

        // get view group using reference
        ConstraintLayout constraintLayout =  findViewById(R.id.layout);
        // convert view group to bitmap
        constraintLayout.setDrawingCacheEnabled(true);
        constraintLayout.buildDrawingCache();
        Bitmap bm = constraintLayout.getDrawingCache();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, bytes);

        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "image.png");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void imageToPDF() throws FileNotFoundException {
        try {
            Document document = new Document();
            dirpath = android.os.Environment.getExternalStorageDirectory().toString();
            PdfWriter.getInstance(document, new FileOutputStream(dirpath + "/NewPDF.pdf")); //  Change pdf's name.
            document.open();
            Image img = Image.getInstance(Environment.getExternalStorageDirectory() + File.separator + "image.png");
            float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                    - document.rightMargin() - 0) / img.getWidth()) * 100;
            img.scalePercent(scaler);
            img.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
            document.add(img);
            document.close();
            Toast.makeText(this, "PDF Generated successfully!..", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }

}
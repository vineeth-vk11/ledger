package com.ledger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    EditText txtUserId;
    EditText txtPassword;
    Button login;
    FirebaseFirestore db;
    ProgressBar progressBar;

    public static final String SHARED_PREFS = "sharedPrefs";

    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        txtUserId = findViewById(R.id.login_phone_edit);
        txtPassword = findViewById(R.id.password_edit);
        login = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progressBar);

        db = FirebaseFirestore.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginNow();

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE,"HEAD" );
                firebaseAnalytics.logEvent("Login", bundle);

            }
        });
    }

    private void loginNow(){

        progressBar.setVisibility(View.VISIBLE);

        final String user = txtUserId.getText().toString();
        final String password = txtPassword.getText().toString();

        if(TextUtils.isEmpty(user)){
            Toast.makeText(this, "Please enter user id", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
        else {

            db.collection("Users").whereEqualTo("phoneNumber",user).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if(!task.getResult().isEmpty()) {

                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                        String pass = documentSnapshot.getString("password");
                        String type = documentSnapshot.getString("role");
                        String name = documentSnapshot.getString("name");
                        String address = documentSnapshot.getString("address");
                        String userId = documentSnapshot.getId();
                        Boolean accepted;

                        if(documentSnapshot.getBoolean("T&C")!=null){
                            accepted = documentSnapshot.getBoolean("T&C");
                        }
                        else {
                            accepted = false;
                        }

                        if (password.equals(pass)) {

                            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            if (type.equals("head")) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("userId", userId);
                                intent.putExtra("type","head");
                                intent.putExtra("name",name);
                                intent.putExtra("address",address);

                                Intent intent1 = new Intent(LoginActivity.this, TermsActivity.class);
                                intent1.putExtra("userId", userId);
                                intent1.putExtra("type","head");
                                intent1.putExtra("name",name);
                                intent1.putExtra("address",address);

                                if(!accepted){
                                    startActivity(intent1);
                                }
                                else {
                                    startActivity(intent);
                                }

                                progressBar.setVisibility(View.INVISIBLE);
                                finish();

                            } else if (type.equals("sales")) {

                                String company = documentSnapshot.getString("company");

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("userId", userId);
                                intent.putExtra("company", company);
                                intent.putExtra("type","sales");
                                intent.putExtra("name",name);
                                intent.putExtra("address",address);

                                Intent intent1 = new Intent(LoginActivity.this, TermsActivity.class);
                                intent1.putExtra("userId", userId);
                                intent1.putExtra("company", company);
                                intent1.putExtra("type","sales");
                                intent1.putExtra("name",name);
                                intent1.putExtra("address",address);

                                if(!accepted){
                                    startActivity(intent1);
                                }
                                else {
                                    startActivity(intent);
                                }

                                progressBar.setVisibility(View.INVISIBLE);
                                finish();

                            } else if (type.equals("dealer")) {

                                String company = documentSnapshot.getString("company");
                                String sales = documentSnapshot.getString("sales");
                                String number = documentSnapshot.getString("phoneNumber");

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("userId", userId);
                                intent.putExtra("company", company);
                                intent.putExtra("sales",sales);
                                intent.putExtra("type","dealer");
                                intent.putExtra("name",name);
                                intent.putExtra("address",address);
                                intent.putExtra("number",number);

                                Intent intent1 = new Intent(LoginActivity.this, TermsActivity.class);
                                intent1.putExtra("userId", userId);
                                intent1.putExtra("company", company);
                                intent1.putExtra("sales",sales);
                                intent1.putExtra("type","dealer");
                                intent1.putExtra("name",name);
                                intent1.putExtra("address",address);
                                intent1.putExtra("number",number);

                                if(!accepted){
                                    startActivity(intent1);
                                }
                                else {
                                    startActivity(intent);
                                }

                                progressBar.setVisibility(View.INVISIBLE);
                                finish();

                            } else {
                                Toast.makeText(LoginActivity.this, "An error occurred, please contact the administrator", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);

                            }

                        }

                        else{
                            Toast.makeText(LoginActivity.this, "Please enter a correct password", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);

                        }

                    }
                    else if (task.getResult().isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Please enter a valid user Id", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);

                    } else {
                        Toast.makeText(LoginActivity.this, "Error Occurred, Please contact an administrator", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);

                    }
                }
            });
        }
    }

}
package com.ledger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ledger.CompaniesHelper.CompaniesFragment;
import com.ledger.DealersHelper.DealersFragment;
import com.ledger.NavigationHelper.CompanyProfileFragment;
import com.ledger.NavigationHelper.EditProfileFragment;
import com.ledger.TransactionsHelper.TransactionsFragment;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    String type;
    String userId;
    String company;
    String sales;

    DrawerLayout drawerLayout;

    Uri uri;
    private static final int PICK_IMAGE = 1;

    public static final String SHARED_PREFS = "sharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        if(type.equals("head")){
            userId = intent.getStringExtra("userId");
        }
        else if(type.equals("sales")){
            userId = intent.getStringExtra("userId");
            company = intent.getStringExtra("company");
        }
        else if(type.equals("dealer")){
            userId = intent.getStringExtra("userId");
            company = intent.getStringExtra("company");
            sales = intent.getStringExtra("sales");
        }

        final NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
        navigationView.setCheckedItem(R.id.home);

        if(type.equals("head")){

            CompaniesFragment companiesFragment = new CompaniesFragment();

            Bundle bundle = new Bundle();
            bundle.putString("userId", userId);
            companiesFragment.setArguments(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_frame,companiesFragment);
            fragmentTransaction.commit();

        }

        else if(type.equals("sales")){

            DealersFragment dealersFragment = new DealersFragment();

            Bundle bundle = new Bundle();
            bundle.putString("userId", userId);
            bundle.putString("company",company);
            bundle.putString("from", "sales");
            dealersFragment.setArguments(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_frame,dealersFragment);
            fragmentTransaction.commit();

        }

        else if(type.equals("dealer")){
            TransactionsFragment transactionsFragment = new TransactionsFragment();

            Bundle bundle = new Bundle();
            bundle.putString("userId", userId);
            bundle.putString("company",company);
            bundle.putString("sales",sales);
            transactionsFragment.setArguments(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_frame,transactionsFragment);
            fragmentTransaction.commit();

        }

        drawerLayout = findViewById(R.id.drawerLayout);
        findViewById(R.id.imageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        if(type.equals("head")){

                            CompaniesFragment companiesFragment = new CompaniesFragment();

                            Bundle bundle = new Bundle();
                            bundle.putString("userId", userId);
                            companiesFragment.setArguments(bundle);

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.main_frame,companiesFragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            drawerLayout.closeDrawer(GravityCompat.START);

                        }

                        else if(type.equals("sales")){

                            DealersFragment dealersFragment = new DealersFragment();

                            Bundle bundle = new Bundle();
                            bundle.putString("userId", userId);
                            bundle.putString("company",company);
                            bundle.putString("from","sales");

                            dealersFragment.setArguments(bundle);

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.main_frame,dealersFragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            drawerLayout.closeDrawer(GravityCompat.START);

                        }

                        else if(type.equals("dealer")){
                            TransactionsFragment transactionsFragment = new TransactionsFragment();

                            Bundle bundle = new Bundle();
                            bundle.putString("userId", userId);
                            bundle.putString("company",company);
                            bundle.putString("sales",sales);
                            bundle.putString("type","dealer");
                            transactionsFragment.setArguments(bundle);

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.main_frame,transactionsFragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            drawerLayout.closeDrawer(GravityCompat.START);

                        }

                        return true;

                    case R.id.company:

                        CompanyProfileFragment companyProfileFragment = new CompanyProfileFragment();

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.main_frame,companyProfileFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        drawerLayout.closeDrawer(GravityCompat.START);

                        return true;

                    case R.id.profile:
                        EditProfileFragment editProfileFragment = new EditProfileFragment();

                        Bundle bundle = new Bundle();
                        bundle.putString("id",userId);

                        editProfileFragment.setArguments(bundle);

                        FragmentManager fragmentManager1 = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();
                        fragmentTransaction1.replace(R.id.main_frame,editProfileFragment);
                        fragmentTransaction1.addToBackStack(null);
                        fragmentTransaction1.commit();
                        drawerLayout.closeDrawer(GravityCompat.START);

                        return true;

                    case R.id.logout:
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Logout")
                                .setMessage("Are you sure you want to logout?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getApplicationContext().getSharedPreferences(SHARED_PREFS,0).edit().clear().apply();
                                navigationView.setCheckedItem(R.id.home);
                            }
                        }).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE &&
                resultCode == Activity.RESULT_OK) {
            Uri imageuri = CropImage.getPickImageResultUri(this,data);
            if(CropImage.isReadExternalStoragePermissionsRequired(this,imageuri)){
                uri = imageuri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
            }
            else {
                startCrop(imageuri);
            }
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                uri = result.getUri();

                StorageReference storageReference;
                storageReference = FirebaseStorage.getInstance().getReference();

                final StorageReference storageReference1 = storageReference.child(String.valueOf(userId));
                storageReference1.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri downloadUrl = uri;
                                Log.i("Download Url",String.valueOf(downloadUrl));

                                HashMap<String, Object> image = new HashMap<>();
                                image.put("pic",String.valueOf(downloadUrl));

                                FirebaseFirestore db;
                                db = FirebaseFirestore.getInstance();
                                db.collection("Users").document(userId).update(image).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                            }
                        });
                    }
                });

            }
        }
    }

    private void startCrop(Uri imageuri){
        CropImage.activity(imageuri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAspectRatio(1,1)
                .start(this);

    }

}
package com.ledger.SalesHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.ledger.R;
import com.ledger.SalesHelper.AttendanceHelper.SalesAttendanceFragment;

import java.util.ArrayList;
import java.util.List;

public class SalesDashboardActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    SalesAttendanceFragment salesAttendanceFragment = new SalesAttendanceFragment();
    SalesStatsFragment salesStatsFragment = new SalesStatsFragment();

    String company, sales;

    TextView nameOfSales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_dashboard);

        nameOfSales = findViewById(R.id.nameOfUser);

        Intent intent = getIntent();
        company = intent.getStringExtra("company");
        sales = intent.getStringExtra("sales");

        nameOfSales.setText("");

        Bundle bundle = new Bundle();
        bundle.putString("company",company);
        bundle.putString("sales",sales);

        salesAttendanceFragment.setArguments(bundle);
        salesStatsFragment.setArguments(bundle);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.view_pager);

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),0);
        viewPagerAdapter.addFragment(salesAttendanceFragment,"Attendance");
        viewPagerAdapter.addFragment(salesStatsFragment,"Stats");

        viewPager.setAdapter(viewPagerAdapter);


    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList = new ArrayList<>();
        private List<String> fragmentTitle = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title){
            fragmentList.add(fragment);
            fragmentTitle.add(title);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }
}
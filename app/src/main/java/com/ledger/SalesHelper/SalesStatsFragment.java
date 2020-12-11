package com.ledger.SalesHelper;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.protobuf.Any;
import com.itextpdf.styledxmlparser.node.INode;
import com.ledger.R;

import java.util.ArrayList;
import java.util.List;


public class SalesStatsFragment extends Fragment {

    String[] flags = {"Flagged", "Not Flagged"};
    String[] health = {"Good","Ok","Bad"};

    FirebaseFirestore db;
    ArrayList<Integer> flagData = new ArrayList<>();;
    ArrayList<Integer> colors = new ArrayList<>();
    ArrayList<Integer> colors1 = new ArrayList<>();

    String company, sales;

    PieChart pieChart, healthChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sales_stats, container, false);

        Bundle bundle = getArguments();
        company = bundle.getString("company");
        sales = bundle.getString("sales");

        colors.add(Color.RED);
        colors.add(Color.GREEN);

        colors1.add(Color.GREEN);
        colors1.add(Color.YELLOW);
        colors1.add(Color.RED);

        pieChart = view.findViewById(R.id.flagsChart);
        healthChart = view.findViewById(R.id.healthChart);

        db = FirebaseFirestore.getInstance();
        db.collection("Companies").document(company).collection("sales").document(sales).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();

                flagData.add(Integer.parseInt(documentSnapshot.getString("flagged")));
                flagData.add(Integer.parseInt(documentSnapshot.getString("notFlagged")));

                ArrayList<PieEntry> flagDataPie = new ArrayList<>();
                flagDataPie.add(new PieEntry(Integer.parseInt(documentSnapshot.getString("flagged")), "Flag"));
                flagDataPie.add(new PieEntry(Integer.parseInt(documentSnapshot.getString("notFlagged")), "No Flag"));

                ArrayList<PieEntry> healthDataPie = new ArrayList<>();
                healthDataPie.add(new PieEntry(Integer.parseInt(documentSnapshot.getString("GoodHealth")), "Good"));
                healthDataPie.add(new PieEntry(Integer.parseInt(documentSnapshot.getString("OkHealth")), "Ok"));
                healthDataPie.add(new PieEntry(Integer.parseInt(documentSnapshot.getString("BadHealth")), "Bad"));


                PieDataSet pieDataSet = new PieDataSet(flagDataPie, "");
                pieDataSet.setColors(colors);
                pieDataSet.setValueTextColor(Color.BLACK);
                pieDataSet.setValueTextSize(16f);

                PieData pieData = new PieData(pieDataSet);

                pieChart.setData(pieData);
                pieChart.getDescription().setEnabled(false);
                pieChart.setCenterText("Flags");
                pieChart.animate();
                pieChart.invalidate();

                PieDataSet pieDataSet1 = new PieDataSet(healthDataPie,"" );
                pieDataSet1.setColors(colors1);
                pieDataSet1.setValueTextColor(Color.BLACK);
                pieDataSet1.setValueTextSize(16f);

                PieData pieData1 = new PieData(pieDataSet1);

                healthChart.setData(pieData1);
                healthChart.getDescription().setEnabled(false);
                healthChart.setCenterText("Health");
                healthChart.animate();
                healthChart.invalidate();
            }
        });

        return view;
    }
}
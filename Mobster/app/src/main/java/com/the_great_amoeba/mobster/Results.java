package com.the_great_amoeba.mobster;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.listener.PieRadarChartTouchListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.common.data.DataBuffer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Helper.HelperMethods;

/**
 * Created by natalie on 2/6/2017.
 */

public class Results extends AppCompatActivity{

    private PieChart chart;
    private String[] xAxis;
    private float[] yAxis;
    private HashMap<String, Float> map;

    private ListView list;
    private List<Map.Entry<String, Float>> ordered;

    String questionPassed;


    public static final String DB_URL = "https://mobster-3ba43.firebaseio.com/";
    private DatabaseReference mDatabase;
    private Map<String, Integer> choices;
//    private String[] options;
//    private float[] votes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HelperMethods.setChosenTheme(this, getApplicationContext());

        setContentView(R.layout.activity_results);
        Bundle bundle = getIntent().getExtras();
        questionPassed = bundle.getString("questionPassed");
        final char home = bundle.getChar("homeTabPassed");
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL);
        DatabaseReference choicesRef = mDatabase.child("questions")
                .child(questionPassed).child("choices");
        choicesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int index = 0;
                int childCount = (int)(dataSnapshot.getChildrenCount());
                xAxis = new String[childCount];
                yAxis = new float[childCount];
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    xAxis[index] = (String)d.child("option").getValue();
                    yAxis[index] = ((Long)d.child("vote").getValue()).floatValue();
                    index++;

                }
                createGraph();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Results.this, MainActivity.class);
                startActivity(intent);
            }
        };
        if (home == 'm') {
            View b = findViewById(R.id.back_to_main);
            b.setVisibility(View.GONE);
        } else {
            Button button = (Button) findViewById(R.id.back_to_main);
            button.setOnClickListener(listener);
        }

        View.OnClickListener commentListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Results.this, CommentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("questionPassed", questionPassed);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        };
        Button commentButton = (Button) findViewById(R.id.commentsButton);
        commentButton.setOnClickListener(commentListener);

    }

    private void createGraph() {
        map = new HashMap<>();
        HashMap<String, Float> mapList = new HashMap<>();
        for (int i = 0; i < xAxis.length; i++) {
            mapList.put(xAxis[i], yAxis[i]);
            if (yAxis[i] > 0) {
                map.put(xAxis[i], yAxis[i]);

            }
        }

        // set data in chart
        chart = (PieChart) findViewById(R.id.chart);
        List<PieEntry> entries = new ArrayList<>();
        for (String m : map.keySet()) {
            entries.add(new PieEntry(map.get(m), m));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Choices");

        // chart configs
        dataSet.setValueFormatter(new PercentFormatter());
        chart.setUsePercentValues(true);
        Description chartDescription = new Description();
        chartDescription.setText("");
        chart.setDescription(chartDescription);

        // hole configs
        chart.setDrawHoleEnabled(true);
        chart.setTransparentCircleAlpha(0);
        chart.setHoleRadius(0);
//        chart.setTransparentCircleRadius(60);
//        chart.setDrawCenterText(true);
//        chart.setCenterText("Question?");

        // interaction
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry slice = (PieEntry) e;
                Toast.makeText(Results.this, slice.getLabel() +
                                ": " + Math.round(slice.getValue()) + " votes",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        chart.setRotationEnabled(true);
        chart.setRotationAngle(0);
        chart.setHighlightPerTapEnabled(true);


        // legend config
        Legend legend = chart.getLegend();
        legend.setEnabled(false);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setTextColor(Color.WHITE);
        legend.setTextSize(15);
        legend.setWordWrapEnabled(true);

        // dataset configs
        ArrayList<Integer> colors = new ArrayList<>();
        if (SaveSharedPreferences.getChosenTheme(getApplicationContext()).equals("dark")) {
            for (int c : ColorTemplate.COLORFUL_COLORS)
                colors.add(c);
        } else {
            for (int c : ColorTemplate.PASTEL_COLORS)
                colors.add(c);
        }
//        for (int c : ColorTemplate.COLORFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.JOYFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.VORDIPLOM_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.LIBERTY_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.PASTEL_COLORS)
//            colors.add(c);
        dataSet.setColors(colors);

        //dataSet.setSliceSpace(2);
        dataSet.setSelectionShift(10);


        // set and config data
        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(20);


        // statistics
        // http://www.java2novice.com/java-interview-programs/sort-a-map-by-value/
        Set<Map.Entry<String, Float>> set = mapList.entrySet();
        ordered = new ArrayList<>(set);
        Collections.sort(ordered, new Comparator<Map.Entry<String, Float>>() {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                return (o2.getValue().compareTo(o1.getValue()));
            }
        });
//        for (Map.Entry<String, Float> entry: ordered) {
//            System.out.println(entry.getKey() + ": " + entry.getValue());
//        }

        ArrayList<String> displayed = new ArrayList<>();
        for (Map.Entry<String, Float> o : ordered) {
            displayed.add(o.getKey() + ": " + Math.round(o.getValue()) + " votes");
        }
        list = (ListView) findViewById(R.id.results_list);
        String[] displayed_arr =  new String[displayed.size()];
        displayed_arr = displayed.toArray(displayed_arr);
        list.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, displayed_arr));


        chart.setData(pieData);
        chart.invalidate();
    }




}

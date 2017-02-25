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

/**
 * Created by natalie on 2/6/2017.
 */

public class Results extends AppCompatActivity{

    private PieChart chart;
    private String[] xAxis; //= {"A", "B", "C", "D", "E"};
    private float[] yAxis; //= {2,4,6,8,10};
    private HashMap<String, Float> map;

    private ListView list;
    private List<Map.Entry<String, Float>> ordered;


    public static final String DB_URL = "https://mobster-3ba43.firebaseio.com/";
    private DatabaseReference mDatabase;
    private Map<String, Integer> choices;
//    private String[] options;
//    private float[] votes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Bundle bundle = getIntent().getExtras();
        String questionPassed = bundle.getString("questionPassed");
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

    }

    private void createGraph() {
        map = new HashMap<>();
        for (int i = 0; i < xAxis.length; i++) {
            map.put(xAxis[i], yAxis[i]);
        }

        // set data in chart
        chart = (PieChart) findViewById(R.id.chart);
        List<PieEntry> entries = new ArrayList<>();
        for (String m : map.keySet()) {
            entries.add(new PieEntry(map.get(m), m));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Choices");

        // chart configs
        chart.setUsePercentValues(true);

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
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        dataSet.setColors(colors);

        //dataSet.setSliceSpace(2);
        dataSet.setSelectionShift(10);


        // set and config data
        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(20);


        // statistics
        // http://www.java2novice.com/java-interview-programs/sort-a-map-by-value/
        Set<Map.Entry<String, Float>> set = map.entrySet();
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

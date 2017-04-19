package com.the_great_amoeba.mobster;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Result fragment class.
 *
 * @author Ani
 * @version 1.0
 */
public class ResultsFragment extends Fragment {

    private PieChart chart;
    private String[] xAxis;
    private float[] yAxis;
    private HashMap<String, Float> map;

    View view;

    private ListView list;
    private List<Map.Entry<String, Float>> ordered;

    String questionPassed;
    public static final String DB_URL = "https://mobster-3ba43.firebaseio.com/";
    private DatabaseReference mDatabase;
    private Map<String, Integer> choices;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.results_tab, container, false);
        Bundle bundle = getArguments();
        questionPassed = bundle.getString("questionPassed");
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

        return view;
    }

    /**
     * Create the pi graph for the result screen.
     */
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
        chart = (PieChart) this.view.findViewById(R.id.chart);
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

        // interaction
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry slice = (PieEntry) e;
                Toast.makeText(getContext(), slice.getLabel() +
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
        if (SaveSharedPreferences.getChosenTheme(getContext()).equals("dark")) {
            for (int c : ColorTemplate.COLORFUL_COLORS)
                colors.add(c);
        } else {
            for (int c : ColorTemplate.PASTEL_COLORS)
                colors.add(c);
        }

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

        ArrayList<String> displayed = new ArrayList<>();
        for (Map.Entry<String, Float> o : ordered) {
            displayed.add(o.getKey() + ": " + Math.round(o.getValue()) + " votes");
        }
        list = (ListView) this.view.findViewById(R.id.results_list);
        String[] displayed_arr =  new String[displayed.size()];
        displayed_arr = displayed.toArray(displayed_arr);
        list.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, displayed_arr));

        chart.setData(pieData);
        chart.invalidate();
    }
}

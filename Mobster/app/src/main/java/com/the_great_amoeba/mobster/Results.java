package com.the_great_amoeba.mobster;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by natalie on 2/6/2017.
 */

public class Results extends AppCompatActivity{

    private PieChart chart;
    private float[] yAxis = {2,4,6,8,10};
    private String[] xAxis = {"A", "B", "C", "D", "E"};
    private HashMap<String, Float> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // get all data
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

        // legend config
        Legend legend = chart.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legend.setTextColor(Color.WHITE);

        // dataset configs
        ArrayList<Integer> colors = new ArrayList<Integer>();
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


        // set data
        PieData pieData = new PieData(dataSet);



        chart.setData(pieData);
        chart.invalidate();

    }
}

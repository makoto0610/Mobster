package com.the_great_amoeba.mobster;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import java.util.Calendar;
import java.util.Date;

import Helper.HelperMethods;

public class CreateQuestion extends AppCompatActivity {
    Button timePicker;
    Button datePicker;

    TextView textDate;
    TextView textTime;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    private Context context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);

        datePicker = (Button) findViewById(R.id.date_picker);
        timePicker = (Button) findViewById(R.id.time_picker);

        textDate = (EditText) findViewById(R.id.end_date_text);
        textTime = (EditText) findViewById(R.id.end_time_text);
        context = this;
    }

    public void onDatePickerClick(View v) {

        // Process to get Current Date
        final Calendar c = Calendar.getInstance();
        final int curYear = c.get(Calendar.YEAR);
        final int curMonth = c.get(Calendar.MONTH);
        final int curDay = c.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int aYear,
                                          int monthOfYear, int dayOfMonth) {

                        if (isValidDate(monthOfYear, dayOfMonth, aYear)) {
                            // Display Selected date in textbox
                            textDate.setText((monthOfYear + 1) + "-"
                                    + (dayOfMonth) + "-" + aYear); // + 1 for display purposes for month
                            year = aYear;
                            month = monthOfYear;
                            day = dayOfMonth;


                        } else {
                            HelperMethods.errorDialog(context,"Date invalid",
                                    "Invalid Date");
                            textDate.setText("END DATE");
                        }

                    }
                }, curYear, curMonth, curDay);
        dpd.show();
    }

    public void onTimePicker(View v) {

        // Process to get Current Time
        final Calendar c = Calendar.getInstance();

        // Launch Time Picker Dialog
        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int aMinute) {
                        if (isValidTime(month, day, year, hourOfDay, aMinute)) {

                            // Display Selected time in textbox
                            textTime.setText(displayTime(hourOfDay, aMinute));
                            hour = hourOfDay;
                            minute = aMinute;

                        } else {
                            HelperMethods.errorDialog(context,"Time invalid",
                                    "Invalid Time");
                            textTime.setText("END TIME");
                        }
                    }
                }, hour, minute, false);
        tpd.show();

    }

    private boolean isValidDate(int month, int day, int year) {
        Calendar current = Calendar.getInstance();
        Calendar guess = Calendar.getInstance();
        guess.set(year, month, day, 23, 59, 59);
        return current.before(guess);
    }

    private boolean isValidTime(int month, int day, int year, int hour, int minute) {
        Calendar current = Calendar.getInstance();
        Calendar guess = Calendar.getInstance();
        guess.set(year, month, day, hour, minute, 0);
        return current.before(guess);
    }

    private String displayMinutes(int minutes) {
        if (minutes < 10) {
            return "0" + minutes;
        }
        return Integer.toString(minutes);
    }

    private String displayTime(int hours, int minutes) {
        if (hours > 12) {
            return Integer.toString(hours - 12) + ":" + displayMinutes(minutes) + " PM";
        } else {
            return Integer.toString(hours) + ":" + displayMinutes(minutes) + " AM";
        }
    }
}

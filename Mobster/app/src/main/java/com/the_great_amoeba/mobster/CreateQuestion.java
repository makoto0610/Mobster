package com.the_great_amoeba.mobster;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import Helper.HelperMethods;

public class CreateQuestion extends AppCompatActivity {
    Button timePicker;
    Button datePicker;
    Button add;
    Button submit;

    TextView textDate;
    TextView textTime;
    TextView textIn;

    LinearLayout containerList;

    private int year = -1;
    private int month = -1;
    private int day = -1;
    private int hour = -1;
    private int minute = -1;

    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);

        datePicker = (Button) findViewById(R.id.date_picker);
        timePicker = (Button) findViewById(R.id.time_picker);
        add = (Button) findViewById(R.id.add_option);
        submit = (Button) findViewById(R.id.submit_question);

        textDate = (EditText) findViewById(R.id.end_date_text);
        textTime = (EditText) findViewById(R.id.end_time_text);
        context = this;


        textIn = (EditText) findViewById(R.id.add_option_text);
        add = (Button) findViewById(R.id.add_option);
        containerList = (LinearLayout) findViewById(R.id.container_list);

        add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                LayoutInflater layoutInflater =
                        (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView = layoutInflater.inflate(R.layout.row, null);
                TextView textOut = (TextView) addView.findViewById(R.id.option_text_view);
                textOut.setText(textIn.getText().toString());
                Button buttonRemove = (Button) addView.findViewById(R.id.remove_option);
                buttonRemove.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((LinearLayout) addView.getParent()).removeView(addView);
                    }
                });

                containerList.addView(addView);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                System.out.println("HERLLLO");

            }
        });

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

                        if ((isValidDate(monthOfYear, dayOfMonth, aYear) && hour == -1)
                                || (isValidTime(monthOfYear, dayOfMonth, aYear, hour, minute))) {
                            // Display Selected date in textbox
                            textDate.setText((monthOfYear + 1) + "-"
                                    + (dayOfMonth) + "-" + aYear); // + 1 for display purposes for month
                            year = aYear;
                            month = monthOfYear;
                            day = dayOfMonth;
                        } else {
                            HelperMethods.errorDialog(context, "Date/Time invalid",
                                    "Invalid Date and/or invalid Time");
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
                        if (year == 0) {
                            HelperMethods.errorDialog(context, "Date/Time invalid",
                                    "Please select an end date first");
                        } else if (isValidTime(month, day, year, hourOfDay, aMinute)) {

                            // Display Selected time in textbox
                            textTime.setText(displayTime(hourOfDay, aMinute));
                            hour = hourOfDay;
                            minute = aMinute;

                        } else {
                            HelperMethods.errorDialog(context, "Date/Time invalid",
                                    "Invalid Time");
                            textTime.setText("END TIME");
                        }
                    }
                }, 0, 0, false); // displays 12:00 AM
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
        } else if (hours == 12) {
            return "12:" + displayMinutes(minutes) + " PM";
        } else if (hours == 0) {
            return "12:" + displayMinutes(minutes) + " AM";
        } else {
            return Integer.toString(hours) + ":" + displayMinutes(minutes) + " AM";
        }
    }
}

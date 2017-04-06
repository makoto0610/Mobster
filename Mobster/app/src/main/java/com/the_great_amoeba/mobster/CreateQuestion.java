package com.the_great_amoeba.mobster;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.Duration;

import java.security.Security;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

import Constants.Constant;
import Helper.HelperMethods;
import Objects.Choice;
import Objects.Question;

public class CreateQuestion extends AppCompatActivity implements
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    Button timePicker;
    Button datePicker;
    private Button add;
    private Button submit;
    private Button addKeyword;

    private EditText question;

    private TextView textDate;
    private TextView textTime;
    private TextView textIn;
    private TextView textInKeyword;

    private LinearLayout containerList;
    private LinearLayout containerKeywordList;

    private ArrayList<String> options = new ArrayList<String>();
    private ArrayList<String> keywords = new ArrayList<>();

    private int year = -1;
    private int month = -1;
    private int day = -1;
    private int hour = -1;
    private int minute = -1;

    private Context context;

    public static final String DB_URL = "https://mobster-3ba43.firebaseio.com/";
    private DatabaseReference mDatabase;

    private Location loc;
    private GoogleApiClient client;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HelperMethods.setChosenTheme(this, getApplicationContext());
        setContentView(R.layout.activity_create_question);

        mDatabase = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(DB_URL);

        // Initializes all of the needed elements from the UI
        datePicker = (Button) findViewById(R.id.date_picker);
        timePicker = (Button) findViewById(R.id.time_picker);
        add = (Button) findViewById(R.id.add_option);
        addKeyword = (Button) findViewById(R.id.add_keyword);
        submit = (Button) findViewById(R.id.submit_question);

        textDate = (EditText) findViewById(R.id.end_date_text);
        textTime = (EditText) findViewById(R.id.end_time_text);
        context = this;
        question = (EditText) findViewById(R.id.create_question);

        textIn = (EditText) findViewById(R.id.add_option_text);
        textInKeyword = (EditText) findViewById(R.id.add_keyword_text);
        add = (Button) findViewById(R.id.add_option);
        containerList = (LinearLayout) findViewById(R.id.container_list);
        containerKeywordList = (LinearLayout) findViewById(R.id.container_keyword_list);

        add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (options.size() <= 9) {
                    if (!options.contains(textIn.getText().toString().toLowerCase())) {
                        // Creates the potential view (which is a row with the added textview and remove button
                        LayoutInflater layoutInflater =
                                (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View addView = layoutInflater.inflate(R.layout.row, null);
                        final TextView addedOption = (TextView) addView.findViewById(R.id.option_text_view);
                        addedOption.setText(textIn.getText().toString().toLowerCase());

                        containerList.addView(addView);
                        // Adds the view and logic for the remove button
                        if (addedOption.getText().toString().toLowerCase().trim().length() > 0) {

                            options.add(addedOption.getText().toString().toLowerCase());


                            Button buttonRemove = (Button) addView.findViewById(R.id.remove_option);
                            buttonRemove.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    String toRemove = addedOption.getText().toString().toLowerCase();
                                    options.remove(toRemove);
                                    ((LinearLayout) addView.getParent()).removeView(addView);

                                }
                            });
                            textIn.setText("");

                        } else {
                            ((LinearLayout) addView.getParent()).removeView(addView);
                            HelperMethods.errorDialog(context, "Empty option",
                                    "You cannot have an empty option");
                        }
                    } else {
                        HelperMethods.errorDialog(context, "Duplicated Choice",
                                "You may not have duplicated choices.");
                    }
                } else {
                    HelperMethods.errorDialog(context, "Too many options!",
                            "You can only have up to 10 options");
                }

            }
        });

        addKeyword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (keywords.size() <= 5) {

                    // Creates the potential view (which is a row with the added textview and remove button
                    LayoutInflater layoutInflater =
                            (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View addView = layoutInflater.inflate(R.layout.row, null);
                    final TextView addedKeyword = (TextView) addView.findViewById(R.id.option_text_view);
                    addedKeyword.setText(textInKeyword.getText().toString().toLowerCase());

                    containerKeywordList.addView(addView);
                    // Adds the view and logic for the remove button
                    if (addedKeyword.getText().toString().toLowerCase().trim().length() > 0) {

                        keywords.add(addedKeyword.getText().toString().toLowerCase());


                        Button buttonRemove = (Button) addView.findViewById(R.id.remove_option);
                        buttonRemove.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                String toRemove = addedKeyword.getText().toString().toLowerCase();
                                keywords.remove(toRemove);
                                ((LinearLayout) addView.getParent()).removeView(addView);

                            }
                        });

                    textInKeyword.setText("");

                    } else {
                        ((LinearLayout) addView.getParent()).removeView(addView);
                        HelperMethods.errorDialog(context, "Empty option",
                                "You cannot have an empty option");
                    }

                } else {
                    HelperMethods.errorDialog(context, "Too many keywords!",
                            "You can only have up to 5 keywords");
                }

            }
        });



        // Logic for the submit button
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitButtonClick(v);
            }
        });

        initializeGoogleApi();

    }

    @Override
    protected void onStart() {
        super.onStart();
        /** If location permissions enabled, add location to the question (else loc will be null) **/
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            Helper.Log.d(Constant.DEBUG, "onStart() Location permission not granted. Requesting permission.");
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constant.LOC_PERMISSION);
        } else {
            client.connect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            case Constant.LOC_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Helper.Log.d(Constant.DEBUG, "Location permission granted.");
                    client.connect();
                } else {
                    Helper.Log.d(Constant.DEBUG, "Location permission NOT granted.");
                }
                break;
            }

            default: {
                Helper.Log.d(Constant.DEBUG, "Unknown permission request.");
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        client.disconnect();
    }

    /**
     * Initialize Google Api Client needed for location services
     */
    public void initializeGoogleApi() {
        if (client == null) {
            client = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        return;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            loc = LocationServices.FusedLocationApi.getLastLocation(
                    client);
        } catch(SecurityException e) {
            Helper.Log.d(Constant.DEBUG, "Location permission not granted.");
        }

        if (loc != null) {
            Helper.Log.d(Constant.DEBUG, "Location marked at: \n" + loc.toString());
        } else {
            Helper.Log.d(Constant.DEBUG, "LocationServices returned null location.");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Helper.Log.d(Constant.DEBUG, "Connection to Google API client failed.");
        return;
    }


    /**
     * Retreives the date based on what the user has chosen using the date picker widget
     *
     * @param v The view to use (mainly the Create Question Activity)
     */
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

    /**
     * Retrieves the time based on what the user has chosen using the time picker widget
     *
     * @param v The view to use (mainly the Create Question Activity)
     */
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

    /**
     * Stores the question information and ensures that there are no empty fields, and more than 1 options
     *
     * @param v the current view
     */
    private void onSubmitButtonClick(View v) {
        if (!(question.getText().toString().trim().length() > 0)) {
            HelperMethods.errorDialog(context, "Invalid Question",
                    "Question cannot be empty");
        } else if (options.size() < 2) {
            HelperMethods.errorDialog(context, "Not enough options",
                    "There must be at least 2 options");
        } else if (hour == -1 || day == -1) {
            HelperMethods.errorDialog(context, "Empty Date/Time",
                    "The end date and time must be chosen");
        } else {
            Toast.makeText(context, "Question Posted!",
                    Toast.LENGTH_LONG).show();
            scheduleNotification(createNotification(question.getText().toString()), getDelay());

            storeQuestion();

            Intent intent = new Intent(CreateQuestion.this, MainActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Logic for storing the question data into Firebase
     *
     * @return true if the store was successful
     */
    private boolean storeQuestion() {
        try {
            LinkedList<Choice> choices = new LinkedList<>();
            for (String option : options) {
                Choice choice = new Choice(option);
                choices.add(choice);
            }
            LinkedList<String> keywordLL = new LinkedList<>();
            for (String k : keywords) {
                keywordLL.add(k);
            }

            LinkedList<String> commentLL = new LinkedList<>();

            final Calendar current = Calendar.getInstance();

            Calendar end = Calendar.getInstance();
            end.set(year, month, day, hour, minute, 0);

            String username = SaveSharedPreferences.getUserName(getApplicationContext());

            Question questionToAdd = new Question(this.question.getText().toString(), choices,
                    keywordLL, current, end, username, loc, commentLL);
            DatabaseReference choicesRef = mDatabase.child("questions");
            choicesRef.push().setValue(questionToAdd);

            DatabaseReference asked = mDatabase.child("users").child(username).child("asked");
            final String finalUsername = username;
            asked.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int currentAsked = dataSnapshot.getValue(Integer.class);
                    updateAsked(currentAsked + 1, finalUsername);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }


            });

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            HelperMethods.errorDialog(context, "Posted Question Error",
                    "Could not post your question.");
            return false;
        }
    }

    private void updateAsked(int current, String username) {
        DatabaseReference toUpdate = mDatabase.child("users").child(username).child("asked");
        toUpdate.setValue(current);
    }

    /**
     * Checks if the input date is valid
     *
     * @param month potential end month
     * @param day   potential end day
     * @param year  potential end year
     * @return true if the entered date is later than the current date
     */
    private boolean isValidDate(int month, int day, int year) {
        Calendar current = Calendar.getInstance();
        Calendar guess = Calendar.getInstance();
        guess.set(year, month, day, 23, 59, 59);
        return current.before(guess);
    }

    /**
     * Checks if the input time is valid
     *
     * @param month  inputted month
     * @param day    inputted day
     * @param year   inputted year
     * @param hour   potential end hour
     * @param minute potential end minute
     * @return true it the time and date are all after the current date and time
     */
    private boolean isValidTime(int month, int day, int year, int hour, int minute) {
        Calendar current = Calendar.getInstance();
        Calendar guess = Calendar.getInstance();
        guess.set(year, month, day, hour, minute, 0);
        return current.before(guess);
    }

    /**
     * Displays the format for minutes of a clock
     *
     * @param minutes to be formatted
     * @return String version of the formatted minutes
     */
    private String displayMinutes(int minutes) {
        if (minutes < 10) {
            return "0" + minutes;
        }
        return Integer.toString(minutes);
    }

    /**
     * Displays the time correctly
     *
     * @param hours   to be formatted
     * @param minutes to be formatted
     * @return String version of the final formatted time
     */
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

    // Notification creation and scheduling
    private void scheduleNotification(Notification notification, long delay) {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification createNotification(String note) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Question " + "'" + note + "'" + " has expired.");
        builder.setContentText("Check the results now!");
        builder.setSmallIcon(R.drawable.ic_character);


        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);
        return builder.build();
    }

    private long getDelay() {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.set(year, month, day, hour, minute, start.get(Calendar.SECOND));
        return end.getTimeInMillis() - start.getTimeInMillis();
    }
}
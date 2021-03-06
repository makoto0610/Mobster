package com.the_great_amoeba.mobster;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.Map;

import Constants.Constant;
import Helper.HelperMethods;
import Objects.Adapters.CustomInfoWindowAdapter;
import Objects.LocationWrapper;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_AZURE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_CYAN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_MAGENTA;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_ORANGE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_ROSE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_VIOLET;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_YELLOW;

/**
 * Map activity for the application.
 *
 * @author Ani
 * @version 1.0
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    /* NOTE: Location, questionID, and duration should be put into an object instead of 3 diff. hashmaps */
    private Map<String, LocationWrapper> questionLocationMap;
    private Map<String, String> questionQuestionIdMap;
    private Map<String, Long> questionDurationMap;
    private Map<String, String> questionUserAskedMap;

    private CustomInfoWindowAdapter infoWinAdapter;

    /**
     * Float array of colors
     */
    private static float[] colors = {
        HUE_AZURE, HUE_GREEN, HUE_CYAN, HUE_BLUE, HUE_MAGENTA, HUE_ORANGE,
            HUE_RED, HUE_ROSE, HUE_VIOLET, HUE_YELLOW
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mDatabase = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constant.DB_URL);
        questionLocationMap = new HashMap<>();
        questionQuestionIdMap = new HashMap<>();
        questionDurationMap = new HashMap<>();
        questionUserAskedMap = new HashMap<>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Move the camera to Atlanta
        LatLng atlanta = new LatLng(Constant.ATLANTA_LAT, Constant.ATLANTA_LONG);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(atlanta, Constant.DEFAULT_ZOOM));


        this.infoWinAdapter = new CustomInfoWindowAdapter(getApplicationContext());
        mMap.setInfoWindowAdapter(this.infoWinAdapter);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        getQuestionLocationsFromFirebase();

    }

    /**
     * Get questions' location from the firebase database
     */
    public void getQuestionLocationsFromFirebase() {
        Helper.Log.d(Constant.DEBUG, "inMapActivity, getting Questions from FB");
        Query question = mDatabase.child("questions").orderByKey().limitToFirst(
                Constant.NUM_OF_QUESTIONS);
        question.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    // Can use the firebase method itself to deserialize
                    // NOTE: can't deserialize Location.class(has no no-args constructor or
                    // Question.class (problem with Calendar objects)

                    String questionId = (String) postSnapshot.getKey();
                    String keyQuestion = (String) postSnapshot.child("question").getValue();
                    questionQuestionIdMap.put(keyQuestion, questionId);
                    LocationWrapper locWrap = postSnapshot.child("loc").getValue(LocationWrapper.class);
                    if (locWrap == null) {
                        Helper.Log.d(Constant.DEBUG, "Location was null for question: " +
                            keyQuestion);
                        continue;
                    }
                    Helper.Log.d(Constant.DEBUG, locWrap.toString());
                    questionLocationMap.put(keyQuestion, locWrap);
                    HashMap end = (HashMap) postSnapshot.child("end").getValue();
                    long endTime = (long) end.get("timeInMillis");
                    questionDurationMap.put(keyQuestion, HelperMethods.computeDuration(endTime));
                    String userAsked = (String) postSnapshot.child("username").getValue();
                    questionUserAskedMap.put(keyQuestion, userAsked);
                }
                initializeQuestionMarkers();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: questions not loading error message
            }
        });
    }

    /**
     * Initialize question markers based on their locations
     */
    public void initializeQuestionMarkers() {
        int i = 0;
        for (Map.Entry<String, LocationWrapper> e : questionLocationMap.entrySet()) {
            Helper.Log.d(Constant.DEBUG, "Map entry set" + e.toString());
            String question = e.getKey();
            LocationWrapper locWrap = e.getValue();
            LatLng ll = new LatLng(locWrap.getLatitude(), locWrap.getLongitude());
            float color = colors[ ((i++) % colors.length)];
            mMap.addMarker(new MarkerOptions().position(ll).title(question).icon(
                    BitmapDescriptorFactory.defaultMarker(color)));
        }
    }


    /**
     * On marker click, load that question's info from FB and save it as the marker's tag
     * @param marker - marker that was clicked
     * @return - determines whether we propagate event up stack
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
//        return true;
        return false;
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Helper.Log.d(Constant.DEBUG, "in onInfoWindowClick with marker: " + marker.getTitle());
        final String question = marker.getTitle();
        String questionID = this.questionQuestionIdMap.get(question);
        String user = this.questionUserAskedMap.get(question);

        //if the question is the current user's question, go to the results page instead of voting page
        Bundle bundle = new Bundle();
        bundle.putString("questionPassed", questionID);
        if (SaveSharedPreferences.getUserName(this).equals(user)) {
            Intent intent = new Intent(getApplicationContext(), Results.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), Voting.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}

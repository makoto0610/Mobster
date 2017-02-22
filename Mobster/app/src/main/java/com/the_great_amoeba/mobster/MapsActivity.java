package com.the_great_amoeba.mobster;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.Duration;

import java.util.HashMap;
import java.util.Map;

import Constants.Constant;
import Objects.Adapters.CustomInfoWindowAdapter;
import Objects.DisplayQuestion;
import Objects.LocationWrapper;
import Objects.Question;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    private Map<String, LocationWrapper> questionLocationMap;
    private Map<String, String> questionQuestionIdMap;

    private CustomInfoWindowAdapter infoWinAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mDatabase = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constant.DB_URL);
        questionLocationMap = new HashMap<>();
        questionQuestionIdMap = new HashMap<>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getQuestionLocationsFromFirebase();
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


        //Add a dummy marker for Atlanta and move the camera
        LatLng atlanta = new LatLng(Constant.ATLANTA_LAT, Constant.ATLANTA_LONG);
//        mMap.addMarker(new MarkerOptions().position(atlanta).title("Marker in Atlanta"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(atlanta, Constant.DEFAULT_ZOOM));


        this.infoWinAdapter = new CustomInfoWindowAdapter(getApplicationContext());
        mMap.setInfoWindowAdapter(this.infoWinAdapter);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);


    }


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
                }

                initializeQuestionMarkers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: questions not loading error message
            }
        });
    }

    public void initializeQuestionMarkers() {
        for (Map.Entry<String, LocationWrapper> e : questionLocationMap.entrySet()) {
            String question = e.getKey();
            LocationWrapper locWrap = e.getValue();
            LatLng ll = new LatLng(locWrap.getLatitude(), locWrap.getLongitude());
            mMap.addMarker(new MarkerOptions().position(ll).title(question).snippet("Duration: 1h"));
        }


    }


    /**
     * On marker click, load that question's info from FB and save it as the marker's tag
     * @param marker - marker that was clicked
     * @return - determines whether we propagate event up stack
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
//        getQuestionInfoFromFB(marker);
//        return true;
        return false;
    }

    public void getQuestionInfoFromFB(final Marker marker) {
        final String question = marker.getTitle();
        Helper.Log.d(Constant.DEBUG, "in getQuestionInfoFromFB");
        marker.hideInfoWindow();

        /**
         * Query the database for the question object w/ given question field
         */
        Query questionQuery = mDatabase.child("questions").orderByChild("question").
                equalTo(question);
        questionQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String keyQuestion = postSnapshot.getKey();
                    HashMap value = (HashMap) postSnapshot.getValue();

                    //TODO: error message if null (other places as well)
                    if (value == null) return;
                    Helper.Log.i(Constant.DEBUG, "keys:" + value.keySet().toString());
                    Helper.Log.i(Constant.DEBUG, "values: " + value.values().toString());
                    long upvotes = (long) value.get("num_upvotes");
                    long downvotes = (long) value.get("num_downvotes");
                    long rating = upvotes - downvotes;
                    long access = (long) value.get("num_access");

                    DisplayQuestion question = new DisplayQuestion((String) value.get("question"),
                            new Duration(6000000),
                            rating, keyQuestion, access);
                    marker.setTag(question);
                    infoWinAdapter.dq = question;
                    marker.showInfoWindow();

                    //There should only be one question (no other questions should have same question string)
                    break;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: questions not loading error message
            }
        });

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Helper.Log.d(Constant.DEBUG, "in onInfoWindowClick with marker: " + marker.getTitle());
        final String question = marker.getTitle();
        String questionID = this.questionQuestionIdMap.get(question);
        Intent intent = new Intent(getApplicationContext(), Voting.class);
        Bundle bundle = new Bundle();
        bundle.putString("questionPassed", questionID);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}

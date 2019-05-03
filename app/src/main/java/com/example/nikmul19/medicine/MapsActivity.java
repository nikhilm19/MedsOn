package com.example.nikmul19.medicine;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.api.Places;

import com.google.android.gms.places_placereport.*;
import com.google.android.libraries.places.api.net.PlacesClient;

//import com.google.android


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.nikmul19.medicine.BuildConfig.GOOGLE_MAPS_API_KEY;

import com.google.android.libraries.places.api.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,LocationListener {

    private List<String> hasMedicineList;
    List<String> chemistIDs;

    private GoogleMap mMap;
    private boolean mLocationPermissionGranted = false;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 542;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mGpsEnabled = false;

    String medicine, medicineName;

    DatabaseReference db;
    Circle circle;
    ArrayList<String> names = new ArrayList<String>();
    ArrayList<String> mid = new ArrayList<String>();
    ArrayList<Integer> prices = new ArrayList<Integer>();

    ArrayList<String> storeNames = new ArrayList<String>();
    ArrayList<String> storeIds = new ArrayList<String>();

    @Override
    public void onLocationChanged(Location location) {
        Log.i("onLocationChanged",location.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("onActivity",String.valueOf(requestCode));

        if(requestCode==267){



            if (resultCode==RESULT_OK){

                getCurrentLocation();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        medicine = intent.getStringExtra("medicine");
        Log.i("Medicine", medicine);


        setTitle("Medson");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        getLocationPermission();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Places.initialize(getApplicationContext(), GOOGLE_MAPS_API_KEY);
        PlacesClient placesClient = Places.createClient(this);

        db = FirebaseDatabase.getInstance().getReference();

        //urlRequest();
        db.child("medicines").orderByChild("name").equalTo(medicine).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String key = "";

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    key = childSnapshot.getKey();
                    Log.i("namess", key);
                }


                String medId = dataSnapshot.child(key + "/medicine_id").getValue(String.class);
                medicineName = medicine;
                medicine = medId;
                Log.i("Fetch-Id", dataSnapshot.getKey());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mLocationPermissionGranted = true;

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        Log.i("permission",String.valueOf(requestCode));
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    Log.i("permission","granted");
                    getCurrentLocation();
                }

        }
    }

    private void getCurrentPlace() {
        //List<Place.>

        Places.initialize(getApplicationContext(), GOOGLE_MAPS_API_KEY);
        PlacesClient placesClient = Places.createClient(this);

    }


    private void getCurrentLocation() {


        try {
            Log.i("Location", "inside try");
           final LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            mGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);


            if (mLocationPermissionGranted && mGpsEnabled) {
                mMap.getUiSettings().setMyLocationButtonEnabled(true);


                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();

                locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {

                        Log.i("Location", "inside onComplete");

                        if (task.isSuccessful()) {

                            Location mLastKnownLocation = task.getResult();
                            if (circle != null) {
                                circle.remove();
                            }


                            if (mLastKnownLocation != null) {

                                Log.i("Current", mLastKnownLocation.toString());

                                LatLng current = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());


                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), 15));
                                mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.person_pin)).position(current).title("Current Location"));
                                CircleOptions circleOptions = new CircleOptions().center(current).radius(2000).fillColor(Color.argb(150, 212, 227, 252));
                                circle = mMap.addCircle(circleOptions);
                                urlRequest(current);

                                findNearestStore(current);
                            } else


                            {
                                Log.i("Location", "Null");

                                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new android.location.LocationListener() {
                                    @Override
                                    public void onLocationChanged(Location location) {
                                        Log.v("Location Changed", location.getLatitude() + " and " + location.getLongitude());
                                        lm.removeUpdates(this);
                                    }

                                    @Override
                                    public void onStatusChanged(String s, int i, Bundle bundle) {

                                    }

                                    @Override
                                    public void onProviderEnabled(String s) {

                                    }

                                    @Override
                                    public void onProviderDisabled(String s) {

                                    }
                                });
                            }


                        } else {
                            Log.i("Location", "failed");
                        }
                    }
                });
            } else {

                displayLocationSettingsRequest(getApplicationContext());








                /*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Hey", Toast.LENGTH_SHORT).show();

                        getApplicationContext().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        getCurrentLocation();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                alertDialogBuilder.setMessage("Location Not enabled");
                alertDialogBuilder.show();
                */
            }
        } catch (Exception e) {
        }
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

        // Add a marker in Sydney and move the camera

        getCurrentLocation();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                getCurrentLocation();
                return true;
            }
        });
    }

    private void displayLocationSettingsRequest(Context context) {
        final int REQUEST_CHECK_SETTINGS = 267;

        final String TAG="permission";
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                           // getCurrentLocation();
                            status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

        public void findNearestStore(final LatLng current) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);


        String nearbyPharmUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + current.latitude + "," + current.longitude +
                "&type=pharmacy&radius=2000&key=" + BuildConfig.GOOGLE_MAPS_API_KEY;

        Log.i("URL", nearbyPharmUrl);

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, nearbyPharmUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                ArrayList placeIds = new ArrayList<String>();


                try {
                    JSONArray array = (JSONArray) response.get("results");
                    Log.i("Nearby",array.toString()+"");


                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = (JSONObject) array.get(i);

                        JSONObject geo = (JSONObject) (obj.get("geometry"));

                        JSONObject loc = geo.getJSONObject("location");


                        //Log.i("Nearby",loc.toString());
                        LatLng store = new LatLng(loc.getDouble("lat"), loc.getDouble("lng"));
                        mMap.addMarker(new MarkerOptions().position(store).title("store").icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_local_pharmacy_black_18dp)));

                        placeIds.add(obj.getString("place_id"));

                        // Toast.makeText(getApplicationContext(), obj.getString("place_id") + " " + obj.getString("name"), Toast.LENGTH_SHORT).show();
                        // Log.i("Nearby",obj.getString("name"));

                        // Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), obj.getString("name"), Snackbar.LENGTH_LONG);
                        //snackbar.show();


                    }
                    checkMedA(placeIds);
                    // checkMedicineAvailability(placeIds);
                    Log.i("id-places", placeIds.toString());
                    //Log.i("Nearby",obj.toString()+"");
                    // Toast.makeText(getApplicationContext(),obj+"",Toast.LENGTH_LONG).show();


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Nearby", error.toString() + "");
                Toast.makeText(getApplicationContext(), error + "", Toast.LENGTH_LONG).show();

            }
        });

        requestQueue.add(objectRequest);


    }

    public void urlRequest(LatLng curr) {

        final RequestQueue req = Volley.newRequestQueue(this);
        // JsonObjectRequest jsonObjectRequest = new JsonObjectRequest()
        String nearByUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + curr.latitude + "," + curr.longitude + "&radius=2000&type=pharmacy&key=" + BuildConfig.GOOGLE_MAPS_API_KEY;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, nearByUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // Toast.makeText(getApplicationContext(),response+"",Toast.LENGTH_LONG).show();
                Log.i("PLACES", response + "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("PLACES", error + "");

            }
        });
        req.add(jsonArrayRequest);

    }


    public void checkMedA(final ArrayList places) {
        hasMedicineList = new ArrayList<String>();

        chemistIDs = new ArrayList<String>();
        chemistIDs.add("c_0");
        chemistIDs.add("c_1");
        chemistIDs.add("c_2");

        DatabaseReference stockRef, stockRef2;
        stockRef = FirebaseDatabase.getInstance().getReference().child("stock");


        final int size1 = places.size();

        Log.i("med-data", "size of places" + places.size());

        for (int i = 0; i < size1; i++) {

            stockRef2 = stockRef.child(places.get(i).toString());
            Log.i("ref", stockRef2.toString());

            final int finalI = i;
            stockRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {


                        Log.i("Checking", dataSnapshot.getValue().toString());

                        if (dataSnapshot.hasChild(medicine))//todo && dataSnapshot.child(medicine).child("Quantity")>=reqdQty*)
                        {

                            hasMedicineList.add(places.get(finalI).toString());
                            Log.i("hasmedicine", hasMedicineList.toString());
                            Log.i("avail-data", places.get(finalI).toString());
                            Log.i("medicines-data", dataSnapshot.child(medicine).getValue().toString());


                            // prices.add(dataSnapshot.child(medicine).child("price").getValue(Integer.class));
                            db.child("medicines").child(medicine).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    prices.add(dataSnapshot.child("price").getValue(Integer.class));

                                    mid.add(dataSnapshot.child("medicine_id").getValue(String.class));
                                    Log.i("prices", dataSnapshot.getValue().toString());

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }


                    }
                    if (finalI == size1 - 1)
                        printChemistThatHaveThisMedicine();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

    private void printChemistThatHaveThisMedicine() {
        final int size2 = hasMedicineList.size();
        int i=0;
        Log.i("hasmedicine", hasMedicineList.toString());

        prices= new ArrayList<>();
        storeNames= new ArrayList<>();
        names= new ArrayList<>();
        storeIds= new ArrayList<>();




        for (i = 0; i < size2; i++) {
            final int finalI=i;

            final String str = hasMedicineList.get(i);


            db.child("chemist").orderByChild("place_id").equalTo(str).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String key = "";

                    //todo: add place id as key
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        key = childSnapshot.getKey();
                        Log.i("names", key);
                    }
                    String name = dataSnapshot.child(key).child("name").getValue(String.class);
                    String location = dataSnapshot.child(key).child("formatted_address").getValue().toString();
                    String geoLoc = dataSnapshot.child(key).child("geometry/location").getValue().toString();

                    JsonObject geo = (JsonObject) new JsonParser().parse(geoLoc);

                    Double lat = Double.parseDouble(geo.get("lat").toString());
                    Double lng = Double.parseDouble(geo.get("lng").toString());

                    LatLng availStore = new LatLng(lat, lng);

                    mMap.addMarker(new MarkerOptions().position(availStore).title(name));

                    Log.i("names", lat.toString() + " " + lng.toString());


                    storeNames.add(dataSnapshot.child(key).child("name").getValue(String.class));

                    names.add(medicineName);
                    prices.add(dataSnapshot.child(key).child("price").getValue(Integer.class));

                    storeIds.add(str);






                    if(storeNames.size()==size2)showMedicines();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




            // Toast.makeText(MapsActivity.this,hasMedicineList.toString(),Toast.LENGTH_SHORT).show();
        }

    }

    public void showMedicines() {

        Bundle bundle = new Bundle();

        bundle.putString("activity", "maps");

        bundle.putIntegerArrayList("price", prices);
        bundle.putStringArrayList("name", names);

        bundle.putStringArrayList("mid", mid);

        bundle.putStringArrayList("stores", storeNames);

        bundle.putStringArrayList("store-ids", storeIds);
        Toast.makeText(MapsActivity.this, "Medicine available in " + storeNames.toString(), Toast.LENGTH_LONG).show();
        Log.i("store-names",storeNames.toString());
        Log.i("prices-final", prices.toString());
        Log.i("mids", mid.toString());
        Log.i("names-final", names.toString());
        Intent intent = new Intent(MapsActivity.this, DrawerActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }


//TODO find the nearby store's place id and c_id and check if stock avalaible or not.
    //for that get the c_id first and then get stock of that chemist and search of medicine_id in stock



}


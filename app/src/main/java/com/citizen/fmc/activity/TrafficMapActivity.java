package com.citizen.fmc.activity;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.citizen.fmc.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class TrafficMapActivity extends AppCompatActivity implements OnMapReadyCallback, android.location.LocationListener {

    private GoogleMap map;
    private String latAndLongFrom;
    private String updateLatLngFrom;
    private String latAndLongTo;
    private String updateLatLngTo;
    private Double mapLatLng;
    private double longitude = 0.0;
    private double latitude = 0.0;
    private SupportMapFragment mapFragment;
    private LocationManager locationManager;
    private Toolbar toolbar;
    private ImageView iv_bidirectional_icon;
    private int FLAG = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.traffic_map_layout);
        toolbar = findViewById(R.id.toolbar);
        iv_bidirectional_icon = findViewById(R.id.iv_bidirectional_icon);
        setSupportActionBar(toolbar);
        permissionCheck();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        getLocation();
        final PlaceAutocompleteFragment autocompleteFragmentFrom = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_from);
        final PlaceAutocompleteFragment autocompleteFragmentTo = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_to);
        autocompleteFragmentFrom.setHint(getResources().getString(R.string.current_location));
        autocompleteFragmentTo.setHint(getResources().getString(R.string.destination));


//        ((EditText)autocompleteFragmentFrom.getView().findViewById(androidx.appcompat.R.id.place_autocomplete_search_input)).setTextSize(13.0f);
//        ((EditText)autocompleteFragmentFrom.getView().findViewById(R.id.place_autocomplete_search_input)).setTextColor(getResources().getColor(R.color.colorWhite));
//        autocompleteFragmentFrom.getView().findViewById(R.id.place_autocomplete_search_input).setBackgroundColor(getResources().getColor(R.color.colorLightBlue));
//        ((EditText) autocompleteFragmentFrom.getView().findViewById(R.id.place_autocomplete_search_input)).setHintTextColor(getResources().getColor(R.color.colorWhite));
//        ((EditText)autocompleteFragmentTo.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(13.0f);
//        ((EditText)autocompleteFragmentTo.getView().findViewById(R.id.place_autocomplete_search_input)).setTextColor(getResources().getColor(R.color.colorWhite));
//        autocompleteFragmentTo.getView().findViewById(R.id.place_autocomplete_search_input).setBackgroundColor(getResources().getColor(R.color.colorLightBlue));
//        ((EditText) autocompleteFragmentTo.getView().findViewById(R.id.place_autocomplete_search_input)).setHintTextColor(getResources().getColor(R.color.colorWhite));

        //Handeling Multiple OnClick Event on Single Imageview:-
            iv_bidirectional_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if(FLAG == 0){
                       autocompleteFragmentFrom.setHint(getResources().getString(R.string.destination));
                       autocompleteFragmentTo.setHint(getResources().getString(R.string.current_location));
                       FLAG = 1;
                   }else if(FLAG == 1){
                       autocompleteFragmentFrom.setHint(getResources().getString(R.string.current_location));
                       autocompleteFragmentTo.setHint(getResources().getString(R.string.destination));
                       FLAG = 0;
                   }
                }
            });

        //AutoCompleteFragmentFrom OnSelect Listener:-
        autocompleteFragmentFrom.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                latAndLongFrom = String.valueOf(place.getLatLng());
                updateLatLngFrom = latAndLongFrom.replace("lat/lng:", "").
                        replace("(", "").
                        replace(")", "");
                Log.v("PLACE_LAT_LNG", latAndLongFrom);
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(TrafficMapActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();

            }
        });

        //AutoCompleteFrom OnClear Click Event:-
//        autocompleteFragmentFrom.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
//        autocompleteFragmentFrom.getView().findViewById(R.id.place_autocomplete_clear_button).setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v) {
//                map.clear();
//                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                ((PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_from)).setText("");
//            }
//        });


        //AutoCompleteFragmentTo OnSelect Listener:-
        autocompleteFragmentTo.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                latAndLongTo = String.valueOf(place.getLatLng());
                updateLatLngTo = latAndLongTo.replace("lat/lng:", "").
                        replace("(", "").
                        replace(")", "");
                Log.v("PLACE_LAT_LNG", updateLatLngTo);

                if (mapFragment != null) {
                    mapFragment.getMapAsync(TrafficMapActivity.this);
                }
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(TrafficMapActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();

            }
        });

        //AutoCompleteFragmentTo OnClear Click Event:-
//        autocompleteFragmentTo.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
//        autocompleteFragmentTo.getView().findViewById(R.id.place_autocomplete_clear_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                map.clear();
//                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                ((PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_to)).setText("");
//            }
//        });
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (updateLatLngFrom == null && updateLatLngTo == null) {
            LatLng defaultPoint = new LatLng(latitude, longitude);
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }else {
            String[] mapLatLngFrom = updateLatLngFrom.split(",");
            String[] mapLatLngTo = updateLatLngTo.split(",");
            LatLng fromPoint = new LatLng(Double.parseDouble(mapLatLngFrom[0]) , Double.parseDouble(mapLatLngFrom[1]));
            LatLng toPoint =  new LatLng(Double.parseDouble(mapLatLngTo[0]) , Double.parseDouble(mapLatLngTo[1]));

            PolylineOptions popOptions = new PolylineOptions();
            popOptions.add(fromPoint).add(toPoint).add(fromPoint).width(15f).color(Color.RED).geodesic(true);
            map.addPolyline(popOptions);
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom( fromPoint, 15f));
            googleMap.addMarker(new MarkerOptions().position(fromPoint));
            map.setTrafficEnabled(true);

        }
    }

    public void permissionCheck(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
                .onSameThread()
                .check();
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLongitude();
        longitude = location.getLatitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

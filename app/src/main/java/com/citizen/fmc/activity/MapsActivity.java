package com.citizen.fmc.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.citizen.fmc.R;
import com.citizen.fmc.utils.Constants;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
/*import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;*/

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener {

    private GoogleMap mMap;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private SupportMapFragment mapFragment;
    private LocationManager locationManager;
    private Button normalBtn;
    private Button hybridBtn;
    private Button satelliteBtn;
    private Button terrainBtn;
    private GoogleMap googleMap;
    private TextView from_et,to_et;
    private SupportPlaceAutocompleteFragment autocompleteFragment;
    private  int PLACE_AUTOCOMPLETE_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        permissionCheck();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        from_et = (TextView) findViewById(R.id.from_et);
        to_et = (TextView) findViewById(R.id.to_et);
          autocompleteFragment = (SupportPlaceAutocompleteFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


/* autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {

                        from_et.setText(place.getName());
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));
                    }

                    @Override
                    public void onError(Status status) {

                    }


                });*/

        from_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),PlaceAutoCompleteActivity.class);
                startActivity(intent);

                /*try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(MapsActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }*/

               /* try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(MapsActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }*/
               /* autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {

                        from_et.setText(place.getName());
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));
                    }

                    @Override
                    public void onError(Status status) {

                    }


                });*/
            }
        });


        to_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {

                        to_et.setText(place.getName());
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));
                    }

                    @Override
                    public void onError(Status status) {

                    }


                });*/

            }
        });

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getLocation();
        gpsEnabledCheck();


       /* AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();

        autocompleteFragment.setFilter(typeFilter);*/



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i("place", "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("status", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
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

        try {

            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
            }
            // Loading map
            // initilizeMap();

            // Changing map type
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);

            // Showing / hiding your current location

            mMap.setMyLocationEnabled(true);
            mMap.setTrafficEnabled(true);
            // Enable / Disable zooming controls
            mMap.getUiSettings().setZoomControlsEnabled(true);

            // Enable / Disable my location button
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            // Enable / Disable Compass icon
            mMap.getUiSettings().setCompassEnabled(true);

            // Enable / Disable Rotate gesture
            mMap.getUiSettings().setRotateGesturesEnabled(true);

            // Enable / Disable zooming functionality
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            LatLng pos = new LatLng(latitude, longitude);
           /* LatLng pos = new LatLng(latitude, longitude);
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title("Your Location")
                    .snippet(String.valueOf(latitude) + ", " + String.valueOf(longitude)));
                    //.icon(BitmapDescriptorFactory.fromBitmap(Utils.getBitmapFromVectorDrawable(this, R.drawable.ic_pin)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
            marker.showInfoWindow();*/


         //   latLng = new LatLng(userLat, userLong);

         //   geoAddressTextView.setText(Html.fromHtml(Constants.getGeoLocationAddress(CreateFieldInspectionActivity.this, userLat, userLong)));
//            title = Constants.showGeoLocationAddress(CreatePTUInspectionActivity.this, userLat, userLong);
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title("Your Location")
                    .snippet(String.valueOf(pos.latitude) + ", " + String.valueOf(pos.longitude))
                    .icon(BitmapDescriptorFactory.fromBitmap(Constants
                            .getBitmapFromVectorDrawable(MapsActivity.this, R.drawable.ic_placeholder))));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
            marker.showInfoWindow();



        } catch (Exception e) {
            e.printStackTrace();

        }


      /*  mMap.addMarker(new MarkerOptions().position(pos).title("You are Here !"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f));*/
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        gpsEnabledCheck();
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0F, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        gpsEnabledCheck();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    public void permissionCheck(){
        if ((ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

        }
        /*Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                })
                .onSameThread()
                .check();*/
    }

   /* @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.normalBtn:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.hybridBtn:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.terrainBtn:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.satelliteBtn:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
        }
    }*/

    private void gpsEnabledCheck() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onClick(View view) {

    }
}

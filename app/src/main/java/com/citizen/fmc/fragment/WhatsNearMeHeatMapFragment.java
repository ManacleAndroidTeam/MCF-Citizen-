package com.citizen.fmc.fragment;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.citizen.fmc.R;
import com.citizen.fmc.adapter.HeatMapInfoAdapter;
import com.citizen.fmc.model.LocationModal;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;
import java.util.List;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;

public class WhatsNearMeHeatMapFragment extends Fragment implements OnMapReadyCallback {

    //Defining Views to Use:-
    private FragmentActivity activity;
    private MapFragment mapFragment;
    private int mapFlag;
    private ArrayList<LocationModal> locationModalArrayList = new ArrayList<>();
    private RadioRealButtonGroup radioRealButtonGroup;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        View mView = inflater.inflate(R.layout.whats_near_me_heat_map, container, false);
        String toolbarTitle = getArguments().getString(Constants.KEY_TOOL_BAR_TITLE);
        radioRealButtonGroup = mView.findViewById(R.id.radio_gp_dialog);
        locationModalArrayList = getArguments().getParcelableArrayList(Constants.LATITUDE_LONGITUDE_LIST);
        Utils.setToolbarTitle(activity, toolbarTitle);
        mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

              //RadioGroup SetOnClick Event:-
               radioRealButtonGroup.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
                    @Override
                    public void onClickedButton(RadioRealButton button, int position) {
                        mapFlag = position;
                        callMapView();
                    }
                });

        return mView;
    }

    //Different Map View:-
    private void callMapView() {
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(false);
        menu.findItem(R.id.action_notification).setVisible(false);

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        if (mapFlag == 0) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (mapFlag == 1) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else if (mapFlag == 2) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }

        if (locationModalArrayList.size() > 0) {
            addHeatMap(googleMap);

            for (int i = 0; i < locationModalArrayList.size(); i++) {
                    createMarker(googleMap,
                            Double.parseDouble(locationModalArrayList.get(i).getLatitude()),
                            Double.parseDouble(locationModalArrayList.get(i).getLongitude()),
                            "Address : " + locationModalArrayList.get(i).getAddress());
            }
        }

        LatLng NDMCOfficeCoordinates = new LatLng(28.628627, 77.215525);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(NDMCOfficeCoordinates));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));

        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void createMarker(GoogleMap googleMap, double latitude, double longitude, String title) {
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
//                .anchor(0.5f, 0.5f)
                .title(title)
                .icon(BitmapDescriptorFactory.fromBitmap(Constants
                        .getBitmapFromVectorDrawable(getActivity(), R.drawable.ic_pin))));
        googleMap.setInfoWindowAdapter(new HeatMapInfoAdapter(getActivity()));
    }

    private void addHeatMap(GoogleMap googleMap) {
        List<WeightedLatLng> list;

        list = readItems();

        // Create the gradient.
        int[] colors = {
                Color.rgb(102, 225, 0), // green
                Color.rgb(255, 0, 0)    // red
        };

        float[] startPoints = {
                0.2f, 1f
        };

        Gradient gradient = new Gradient(colors, startPoints);

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        if (list != null) {
            HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                    .weightedData(list)
                    .gradient(gradient)
                    .build();

            // Add a tile overlay to the map, using the heat map tile provider.
            TileOverlay mOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
            mProvider.setOpacity(1);
            mProvider.setRadius(30);
            mOverlay.clearTileCache();
        }
    }

        private ArrayList<WeightedLatLng> readItems() throws NullPointerException {
            ArrayList<WeightedLatLng> list = new ArrayList<>();
            for (int i = 0; i < locationModalArrayList.size(); i++) {
                list.add(new WeightedLatLng(
                        new LatLng(Double.parseDouble(locationModalArrayList.get(i).getLatitude()),
                                Double.parseDouble(locationModalArrayList.get(i).getLongitude())), 20.0));
            }
            return list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null)
            getActivity().getFragmentManager().beginTransaction().remove(mapFragment).commit();
    }

}

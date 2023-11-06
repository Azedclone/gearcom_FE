package com.gearcom.ui.location;

import static com.mapbox.maps.plugin.gestures.GesturesUtils.getGestures;
import static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gearcom.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.LocationPuck2D;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private MapView map;
    private FloatingActionButton myLocation;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocationFragment newInstance(String param1, String param2) {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    });

    private final OnIndicatorBearingChangedListener onIndicatorBearingChangedListener = new OnIndicatorBearingChangedListener() {
        @Override
        public void onIndicatorBearingChanged(double v) {
            map.getMapboxMap().setCamera(new CameraOptions.Builder().bearing(v).build());
        }
    };

    private final OnIndicatorPositionChangedListener onIndicatorPositionChangedListener = new OnIndicatorPositionChangedListener() {
        @Override
        public void onIndicatorPositionChanged(@NonNull Point point) {
            map.getMapboxMap().setCamera(new CameraOptions.Builder().center(point).zoom(20.0).build());
            getGestures(map).setFocalPoint(map.getMapboxMap().pixelForCoordinate(point));
        }
    };

    private final OnMoveListener onMoveListener = new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            getLocationComponent(map).removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
            getLocationComponent(map).removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
            getGestures(map).removeOnMoveListener(onMoveListener);
            myLocation.show();
        }

        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
            return false;
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            map = getView().findViewById(R.id.map);
            myLocation = getView().findViewById(R.id.myLocation);
            myLocation.hide();

            // Set permission to access location service
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            map.getMapboxMap().loadStyleUri(Style.SATELLITE, new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    map.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(10.0).build());
                    LocationComponentPlugin plugin = getLocationComponent(map);
                    plugin.setEnabled(true);
                    LocationPuck2D locationPuck2D = new LocationPuck2D();
                    locationPuck2D.setBearingImage(AppCompatResources.getDrawable(getActivity(), R.drawable.location));
                    plugin.setLocationPuck(locationPuck2D);
                    // 7. Track and update user location using onIndicatorPositionChangedListener
                    plugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
                    plugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
                    getGestures(map).addOnMoveListener(onMoveListener);

                    myLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            plugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
                            plugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
                            getGestures(map).addOnMoveListener(onMoveListener);
                            myLocation.hide();
                        }
                    });
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false);
    }


}
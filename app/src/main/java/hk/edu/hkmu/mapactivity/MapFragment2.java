package hk.edu.hkmu.mapactivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapFragment2 extends Fragment implements OnMapReadyCallback {

    private TextView dialogTextView;
    private AppCompatRatingBar ratingTextView;
    private TextView locationTextView;
    private TextView distanceTextView;

    private Button cmtButton;

    private GoogleMap googleMap;

    private ViewPager2 viewPager2;

    private LatLngBounds.Builder boundsBuilder;

    private List<Marker> markerList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map2, container, false);

        cmtButton = view.findViewById(R.id.cmtButton);
        cmtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace fragment_map2 with fragment_map3
                MapFragment3 mapFragment3 = new MapFragment3();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, mapFragment3);
                fragmentTransaction.remove(MapFragment2.this);
                fragmentTransaction.addToBackStack(null); // Optional, to allow back navigation
                fragmentTransaction.commit();
            }
        });
//        dialogTextView = view.findViewById(R.id.dialogTextView);
        ratingTextView = view.findViewById(R.id.ratingTextView);
        locationTextView = view.findViewById(R.id.locationTextView);
        distanceTextView = view.findViewById(R.id.distanceTextView);

        // Set the location and distance text views
//        locationTextView.setText("Patan dhoka Road (Lalitpur, Nepal)"); // Set the location text
        distanceTextView.setText("10 meters"); // Set the distance text

        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapImageView);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        MapActivity mapActivity = (MapActivity) getActivity();
        if (mapActivity != null) {
            viewPager2 = mapActivity.getViewPager2();
        }
        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String locationName = (String) marker.getTag();
                locationTextView.setText(locationName);
                return false;
            }
        });

        // Disable the default two button
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        // Add multiple locations using LatLng (latitude, longitude)
        addLocationToMap(22.301115, 114.171868, "Mira Place");
//        addLocationToMap(22.303536, 114.171094, "The One");
//        addLocationToMap(22.304386, 114.172137, "Prudential Centre");

        // Zoom to fit all markers on the map
        zoomToMarkers();
    }

    private void addLocationToMap(double latitude, double longitude, String locationName) {
        LatLng latLng = new LatLng(latitude, longitude);
        BitmapDescriptor customMarkerIcon = getBitmapFromVectorDrawable(R.drawable.ic_restroom);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(locationName)
                .icon(customMarkerIcon);

        Marker marker = googleMap.addMarker(markerOptions);
        marker.setTag(locationName);

        // Extend the bounds to include the marker's position
        if (boundsBuilder == null) {
            boundsBuilder = new LatLngBounds.Builder().include(latLng);
        } else {
            boundsBuilder.include(latLng);
        }

        markerList.add(marker); // Add the marker to the list

    }

    private BitmapDescriptor getBitmapFromVectorDrawable(int vectorDrawableResourceId) {
        Drawable drawable = ContextCompat.getDrawable(requireContext(), vectorDrawableResourceId);
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void zoomToMarkers() {
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (Marker marker : markerList) {
            boundsBuilder.include(marker.getPosition());
        }

        LatLngBounds bounds = boundsBuilder.build();
        int padding = 100; // Adjust the padding as per your needs
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.moveCamera(cameraUpdate);
    }
}

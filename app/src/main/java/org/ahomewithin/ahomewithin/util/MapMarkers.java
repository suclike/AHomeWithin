package org.ahomewithin.ahomewithin.util;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.ahomewithin.ahomewithin.AHomeWithinClient;
import org.ahomewithin.ahomewithin.R;
import org.ahomewithin.ahomewithin.models.User;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by barbara on 3/12/16.
 */
public class MapMarkers {

    private ArrayList<User> users;
    private HashMap<String, User> markerMap= new HashMap<String, User>();

    class CustomWindowAdapter implements GoogleMap.InfoWindowAdapter {
        LayoutInflater mInflater;

        public CustomWindowAdapter(LayoutInflater i){
            mInflater = i;
        }

        // This defines the contents within the info window based on the marker
        @Override
        public View getInfoContents(Marker marker) {
            View v = mInflater.inflate(R.layout.map_info_window, null);
            if (markerMap != null) {
                User user = markerMap.get(marker.getId());

                final TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
                tvTitle.setText(user.getFullName());

                final TextView tvDescription = (TextView) v.findViewById(R.id.tvDescription);
                tvDescription.setText(user.getDescription());

                final Button btnChat = (Button) v.findViewById(R.id.btnChat);
                btnChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "button clicked", Toast.LENGTH_SHORT).show();
                        Log.d("MAP", "Chat button clicked");
                    }
                });
            }
            // Return info window contents
            return v;
        }

        // This changes the frame of the info window; returning null uses the default frame.
        // This is just the border and arrow surrounding the contents specified above
        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }
    }


    public MapMarkers(Context context) {
        users = new ArrayList<User>();
        loadUsers(context);
    }

    public void addMarkersToMap(GoogleMap map, LayoutInflater inflater) {
        map.setInfoWindowAdapter(new CustomWindowAdapter(inflater));

        markerMap= new HashMap<String, User>();
        if (users != null) {
            for(User user: users) {
                createMarkerForUser(map, user);
            }
        }
    }

    private void loadUsers(Context context) {
        try {
            JSONObject response = AHomeWithinClient.getUsers(context);
            users = User.fromJSONArray(response.getJSONArray("users"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createMarkerForUser(GoogleMap map, User user) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(user.getLat(), user.getLng()));
        switch(user.type) {
            case SERVICE_PROVIDER:
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                break;
            default: // COMMUNITY:
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        }
        Marker marker = map.addMarker(markerOptions);
        dropPinEffect(marker);
        markerMap.put(marker.getId(), user);
    }

    private void dropPinEffect(final Marker marker) {
        // Handler allows us to repeat a code block after a specified delay
        final android.os.Handler handler = new android.os.Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        // Use the bounce interpolator
        final android.view.animation.Interpolator interpolator =
                new BounceInterpolator();

        // Animate marker with a bounce updating its position every 15ms
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                // Calculate t for bounce based on elapsed time
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                // Set the anchor
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    // Post this event again 15ms from now.
                    handler.postDelayed(this, 15);
                } else { // done elapsing, show window
                    marker.showInfoWindow();
                }
            }
        });
    }


}
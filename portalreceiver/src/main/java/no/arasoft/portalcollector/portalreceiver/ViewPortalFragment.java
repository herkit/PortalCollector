package no.arasoft.portalcollector.portalreceiver;

/**
 * Created by Henrik on 10.04.14.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

/**
 * A placeholder fragment containing a simple view.
 */
public class ViewPortalFragment extends Fragment {

    public static final String PORTAL_ID = "portal_id";
    public static final String PORTAL_TITLE = "portal_title";
    public static final String PORTAL_LAT = "portal_lat";
    public static final String PORTAL_LON = "portal_lon";
    private Portal _portal;

    public ViewPortalFragment()
    {
        Db db = new Db(getActivity());
        db.open();
    }

    public ViewPortalFragment(Portal portal) {
        _portal = portal;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            Log.d("view-portal", "saved instance received");
            _portal = new Portal();
            _portal.setId(savedInstanceState.getLong(PORTAL_ID));
            _portal.setTitle(savedInstanceState.getString(PORTAL_TITLE));
            _portal.setLat(savedInstanceState.getFloat(PORTAL_LAT));
            _portal.setLon(savedInstanceState.getFloat(PORTAL_LON));
        }

        View rootView = inflater.inflate(R.layout.fragment_portal_view, container, false);

        TextView portal_title = (TextView)rootView.findViewById(R.id.portal_title);

        Fragment mapview = getFragmentManager().findFragmentById(R.id.portal_position_map);

        final GoogleMap map = ((SupportMapFragment)mapview).getMap();

        Log.d("portal-view", "Maptype: " + map.getMapType());

        LatLng portal_position = new LatLng(_portal.getLat(), _portal.getLon());
        map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(portal_position , 15, 0, 0)));

        MarkerOptions marker = new MarkerOptions();
        marker.position(portal_position);
        marker.title(_portal.getTitle());

        map.addMarker(marker);

        ImageButton navigateButton = (ImageButton)rootView.findViewById(R.id.navigate_to_portal);

        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri intent_uri = Uri.parse("google.navigation:q=" + _portal.getLat() + "," + _portal.getLon());
                Log.d("portal-nav", intent_uri.toString());
                Intent navigateIntent = new Intent(Intent.ACTION_VIEW, intent_uri);
                startActivity(navigateIntent);
            }
        });

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                VisibleRegion area = map.getProjection().getVisibleRegion();
                LatLng fl = area.farLeft;
                LatLng fr = area.farRight;
            }
        });

        portal_title.setText(_portal.getTitle());

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putLong(PORTAL_ID, _portal.getId());
        savedInstanceState.putString(PORTAL_TITLE, _portal.getTitle());
        savedInstanceState.putFloat(PORTAL_LAT, _portal.getLat());
        savedInstanceState.putFloat(PORTAL_LON, _portal.getLon());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

}
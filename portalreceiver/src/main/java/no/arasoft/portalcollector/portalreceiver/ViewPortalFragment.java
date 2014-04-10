package no.arasoft.portalcollector.portalreceiver;

/**
 * Created by Henrik on 10.04.14.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A placeholder fragment containing a simple view.
 */
public class ViewPortalFragment extends Fragment {

    private Portal _portal;

    public ViewPortalFragment(Portal portal) {
        _portal = portal;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_portal_view, container, false);

        TextView portal_title = (TextView)rootView.findViewById(R.id.portal_title);

        Fragment mapview = getFragmentManager().findFragmentById(R.id.portal_position_map);

        GoogleMap map = ((SupportMapFragment)mapview).getMap();

        Log.d("portal-view", "Maptype: " + map.getMapType());

        LatLng portal_position = new LatLng(_portal.getLat(), _portal.getLon());
        map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(portal_position , 15, 0, 0)));

        MarkerOptions marker = new MarkerOptions();
        marker.position(portal_position);
        marker.title(_portal.getTitle());

        map.addMarker(marker);

        portal_title.setText(_portal.getTitle());

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
package no.arasoft.portalcollector.portalreceiver;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.UrlQuerySanitizer;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ReceivePortal extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new Db(this);

        Intent intent = getIntent();
        String action = intent.getAction();
        String scheme = intent.getScheme();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            handlePortalLink(intent);
        }

    }

    private Db db;
    private float lat;
    private float lon;
    private String title;

    void handlePortalLink(Intent intent) {
        String content = intent.getStringExtra(Intent.EXTRA_TEXT);

        UrlQuerySanitizer sanitizer = new UrlQuerySanitizer();

        sanitizer.setAllowUnregisteredParamaters(true);
        sanitizer.parseUrl(content);
        String pll = sanitizer.getValue("pll");
        String ll = sanitizer.getValue("ll");

        Log.i("ingress-url", "pll=" + pll);
        Log.i("ingress-url", "ll=" + ll);

        title = intent.getStringExtra(Intent.EXTRA_SUBJECT);

        String pos;
        if (pll != null)
            pos = pll;
        else
            pos = ll;

        if (pos != null) {
            String[] latlng = pos.split(",");
            Log.i("ingress-url", "lat="+latlng[0]);
            Log.i("ingress-url", "lng="+latlng[1]);
            lat = Float.valueOf(latlng[0]);
            lon = Float.valueOf(latlng[1]);

            setMapPosition();

            Log.i("portal", lon + ", " + lat + ", \"" + title + "\"");

            setContentView(R.layout.activity_receive_portal);

            TextView portalName = (TextView)findViewById(R.id.portal_name);

            portalName.setText(title);

            db.open();
            Cursor existingPortals = db.fetchPortalsByTitle(title);

            LinearLayout existingPortalsFound = (LinearLayout)findViewById(R.id.existing_portals_found);

            if (existingPortals.moveToFirst()) {
                ListView existingPortalList = (ListView) findViewById(R.id.existing_portals);

                existingPortalsFound.setVisibility(View.VISIBLE);

                SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.portal_list_item, existingPortals, new String[]{Db.KEY_TITLE, Db.KEY_LAT, Db.KEY_LON}, new int[]{R.id.portal_title, R.id.portal_lat, R.id.portal_lon});

                existingPortalList.setAdapter(adapter);
            } else {
                existingPortalsFound.setVisibility(View.INVISIBLE);
            }

            Button ok = (Button)findViewById(R.id.ok);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.open();
                    long id = db.createPortal(title, lat, lon);
                    Log.d("newportal", "Portal added to db: " + id);
                    db.close();
                    finish();
                }
            });

            Button cancel = (Button)findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }

        Log.i("intent", content);

    }

    private void setMapPosition() {
        try {
            Fragment f = getFragmentManager().findFragmentById(R.id.portal_position_map);
            GoogleMap map = ((MapFragment)f).getMap();
            LatLng latlng = new LatLng((double)lat, (double)lon);
            Marker newmarker = map.addMarker(new MarkerOptions().position(latlng).title("marker title"));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latlng).zoom(14.0f).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            map.moveCamera(cameraUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.receive_portal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

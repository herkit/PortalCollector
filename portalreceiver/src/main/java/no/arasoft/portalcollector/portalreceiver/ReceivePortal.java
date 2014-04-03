package no.arasoft.portalcollector.portalreceiver;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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
        Pattern pattern = Pattern.compile("ingress\\.com/intel\\?ll=(\\d+\\.\\d+),(\\d+\\.\\d+)");

        title = intent.getStringExtra(Intent.EXTRA_SUBJECT);

        Log.i("intent", content);

        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            lat = Float.valueOf(matcher.group(1));
            lon = Float.valueOf(matcher.group(2));
            Log.i("portal", lon + ", " + lat + ", \"" + title + "\"");

            setContentView(R.layout.activity_receive_portal);

            Button ok = (Button)findViewById(R.id.ok);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.open();
                    long id = db.createPortal(title, lat, lon);
                    Log.d("newportal", "Portal added to db: " + id);
                    db.close();

                }
            });
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

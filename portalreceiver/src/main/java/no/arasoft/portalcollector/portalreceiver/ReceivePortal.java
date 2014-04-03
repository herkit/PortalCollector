package no.arasoft.portalcollector.portalreceiver;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ReceivePortal extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String action = intent.getAction();
        String scheme = intent.getScheme();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            handlePortalLink(intent);
        }


    }

    void handlePortalLink(Intent intent) {
        String title = intent.getStringExtra(Intent.EXTRA_SUBJECT);
        String content = intent.getStringExtra(Intent.EXTRA_TEXT);
        Pattern pattern = Pattern.compile("ingress\\.com/intel\\?ll=(\\d+\\.\\d+),(\\d+\\.\\d+)");

        Log.i("intent", content);

        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            Log.i("portal", title);
            Log.i("portal", "Start index: " + matcher.start());
            Log.i("portal", "End index: " + matcher.end() + " ");
            Log.i("portal", matcher.group());
        }
        setContentView(R.layout.activity_receive_portal);
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

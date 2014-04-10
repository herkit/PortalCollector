package no.arasoft.portalcollector.portalreceiver;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PortalViewActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal_view);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            long id = intent.getLongExtra("id", 1);
            if (id > 0) {
                Log.d("view portal", "id: " + id);
            }

            Db db = new Db(this);
            db.open();
            Portal portal = db.fetchPortalItem(id);
            db.close();

            Fragment portalFragment = (Fragment)new ViewPortalFragment(portal);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, portalFragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.portal_view, menu);
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

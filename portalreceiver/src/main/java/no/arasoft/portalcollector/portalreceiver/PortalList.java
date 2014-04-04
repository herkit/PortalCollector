package no.arasoft.portalcollector.portalreceiver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Locale;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class PortalList extends ActionBarActivity {

    private ListView portalList;
    private Db db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal_list);

        portalList = (ListView)findViewById(R.id.portalList);

        db = new Db(this);

        db.open();

        Cursor c = db.fetchAllPortalsOrderByTitle();

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.portal_list_item, c, new String[] { Db.KEY_TITLE, Db.KEY_LAT, Db.KEY_LON }, new int[] { R.id.portal_title, R.id.portal_lat, R.id.portal_lon }  );

        portalList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.portal_list, menu);
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
        if (id == R.id.action_export) {
            return exportPortals();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean exportPortals() {
        File root = android.os.Environment.getExternalStorageDirectory();

        File dir = new File (root.getAbsolutePath() + "/pois");
        dir.mkdirs();

        File file = new File(dir, "portals.csv");

        try {

            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            PrintWriter pw = new PrintWriter(fOut);

            Cursor c = db.fetchAllPortalsOrderByTitle();

            Boolean hasData = c.moveToFirst();
            while (hasData) {
                // Write the string to the file
                String row = c.getFloat(c.getColumnIndex(Db.KEY_LAT)) + ", " +
                        c.getFloat(c.getColumnIndex(Db.KEY_LON)) + ", \"" +
                        c.getString(c.getColumnIndex(Db.KEY_TITLE)) + "\"";

                pw.println(row);

                Log.d("export-portals", row);
                hasData = c.moveToNext();
            }

            pw.flush();
            pw.close();
            fOut.close();

            Toast.makeText(getApplicationContext(), "Portals exported to " + file.toString(),
                    Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("export-portals", "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
    return true;
    }

}

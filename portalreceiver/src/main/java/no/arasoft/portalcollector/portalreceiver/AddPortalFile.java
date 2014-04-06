package no.arasoft.portalcollector.portalreceiver;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import au.com.bytecode.opencsv.CSVReader;


public class AddPortalFile extends ActionBarActivity {

    private Db db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new Db(this);
        db.open();

        Intent intent = getIntent();
        String action = intent.getAction();
        String scheme = intent.getScheme();
        String type = intent.getType();

        if (Intent.ACTION_VIEW.equals(action) && type.equals("text/csv")) {
            Log.i("import", intent.getData().toString());
            Uri file = intent.getData();
            try {
                InputStream csvStream = getContentResolver().openInputStream(file);
                InputStreamReader csvStreamReader = new InputStreamReader(csvStream, "ISO-8859-1");
                csvStreamReader.getEncoding();
                String next[] = {};
                CSVReader csvReader = new CSVReader(csvStreamReader);
                    for(;;) {
                        next = csvReader.readNext();
                        if(next != null) {
                            db.createPortal(next[2], Float.parseFloat(next[1]), Float.parseFloat(next[0]));
                        } else {
                            break;
                        }
                    }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        setContentView(R.layout.activity_add_portal_file);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_portal_file, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main2, container, false);
            return rootView;
        }
    }
}

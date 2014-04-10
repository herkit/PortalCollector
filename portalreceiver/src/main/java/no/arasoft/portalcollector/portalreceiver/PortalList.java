package no.arasoft.portalcollector.portalreceiver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.security.Provider;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class PortalList extends ActionBarActivity {

    private ListView portalList;
    private TextView portalCount;
    private Db db;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal_list);

        portalList = (ListView)findViewById(R.id.portalList);
        portalCount = (TextView)findViewById(R.id.database_portal_count);

        registerForContextMenu(portalList);
        registerForContextMenu(portalCount);

        portalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), PortalViewActivity.class);
                intent.putExtra("id", l);
                startActivity(intent);
            }
        });

        db = new Db(this);
    }

    private void loadPortalData() {
        Cursor allPortalsCursor = db.fetchAllPortalsOrderByTitle();

        if (adapter == null)
        {
            adapter = new SimpleCursorAdapter(this, R.layout.portal_list_item, allPortalsCursor, new String[] { Db.KEY_TITLE, Db.KEY_LAT, Db.KEY_LON }, new int[] { R.id.portal_title, R.id.portal_lat, R.id.portal_lon }  );
            portalList.setAdapter(adapter);
        }
        else
        {
            adapter.changeCursor(allPortalsCursor);
            adapter.notifyDataSetChanged();
        }



        int count = db.countPortals();
        portalCount.setText(Integer.toString(count));

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();

        switch (v.getId()) {
            case R.id.portalList:
                inflater.inflate(R.menu.list_item_popup, menu);
                break;
            case R.id.database_portal_count:
                inflater.inflate(R.menu.database_action, menu);
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo group = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menu_delete_portal: {

                if (db.deletePortal(group.id)) {
                    Toast.makeText(this, "Deleted " + group.id, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Was not able to delete portal", Toast.LENGTH_SHORT).show();
                }

                loadPortalData();

                return true;
            }
            case R.id.menu_clear_database:
                clearDatabase();
                return true;
            default: {
                return super.onContextItemSelected(item);
            }
        }
    }

    private void clearDatabase() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final Context toastCtx = this;

        alert.setTitle("Clear database");
        alert.setMessage("Are you sure you want to clear database? This is an irreversible action!");

        alert.setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int deleteCount = db.deleteAllPortals();
                Toast.makeText(toastCtx, "Deleted " + deleteCount + " portals", Toast.LENGTH_SHORT).show();
                loadPortalData();
            }
        });

        alert.setNegativeButton("Heck no!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.portal_list, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        db.open();
        loadPortalData();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
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


        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        String filename = shared.getString("portal_export_filename", "portals.csv");
        String encoding = shared.getString("portal_export_encoding", "ISO-8859-1");

        File file = new File(dir, filename);

        try {

            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fOut, encoding);
            PrintWriter pw = new PrintWriter(fOut);

            Cursor c = db.fetchAllPortalsOrderByTitle();

            Boolean hasData = c.moveToFirst();
            while (hasData) {
                // Write the string to the file
                String row = c.getFloat(c.getColumnIndex(Db.KEY_LON)) + ", " +
                        c.getFloat(c.getColumnIndex(Db.KEY_LAT)) + ", \"" +
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

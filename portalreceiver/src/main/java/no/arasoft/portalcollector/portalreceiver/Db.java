package no.arasoft.portalcollector.portalreceiver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Db {

    private static final String TAG = "PortalsDbAdapter";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE_PORTALS = "portals";
    private static final String DATABASE_TABLE_TAGS = "tags";
    private static final String DATABASE_TABLE_PORTAL_TAGS = "portal_tags";

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 4;


    public static final String KEY_ROWID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LON = "lon";

    public static final String SORT_ASC = " ASC";
    public static final String SORT_DESC = " DESC";


    private static final String CREATE_TABLE_PORTALS = "CREATE TABLE IF NOT EXISTS  " + DATABASE_TABLE_PORTALS + " ("
            + KEY_ROWID     + " integer primary key autoincrement, "
            + KEY_TITLE     + " text not null, "
            + KEY_LAT       + " float not null, "
            + KEY_LON       + " float not null"
            + ");";

    private static final String CREATE_TABLE_TAGS = "CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE_TAGS + " ("
            + KEY_ROWID     + " integer primary key autoincrement, "
            + KEY_TITLE     + " text not null "
            + ");";

    public static final String KEY_PORTAL_ID = "portal_id";
    public static final String KEY_TAG_ID = "tag_id";

    private static final String CREATE_TABLE_PORTAL_TAGS = "CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE_PORTAL_TAGS + " ("
            + KEY_ROWID     + " integer primary key autoincrement, "
            + KEY_PORTAL_ID + " integer not null, "
            + KEY_TAG_ID    + " integer not null "
            + ");";


    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private final Context mContext;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, "Creating database " + DATABASE_NAME);

            db.execSQL(CREATE_TABLE_PORTALS);
            db.execSQL(CREATE_TABLE_TAGS);
            db.execSQL(CREATE_TABLE_PORTAL_TAGS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");

            if (oldVersion < 4 && newVersion == 4) {
                db.execSQL(CREATE_TABLE_TAGS);
                db.execSQL(CREATE_TABLE_PORTAL_TAGS);
            }

            //TODO: modifisere DB skjema, legge til created og modified date

            onCreate(db);
        }
    }

    public Db(Context ctx) {
        this.mContext = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public Db open() throws SQLException {
        mDbHelper = new DatabaseHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public long createPortal(String title, float lat, float lon) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_LAT, lat);
        initialValues.put(KEY_LON, lon);
        return mDb.insert(DATABASE_TABLE_PORTALS, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     *
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deletePortal(long rowId) {
        return mDb.delete(DATABASE_TABLE_PORTALS, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public int deleteAllPortals() {
        return mDb.delete(DATABASE_TABLE_PORTALS, "", null);
    }




    public Cursor fetchAllPortals() {
        return fetchAllPortalsOrderBy(null, null);
    }

    public int countPortals() {
        Cursor q = mDb.rawQuery("SELECT Count(*) as PortalCount FROM " + DATABASE_TABLE_PORTALS, null);
        if (q.moveToFirst())
            return q.getInt(0);
        else
            return 0;
    }

    public Cursor fetchAllPortalsOrderByTitle() {
        return mDb.query(DATABASE_TABLE_PORTALS,
                new String[] {KEY_ROWID, KEY_TITLE, KEY_LAT, KEY_LON},
                null,
                null,
                null,
                null,
                KEY_TITLE + " ASC");
    }

    public Cursor fetchAllPortalsOrderBy(String orderBy, String ascDesc) {
        Log.d("DB---------", "fikk: orderBy: "+orderBy);

        String order = orderBy;
        if (ascDesc == null)
            order += SORT_ASC; //Default sort order
        else
            order += SORT_DESC;

        return mDb.query(DATABASE_TABLE_PORTALS,
                new String[] {KEY_ROWID, KEY_TITLE, KEY_LAT, KEY_LON},
                null,
                null,
                null,
                null,
                orderBy + ascDesc);
    }


    public Cursor fetchPortal(long rowId) throws SQLException {
        Cursor mCursor = mDb.query(true, DATABASE_TABLE_PORTALS,
                new String[] {KEY_ROWID, KEY_TITLE, KEY_LAT, KEY_LON},
                KEY_ROWID + "=" + rowId,
                null,
                null,
                null,
                null,
                null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchPortalsByTitle(String title) {
        Cursor mCursor = mDb.query(true, DATABASE_TABLE_PORTALS,
                new String[] {KEY_ROWID, KEY_TITLE, KEY_LAT, KEY_LON},
                KEY_TITLE + "=\"" + title + "\"",
                null,
                null,
                null,
                null,
                null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Portal fetchPortalItem(long rowId) throws SQLException {
        Cursor cursor = fetchPortal(rowId);

        Portal note = new Portal();
        note.setId(cursor.getLong(cursor.getColumnIndex(KEY_ROWID)));
        note.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
        note.setLat(cursor.getFloat(cursor.getColumnIndex(KEY_LAT)));
        note.setLon(cursor.getFloat(cursor.getColumnIndex(KEY_LON)));

        return note;
    }

    public boolean updatePortal(long rowId, String title, float lat, float lon) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_LAT, lat);
        args.put(KEY_LON, lon);

        return mDb.update(DATABASE_TABLE_PORTALS,
                args,
                KEY_ROWID + "=" + rowId,
                null) > 0;
    }



}

package hippo.com.xuhangtest.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class PowerProvider extends ContentProvider {
    private DatabaseHelper mHelper ;
    private String dbname = "pow";
    private static final UriMatcher matcher ;
    private static final String AUTHORITY ="hippo.com.xuhangtest.db.PowerProvider";
    private static final String TAG="Provider";
    private static final int POWERPERCENT = 1;
    SQLiteDatabase db;
    private DBManager mDBManager;
    static{
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY,"power",POWERPERCENT);
        matcher.addURI(AUTHORITY,"power/#",POWERPERCENT);
    }

    @Override
    public boolean onCreate() {
        mDBManager = new DBManager(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //mHelper.getReadableDatabase();
        String table = getTableName(uri);
        return mDBManager.query(table,projection,selection,selectionArgs);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (matcher.match(uri)){
            case POWERPERCENT:
                return "vnd.android.cursor.dir/vnd.hippo.com.xuhangtest.db.PowerProvider";
                default:
                throw new IllegalArgumentException("getType --Unknown URI " + uri);

        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
//        ContentValues localvalue = null;
//        if(values!=null){
//            localvalue = new ContentValues(values);
//            localvalue.put("percent","101");
//            localvalue.put("localtime","20181121");
//        }
//        long rowid = db.insert("power",null,localvalue);
//        if(rowid>0){
//            getContext().getContentResolver().notifyChange(uri,null);
//            return ContentUris.withAppendedId(uri,rowid);
//        }
//        else return null;
        String table = getTableName(uri);
        mDBManager.insert(table,values);
        return uri;
    }

    private String getTableName(Uri uri) {
        switch (matcher.match(uri)){
            case POWERPERCENT:
                return "power";
                default:
                    return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}

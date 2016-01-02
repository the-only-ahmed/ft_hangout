package DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by pcv on 30/12/2015.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    private static final String TABLE_CONTACTS = "table_contacts";
    private static final String COL_ID = "ID";
    private static final String COL_NAME = "Name";
    private static final String COL_LAST_NAME = "LastName";
    private static final String COL_PHONE = "Phone";
    private static final String COL_EMAIL = "Email";
    private static final String COL_ADDRESS = "Address";
    private static final String COL_PIC = "Picture";
    private static final String COL_FAV = "Favorite";

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_CONTACTS + "(" + COL_ID + " integer primary key autoincrement, "
            + COL_NAME + " text not null, " + COL_LAST_NAME + " text not null, "
            + COL_PHONE + " text not null, " + COL_EMAIL + " text not null, "
            + COL_ADDRESS + " text not null, " + COL_PIC + " text not null, "
            + COL_FAV + " text not null);";

    public MySQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }
}
package DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by pcv on 30/12/2015.
 */
public class ContactDB {
    private static final int VERSION_BDD = 1;
    private static final String DB_NAME = "contact.db";

    private static final String TABLE_CONTACTS = "table_contacts";
    private static final String COL_ID = "ID";
    private static final String COL_NAME = "Name";
    private static final String COL_LAST_NAME = "LastName";
    private static final String COL_PHONE = "Phone";
    private static final String COL_EMAIL = "Email";
    private static final String COL_ADDRESS = "Address";
    private static final String COL_PIC = "Picture";
    private static final String COL_FAV = "Favorite";

    private static final int NUM_COL_ID = 0;
    private static final int NUM_COL_NAME = 1;
    private static final int NUM_COL_LAST = 2;
    private static final int NUM_COL_PHONE = 3;
    private static final int NUM_COL_EMAIL = 4;
    private static final int NUM_COL_ADDRESS = 5;
    private static final int NUM_COL_PIC = 6;
    private static final int NUM_COL_FAV = 7;

    private SQLiteDatabase db;
    private static ContactDB instance = null;

    private static MySQLiteHelper maBaseSQLite;

    public ContactDB(Context context){
        if (instance == null) {
            instance = this;
            maBaseSQLite = new MySQLiteHelper(context, DB_NAME, null, VERSION_BDD);
        }
    }

    public void open(){
        db = maBaseSQLite.getWritableDatabase();
    }

    public void close(){
        Log.d("satabase", "closed");
        db.close();
    }

    public void dropTable() {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
    }

    public static ContactDB getDataBase() { return instance; }

    public SQLiteDatabase getDB(){
        return db;
    }

    public long insertContact(Contact contact){
        ContentValues values = new ContentValues();
        values.put(COL_NAME, contact.getName());
        values.put(COL_LAST_NAME, contact.getLastName());
        values.put(COL_PHONE, contact.getPhone());
        values.put(COL_EMAIL, contact.getEmail());
        values.put(COL_ADDRESS, contact.getAddress());
        if (contact.getPicture() != null)
            values.put(COL_PIC, contact.getPicture().toString());
        else
            values.put(COL_PIC, "");
        values.put(COL_FAV, contact.getFav());
        return db.insert(TABLE_CONTACTS, null, values);
    }

    public int updateContact(Contact contact){
        ContentValues values = new ContentValues();
        values.put(COL_NAME, contact.getName());
        values.put(COL_LAST_NAME, contact.getLastName());
        values.put(COL_PHONE, contact.getPhone());
        values.put(COL_EMAIL, contact.getEmail());
        values.put(COL_ADDRESS, contact.getAddress());
        if (contact.getPicture() != null)
            values.put(COL_PIC, contact.getPicture().toString());
        else
            values.put(COL_PIC, "");
        values.put(COL_FAV, contact.getFav());
        return db.update(TABLE_CONTACTS, values, COL_ID + " = " + contact.getId(), null);
    }

    public int removeContactWithID(long id) {
        return db.delete(TABLE_CONTACTS, COL_ID + " = " + id, null);
    }

    public Contact getContactWithNum(String num, Context context) {
        String query = "SELECT * FROM " + TABLE_CONTACTS + " WHERE " + COL_PHONE + " = '" + num + "'";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        return cursorToContact(c, context);
    }

    public Contact getContactWithId(Long id, Context context) {
        String query = "SELECT * FROM " + TABLE_CONTACTS + " WHERE id = '" + id + "'";
        Cursor result = db.rawQuery(query, null);
        result.moveToFirst();
        return cursorToContact(result, context);
    }

    private Contact cursorToContact(Cursor c, Context context){
        if (c.getCount() <= 0)
            return null;

        Contact contact = new Contact(context);
        contact.setId(c.getInt(NUM_COL_ID));
        contact.setName(c.getString(NUM_COL_NAME));
        contact.setLastName(c.getString(NUM_COL_LAST));
        contact.setPhone(c.getString(NUM_COL_PHONE));
        contact.setEmail(c.getString(NUM_COL_EMAIL));
        contact.setAddress(c.getString(NUM_COL_ADDRESS));
        try {
            contact.setPicture(Uri.parse(c.getString(NUM_COL_PIC)));
        } catch (Exception e) {
            contact.setPicture(null);
        }
        contact.setFav(c.getInt(NUM_COL_FAV) > 0);

        return contact;
    }

    public ArrayList<Contact> getAllContacts(Context context) {
        Cursor c = db.query(TABLE_CONTACTS, new String[] {COL_ID, COL_NAME, COL_LAST_NAME, COL_PHONE, COL_EMAIL, COL_ADDRESS, COL_PIC, COL_FAV}
                , null, null, null, null, null);
        ArrayList<Contact> list = new ArrayList<>();

        if (c.getCount() <= 0)
            return list;

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                list.add(cursorToContact(c, context));
                c.moveToNext();
            }
        }
        c.close();
        return list;
    }
}
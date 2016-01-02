package DataBase;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;

import com.hangout.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;

/**
 * Created by pcv on 30/12/2015.
 */
public class Contact {
    private long id;
    private String lastName = "";
    private String name = "";
    private String phone = "";
    private String email = "";
    private String address = "";
    private boolean isFav = false;
    private Uri picture = null;
    private Context mContext;
    private static DownloadContacts download = null;
    private static HashMap<String, Pair<String, Uri>> _Contacts = new HashMap<>();

    public Contact(Context context) {
        this.mContext = context;
        if (download == null) {
            download = new DownloadContacts();
            download.execute();
        }
    }

    public Contact(String name, String phone, Context context) {
        this.mContext = context;
        if (download == null) {
            download = new DownloadContacts();
            download.execute();
        }
        this.name = name;
        this.phone = phone;
        if (name.equals(phone)) {
            if (_Contacts.containsKey(phone)) {
                this.name = _Contacts.get(phone).first;
                this.picture = _Contacts.get(phone).second;
            }
        }
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public Uri getPicture() { return picture; }
    public boolean getFav() { return isFav; }

    public void setId(long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
    public void setPicture(Uri picture) { this.picture = picture; }
    public void setFav(boolean fav) { this.isFav = fav; }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return "ID : "+ id + "\nName : " + name + "\nPhone Nb : " + phone;
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        Uri uri = null;
        try {
            uri = Uri.parse(path);
        } catch (Exception e) {
            Log.e("ParseUri", e.getMessage());
            e.printStackTrace();
        }
        return uri;
    }

    private Bitmap retrieveContactPhoto(long idtusais) {
        Bitmap photo = null;
        try {
            photo = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_profile_no_picture_small);
        } catch (Exception e) {
            Log.e("Retrieve ContactPic", e.getMessage());
            e.printStackTrace();
        }

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(mContext.getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(idtusais)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return photo;
    }

    class DownloadContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            setPhoneContacts();
            return null;
        }

        private void setPhoneContacts() {
            ContentResolver cr = mContext.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    long idtusais = Long.parseLong(id);
                    final String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (Integer.parseInt(cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            final String phoneNo = pCur
                                    .getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[ ,-]", "");
                            Bitmap bitmap = retrieveContactPhoto(idtusais);
                            Uri uri = getImageUri(mContext, bitmap);
                            Pair<String, Uri> bitmapHashMap = new Pair<>(name, uri);
                            _Contacts.put(phoneNo.toString(), bitmapHashMap);
                        }
                        pCur.close();
                    }
                }
            }
            cur.close();
        }
    }
}
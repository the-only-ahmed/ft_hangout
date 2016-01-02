package com.hangout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;

import DataBase.Contact;
import DataBase.ContactDB;

/**
 * Created by pcv on 31/12/2015.
 */
public class ProfileAdapter extends BaseAdapter {
    private ArrayList<Contact> contacts;
    private Context context;

    public ProfileAdapter(ArrayList<Contact> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return contacts.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //final View result;
        ViewHolderItem viewHolder;

        // inflate the layout
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.adapter_layout, parent, false);

        //if (convertView == null) {
            // well set up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.phone = (TextView) convertView.findViewById(R.id.phone);
            viewHolder.picture = (ImageView) convertView.findViewById(R.id.picture);
            viewHolder.favori = (ImageView) convertView.findViewById(R.id.favoris);
            // store the holder with the view.
            convertView.setTag(viewHolder);
        /*} else
            viewHolder = (ViewHolderItem) convertView.getTag();*/

        final Contact contact = (Contact) getItem(position);
        viewHolder.name.setText(contact.getName());
        viewHolder.phone.setText(contact.getPhone());

        viewHolder.favori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contact.setFav(!contact.getFav());
                ContactDB.getDataBase().updateContact(contact);
                notifyDataSetChanged();
            }
        });

        if (contact.getFav())
            viewHolder.favori.setImageResource(android.R.drawable.btn_star_big_on);
        else
            viewHolder.favori.setImageResource(android.R.drawable.btn_star_big_off);

        Uri uri = contact.getPicture();
        if (!(uri == null || uri.toString().isEmpty())) {
            try {
                Bitmap bmp = getBitmapFromUri(uri, context);
                viewHolder.picture.setImageBitmap(RoundedImageView.getCroppedBitmap(bmp, 120));
            } catch (Exception e) {
                Log.e("ProfileAdapter", e.getMessage());
                e.printStackTrace();
            }
        }

        return convertView;
    }

    public static Bitmap getBitmapFromUri(Uri uri, Context context) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = null;
        try {
            image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        } catch (Exception e) {
            Log.e("BitmapFromUri", e.getMessage());
            e.printStackTrace();
        }
        parcelFileDescriptor.close();
        return image;
    }

    class ViewHolderItem {
        TextView name, phone;
        ImageView picture, favori;
    }
}
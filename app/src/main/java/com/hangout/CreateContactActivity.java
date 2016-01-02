package com.hangout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import DataBase.Contact;
import DataBase.ContactDB;

public class CreateContactActivity extends AppCompatActivity implements View.OnClickListener {
    private Button save;
    private ImageView picture;
    private EditText name, lastName, phone, email, adress;

    private Uri myUri = null;
    private static final int GALLERY_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        picture = (ImageView) findViewById(R.id.picture);
        save = (Button) findViewById(R.id.save);

        name = (EditText) findViewById(R.id.name);
        lastName = (EditText) findViewById(R.id.lastName);
        phone = (EditText) findViewById(R.id.phone);
        email = (EditText) findViewById(R.id.email);
        adress = (EditText) findViewById(R.id.adress);

        save.setOnClickListener(this);
        picture.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(getResources().getColor(MainActivity.colId));
            getWindow().setStatusBarColor(getResources().getColor(MainActivity.colId));
            save.setBackgroundColor(getResources().getColor(MainActivity.colId));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                String _name = name.getText().toString();
                String _phone = phone.getText().toString();
                if (_name.isEmpty() || _phone.isEmpty()) {
                    Toast.makeText(CreateContactActivity.this, getResources().getString(R.string.emptyNP), Toast.LENGTH_SHORT).show();
                    return;
                }
                Contact contact = new Contact(_name, _phone, this);
                contact.setLastName(lastName.getText().toString());
                contact.setEmail(email.getText().toString());
                contact.setAddress(adress.getText().toString());
                contact.setPicture(this.myUri);
                Log.d("contact", this.myUri + "");
                ContactDB.getDataBase().insertContact(contact);
                startActivity(new Intent(CreateContactActivity.this, MainActivity.class));
                break;
            case R.id.picture:
                ChooseExisting(this);
                break;
        }
    }

    public static void ChooseExisting(Activity context) {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.addCategory(Intent.CATEGORY_OPENABLE);

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        context.startActivityForResult(chooserIntent, GALLERY_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                try {
                    if (requestCode == GALLERY_PICTURE) {
                        this.myUri = data.getData();
                        picture.setImageURI(this.myUri);
                    }
                } catch (Exception e) {
                    Log.e("onActivityResult", e.getMessage());
                    e.printStackTrace();
                }
            } else
                Log.e("popup", "data is null");
        }
    }
}
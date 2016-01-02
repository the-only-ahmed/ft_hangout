package activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hangout.R;

import DataBase.Contact;
import DataBase.ContactDB;

public class ContactPageActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView picture;
    private EditText name, lastName, phone, email, address;
    private Button save;

    private ImageView sms, call, edit, remove;
    private Contact contact;

    private Uri myUri = null;
    private final int GALLERY_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_page);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle b = getIntent().getExtras();
        long id = b.getLong("id");

        contact = ContactDB.getDataBase().getContactWithId(id, this);

        picture = (ImageView) findViewById(R.id.picture);
        save = (Button) findViewById(R.id.save);

        sms = (ImageView) findViewById(R.id.message);
        call = (ImageView) findViewById(R.id.call);
        edit = (ImageView) findViewById(R.id.edit);
        remove = (ImageView) findViewById(R.id.remove);

        name = (EditText) findViewById(R.id.name);
        lastName = (EditText) findViewById(R.id.lastName);
        phone = (EditText) findViewById(R.id.phone);
        email = (EditText) findViewById(R.id.email);
        address = (EditText) findViewById(R.id.adress);

        RelativeLayout stateLayout = (RelativeLayout) findViewById(R.id.states);

        name.setText(contact.getName());
        lastName.setText(contact.getLastName());
        phone.setText(contact.getPhone());
        email.setText(contact.getEmail());
        address.setText(contact.getAddress());

        Uri uri = contact.getPicture();
        if (!(uri == null || uri.toString().isEmpty()))
            picture.setImageURI(uri);

        name.setEnabled(false);
        lastName.setEnabled(false);
        phone.setEnabled(false);
        email.setEnabled(false);
        address.setEnabled(false);

        save.setOnClickListener(this);
        sms.setOnClickListener(this);
        call.setOnClickListener(this);
        edit.setOnClickListener(this);
        remove.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(getResources().getColor(MainActivity.colId));
            getWindow().setStatusBarColor(getResources().getColor(MainActivity.colId));
            save.setBackgroundColor(getResources().getColor(MainActivity.colId));
            stateLayout.setBackgroundColor(getResources().getColor(MainActivity.colId));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message:
                Intent smsIntent = new Intent(ContactPageActivity.this, MessagingActivity.class);
                Bundle b = new Bundle();
                b.putString("num", contact.getPhone());
                smsIntent.putExtras(b);
                startActivity(smsIntent);
                break;
            case R.id.call:
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact.getPhone()));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ContactPageActivity.this, "Call Permission Requered", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(callIntent);
                break;
            case R.id.edit:
                name.setEnabled(true);
                lastName.setEnabled(true);
                phone.setEnabled(true);
                email.setEnabled(true);
                address.setEnabled(true);
                picture.setOnClickListener(this);

                name.setHint(getResources().getString(R.string.name));
                lastName.setHint(getResources().getString(R.string.lastName));
                phone.setHint(getResources().getString(R.string.phone));
                email.setHint(getResources().getString(R.string.email));
                address.setHint(getResources().getString(R.string.adress));
                save.setVisibility(View.VISIBLE);
                break;
            case R.id.remove:
                ContactDB.getDataBase().removeContactWithID(contact.getId());
                Intent intent = new Intent(ContactPageActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.save:
                name.setHint("");
                lastName.setHint("");
                phone.setHint("");
                email.setHint("");
                address.setHint("");

                save.setVisibility(View.INVISIBLE);
                name.setEnabled(false);
                lastName.setEnabled(false);
                phone.setEnabled(false);
                email.setEnabled(false);
                address.setEnabled(false);
                picture.setOnClickListener(null);

                contact.setName(name.getText().toString());
                contact.setLastName(lastName.getText().toString());
                contact.setPhone(phone.getText().toString());
                contact.setEmail(email.getText().toString());
                contact.setAddress(address.getText().toString());
                if (myUri != null)
                    contact.setPicture(myUri);

                ContactDB.getDataBase().updateContact(contact);
                break;
            case R.id.picture:
                CreateContactActivity.ChooseExisting(this);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                try {
                    if (requestCode == GALLERY_PICTURE) {
                        myUri = data.getData();
                        picture.setImageURI(myUri);
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
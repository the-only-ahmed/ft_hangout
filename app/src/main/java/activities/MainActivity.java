package activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.hangout.ProfileAdapter;
import com.hangout.R;

import java.util.ArrayList;

import DataBase.Contact;
import DataBase.ContactDB;

public class MainActivity extends Activity implements View.OnClickListener {
    private ContactDB database;
    private ListView favList, phoneList;
    private FloatingActionButton fab;
    private ArrayList<Contact> _contacts;
    private Button color;
    private Toolbar toolbar;
    public static int colId = R.color.colorPrimary;
    private IntentFilter intentFilter;
    private TextView _Phone, _Favourites;
    private ProfileAdapter adapterFav;

    private ArrayList<Contact> keepFav() {
        ArrayList<Contact> keep = new ArrayList<>();
        for (Contact k : _contacts) {
            if (k.getFav())
                keep.add(k);
        }
        return keep;
    }

    private void initUI() {
        _contacts = database.getAllContacts(this);
        ProfileAdapter adapter = new ProfileAdapter(_contacts, this);
        adapterFav = new ProfileAdapter(keepFav(), this);

        phoneList.setAdapter(adapter);
        favList.setAdapter(adapterFav);

        fab.setOnClickListener(this);
        color.setOnClickListener(this);

        phoneList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ContactPageActivity.class);
                Contact contact = (Contact) parent.getItemAtPosition(position);
                Bundle b = new Bundle();
                b.putLong("id", contact.getId());
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        favList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ContactPageActivity.class);
                Contact contact = (Contact) parent.getItemAtPosition(position);
                Bundle b = new Bundle();
                b.putLong("id", contact.getId());
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(getResources().getColor(colId));
            getWindow().setStatusBarColor(getResources().getColor(colId));
        }

        database = new ContactDB(this);
        try {
            database.open();
        }catch(Exception sqle){
            throw sqle;
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        favList = (ListView) findViewById(R.id.FavList);
        phoneList = (ListView) findViewById(R.id.PhoneList);
        color = (Button) findViewById(R.id.color);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        _Phone = (TextView) findViewById(R.id.FriendsWall);
        _Favourites = (TextView) findViewById(R.id.PopularWall);

        //deleteDatabase("contact.db");
        PhoneSelected();

        _Phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneSelected();
            }
        });
        _Favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FavouriteSelected();
            }
        });
        initUI();
    }

    private void PhoneSelected() {
        _Phone.setSelected(true);
        _Favourites.setSelected(false);
        _Phone.setTextColor(Color.WHITE);
        _Favourites.setTextColor(Color.parseColor("#9A9A9A"));
        phoneList.setSelectionAfterHeaderView();
        phoneList.setVisibility(View.VISIBLE);
        favList.setVisibility(View.GONE);
    }

    private void FavouriteSelected() {
        adapterFav = new ProfileAdapter(keepFav(), this);
        favList.setAdapter(adapterFav);

        _Favourites.setSelected(true);
        _Phone.setSelected(false);
        _Favourites.setTextColor(Color.WHITE);
        _Phone.setTextColor(Color.parseColor("#9A9A9A"));
        favList.setSelectionAfterHeaderView();
        favList.setVisibility(View.VISIBLE);
        phoneList.setVisibility(View.GONE);
    }

    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //---display the SMS received in the TextView---
            String[] sms = intent.getExtras().getStringArray("sms");
            String[] sender = intent.getExtras().getStringArray("sender");
            Intent i = new Intent(MainActivity.this, MessagingActivity.class);
            i.putExtra("sender", sender);
            i.putExtra("sms", sms);
            startActivity(i);
        }
    };

    @Override
    protected void onPause() {
        //---unregister the receiver---
        try {
            unregisterReceiver(intentReceiver);
        } catch (Exception e) {
            Log.e("unregister", e.getMessage());
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (isFinishing()) {
            try {
                unregisterReceiver(intentReceiver);
            } catch (Exception e) {
                Log.e("unregister", e.getMessage());
                e.printStackTrace();
            }
        }
        super.onDestroy();
        if (isFinishing())
            database.close();
    }

    @Override
    protected void onResume() {
        registerReceiver(intentReceiver, intentFilter);
        super.onResume();
        initUI();
        /*_contacts = database.getAllContacts(this);
        adapter = new ProfileAdapter(_contacts, this);
        contactList.setAdapter(adapter);*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                startActivity(new Intent(MainActivity.this, CreateContactActivity.class));
                break;
            case R.id.color:
                showPopupMenu(v);
                break;
        }
    }

    private void showPopupMenu(View v){
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuRed:
                        colId = R.color.red;
                        break;
                    case R.id.menuGreen:
                        colId = R.color.green;
                        break;
                    case R.id.menuBlue:
                        colId = R.color.blue;
                        break;
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    getWindow().setNavigationBarColor(getResources().getColor(colId));
                    getWindow().setStatusBarColor(getResources().getColor(colId));
                    toolbar.setBackgroundColor(getResources().getColor(colId));
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(colId)));
                }
                return true;
            }
        });

        popupMenu.show();
    }
}
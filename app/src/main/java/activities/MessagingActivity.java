package activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.hangout.R;

import java.util.ArrayList;

import DataBase.Contact;
import DataBase.ContactDB;

public class MessagingActivity extends AppCompatActivity {
    private Button btnSendSMS;
    private EditText text;
    private ListView lst;
    private ArrayList<String> msg = new ArrayList<>();
    private String name, num;
    private ArrayAdapter<String> adapter;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(getResources().getColor(MainActivity.colId));
            getWindow().setStatusBarColor(getResources().getColor(MainActivity.colId));
        }

        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");

        Bundle b = getIntent().getExtras();
        String[] sms = b.getStringArray("sms");
        String[] sender = b.getStringArray("sender");

        if (sms != null && sender != null) {
            Contact contact = ContactDB.getDataBase().getContactWithNum(sender[0], this);
            if (contact == null) {
                contact = new Contact(sender[0], sender[0], this);
                long x = ContactDB.getDataBase().insertContact(contact);
                Log.d("contact", x + "");
            }
            num = contact.getPhone();
            name = contact.getName();
            int i = 0;
            for (String s : sender) {
                if (s.equals(num))
                    msg.add(name + " : " + sms[i]);
                else if (ContactDB.getDataBase().getContactWithNum(s, this) == null) {
                    contact = new Contact(s, s, this);
                    ContactDB.getDataBase().insertContact(contact);
                }
                i++;
            }
        } else {
            num = b.getString("num");
            name = b.getString("name");
        }

        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
        text = (EditText) findViewById(R.id.texting);
        lst = (ListView) findViewById(R.id.messages);

        adapter = new ArrayAdapter<>(MessagingActivity.this,
                android.R.layout.simple_list_item_1, msg);
        lst.setAdapter(adapter);

        btnSendSMS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String str = text.getText().toString();
                sendSMS(num, str);
                msg.add("YOU : " + str);
                adapter.notifyDataSetChanged();
                text.setText("");
            }
        });
    }

    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //---display the SMS received in the TextView---
            String[] sms = intent.getExtras().getStringArray("sms");
            String[] sender = intent.getExtras().getStringArray("sender");
            for (String s : sender) {
                if (sender.equals(num))
                    msg.add(name + " : " + sms);
                else if (ContactDB.getDataBase().getContactWithNum(s, context) == null) {
                    Contact contact = new Contact(s, s, context);
                    ContactDB.getDataBase().insertContact(contact);
                }
            }
            adapter.notifyDataSetChanged();
        }
    };

    //---sends an SMS message to another device---
    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    @Override
    protected void onResume() {
        //---register the receiver---
        registerReceiver(intentReceiver, intentFilter);
        super.onResume();
    }
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
        //---unregister the receiver---
        try {
            unregisterReceiver(intentReceiver);
        } catch (Exception e) {
            Log.e("unregister", e.getMessage());
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
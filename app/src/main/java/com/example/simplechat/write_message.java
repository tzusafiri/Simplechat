package com.example.simplechat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.PendingIntent.getActivity;

public class write_message extends AppCompatActivity {

    EditText wrt_sms;
    ImageButton send_btn;
    int MY_PERMISSION_REQUEST_SEND_SMS = 1;
    private final int PICK_CONTACT = 1;

    private ImageButton cont_btn;
    private String TAG = "Contacts";
    private static final int RESULT_PICK_CONTACT = 1;

    String SENT = "SMS_SENT";
    String DELIVERY = "SMS_DELIVERY";
    PendingIntent sentAPI, deliveredAPI;
    BroadcastReceiver smsSentReceiver, smsDeliveryReceiver;

    private ArrayList<Map<String, String>> mPeopleList;
    private SimpleAdapter mAdapter;
    private AutoCompleteTextView mTxtPhoneNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_message);
        wrt_sms = findViewById(R.id.write_sms);
        send_btn = findViewById(R.id.send_button);

        sentAPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        deliveredAPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERY), 0);

        cont_btn = findViewById(R.id.add_contact);
        cont_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callContact(v);
            }
        });

        mPeopleList = new ArrayList<Map<String, String>>();
        PopulatePeopleList();
        mTxtPhoneNo = (AutoCompleteTextView) findViewById(R.id.phonenumber);

        mAdapter = new SimpleAdapter(this, mPeopleList, R.layout.contact_view ,new String[] { "Name", "Phone" , "Type" }, new int[] { R.id.ccontName, R.id.ccontNo, R.id.ccontType });

        mTxtPhoneNo.setAdapter(mAdapter);

    }

    public void PopulatePeopleList() {

        mPeopleList.clear();

        Cursor people = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        while (people.moveToNext()) {
            String contactName = people.getString(people.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME));

            String contactId = people.getString(people.getColumnIndex(
                    ContactsContract.Contacts._ID));
            String hasPhone = people.getString(people.getColumnIndex(
                    ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if ((Integer.parseInt(hasPhone) > 0)) {

                // You know have the number so now query it like this
                Cursor phones = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                        null, null);
                while (phones.moveToNext()) {

                    //store numbers and display a dialog letting the user select which.
                    String phoneNumber = phones.getString(
                            phones.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));

                    String numberType = phones.getString(phones.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.TYPE));

                    Map<String, String> NamePhoneType = new HashMap<String, String>();

                    NamePhoneType.put("Name", contactName);
                    NamePhoneType.put("Phone", phoneNumber);

                    if (numberType.equals("0"))
                        NamePhoneType.put("Type", "Work");
                    else if (numberType.equals("1"))
                        NamePhoneType.put("Type", "Home");
                    else if (numberType.equals("2"))
                        NamePhoneType.put("Type", "Mobile");
                    else
                        NamePhoneType.put("Type", "Other");

                    //Then add this map to the list.
                    mPeopleList.add(NamePhoneType);
                }
                phones.close();
            }
        }
        people.close();
        startManagingCursor(people);
    }


    public void callContact(View view){
             Intent cont_intent = new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI);
             startActivityForResult(cont_intent, PICK_CONTACT);

    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == PICK_CONTACT){
            if (resultCode == AppCompatActivity.RESULT_OK){
                     Uri contactData = data.getData();
                     Cursor cursor = getContentResolver().query(contactData,null,null,null,null);

                     if (cursor.moveToFirst()){
                         String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                         Toast.makeText(this, "number picked"+name, Toast.LENGTH_LONG).show();
                     }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(write_message.this, "SMS sent", Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(write_message.this, "SMS failed", Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(write_message.this, "No services", Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(write_message.this, "null PDU", Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(write_message.this, "Radio off", Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        };

        smsDeliveryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(write_message.this, "SMS delivery", Toast.LENGTH_SHORT).show();
                        break;

                    case Activity.RESULT_CANCELED:
                        Toast.makeText(write_message.this, "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        registerReceiver(smsSentReceiver, new IntentFilter(SENT));
        registerReceiver(smsDeliveryReceiver, new IntentFilter(DELIVERY));
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(smsDeliveryReceiver);
        unregisterReceiver(smsSentReceiver);
    }

    public void sendbtn(View view) {
        String new_message = wrt_sms.getText().toString();
        String phone_nu = mTxtPhoneNo.getText().toString();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSION_REQUEST_SEND_SMS);
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone_nu, null, new_message, sentAPI, deliveredAPI);

        }
    }
}
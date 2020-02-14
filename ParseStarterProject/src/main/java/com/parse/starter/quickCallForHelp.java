package com.parse.starter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.sql.Types.NULL;

public class quickCallForHelp extends AppCompatActivity {


    private static final int PERMISSION_REQUEST_CODE = 1;

    Button callButton;
    int phones[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_call_for_help);

        if (ContextCompat.checkSelfPermission(quickCallForHelp.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            requestPermission();
        }else {


            final ListView listView = findViewById(R.id.listView);
            final ArrayList<String> usernames = new ArrayList<String>();
            final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, usernames);

            final ArrayList<Integer> policePhone = new ArrayList<>();


            ParseQuery<ParseObject> query = ParseQuery.getQuery("callerUsername");

            query.whereNotEqualTo("policeName", ParseUser.getCurrentUser().getUsername());
            query.addAscendingOrder("policeName");

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() > 0) {
                            for (ParseObject object : objects) {
                                usernames.add(object.getString("policeName")); //GETTING POLICE NAMES FROM THE POLICANAME COLUMN AND ADDING THEM IN THE ARRAYLIST USERNAME
                            }
                            listView.setAdapter(arrayAdapter);
                        }
                    } else {
                        e.printStackTrace();
                    }
                }
            }); // has to change

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() > 0) {
                            for (ParseObject object : objects) {
                                policePhone.add(object.getInt("phoneNumber"));// GETTING THE PHONE NUMBER FROM THE ROW PHONE NUMBER AND PUTTING IT IN POLICEPHONE ARRAYLIST
                            }
                            //IF I WANT TO ADD IN  A LISTVIEW //
                        }
                    } else {
                        e.printStackTrace();
                    }
                }
            }); // has to change

            int i = 0;
            Iterator itr = policePhone.iterator();
            while (itr.hasNext()) {
                phones[i] = (Integer) itr.next();
                i++;
            }//GETTING ALL THE PHONE NUMBERS FROM THE ARRAYLIST TO A ARRAY PHONES[]//
        }
    }


    public boolean checkPermission() {

        int CallPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);
        int ContactPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS);

        return CallPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ContactPermissionResult == PackageManager.PERMISSION_GRANTED;

    }                                                                // checking permission that will retuen a boolean value


    private void requestPermission() {

        ActivityCompat.requestPermissions(quickCallForHelp.this, new String[]
                {
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.CALL_PHONE
                }, PERMISSION_REQUEST_CODE);

    }                                                          //requesting permission




    public void call(View view) {
        final int phoneNumber = phones[1];

        //if it doesn't work remember to convert to string
        if (phoneNumber != NULL) {
            String dial = "tel:" + phoneNumber;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                requestPermission();
                return;
            }
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }else {
            Toast.makeText(quickCallForHelp.this, "Please enter a valid telephone number", Toast.LENGTH_SHORT).show();
        }

    }                                                                   //call button 
}

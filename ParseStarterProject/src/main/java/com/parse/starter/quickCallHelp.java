package com.parse.starter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class quickCallHelp extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    LocationManager locationManager;
    LocationListener locationListener;
    TextView textView;

    String admin_area,address2,locality,street_addess;

    final ArrayList phone = new ArrayList();
    final ArrayList states = new ArrayList();
    final ArrayList districts = new ArrayList();
    final ArrayList cities = new ArrayList();
    final ArrayList pin = new ArrayList();

    String phones[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_call_help);


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE); // get the location
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocation(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

        }else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener); // first i have to get permission from the .xml file
            Location lastKnowLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastKnowLocation != null){
                updateLocation(lastKnowLocation);
            }
        }


        if (ContextCompat.checkSelfPermission(quickCallHelp.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            requestPermission();
        }else {
            final ListView listView = findViewById(R.id.listView);
            final ArrayList<String> usernames = new ArrayList<>();
            final ArrayAdapter arrayAdapter = new ArrayAdapter(quickCallHelp.this, android.R.layout.simple_list_item_1, usernames);






            ParseQuery<ParseObject> query = ParseQuery.getQuery("HelpInfo");

            query.whereEqualTo("pin",street_addess);//PIN NUMBER

            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> police, ParseException e) {
                    if (e == null) {

                        Log.d("police", "Retrieved " + police.size() + " scores");

                        for(ParseObject polices : police){
                            Log.d("police", "Retrieved " + polices.getString("policeName") );
                            usernames.add(polices.getString("policeName"));
                        }
                        for(ParseObject phoneNumbers : police){
                            phone.add(phoneNumbers.getString("phone"));
                            Log.d("police", "Retrieved " + phone );
                        }
                        for(ParseObject state : police){

                            states.add(state.getString("state"));

                        }
                        for (ParseObject district : police){
                            districts.add(district.getString("district"));
                        }
                        for (ParseObject city : police){
                            cities.add(city.getString("city"));
                        }
                        for (ParseObject pins : police){
                            pin.add(pins.getString("pin"));
                        }

                        listView.setAdapter(arrayAdapter);
                    } else {
                        Log.d("score", "Error: " + e.getMessage());
                    }
                }
            }); // has to change


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (!phone.isEmpty()) {
                        String dial = "tel:" + phone.get(0);
                        if (ActivityCompat.checkSelfPermission(quickCallHelp.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
                        Toast.makeText(quickCallHelp.this, "Please enter a valid telephone number", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startListening();
        }
    }

    public void startListening (){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener); // first i have to get permission from the .xml file

    }
    public void updateLocation(Location location){


        textView = findViewById(R.id.address);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());  // getting the address information where i am right now by default // nation
        try {
            List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

            if(listAddresses != null && listAddresses.size() >0){

                String address = "Address : \n";
                if(listAddresses.get(0).getThoroughfare() != null){
                    address += listAddresses.get(0).getThoroughfare() + " ";
                    admin_area= listAddresses.get(0).getThoroughfare();
                } // Road name
                if(listAddresses.get(0).getLocality() != null){
                    address += listAddresses.get(0).getLocality() + " ";
                    locality = listAddresses.get(0).getLocality();
                }// Bongal Gaon
                if(listAddresses.get(0).getPostalCode() != null){
                    address += listAddresses.get(0).getPostalCode() + " ";
                    street_addess = listAddresses.get(0).getPostalCode();

                    Log.i("ghhghghghg",street_addess);
                }// 785614
                if(listAddresses.get(0).getAdminArea() != null){
                    address += listAddresses.get(0).getAdminArea() + " ";
                    address2 = listAddresses.get(0).getAdminArea();
                }//Assam

                textView.setText(address);
                Log.i("here it i s :::",address);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("here it i s :::","Error BC !!!!!!");
        }

    }


    private void requestPermission() {

        ActivityCompat.requestPermissions(quickCallHelp.this, new String[]
                {

                        Manifest.permission.CALL_PHONE
                }, PERMISSION_REQUEST_CODE);

    }                                                          //requesting permission

    public void call(View view) {

        //if it doesn't work remember to convert to string
        if (!phone.isEmpty()) {
            String dial = "tel:" + phone.get(0);
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
            Toast.makeText(quickCallHelp.this, "Please enter a valid telephone number", Toast.LENGTH_SHORT).show();
        }

    }


}


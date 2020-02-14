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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class sendYourLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int PERMISSION_REQUEST_CODE = 1;


    LocationManager locationManager;

    LocationListener locationListener;


    boolean flag = false;



    protected FusedLocationProviderClient fusedLocationClient;
    float a;

    Button callHelpButton;

    Boolean requestActive = false;

    Handler handler = new Handler();

    TextView infoTextView;

    Boolean driverActive = true;

    Location lastknown;
    protected Button button;

    final ArrayList phone = new ArrayList();

    public void checkForUpdates() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());


        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null && objects.size() > 0) {

                    driverActive = true;

                    final ParseQuery<ParseUser> query = ParseUser.getQuery();

                    query.whereEqualTo("username", objects.get(0).getString("HelperName"));

                    //button.setVisibility(View.VISIBLE);
                    //phone.add(objects.get(0).getString("phone"));
                    query.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {

                            if (e == null && objects.size() > 0) {



                                ParseGeoPoint driverLocation = objects.get(0).getParseGeoPoint("location");

                                if (Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(sendYourLocation.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                    if (lastKnownLocation != null) {

                                        ParseGeoPoint userLocation = new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

                                        Double distanceInMiles = driverLocation.distanceInMilesTo(userLocation);

                                        if (distanceInMiles < 0.01) {


                                            infoTextView.setText("Dont worry !! Your Help is here ");

                                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
                                            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

                                            query.findInBackground(new FindCallback<ParseObject>() {
                                                @Override
                                                public void done(List<ParseObject> objects, ParseException e) {

                                                    if (e == null) {

                                                        for (ParseObject object : objects) {

                                                            object.deleteInBackground();
                                                          //  button.setVisibility(View.INVISIBLE);
                                                            //phone.clear();

                                                        }


                                                    }

                                                }
                                            });

                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {

                                                    infoTextView.setText("");
                                                    callHelpButton.setVisibility(View.VISIBLE);
                                                    callHelpButton.setText("GET HELP");
                                                    requestActive = false;
                                                    driverActive = false;

                                                }
                                            }, 5000);

                                        } else {

                                            Double distanceOneDP = (double) Math.round(distanceInMiles * 10) / 10;

                                            infoTextView.setText("Your help is " + distanceOneDP.toString() + " KM away!");

                                            LatLng driverLocationLatLng = new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude());

                                            LatLng requestLocationLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());

                                            ArrayList<Marker> markers = new ArrayList<>();

                                            mMap.clear();

                                            markers.add(mMap.addMarker(new MarkerOptions().position(driverLocationLatLng).title("Driver Location")));
                                            markers.add(mMap.addMarker(new MarkerOptions().position(requestLocationLatLng).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));

                                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                            for (Marker marker : markers) {
                                                builder.include(marker.getPosition());
                                            }
                                            LatLngBounds bounds = builder.build();


                                            int padding = 60; // offset from edges of the map in pixels
                                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                                            mMap.animateCamera(cu);


                                            callHelpButton.setVisibility(View.INVISIBLE);

                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {

                                                    checkForUpdates();

                                                }
                                            }, 2000);

                                        }

                                    }

                                }

                            }

                        }
                    });


                }


            }
        });


    }
    private void requestPermission() {

        ActivityCompat.requestPermissions(sendYourLocation.this, new String[]
                {

                        Manifest.permission.CALL_PHONE
                }, PERMISSION_REQUEST_CODE);

    }
    public void callHelper(View view){


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
            Toast.makeText(sendYourLocation.this, "No number available", Toast.LENGTH_SHORT).show();
        }
    }

    public void logout(View view) {

        ParseUser.logOut();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);


    }

    public void setCallHelpButton(final View view) {

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location locationLast) {
                        // Got last known location. In some rare situations this can be null.
                        if (locationLast != null) {
                            // Logic to handle location object
                            Log.i("here it is : ",locationLast.toString());
                            lastknown = locationLast;

                        }
                    }
                });

        Log.i("Info", "Call Uber");

        if (requestActive) {

            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Request");

            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e == null) {

                        if (objects.size() > 0) {

                            for (ParseObject object : objects) {

                                object.deleteInBackground();

                            }

                            requestActive = false;
                            callHelpButton.setText("GET HELP QUICKLY");

                        }

                    }

                }
            });


        } else {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


                if (lastknown != null) {

                    final ParseObject request = new ParseObject("Request");

                    request.put("username", ParseUser.getCurrentUser().getUsername());



                    ParseGeoPoint parseGeoPoint = new ParseGeoPoint(lastknown.getLatitude(), lastknown.getLongitude());

                    request.put("location", parseGeoPoint);

                    request.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            if (e == null) {

                                callHelpButton.setText("Cancel Help Request");
                                requestActive = true;

                                checkForUpdates();

                               // request.put("phone1_emg",ParseUser.getCurrentUser().getString("phone1")); // from user phone1 to request phone DB
                                //phone.add(request.getString("HelperName"));

                            }

                        }
                    });

                } else {

                    Toast.makeText(this, "Could not find location. Please try again later.", Toast.LENGTH_SHORT).show();

                }

            }

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }

    }

    public void updateMap(Location location) {

            Log.i("hihihi","knn");
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

            mMap.clear();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
            mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_your_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        button = findViewById(R.id.callHelper);
        callHelpButton = (Button) findViewById(R.id.callUberButton);

        infoTextView = (TextView) findViewById(R.id.infoTextView);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Request");

        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null) {

                    if (objects.size() > 0) {

                        requestActive = true;
                        callHelpButton.setText("Cancel Request!!");

                        checkForUpdates();

                    }

                }

            }
        });

    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mMap.clear();
                Log.i("LOCATION :", location.toString());
                LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());





                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try{
                    List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                    if(listAddresses!= null && listAddresses.size() >0){


                        String address = "You are at: ";
                        String address2= "";

                        if(listAddresses.get(0).getSubLocality() != null)
                        {
                            address += listAddresses.get(0).getSubLocality() + ", ";
                        }
                        if(listAddresses.get(0).getLocality() != null)
                        {
                            address += listAddresses.get(0).getLocality();
                        }
                        if(listAddresses.get(0).getAdminArea() != null)
                        {
                            address2 += listAddresses.get(0).getAdminArea() + ", ";
                        }
                        if(listAddresses.get(0).getCountryName() != null)
                        {
                            address2 += listAddresses.get(0).getCountryName();
                        }
                        Log.i("HERE: ",address);


                        mMap.addMarker(new MarkerOptions().position(newLocation).title(address).snippet(address2).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        if(flag == true)
                        {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation,15));
                        }
                        else
                        {


                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation,17));
                            flag = true;
                        }




                    }
                    else
                    {
                        mMap.addMarker(new MarkerOptions().position(newLocation).title("You are here! No Address found").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        if(flag == true)
                        {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation,17));
                        }
                        else
                        {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation,17));
                            flag = true;
                        }
                    }
                }catch (Exception e)
                {
                    mMap.addMarker(new MarkerOptions().position(newLocation).title("You are here! No Address found").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    if(flag == true)
                    {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation,17));
                    }
                    else
                    {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation,17));
                        flag = true;
                    }
                    e.printStackTrace();
                }


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            // Logic to handle location object
                            if (location != null) {

                                if (mMap != null) {
                                    mMap.clear();
                                }
                                LatLng newL = new LatLng(location.getLatitude(), location.getLongitude());

                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                try {
                                    List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                                    if (listAddresses != null && listAddresses.size() > 0) {
                                        String address = "You are at: ";
                                        String address2 = "";

                                        if (listAddresses.get(0).getSubLocality() != null) {
                                            address += listAddresses.get(0).getSubLocality() + ", ";
                                        }
                                        if (listAddresses.get(0).getLocality() != null) {
                                            address += listAddresses.get(0).getLocality();
                                        }
                                        if (listAddresses.get(0).getAdminArea() != null) {
                                            address2 += listAddresses.get(0).getAdminArea() + ", ";
                                        }
                                        if (listAddresses.get(0).getCountryName() != null) {
                                            address2 += listAddresses.get(0).getCountryName();
                                        }
                                        Log.i("HERE: ", address);
                                        flag = true;

                                        if (mMap != null) {
                                            LatLng markerLoc = new LatLng(location.getLatitude(), location.getLongitude());
                                            final CameraPosition cameraPosition = new CameraPosition.Builder()
                                                    .target(markerLoc)      // Sets the center of the map to Mountain View
                                                    .zoom(13)                   // Sets the zoom
                                                    .bearing(90)                // Sets the orientation of the camera to east
                                                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                                                    .build();                   //
                                            mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Marker"));
                                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                                                @Override
                                                public boolean onMyLocationButtonClick() {
                                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                                    return true;
                                                }
                                            });
                                        }
                                    } else {
                                        flag = true;
                                        if (mMap != null) {
                                            mMap.addMarker(new MarkerOptions().position(newL).title("You are here! No Address found").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newL, 17));
                                        }

                                    }

                                } catch (Exception e) {
                                    flag = true;
                                    if (mMap != null) {
                                        mMap.addMarker(new MarkerOptions().position(newL).title("You are here! No Address found").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newL, 17));
                                    }
                                    e.printStackTrace();
                                }

                             }
                            else
                            {
                                // Got last known location. In some rare situations this can be null.
                            }
                        }
                    });



        }


    }
}

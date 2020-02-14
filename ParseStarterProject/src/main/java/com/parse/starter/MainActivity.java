/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {


  private static final int PERMISSION_REQUEST_CODE = 1;

  TextView textView;


  private void requestPermission() {

    ActivityCompat.requestPermissions(MainActivity.this, new String[]
            {

                    Manifest.permission.CALL_PHONE
            }, PERMISSION_REQUEST_CODE);

  }

  @Override
  public void onBackPressed() {
    new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Exit")
            .setMessage("Are you sure you want to Exit")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
            {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                ParseUser.logOutInBackground();
                finish();
              }

            })
            .setNegativeButton("No", null)
            .show();
  }



  public void regestration(View view){
    Intent intent =  new Intent(getApplicationContext(),login_regestration.class);
    startActivity(intent);
  }

  public void adminLogin(View view){
    Intent intent = new Intent((getApplicationContext()),police_login.class);
    startActivity(intent);
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    textView = findViewById(R.id.textView);
    textView.setText("Wrote water woman of heart it total other. By in entirely securing suitable graceful at families improved. Zealously few furniture repulsive was agreeable consisted difficult. Collected breakfast estimable questions in to favourite it. Known he place worth words it as to. Spoke now noise off smart her ready. \n" +
            "\n" +
            "Now indulgence dissimilar for his thoroughly has terminated. Agreement offending commanded my an. Change wholly say why eldest period. Are projection put celebrated particular unreserved joy unsatiable its. In then dare good am rose bred or. On am in nearer square wanted. \n" +
            "\n" +
            "Give lady of they such they sure it. Me contained explained my education. Vulgar as hearts by garret. Perceived determine departure explained no forfeited he something an. Contrasted dissimilar get joy you instrument out reasonably. Again keeps at no meant stuff. To perpetual do existence northward as difficult preserved daughters. Continued at up to zealously necessary breakfast. Surrounded sir motionless she end literature. Gay direction neglected but supported yet her.");
    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

      ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);


    } else {

      requestPermission();

    }

    if ( ParseUser.getCurrentUser().getUsername() == null) {
      //direct login

      Intent intent = new Intent(getApplicationContext(), userFeed.class);
      startActivity(intent);
    }
    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

}
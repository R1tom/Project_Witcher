package com.parse.starter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.ParseUser;

public class userFeed extends AppCompatActivity {

    Button location_police, quick_call;

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Logout")
                .setMessage("Are you sure you want to Logout")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ParseUser.logOut();
                        finish();


                    }

                })
                .setNegativeButton("No", null)
                .show();
    }


    public void setLocation_police(View view){

        Intent intent =  new Intent(getApplicationContext(), sendYourLocation.class);
        startActivity(intent);
    }

    public void setQuick_call(View view){
        Intent intent =  new Intent(getApplicationContext(),quickCallHelp.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);

        Toast.makeText(userFeed.this,"WELCOME "+ ParseUser.getCurrentUser().getUsername() ,Toast.LENGTH_SHORT).show();

        setTitle("User-Feed");


        quick_call = findViewById(R.id.quick_call);
    }
}

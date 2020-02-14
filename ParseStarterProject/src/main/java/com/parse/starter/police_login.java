package com.parse.starter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class police_login extends AppCompatActivity {

    EditText  password,unique;

    public void login(View view){
        if (unique.getText().toString().matches("") || password.getText().toString().matches("") ) {
            Toast.makeText(this, "A username and a password are required.",Toast.LENGTH_SHORT).show();

        }else{

            ParseUser.logInInBackground(unique.getText().toString(), password.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        Log.i("Login","ok!");
                        //login success
                        Intent intent =  new Intent(getApplicationContext(),ViewRequestsActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(police_login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_login);


        password = findViewById(R.id.password_police);
        unique = findViewById(R.id.unique_police);
    }
}

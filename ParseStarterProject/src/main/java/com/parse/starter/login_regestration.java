package com.parse.starter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class login_regestration extends AppCompatActivity {

    EditText userName,password,rePassword,phone1,phone2,phone3;
    Button register;



    public void register(View view){


            Toast.makeText(this, "ok ", Toast.LENGTH_SHORT).show();
            if (userName.getText().toString().matches("") || password.getText().toString().matches("")) {
                Toast.makeText(this, "A username and a password are required.", Toast.LENGTH_SHORT).show();
            }else if(phone1.getText().toString().matches("") || phone2.getText().toString().matches("") || phone3.getText().toString().matches("")){

                Toast.makeText(this, "You have to insert 3 mobile numbers ", Toast.LENGTH_SHORT).show();
            }
            else {
                ParseUser user = new ParseUser();
                user.setUsername(userName.getText().toString());
                user.setPassword(password.getText().toString());
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i("Registration", "Success");
                            intoTheUserDashboard();

                        } else {
                            Log.i("Error","EEEEEEEEEEEEEEE");
                            Toast.makeText(login_regestration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_regestration);
        userName = findViewById(R.id.username);
        password = findViewById(R.id.pass);
        rePassword = findViewById(R.id.repass);

        phone1= findViewById(R.id.phone1);
        phone2 = findViewById(R.id.phone2);
        phone3 = findViewById(R.id.phone3);

        register = findViewById(R.id.button);
    }

    public void intoTheUserDashboard(){
        Intent intent =  new Intent(getApplicationContext(),userFeed.class);
        startActivity(intent);
    }
}

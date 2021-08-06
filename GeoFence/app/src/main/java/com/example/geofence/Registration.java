package com.example.geofence;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Registration extends AppCompatActivity {
    EditText email;
    EditText password;
    EditText address;
    EditText mobile;
    EditText name;
    TextView submit,login;
    String emailid;
    String pass,mobileNum,addr,fname;
    SharedPreferences sharedpreferences ;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        name=(EditText)findViewById(R.id.name);
        address=(EditText)findViewById(R.id.address);
        mobile=(EditText)findViewById(R.id.mobile);
        sharedpreferences= getSharedPreferences("mydata", Context.MODE_PRIVATE);
        getSupportActionBar().hide();
        submit=(TextView) findViewById(R.id.submit);
        login=(TextView) findViewById(R.id.login);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailid=email.getText().toString();
                pass=password.getText().toString();
                mobileNum=mobile.getText().toString();
                addr=address.getText().toString();
                fname=name.getText().toString();
                if(!emailid.isEmpty() || !pass.isEmpty()||!mobileNum.isEmpty() || !addr.isEmpty()||!fname.isEmpty()) {
                    register reg=new register();
                    reg.execute();
                }
                else{
                    email.setError("Please Enter Email Id");
                    password.setError("Please Enter Password");

                }

            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(Registration.this, Login.class);
                startActivity(in);
            }
        });
    }

    class register extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {

            try {
                //Initializing the URL
                URL url = new URL("http://18.221.28.70:3000/signup");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("charset", "utf-8");
                connection.setUseCaches (false);
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", emailid);
                jsonParam.put("password1", pass);
                jsonParam.put("address", addr);
                jsonParam.put("mobile", mobileNum);
                jsonParam.put("name", fname);
                wr.writeBytes(jsonParam.toString());
                wr.flush();
                wr.close();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                System.out.println(" bufferedreader response :" + bufferedReader);

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);


                }
                bufferedReader.close();
                Log.e("res", stringBuilder.toString());
                return stringBuilder.toString();
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }



        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("Output--1",""+s);
            if(s.contentEquals("true")){
                editor = sharedpreferences.edit();
                Toast.makeText(Registration.this, "Login Success", Toast.LENGTH_SHORT).show();
                Intent in=new Intent(Registration.this, MainActivity.class);
                editor.putString("email", emailid);
                editor.commit();
                editor.apply();
                startActivity(in);
                finish();
            }
            else {
                Toast.makeText(Registration.this, "Registration Failed, Email already exists", Toast.LENGTH_SHORT).show();

            }

        }
    }
}

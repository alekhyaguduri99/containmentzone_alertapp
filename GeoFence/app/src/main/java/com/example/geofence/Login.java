package com.example.geofence;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

public class Login extends AppCompatActivity {
    EditText email;
    EditText password;
    TextView submit,register;
    String emailid;
    String pass;
    SharedPreferences sharedpreferences ;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        sharedpreferences= getSharedPreferences("mydata", Context.MODE_PRIVATE);
        getSupportActionBar().hide();
        submit=(TextView) findViewById(R.id.submit);
        register=(TextView) findViewById(R.id.register);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 emailid=email.getText().toString();
                 pass=password.getText().toString();
                if(!emailid.isEmpty() || !pass.isEmpty()) {
                    sendLogin login=new sendLogin();
                    login.execute();
                }
                else{
                    email.setError("Please Enter Email Id");
                    password.setError("Please Enter Password");

                }

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(Login.this, Registration.class);
                startActivity(in);
            }
        });
    }


    class sendLogin extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {

            try {
                //Initializing the URL
                URL url = new URL("http://18.221.28.70:3000/signin");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("charset", "utf-8");
                connection.setUseCaches (false);
                //Sending the Data to the server
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", emailid);
                jsonParam.put("password1", pass);
                wr.writeBytes(jsonParam.toString());
                wr.flush();
                wr.close();
                //Receving the data from the server
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
            if(s.contentEquals("valid")){
                editor = sharedpreferences.edit();
                Toast.makeText(Login.this, "Login Success", Toast.LENGTH_SHORT).show();
                Intent in=new Intent(Login.this, MainActivity.class);
                editor.putString("email", emailid);
                editor.commit();
                editor.apply();
                startActivity(in);
                finish();
            }
            else{
                Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

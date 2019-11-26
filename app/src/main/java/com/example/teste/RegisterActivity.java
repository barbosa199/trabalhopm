package com.example.teste;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.teste.db.DB;
import com.example.teste.encriptar.Seguranca;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    Intent next_activity;
    Cursor c;
    DB mDbHelper;
    SQLiteDatabase db;
    Seguranca s = new Seguranca();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registar);
        mDbHelper = new DB(RegisterActivity.this);
        db = mDbHelper.getReadableDatabase();
        final ContentValues cv = new ContentValues();
        next_activity=new Intent(this, MainActivity.class);
        final EditText name_field= findViewById(R.id.Name);
        final EditText password_field= findViewById(R.id.Password);
        final EditText confirm_password_field= findViewById(R.id.confirm_password);
        Button registerbutton= findViewById(R.id.register);
        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user=name_field.getText().toString();
                final String pass1=password_field.getText().toString();

                if (user.length() < 6 && pass1.length()<6) {
                    Toast.makeText(RegisterActivity.this,R.string.carateres, Toast.LENGTH_SHORT).show();
                }
                else {
                    String url = "http://unhelmeted-mint.000webhostapp.com/myslim/api/utilizador/" + user;

                    // Formulate the request and handle the response.
                    JsonObjectRequest jsObjRequest = new JsonObjectRequest
                            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.NomeReg), Toast.LENGTH_SHORT).show();


                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    String pass2 = confirm_password_field.getText().toString();
                                    String password_enc = null;
                                    if (pass1.equals(pass2)) {
                                        //Encripta a password
                                        try {
                                            password_enc = s.encrypt(pass1);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        //funcao para fazer post
                                        postUsern(user, password_enc);
                                        startActivity(next_activity);

                                    } else {
                                        Toast.makeText(RegisterActivity.this, getResources().getString(R.string.PI), Toast.LENGTH_LONG).show();
                                    }
                                    //Log.d("Erro", error.toString());
                                }
                            });
                    MySingleton.getInstance(RegisterActivity.this).addToRequestQueue(jsObjRequest);


                }


            }

            public void postUsern(String name, String password_enc){
                String url = "https://unhelmeted-mint.000webhostapp.com/myslim/api/utilizador";

                Map<String, String> jsonParams = new HashMap<String, String>();

                jsonParams.put("user", name);
                jsonParams.put("pwd", password_enc);

                // Formulate the request and handle the response.
                JsonObjectRequest postRequest = new JsonObjectRequest
                        (Request.Method.POST, url, new JSONObject(jsonParams), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try{
                                    if (response.getBoolean("status")) {
                                        Toast.makeText(RegisterActivity.this, response.getString("MSG"), Toast.LENGTH_SHORT).show();
                                    } else{
                                        Toast.makeText(RegisterActivity.this, response.getString("MSG"), Toast.LENGTH_SHORT).show();
                                    }

                                }
                                catch(JSONException ex){
                                    Toast.makeText(RegisterActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Log.d("Erro", error.toString());
                            }
                        }) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("User-agent", System.getProperty("http.agent"));
                        return headers;
                    }
                };
                MySingleton.getInstance(RegisterActivity.this).addToRequestQueue(postRequest);
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (c!=null) {
            c.close();
        }
        if (db.isOpen()) {
            db.close();
            db = null;
        }
    }
}
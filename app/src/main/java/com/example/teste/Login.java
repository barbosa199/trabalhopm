package com.example.teste;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.teste.db.DB;
import com.example.teste.encriptar.Seguranca;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    DB mDbHelper;
    SQLiteDatabase db;
    Cursor c;
    Seguranca s = new Seguranca();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mDbHelper = new DB(Login.this);
        db = mDbHelper.getReadableDatabase();
        sharedPreferences=getSharedPreferences("USER_CREDENTIALS",MODE_PRIVATE);
        final Boolean isloggedin=sharedPreferences.getBoolean("ISLOGGEDIN",false);
        if(isloggedin)
        {
            Intent main = new Intent(Login.this, MainActivity.class);
            startActivity(main);
        }

        final EditText name_field= findViewById(R.id.login_user);
        final EditText password_field= findViewById(R.id.login_password);

        Button login= findViewById(R.id.login_button);
        Button register= findViewById(R.id.register_button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name=name_field.getText().toString();
                final String password=password_field.getText().toString();
                String encrip = null;
                //encripta a password para posteriormente comparar com a password na base de dados
                try {
                    encrip = s.encrypt(password);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String url="http://unhelmeted-mint.000webhostapp.com/myslim/api/utilizador/" + name +"&" + password;
                // Formulate the request and handle the response.
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try{
                                    sharedPreferences = getSharedPreferences("USER_CREDENTIALS", MODE_PRIVATE);
                                    editor = sharedPreferences.edit();
                                    editor.putInt("IDUSER", response.getInt("id"));
                                    editor.putString("NAME", name);
                                    editor.putString("PASSWORD", password);
                                    editor.putBoolean("ISLOGGEDIN", true);
                                    editor.commit();
                                    Intent main = new Intent(Login.this, MainActivity.class);
                                    startActivity(main);

                                }catch(JSONException ex){ }
                                //Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                //Log.d("Erro", error.toString());
                            }
                        });
                MySingleton.getInstance(Login.this).addToRequestQueue(jsObjRequest);

            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent register=new Intent(Login.this,RegisterActivity.class);
                startActivity(register);
                finish();
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

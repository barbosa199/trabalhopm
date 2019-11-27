package com.example.teste;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.teste.MySingleton;
import com.example.teste.R;
import com.example.teste.db.Contrato;
import com.example.teste.db.DB;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Adicionar extends AppCompatActivity {

    EditText et, et2, et3, et4, et5, et7;
    Spinner gendrop, localidadedrop;
    String genero, localidade;
    int localidade_id;
    SharedPreferences sharedPreferences;

    int id_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adicionar);

        et = (EditText) findViewById(R.id.nome);
        et2 = (EditText) findViewById(R.id.numero);
        et3 = (EditText) findViewById(R.id.idade);
        et4 = (EditText) findViewById(R.id.pais);
        et5 = (EditText) findViewById(R.id.codpostal);
        et7 = (EditText) findViewById(R.id.email);

        //get the spinner from the xml.
        gendrop = findViewById(R.id.genero);
        String[] items = new String[]{getResources().getString(R.string.genero), getResources().getString(R.string.M), getResources().getString(R.string.FM)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        gendrop.setAdapter(adapter);

        localidadedrop = findViewById(R.id.localidade);
        String[] items2 = new String[]{getResources().getString(R.string.Localidade), getResources().getString(R.string.Viana), getResources().getString(R.string.Braga)};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        localidadedrop.setAdapter(adapter2);

        sharedPreferences = getSharedPreferences("USER_CREDENTIALS", MODE_PRIVATE);
        id_user = sharedPreferences.getInt("IDUSER", -1);

        gendrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (parent.getItemAtPosition(pos).equals(getResources().getString(R.string.genero))) {
                    genero = "";
                } else {
                    genero = (String) parent.getItemAtPosition(pos);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        localidadedrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                localidade = (String) parent.getItemAtPosition(pos);
                if (localidade == getResources().getString(R.string.Viana)) {
                    localidade_id = 1;
                } else if (localidade == getResources().getString(R.string.Braga)) {
                    localidade_id = 2;
                }

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public void novoContacto() {

        String url = "http://unhelmeted-mint.000webhostapp.com/myslim/api/contacto";

        Map<String, String> jsonParams = new HashMap<String, String>();

        jsonParams.put("nome", et.getText().toString());
        jsonParams.put("numero", et2.getText().toString());
        jsonParams.put("idade", et3.getText().toString());

        jsonParams.put("pais", et4.getText().toString());
        jsonParams.put("codigopostal", et5.getText().toString());
        jsonParams.put("email", et7.getText().toString());
        jsonParams.put("genero", genero);
        jsonParams.put("localidade_id", localidade_id + "");
        jsonParams.put("user_id", id_user + "");

        // Formulate the request and handle the response.
        JsonObjectRequest postRequest = new JsonObjectRequest
                (Request.Method.POST, url, new JSONObject(jsonParams), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {
                                Toast.makeText(Adicionar.this, response.getString("MSG"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Adicionar.this, response.getString("MSG"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException ex) {
                            Toast.makeText(Adicionar.this, ex.toString(), Toast.LENGTH_SHORT).show();
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
        MySingleton.getInstance(Adicionar.this).addToRequestQueue(postRequest);

    }


    public void bt1(View view){

        if( et.getText().toString().equals("") || et2.getText().toString().equals(""))
        {
            Toast.makeText(this, getResources().getString(R.string.Campos), Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent();
            novoContacto();
            setResult(RESULT_OK, intent);
            Toast.makeText(Adicionar.this, (R.string.criado), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
package com.example.teste;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.teste.db.Contrato;
import com.example.teste.db.DB;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Ver extends AppCompatActivity implements Serializable {

    SQLiteDatabase db;
    DB mDbHelper;
    Cursor cursor;
    int id_user;
    TextView caixa1,caixa2,caixa3,caixa4,caixa5,caixa6,caixa7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver);
        caixa1   =  findViewById(R.id.nome1);
        caixa2   = findViewById(R.id.numero1);
        caixa3   = findViewById(R.id.idade1);
        caixa4   = findViewById(R.id.pais1);
        caixa5   = findViewById(R.id.codpostal1);
        caixa6   = findViewById(R.id.email1);
        caixa7   = findViewById(R.id.genero1);
        id_user = getIntent().getExtras().getInt("ver");
        String url = "https://unhelmeted-mint.000webhostapp.com/myslim/api/contactov/1" ;
        // Formulate the request and handle the response.
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject job = response.getJSONObject("linha");
                            caixa1.setText(job.getString("nome"));
                            caixa2.setText(job.getString("numero"));
                            caixa3.setText(job.getString("idade"));
                            caixa4.setText(job.getString("pais"));
                            caixa5.setText(job.getString("codigopostal"));
                            caixa6.setText(job.getString("email"));
                            caixa7.setText(job.getString("genero"));
                            //joinIdPais(id);
                        } catch (JSONException ex) {
                        }//Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Ver.this, error.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("Erro", error.toString());
                    }
                });
        MySingleton.getInstance(Ver.this).addToRequestQueue(jsObjRequest);



    }
}

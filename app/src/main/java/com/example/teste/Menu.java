package com.example.teste;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.teste.adapters.CustomArrayAdapter;
import com.example.teste.adapters.MyCursorAdapter;
import com.example.teste.db.Contrato;
import com.example.teste.db.DB;
import com.example.teste.entities.Pessoa;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Menu extends AppCompatActivity implements SensorEventListener {
    DB mDbHelper;
    SQLiteDatabase db;
    Cursor c, cursor;
    ListView lista;
    MyCursorAdapter madapter;
    SharedPreferences sharedPreferences;
    String user_name;
    ArrayList<Pessoa> array = new ArrayList<>();
    int id_user;
    private SensorManager mSensorManager;
    private Sensor mProximity;
    private static final int SENSOR_SENSITIVITY = 4;

    public Menu(){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        registerForContextMenu(findViewById(R.id.lista));
        mDbHelper = new DB(this);
        db = mDbHelper.getReadableDatabase();

        lista = findViewById(R.id.lista);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        sharedPreferences=Menu.this.getSharedPreferences("USER_CREDENTIALS",MODE_PRIVATE);
        user_name=sharedPreferences.getString("NAME","DEFAULT_NAME");
        id_user = sharedPreferences.getInt("IDUSER", -1);

        //Função quando clicar no contacto abre a classe ver
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Pessoa vp = array.get(position);
                Integer id_user = vp.getId();
                Intent intent = new Intent(Menu.this, Ver.class);
                intent.putExtra("ver", id_user); //passa o id pessoa para depois receber na classe ver
                startActivity(intent);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
    /*
    public void getCursor() {
        c = db.rawQuery("select * from " + Contrato.Contactos.TABLE_NAME + " where " + Contrato.Contactos.COLUMN_IDUSER +
                " = ?", new String[]{id_user + ""});
    }
    */
    public void onResume() {
        super.onResume();
        //getCursor();
        madapter = new MyCursorAdapter(this, c);
        lista.setAdapter(madapter);
        //mSensorManager.registerListener(Menu.this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
        if(!array.isEmpty()) {
            array.clear();
        }

        String url = "https://unhelmeted-mint.000webhostapp.com/myslim/api/contactos/" + id_user;


        // Formulate the request and handle the response.
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Toast.makeText(getActivity(), response.getString("status"), Toast.LENGTH_SHORT).show();
                            JSONArray arr = response.getJSONArray("DATA");
                            //Percorre o array e pega nos valores pretendidos
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject obj = arr.getJSONObject(i);

                                //Cria pessoa
                                Pessoa p = new Pessoa(obj.getInt("id"), obj.getString("nome"), obj.getString("numero"), obj.getString("idade"), obj.getString("pais"),
                                        obj.getString("codigopostal"), obj.getString("email"), obj.getString("genero"), obj.getString("localidade_id"));

                                array.add(p);  //adiciona pessoa ao array
                            }
                            CustomArrayAdapter itemsAdapter = new CustomArrayAdapter(Menu.this, array);
                            ((ListView) findViewById(R.id.lista)).setAdapter(itemsAdapter);
                            //itemsAdapter.notifyDataSetChanged();
                        } catch (JSONException ex) {
                        }
                        //Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Menu.this, error.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("Erro", error.toString());
                    }
                });
        MySingleton.getInstance(Menu.this).addToRequestQueue(jsObjRequest);


    }

    //Botao adicionar
    public void adicionar(View view){

        Intent i = new Intent(this, Adicionar.class);
        startActivity(i);
    }

    //Menu contextual
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menueditar, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position; //posição no menu onde clicar
        final Pessoa p = array.get(index);
        Integer id_user = p.getId();

        switch (item.getItemId()) {
            case R.id.editar:
                //Chama a função editar o contacto
                EditBox(id_user);
                return true;
            case R.id.remover:
                //Faz um alerta para confirmar se quer "remover contacto"
                RemoveConfirmationBox(id_user);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    //Funcao para remover o contacto
    public void RemoveConfirmationBox(final int id_user){
        AlertDialog.Builder a_builder = new AlertDialog.Builder(this);
        a_builder.setMessage(getResources().getString(R.string.Aviso))
                .setCancelable(true)
                .setPositiveButton(getResources().getString(R.string.sim),new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = "https://unhelmeted-mint.000webhostapp.com/myslim/api/contactodel/" + id_user;

                        // Formulate the request and handle the response.
                        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Toast.makeText(Menu.this, getResources().getString(R.string.removido), Toast.LENGTH_SHORT).show();
                                        //Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(Menu.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        MySingleton.getInstance(Menu.this).addToRequestQueue(jsObjRequest);

                        onResume();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.nao),new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Se clicar em nao, fecha a caixa de dialogo
                        dialog.cancel();
                    }
                }) ;
        AlertDialog alert = a_builder.create();
        alert.setTitle(getResources().getString(R.string.remover));
        alert.show();

    }

    //Fun
    public void EditBox(final int id_user){
        final Dialog dialog=new Dialog(Menu.this);
        dialog.setContentView(R.layout.editar);
        TextView txtMessage= dialog.findViewById(R.id.txtmessage);
        txtMessage.setText(getResources().getString(R.string.editar));
        //cria caixas de texto para depois passar os valores
        final EditText editText= dialog.findViewById(R.id.txtinput);
        final EditText editText2= dialog.findViewById(R.id.txtinput2);
        final EditText editText3= dialog.findViewById(R.id.txtinput3);
        final EditText editText4= dialog.findViewById(R.id.txtinput4);
        final EditText editText5= dialog.findViewById(R.id.txtinput5);
        final EditText editText6= dialog.findViewById(R.id.txtinput6);
        //query a bd para buscar dados da pessoa com id_user
        String url = "https://unhelmeted-mint.000webhostapp.com/myslim/api/contacto/" + id_user;


        // Formulate the request and handle the response.
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            editText.setText(response.getString("nome"));
                            editText2.setText(response.getString("numero"));
                            editText3.setText(response.getString("idade"));
                            editText4.setText(response.getString("pais"));
                            editText5.setText(response.getString("codigopostal"));
                            editText6.setText(response.getString("email"));
                        } catch (JSONException ex) {
                        }
                        //Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Menu.this, error.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("Erro", error.toString());
                    }
                });
        MySingleton.getInstance(Menu.this).addToRequestQueue(jsObjRequest);

        Button bt= dialog.findViewById(R.id.btdone);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://unhelmeted-mint.000webhostapp.com/myslim/api/contactoe/" + id_user;

                Map<String, String> jsonParams = new HashMap<String, String>();

                jsonParams.put("nome", editText.getText().toString());
                jsonParams.put("numero", editText2.getText().toString());
                jsonParams.put("idade", editText3.getText().toString());
                jsonParams.put("pais", editText4.getText().toString());
                jsonParams.put("codigopostal", editText5.getText().toString());
                jsonParams.put("email", editText6.getText().toString());


                // Formulate the request and handle the response.
                JsonObjectRequest putRequest = new JsonObjectRequest
                        (Request.Method.POST, url, new JSONObject(jsonParams), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try{
                                    if (response.getBoolean("status")) {
                                        Toast.makeText(Menu.this, response.getString("MSG"), Toast.LENGTH_SHORT).show();
                                    } else{
                                        Toast.makeText(Menu.this, response.getString("MSG"), Toast.LENGTH_SHORT).show();
                                    }
                                    onResume();
                                    dialog.dismiss();
                                    Toast.makeText(Menu.this,  getResources().getString(R.string.editado) , Toast.LENGTH_SHORT).show();

                                }
                                catch(JSONException ex){
                                    Toast.makeText(Menu.this, ex.toString(), Toast.LENGTH_SHORT).show();
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
                MySingleton.getInstance(Menu.this).addToRequestQueue(putRequest);
            }

        });
        dialog.show();
    }



    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menulog, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_logout:
                Intent login=new Intent(Menu.this, Login.class);
                final SharedPreferences sharedPreferences= Menu.this.getSharedPreferences("USER_CREDENTIALS",MODE_PRIVATE);
                sharedPreferences.edit().putBoolean("ISLOGGEDIN",false).commit();
                startActivity(login);
                finish();
                break;
            case R.id.action_sort:
                break;
            case R.id.GList:
                onResume();
                break;
            case R.id.orgAZ:
                if(!array.isEmpty()) {
                    array.clear();
                }

                String url = "https://unhelmeted-mint.000webhostapp.com/myslim/api/ordenaraz/" + id_user;

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray arr = response.getJSONArray("DATA");
                                    for (int i = 0; i < arr.length(); i++) {
                                        JSONObject obj = arr.getJSONObject(i);

                                        Pessoa p = new Pessoa(obj.getInt("id"), obj.getString("nome"), obj.getString("numero"), obj.getString("idade"), obj.getString("pais"),
                                                obj.getString("codigopostal"), obj.getString("email"), obj.getString("genero"), obj.getString("localidade_id"));

                                        array.add(p);
                                    }
                                    CustomArrayAdapter itemsAdapter = new CustomArrayAdapter(Menu.this, array);
                                    ((ListView) lista.findViewById(R.id.lista)).setAdapter(itemsAdapter);
                                } catch (JSONException ex) {
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(Menu.this, error.toString(), Toast.LENGTH_SHORT).show();
                                Log.d("Erro", error.toString());
                            }
                        });
                MySingleton.getInstance(Menu.this).addToRequestQueue(jsObjRequest);

                break;
            case R.id.orgIdade:
                if(!array.isEmpty()) {
                    array.clear();
                }

                String url3 = "https://unhelmeted-mint.000webhostapp.com/myslim/api/ordenaridade/" + id_user;

                JsonObjectRequest jsObjRequest3 = new JsonObjectRequest
                        (Request.Method.GET, url3, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray arr = response.getJSONArray("DATA");
                                    for (int i = 0; i < arr.length(); i++) {
                                        JSONObject obj = arr.getJSONObject(i);

                                        Pessoa p = new Pessoa(obj.getInt("id"), obj.getString("nome"), obj.getString("numero"), obj.getString("idade"), obj.getString("pais"),
                                                obj.getString("codigopostal"), obj.getString("email"), obj.getString("genero"), obj.getString("localidade_id"));

                                        array.add(p);
                                    }
                                    CustomArrayAdapter itemsAdapter = new CustomArrayAdapter(Menu.this, array);
                                    ((ListView) lista.findViewById(R.id.lista)).setAdapter(itemsAdapter);
                                } catch (JSONException ex) {
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(Menu.this, error.toString(), Toast.LENGTH_SHORT).show();
                                Log.d("Erro", error.toString());
                            }
                        });
                MySingleton.getInstance(Menu.this).addToRequestQueue(jsObjRequest3);

                break;
            case R.id.soMasc:
                if(!array.isEmpty()) {
                    array.clear();
                }

                String url4 = "https://unhelmeted-mint.000webhostapp.com/myslim/api/ordemmasc/" + id_user;

                JsonObjectRequest jsObjRequest4 = new JsonObjectRequest
                        (Request.Method.GET, url4, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray arr = response.getJSONArray("DATA");
                                    for (int i = 0; i < arr.length(); i++) {
                                        JSONObject obj = arr.getJSONObject(i);

                                        Pessoa p = new Pessoa(obj.getInt("id"), obj.getString("nome"), obj.getString("numero"), obj.getString("idade"), obj.getString("pais"),
                                                obj.getString("codigopostal"), obj.getString("email"), obj.getString("genero"), obj.getString("localidade_id"));

                                        array.add(p);
                                    }
                                    CustomArrayAdapter itemsAdapter = new CustomArrayAdapter(Menu.this, array);
                                    ((ListView) lista.findViewById(R.id.lista)).setAdapter(itemsAdapter);
                                } catch (JSONException ex) {
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(Menu.this, error.toString(), Toast.LENGTH_SHORT).show();
                                Log.d("Erro", error.toString());
                            }
                        });
                MySingleton.getInstance(Menu.this).addToRequestQueue(jsObjRequest4);

                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if( event.sensor.getType() == Sensor.TYPE_PROXIMITY)
        {
            if (event.values[0] >= -SENSOR_SENSITIVITY && event.values[0] <= SENSOR_SENSITIVITY){
                //Listar os ultimos 10 contactos inseridos
                c = db.query(false, Contrato.Contactos.TABLE_NAME, Contrato.Contactos.PROJECTION,
                        Contrato.Contactos.COLUMN_IDUSER + " = ?", new String[]{id_user + ""},
                        null, null,
                        Contrato.Contactos._ID + " DESC", "10");

                madapter = new MyCursorAdapter(this, c);
                lista.setAdapter(madapter);

            }
        }
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
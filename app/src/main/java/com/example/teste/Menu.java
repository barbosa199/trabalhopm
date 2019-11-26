package com.example.teste;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
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

import com.example.teste.adapters.MyCursorAdapter;
import com.example.teste.db.Contrato;
import com.example.teste.db.DB;

public class Menu extends AppCompatActivity implements SensorEventListener {
    DB mDbHelper;
    SQLiteDatabase db;
    Cursor c, cursor;
    ListView lista;
    MyCursorAdapter madapter;
    SharedPreferences sharedPreferences;
    String user_name;
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
        preencheLista();
        getCursor();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        sharedPreferences=Menu.this.getSharedPreferences("USER_CREDENTIALS",MODE_PRIVATE);
        user_name=sharedPreferences.getString("NAME","DEFAULT_NAME");
        id_user = sharedPreferences.getInt("IDUSER", -1);

        //Função quando clicar no contacto abre a classe ver
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                c.moveToPosition(position);  //mover cursor para a posição desejada
                int id_pessoa = c.getInt(c.getColumnIndex(Contrato.Contactos._ID));
                Intent intent = new Intent(Menu.this, Ver.class);
                intent.putExtra("ver", id_pessoa); //passa o id pessoa para depois receber na classe ver
                startActivity(intent);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void getCursor() {
        c = db.rawQuery("select * from " + Contrato.Contactos.TABLE_NAME + " where " + Contrato.Contactos.COLUMN_IDUSER +
                " = ?", new String[]{id_user + ""});
    }

    public void onResume() {
        super.onResume();
        getCursor();
        madapter = new MyCursorAdapter(this, c);
        lista.setAdapter(madapter);
        mSensorManager.registerListener(Menu.this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);

    }
    private void preencheLista(){
        c=db.query(false, Contrato.Contactos.TABLE_NAME, Contrato.Contactos.PROJECTION, null, null,
                null,null,null,null);

        madapter = new MyCursorAdapter(this, c);
        lista.setAdapter(madapter);
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
        c.moveToPosition(index); //move o cursor para o contacto clicado
        int id_pessoa = c.getInt(c.getColumnIndex(Contrato.Contactos._ID)); //obtem o ID do Contacto

        switch (item.getItemId()) {
            case R.id.editar:
                //Chama a função editar o contacto
              EditBox(id_pessoa);
              return true;
            case R.id.remover:
                //Faz um alerta para confirmar se quer "remover contacto"
                RemoveConfirmationBox(id_pessoa);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    //Funcao para remover o contacto
    public void RemoveConfirmationBox(final int id){
        AlertDialog.Builder a_builder = new AlertDialog.Builder(this);
        a_builder.setMessage(getResources().getString(R.string.Aviso))
                .setCancelable(true)
                .setPositiveButton(getResources().getString(R.string.sim),new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.delete(Contrato.Contactos.TABLE_NAME, Contrato.Contactos._ID + " = ?", new String[]{id+""}); //eliminar o contacto com base no ID
                        //onResume para atualizar a lista
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
    public void EditBox(final int id_pessoa){
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
        //query a bd para buscar dados da pessoa com id_pessoa
        cursor = db.query(false, Contrato.Contactos.TABLE_NAME, Contrato.Contactos.PROJECTION,
                Contrato.Contactos._ID + " = ?", new String[]{id_pessoa+""},
                null, null,
                null, null);
        //moves the cursor to the first result (when the set is not empty)
        cursor.moveToFirst();
        //set text para mostrar os valores das variaveis
        editText.setText(cursor.getString(cursor.getColumnIndexOrThrow(Contrato.Contactos.COLUMN_NOME)));
        editText2.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(Contrato.Contactos.COLUMN_NUMERO))));
        editText3.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(Contrato.Contactos.COLUMN_IDADE))));
        editText4.setText(cursor.getString(cursor.getColumnIndexOrThrow(Contrato.Contactos.COLUMN_PAIS)));
        editText5.setText(cursor.getString(cursor.getColumnIndexOrThrow(Contrato.Contactos.COLUMN_CODIGOPOSTAL)));
        editText6.setText(cursor.getString(cursor.getColumnIndexOrThrow(Contrato.Contactos.COLUMN_EMAIL)));
        cursor.close();

        Button bt= dialog.findViewById(R.id.btdone);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues cv = new ContentValues();

                float d=0;

                if((!editText3.getText().toString().isEmpty())){
                    d = Float.parseFloat(editText3.getText().toString());
                }

                cv.put(Contrato.Contactos.COLUMN_NOME, editText.getText().toString());
                cv.put(Contrato.Contactos.COLUMN_NUMERO, Integer.parseInt(editText2.getText().toString()));
                cv.put(Contrato.Contactos.COLUMN_IDADE, d);
                cv.put(Contrato.Contactos.COLUMN_PAIS, editText4.getText().toString());
                cv.put(Contrato.Contactos.COLUMN_CODIGOPOSTAL,editText5.getText().toString());
                cv.put(Contrato.Contactos.COLUMN_EMAIL, editText6.getText().toString());
                db.update(Contrato.Contactos.TABLE_NAME, cv, Contrato.Contactos._ID + " = ?", new String[]{id_pessoa+""});
                onResume();
                dialog.dismiss();
                Toast.makeText(Menu.this,  getResources().getString(R.string.editado) , Toast.LENGTH_SHORT).show();

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
                c = db.query(false, Contrato.Contactos.TABLE_NAME, Contrato.Contactos.PROJECTION,
                        Contrato.Contactos.COLUMN_IDUSER + " = ?", new String[]{id_user+""},
                        null, null,
                        Contrato.Contactos.COLUMN_NOME + " ASC", null);
                //Atualizar lista
                madapter = new MyCursorAdapter(Menu.this, c);
                lista.setAdapter(madapter);
                break;
            case R.id.orgIdade:
                c = db.query(false, Contrato.Contactos.TABLE_NAME, Contrato.Contactos.PROJECTION,
                        Contrato.Contactos.COLUMN_IDUSER + " = ?", new String[]{id_user+""},
                        null, null,
                        Contrato.Contactos.COLUMN_IDADE + " ASC", null);
                //Atualizar lista
                madapter = new MyCursorAdapter(Menu.this, c);
                lista.setAdapter(madapter);
                break;
            case R.id.soMasc:
                c = db.rawQuery("select * from " + Contrato.Contactos.TABLE_NAME + " where " + Contrato.Contactos.COLUMN_GENERO +
                        " = ? AND " + Contrato.Contactos.COLUMN_IDUSER + " = ?", new String[]{"Masculino",id_user+""});
                //Atualizar lista
                madapter = new MyCursorAdapter(Menu.this, c);
                lista.setAdapter(madapter);
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
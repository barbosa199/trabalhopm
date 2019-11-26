package com.example.teste;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teste.db.Contrato;
import com.example.teste.db.DB;

public class Adicionar extends AppCompatActivity {

    DB mDbHelper;
    SQLiteDatabase db;
    EditText nome, numero, idade, pais, codpostal, email;
    SharedPreferences sharedPreferences;
    Spinner gendrop;
    String genero;
    int id_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new DB(this);
        db = mDbHelper.getReadableDatabase();
        setContentView(R.layout.adicionar);

        nome = findViewById(R.id.nome); //aponta o conteudo da caixa de texto para a variavel
        numero = findViewById(R.id.numero);
        idade = findViewById(R.id.idade);
        pais = findViewById(R.id.pais);
        codpostal = findViewById(R.id.codpostal);
        email = findViewById(R.id.email);
        gendrop = findViewById(R.id.genero);

        //create a list of items for the spinner.
        String[] items = new String[]{getResources().getString(R.string.genero),getResources().getString(R.string.M), getResources().getString(R.string.FM)};

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        //set the spinners adapter to the previously created one.
        gendrop.setAdapter(adapter);

        sharedPreferences=getSharedPreferences("USER_CREDENTIALS",MODE_PRIVATE);
        id_user=sharedPreferences.getInt("IDUSER",-1);


        gendrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (parent.getItemAtPosition(pos).equals(getResources().getString(R.string.genero))){

                }
                else {
                    genero = (String) parent.getItemAtPosition(pos);
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (db.isOpen()) {
            db.close();
            db = null;
        }
    }




    public void novoContacto() { //criar novo contacto
        ContentValues cv = new ContentValues();
        if (nome.getText().toString().equals("") || numero.getText().toString().equals("")) {
            Toast.makeText((Adicionar.this), R.string.Campos, Toast.LENGTH_SHORT).show();
        }
        //adiciona o contacto a bd com os campos na ordem correta
        cv = new ContentValues();
        cv.put(Contrato.Contactos.COLUMN_NOME, nome.getText().toString());
        cv.put(Contrato.Contactos.COLUMN_NUMERO, numero.getText().toString());
        cv.put(Contrato.Contactos.COLUMN_IDADE, idade.getText().toString());
        cv.put(Contrato.Contactos.COLUMN_PAIS, pais.getText().toString());
        cv.put(Contrato.Contactos.COLUMN_CODIGOPOSTAL, codpostal.getText().toString());
        cv.put(Contrato.Contactos.COLUMN_EMAIL, email.getText().toString());
        cv.put(Contrato.Contactos.COLUMN_GENERO, genero);
        cv.put(Contrato.Contactos.COLUMN_IDUSER, id_user);
        db.insert(Contrato.Contactos.TABLE_NAME, null, cv);

    }

    //quando clica no botao verifica se o contacto criado tem os campos
    public void bt1(View view){
        if( nome.getText().toString().equals("") || numero.getText().toString().equals(""))
        {
            Toast.makeText(this, getResources().getString(R.string.Campos), Toast.LENGTH_SHORT).show();
        }
        else {
            novoContacto();
            finish();
        }
    }
}
package com.example.teste;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teste.db.Contrato;
import com.example.teste.db.DB;

import java.io.Serializable;

public class Ver extends AppCompatActivity implements Serializable {

    SQLiteDatabase db;
    DB mDbHelper;
    Cursor cursor;
    int id;
    TextView caixa1,caixa2,caixa3,caixa4,caixa5,caixa6,caixa7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver);
        caixa1 = findViewById(R.id.nome1);
        caixa2 = findViewById(R.id.numero1);
        caixa3 = findViewById(R.id.idade1);
        caixa4 = findViewById(R.id.pais1);
        caixa5 = findViewById(R.id.codpostal1);
        caixa6 = findViewById(R.id.email1);
        caixa7 = findViewById(R.id.genero1);
        id = getIntent().getExtras().getInt("ver");
        mDbHelper = new DB(this);
        db = mDbHelper.getReadableDatabase();
        cursor = db.query(false, Contrato.Contactos.TABLE_NAME, Contrato.Contactos.PROJECTION,
                Contrato.Contactos._ID + " = ?", new String[]{id+""},
                null, null,
                null, null);
        //moves the cursor to the first result (when the set is not empty)
        cursor.moveToFirst();
        caixa1.setText(cursor.getString(cursor.getColumnIndexOrThrow(Contrato.Contactos.COLUMN_NOME)));
        caixa2.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(Contrato.Contactos.COLUMN_NUMERO))));
        caixa3.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(Contrato.Contactos.COLUMN_IDADE))));
        caixa4.setText(cursor.getString(cursor.getColumnIndexOrThrow(Contrato.Contactos.COLUMN_PAIS)));
        caixa5.setText(cursor.getString(cursor.getColumnIndexOrThrow(Contrato.Contactos.COLUMN_CODIGOPOSTAL)));
        caixa6.setText(cursor.getString(cursor.getColumnIndexOrThrow(Contrato.Contactos.COLUMN_EMAIL)));
        caixa7.setText(cursor.getString(cursor.getColumnIndexOrThrow(Contrato.Contactos.COLUMN_GENERO)));
        cursor.close();
    }
}

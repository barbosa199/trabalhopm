package com.example.teste;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teste.db.Contrato;
import com.example.teste.db.DB;
import com.example.teste.encriptar.Seguranca;

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
        next_activity=new Intent(this, Login.class);
        final EditText name_field= findViewById(R.id.Name);
        final EditText password_field= findViewById(R.id.Password);
        final EditText confirm_password_field= findViewById(R.id.confirm_password);
        Button registerbutton= findViewById(R.id.register);
        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=name_field.getText().toString();
                c = db.rawQuery("select * from " + Contrato.Utilizador.TABLE_NAME + " where " + Contrato.Utilizador.COLUMN_USER +
                        " = ?", new String[]{name});

                if (name.length() < 6 && password_field.getText().toString().length()<6) {
                        Toast.makeText(RegisterActivity.this,R.string.carateres, Toast.LENGTH_SHORT).show();
                }
                else {
                    if (c != null && c.getCount() > 0) {
                        Toast.makeText(RegisterActivity.this, R.string.existente, Toast.LENGTH_SHORT).show();
                    } else {
                        String password_1 = password_field.getText().toString();
                        String password_2 = confirm_password_field.getText().toString();
                        if (password_1.equals(password_2)) {
                            try {
                                password_1 = s.encrypt(password_1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            cv.put(Contrato.Utilizador.COLUMN_USER, name);
                            cv.put(Contrato.Utilizador.COLUMN_PWD, password_1);
                            db.insert(Contrato.Utilizador.TABLE_NAME, null, cv);
                            startActivity(next_activity);
                        } else {
                            Toast.makeText(RegisterActivity.this, R.string.pwd, Toast.LENGTH_LONG).show();
                        }
                    }
                }
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
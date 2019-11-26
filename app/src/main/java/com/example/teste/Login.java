package com.example.teste;

import android.content.ContentValues;
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

import com.example.teste.db.Contrato;
import com.example.teste.db.DB;
import com.example.teste.encriptar.Seguranca;

public class Login extends AppCompatActivity {
    DB mDbHelper;
    SQLiteDatabase db;
    Cursor c;
    Seguranca s = new Seguranca();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mDbHelper = new DB(Login.this);
        db = mDbHelper.getReadableDatabase();
        final ContentValues cv = new ContentValues();
        final SharedPreferences sharedPreferences=getSharedPreferences("USER_CREDENTIALS",MODE_PRIVATE);
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
                String name=name_field.getText().toString();
                String password=password_field.getText().toString();
                //encripta a password para posteriormente comparar com a password na base de dados
                try {
                    password = s.encrypt(password);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                c = db.rawQuery("select * from " + Contrato.Utilizador.TABLE_NAME + " where " + Contrato.Utilizador.COLUMN_USER +
                        " = ?", new String[]{name});
                String rq_name = null;
                String rq_password = null;

                if(c != null && c.getCount() > 0)
                {
                    c.moveToFirst();
                    rq_name = c.getString(c.getColumnIndexOrThrow(Contrato.Utilizador.COLUMN_USER));
                    rq_password = c.getString(c.getColumnIndexOrThrow(Contrato.Utilizador.COLUMN_PWD));
                }

                if(name.equals(rq_name)&&password.equals(rq_password)) {
                    SharedPreferences sharedPreferences=getSharedPreferences("USER_CREDENTIALS",MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    int idu = c.getInt(c.getColumnIndexOrThrow(Contrato.Utilizador._ID));
                    editor.putInt("IDUSER",idu);
                    editor.putString("NAME",name);
                    editor.putString("PASSWORD",password);
                    editor.putBoolean("ISLOGGEDIN",true);
                    editor.commit();
                    Intent main = new Intent(Login.this, MainActivity.class);
                    startActivity(main);
                }
                else
                {
                    Toast.makeText(Login.this,"Username/Password estão erradas!",Toast.LENGTH_LONG).show();
                }
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

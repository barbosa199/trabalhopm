package com.example.teste.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION=8;
    public static final String DATABASE_NAME= "contactos.db";

    public DB(Context context) {super(context, DATABASE_NAME, null, DATABASE_VERSION);}

    public void onCreate(SQLiteDatabase db){
        db.execSQL(Contrato.Utilizador.SQL_CREATE_ENTRIES);
        db.execSQL(Contrato.Contactos.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Contrato.Utilizador.SQL_DROP_ENTRIES);
        db.execSQL(Contrato.Contactos.SQL_DROP_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion);
    }
}
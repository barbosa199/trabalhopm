package com.example.teste.db;

import android.provider.BaseColumns;

public class Contrato {

    private static final String TEXT_TYPE = " TEXT ";
    private static final String INT_TYPE = " INTEGER ";

    public Contrato(){
    }

    /*Tabela utilizador*/
    public static abstract class Utilizador implements BaseColumns {
        public static final String TABLE_NAME = "utilizador";
        public static final String COLUMN_USER = "user";
        public static final String COLUMN_PWD = "pwd";

        //Seleciona todas as linhas da tabela utilizador
        public static final String[] PROJECTION = {Utilizador._ID, Utilizador.TABLE_NAME, Utilizador.COLUMN_USER, Utilizador.COLUMN_PWD};

            //Criar as variaveis e definir o tipo das variaveis
            public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + Utilizador.TABLE_NAME + "(" + Utilizador._ID + INT_TYPE + " PRIMARY KEY," +
                Utilizador.COLUMN_USER + TEXT_TYPE +"," + Utilizador.COLUMN_PWD + TEXT_TYPE +");";
        //elimina a tabela do utilizador
        public static final String SQL_DROP_ENTRIES = "DROP TABLE " + Utilizador.TABLE_NAME + ";";
    }

    /*Tabela Contactos*/
    public static abstract class Contactos implements BaseColumns{
        public static final String TABLE_NAME = "contactos";
        public static final String COLUMN_NOME = "nome";
        public static final String COLUMN_NUMERO = "numero";
        public static final String COLUMN_IDADE = "idade";
        public static final String COLUMN_PAIS = "pais";
        public static final String COLUMN_CODIGOPOSTAL = "codigopostal";
        public static final String COLUMN_EMAIL= "email";
        public static final String COLUMN_GENERO= "genero";
        public static final String COLUMN_IDUSER= "iduser";

        //Seleciona todas as linhas da tabela Contactos
        public static final String[] PROJECTION = {Contactos._ID, Contactos.COLUMN_NOME, Contactos.COLUMN_NUMERO, Contactos.COLUMN_IDADE,
                Contactos. COLUMN_PAIS, Contactos.COLUMN_CODIGOPOSTAL, Contactos.COLUMN_GENERO, Contactos.COLUMN_EMAIL};

        //Criar as variaveis e definir o tipo das variaveis
        public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + Contactos.TABLE_NAME + "(" +

                Contactos._ID + INT_TYPE + " PRIMARY KEY," +
                Contactos.COLUMN_NOME + TEXT_TYPE + "," +
                Contactos.COLUMN_NUMERO + INT_TYPE + "," +
                Contactos.COLUMN_IDADE + INT_TYPE + "," +
                Contactos.COLUMN_PAIS + TEXT_TYPE + "," +
                Contactos.COLUMN_CODIGOPOSTAL + TEXT_TYPE + "," +
                Contactos.COLUMN_EMAIL + TEXT_TYPE + "," +
                Contactos.COLUMN_GENERO + TEXT_TYPE + "," +
                Contactos.COLUMN_IDUSER + INT_TYPE + "REFERENCES " + Utilizador.TABLE_NAME + "(" + Utilizador._ID + "));";

        //elimina a tabela dos contactos
        public static final String SQL_DROP_ENTRIES = "DROP TABLE " + Contactos.TABLE_NAME + ";";
    }
}

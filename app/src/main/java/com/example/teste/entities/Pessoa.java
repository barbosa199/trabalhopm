package com.example.teste.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Pessoa implements Parcelable{
    public String nome;
    public String numero;
    public String idade;
    public String email;
    public String codpostal;
    public String pais;

    public Pessoa(String nome, String numero, String idade, String email, String codpostal, String pais){
        this.nome=nome;
        this.numero=numero;
        this.idade=idade;
        this.email=email;
        this.codpostal=codpostal;
        this.pais=pais;
    }


    protected Pessoa(Parcel in) {
        nome = in.readString();
        numero = in.readString();
        idade = in.readString();
        email = in.readString();
        codpostal = in.readString();
        pais = in.readString();
    }

    public static final Creator<Pessoa> CREATOR = new Creator<Pessoa>() {
        @Override
        public Pessoa createFromParcel(Parcel in) {
            return new Pessoa(in);
        }

        @Override
        public Pessoa[] newArray(int size) {
            return new Pessoa[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nome);
        dest.writeString(numero);
        dest.writeString(idade);
        dest.writeString(email);
        dest.writeString(codpostal);
        dest.writeString(pais);
    }

    public String getCodpostal() {
        return codpostal;
    }

    public String getEmail() {

        return email;
    }

    public String getIdade() {
        return idade;
    }

    public String getNome() {

        return nome;
    }

    public String getNumero() {

        return numero;
    }

    public String getPais() {

        return pais;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setNumero(String numero) {
        this.numero = numero;
    }
    public void setIdade(String idade) {
        this.idade = idade;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCodpostal(String codpostal) {
        this.codpostal = codpostal;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }
}


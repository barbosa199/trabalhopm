package com.example.teste.entities;

import java.io.Serializable;

public class Pessoa implements Serializable {
    private Integer id;
    private String nome;
    private String numero;
    private String idade;
    private String email;
    private String codpostal;
    private String pais;
    private String genero;
    private String localidade_id;


    public Pessoa(Integer id, String nome, String numero, String idade, String email, String codpostal, String pais, String genero, String localidade_id){
        this.id = id;
        this.nome = nome;
        this.numero = numero;
        this.idade = idade;
        this.email = email;
        this.codpostal = codpostal;
        this.pais = pais;
        this.genero = genero;
        this.localidade_id = localidade_id;
    }

    public Integer getId() {
        return id;
    }

    public String getCodpostal() {
        return codpostal;
    }

    public String getEmail() { return email; }

    public String getIdade() {
        return idade;
    }

    public String getNome() { return nome; }

    public String getNumero() { return numero; }

    public String getPais() { return pais; }

    public String getGenero() { return genero; }

    public String getLocalidade_id() { return localidade_id; }

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

    public void setId(Integer id) {this.id = id;}
    public void setGenero(String genero) {this.genero = genero;}
    public void setLocalidade_id(String localidade_id) {this.localidade_id = localidade_id; }
}



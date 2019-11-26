package com.example.teste.encriptar;

import java.security.MessageDigest;

public class Seguranca {


    public Seguranca(){
    }

    public String encrypt(String data) throws Exception{
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data.getBytes());
        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b : digest){
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

}


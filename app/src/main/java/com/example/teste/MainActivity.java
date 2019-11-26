package com.example.teste;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicial);
    }

    public void next(View view){

        Intent i = new Intent(this, Menu.class);
        startActivity(i);
        finish();
    }
}

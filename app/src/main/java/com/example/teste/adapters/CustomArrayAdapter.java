package com.example.teste.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.teste.R;
import com.example.teste.entities.Pessoa;

import java.util.ArrayList;

public class CustomArrayAdapter extends ArrayAdapter<Pessoa> {

    public CustomArrayAdapter(@NonNull Context context, ArrayList<Pessoa> resource) {
        super(context, 0, resource);
    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {
        Pessoa p= getItem(position);
        if (convertView==null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_linha,parent, false);
        }

        ((TextView) convertView.findViewById(R.id.nome)).setText(p.getNome());
        ((TextView) convertView.findViewById(R.id.telefone)).setText(p.getNumero());
        ((TextView) convertView.findViewById(R.id.email)).setText(p.getPais());
        return convertView;
    }

}

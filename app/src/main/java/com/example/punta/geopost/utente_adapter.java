package com.example.punta.geopost;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.punta.geopost.R;
import com.example.punta.geopost.insieme_di_utenti;

import java.text.DecimalFormat;
import java.util.List;

public class utente_adapter extends ArrayAdapter<insieme_di_utenti.Utente> {
    public utente_adapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }
    public utente_adapter(Context context, int resource, List<insieme_di_utenti.Utente> items){
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.riga_lista, null);
        }

        insieme_di_utenti.Utente u = getItem(position);

        if (u != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.nome_utente);
            TextView tt2 = (TextView) v.findViewById(R.id.messaggio);
            TextView tt3 = (TextView) v.findViewById(R.id.ultima_posizione);
//                TextView tt3 = (TextView) v.findViewById(R.id.lat);
//                TextView tt4 = (TextView) v.findViewById(R.id.lng);

            tt1.setText("Username: " +u.getUsername());
            try {
                if (!u.getMsg().equals(""))
                    tt2.setText("Stato: "+ u.getMsg());
            }
            catch (Exception e) {
                tt2.setText("Stato: ");
            }

            tt3.setText("Distanza: " + u.getDistanza() + " KM");
//                tt3.setText(""+u.getLat());
//                tt4.setText(""+u.getLon());
        }

        return v;
    }
}
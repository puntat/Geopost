package com.example.punta.geopost;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

import static java.lang.Math.ceil;

public class amici extends AppCompatActivity {

    final lista_di_utenti listaUtenti = new lista_di_utenti();
    insieme_di_utenti insieme = new insieme_di_utenti();
    insieme_di_utenti.Utente[] followed;
    NestedScrollView scrollAmici;

    utente_adapter uAdapter = null;

    FloatingActionButton bottone_amici;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amici);

        ListView lista = (ListView)findViewById(R.id.lista_amici);
        uAdapter = new utente_adapter(getApplicationContext(), android.R.layout.simple_list_item_1, listaUtenti);
        lista.setAdapter(uAdapter);

        final SharedPreferences settings = getSharedPreferences("memoria", 0);
        String session_id = settings.getString("session_id","session_idNonRicevuta");
        //SharedPreferences.Editor editor = settings.edit();

        final String base_url = "https://ewserver.di.unimi.it/mobicomp/geopost/";
        final String tipo = "followed";
        final String dati = "?session_id="+session_id;

        RequestQueue coda = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest richiestaJson = new JsonObjectRequest(Request.Method.GET, base_url+tipo+dati, null,  new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response){
                //Log.d("aaaa Response", response);

                Gson gson = new Gson();
                insieme = gson.fromJson(response.toString(),insieme_di_utenti.class);
                followed = insieme.getFollowed();
                double distanza_massima = 0.0;

                for (insieme_di_utenti.Utente u : followed) {

                    listaUtenti.add(u);

                    double latitudine_attuale = Double.parseDouble((settings.getString("lat", Double.toString(0))));
                    double longitudine_attuale = Double.parseDouble((settings.getString("lon", Double.toString(0))));

                    Log.d("Prova mia", Double.toString(longitudine_attuale));
                    //Log.d("Prova", Double.toString(latitudine_attuale));

                    Location location1 = new Location("");
                    location1.setLongitude(longitudine_attuale);
                    location1.setLatitude(latitudine_attuale);

                    //Log.d("Prova", location1.toString());

                    Location location2 = new Location("");
                    location2.setLongitude(u.getLon());
                    location2.setLatitude(u.getLat());

                    Log.d("Prova amico", Double.toString(u.getLon()));

                    double distanza = location2.distanceTo(location1);
                    double distanzainKM = distanza/1000;

                    //Log.d("MainActivityDistanza",Double.toString(distanceinKM) + " " + u.getLat()+ " " + u.getLon());

                    u.distanza= new BigDecimal(distanzainKM).setScale(2, BigDecimal.ROUND_UP).doubleValue();
                    //Log.d("MainActivity","u vale "+u.getUsername()+ " " +u.getMsg()+ " " + u.distanza);

                }

                Collections.sort(listaUtenti, new DistanceOperator());
                //DistanceOperator è una classe che mi serve per mettere in ordine listaUtenti

                uAdapter.notifyDataSetChanged();

            }

        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {

                int codice_errore;
                //Log.e("aaaa LOGIN","Risposta: "+error.toString());

                try {
                    codice_errore = error.networkResponse.statusCode;
                }
                catch (Exception e) {
                    codice_errore = 0;
                }

                if (codice_errore != 0)
                    Toast.makeText(getApplicationContext(), "Codice: "+codice_errore+". Login fallito!", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), "Login fallito per mancanza di connessione!", Toast.LENGTH_LONG).show();
            }

        });

        coda.add(richiestaJson);

        // Acchiappa l'intent che ha cominciato l'attività
        //String nome_utente = getIntent().getExtras().getString("nome");
        //String password = getIntent().getExtras().getString("password");

        bottone_amici = (FloatingActionButton) findViewById(R.id.amici);
        bottone_amici.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }

        });

    }

    //Gestione menù opzioni
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
        switch(id)
        {
            case R.id.Prova_1:
            /*
                Codice di gestione della voce MENU_1
             */
                break;
            case R.id.Prova_2:

            /*
                Codice di gestione della voce MENU_2
             */
        }
        return false;
    }

}
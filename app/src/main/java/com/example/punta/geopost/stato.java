package com.example.punta.geopost;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionButton;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class stato extends AppCompatActivity {

    FloatingActionButton bottone_amici;

    //Inizializzazione del menù opzioni
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_opzioni, menu);
        return true;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stato);

        final SharedPreferences settings = getSharedPreferences("memoria", 0);
        final String session_id = settings.getString("session_id","session_idNonRicevuta");

        String nome = settings.getString("username", "Nessun Username");
        String password = settings.getString("password", "Nessuna Password");

        // Acchiappa l'intent che ha cominciato l'attività
        //String nome_utente = getIntent().getExtras().getString("nome");
        //String password = getIntent().getExtras().getString("password");

        final Double lat = Double.parseDouble(settings.getString("lat", "Nessuna Latitudine"));
        final Double lon = Double.parseDouble(settings.getString("lon", "Nessuna Longitudine"));
        Log.d("aaa MainActivity", Double.toString(lat) + " " + Double.toString(lon));

        final EditText messaggio = (EditText) findViewById(R.id.messaggio);

        final String base_url = "https://ewserver.di.unimi.it/mobicomp/geopost/";
        final String tipo = "status_update?";

        Button button_aggiorna = (Button)findViewById(R.id.button_aggiorna);

        button_aggiorna.setOnClickListener (new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final String url = base_url + tipo + "session_id=" + session_id + "&message=" + messaggio.getText().toString() + "&lat=" + Double.toString(lat) + "&lon=" + Double.toString(lon);
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url , new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response){
                        //Log.d("aaaa Response", response);

                        try {
                            if (response != "") {

                                URL requestURL = new URL(url);

                                HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();

                                Log.d("MainActivity", url);

                                conn.setRequestMethod("GET");
                                conn.setDoOutput(true);
                                conn.setDoInput(true);

                            }else{
                                Toast.makeText(getApplicationContext(), "Problemi nel login", Toast.LENGTH_LONG).show();
                            }

                        } catch (MalformedURLException e){
                            Toast.makeText(getApplicationContext(), "Qualcosa di errato nella connessione", Toast.LENGTH_LONG).show();
                        /*} catch (ProtocolException e) {
                            Toast.makeText(getApplicationContext(), "Qualcosa di errato nella connessione", Toast.LENGTH_LONG).show();*/
                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(), "Qualcosa di errato nella connessione", Toast.LENGTH_LONG).show();
                        }
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

                queue.add(stringRequest);

                finish();

            }
        });

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

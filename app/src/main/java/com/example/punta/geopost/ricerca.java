package com.example.punta.geopost;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

public class ricerca extends AppCompatActivity {

    String[] utenti;
    boolean rispostaRicevuta = false, selezionato = false;
    AutoCompleteTextView autocomplete = null;
    boolean primavolta = true;        //serve perchè la prima volta devo creare l'adapter e lo devo fare per forza dopo on response
    String dati2 = "";
    ArrayAdapter<String> adapter;

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
        setContentView(R.layout.activity_ricerca);

        final SharedPreferences settings = getSharedPreferences("memoria", 0);
        final String session_id = settings.getString("session_id","session_idNonRicevuta");

        final String base_url = "https://ewserver.di.unimi.it/mobicomp/geopost/";
        final String tipo = "users?";
        final String tipo2 = "follow?";

        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        autocomplete = (AutoCompleteTextView) findViewById(R.id.cerca_amici);

        // Acchiappa l'intent che ha cominciato l'attività
        //String nome_utente = getIntent().getExtras().getString("nome");
        //String password = getIntent().getExtras().getString("password");

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //   Non mi serve ma è obbligatorio
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (filterLongEnough()) {

                    dati2 = "&usernamestart=" + autocomplete.getText().toString() + "&limit=20";
                    //Log.d("aaa textwatcher SessID", "l'url completo adesso vale: " + base_url + tipo + "session_id="+ session_id + dati2);

                    JsonObjectRequest richiestaJson = new JsonObjectRequest(Request.Method.GET, base_url + tipo + "session_id="+ session_id + dati2, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {

                                    Gson gson = new Gson();
                                    //Log.d("aaa aggAmici response :", " \n " + response.toString());

                                    try {

                                        utenti = gson.fromJson(response.get("usernames").toString(), String[].class);

                                        for (String s : utenti) {
                                            Log.d("aaa utente vale: ", " \n" + s);
                                        }
                                        rispostaRicevuta = true;

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    if (primavolta) {

                                        Log.d("aaa textwatcher SessID", "sono in on response risposta ricevuta prima volta  !!! ");

                                        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.autocomplete_layout, utenti);
                                        autocomplete.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                        primavolta = false;

                                    } else {

                                        Log.d("aaa textwatcher SessID", "sono in on response notify data set !!! ");
                                        //autocomplete.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();

                                    }
                                }
                            },
                            new Response.ErrorListener() {

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

                            }
                    );

                    queue.add(richiestaJson);

                } else {

                    primavolta = true;

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Non mi serve ma è obbligatorio
            }

            private boolean filterLongEnough() {

                return autocomplete.getText().toString().trim().length() > 1;
            }

        };

        autocomplete.addTextChangedListener(textWatcher);

        autocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
                final String selected = (String)adapter.getItemAtPosition(pos);

                final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                AlertDialog.Builder builder;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(ricerca.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(ricerca.this);
                }

                builder.setTitle("Conferma")
                        .setMessage("Vuoi seguire "+selected+"?")

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                StringRequest stringRequest = new StringRequest(Request.Method.POST, base_url+tipo2+"session_id="+ session_id+"&username="+selected, new Response.Listener<String>() {

                                    @Override
                                    public void onResponse(String response) {

                                        Log.e("FOLLOW","response vale "+response);

                                        Toast.makeText(getApplicationContext(), "Ora segui "+selected+"!", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(getApplicationContext(), home.class);
                                        startActivity(i);

                                    }

                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        int codice;
                                        String errore;
                                        NetworkResponse response;

                                        try {

                                            response = error.networkResponse;
                                            codice = response.statusCode;
                                            errore = new String(response.data);

                                            if (codice == 400) {
                                                switch (errore) {
                                                    case "CANNOT FOLLOW YOURSELF":
                                                        Toast.makeText(getApplicationContext(), "Non puoi seguirti da solo!", Toast.LENGTH_LONG).show();
                                                        break;
                                                    case "ALREADY FOLLOWING USER":
                                                        Toast.makeText(getApplicationContext(), "Segui già "+selected+"!", Toast.LENGTH_LONG).show();
                                                        break;
                                                    case "USERNAME NOT FOUND":
                                                        Toast.makeText(getApplicationContext(), "L'utente "+selected+" non esiste!", Toast.LENGTH_LONG).show();
                                                        break;

                                                }
                                            }

                                            else
                                                Toast.makeText(getApplicationContext(), "Codice: "+codice+". Follow fallito!", Toast.LENGTH_LONG).show();

                                        }

                                        catch (Exception e) {
                                            Toast.makeText(getApplicationContext(), "Follow fallito per mancanza di connessione!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                                queue.add(stringRequest);
                            }
                        })

                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                // Non far niente
                            }
                        })

                        .setIcon(android.R.drawable.checkbox_on_background)
                        .show();

                selezionato = true;
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

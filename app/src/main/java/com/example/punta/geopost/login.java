package com.example.punta.geopost;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {

    Boolean loggato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final SharedPreferences settings = getSharedPreferences("memoria", 0);
        loggato = settings.getBoolean("isLoggato",false);
        final SharedPreferences.Editor editor = settings.edit();

        final TextView text_nome_utente = findViewById(R.id.text_nome_utente);
        final TextView text_password = findViewById(R.id.text_password);

        //Questi due comandi servono solo per far sparire la tastiera quando viene finito di cliccare*/
        text_nome_utente.setImeOptions(EditorInfo.IME_ACTION_DONE);
        text_password.setImeOptions(EditorInfo.IME_ACTION_DONE);

        final EditText nome = (EditText)text_nome_utente;
        final EditText password = (EditText)text_password;

        Button button_login = (Button)findViewById(R.id.button_login);
        button_login.setOnClickListener(new View.OnClickListener() {

            String base_url = "https://ewserver.di.unimi.it/mobicomp/geopost/";
            String tipo = "login";

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            public void onClick(View v) {

                StringRequest stringRequest = new StringRequest(Request.Method.POST, base_url+tipo, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response){
                        //Log.d("aaaa Response", response);

                        if (response != "") {
                            Intent i = new Intent(getApplicationContext(), home.class);
                            editor.putBoolean("isLoggato", true);
                            editor.putString("session_id", response);
                            editor.commit();
                            startActivity(i);

                        }else{
                            Toast.makeText(getApplicationContext(), "Problemi nel login", Toast.LENGTH_LONG).show();
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

                })
                { // PASSAGGIO PARAMETRI POST

                    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {

                        Map<String, String> params = new HashMap<String, String>();
                        params.put("username", nome.getText().toString());
                        params.put("password", password.getText().toString());

                        //Log.d("aaaa Login params",params.toString());

                        editor.putString("username", nome.getText().toString());
                        editor.putString("password", password.getText().toString());
                        editor.putString("lat", Double.toString(0));
                        editor.putString("lon", Double.toString(0));
                        editor.commit();

                        return params;
                    }
                };

                queue.add(stringRequest);

                /*
                String nome = text_nome_utente.getText().toString();
                String password = text_password.getText().toString();

                if ((nome.equals("Punta"))&&(password.equals("123"))) {
                    login.putExtra("nome", nome);
                    login.putExtra("password", password);

                    startActivity(login);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Nome utente o password errati!", Toast.LENGTH_LONG).show();
                }*/

            }

        });
    }

    @Override
    public void onBackPressed() {

        //NON FARE NIENTE

    }


}

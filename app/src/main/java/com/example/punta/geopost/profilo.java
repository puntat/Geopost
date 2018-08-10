package com.example.punta.geopost;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

public class profilo extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    FloatingActionButton bottone_amici;

    final String base_url = "https://ewserver.di.unimi.it/mobicomp/geopost/";
    final String tipo = "profile?";

    GoogleMap mMap;
    private UiSettings mUiSettings;

    private static final int LOCATION_PERMISSION = 1;
    private GoogleApiClient mGoogleApiClient = null;
    private boolean googleApiClientReady = false;
    private boolean permissionGranted = false;
    private TextView mTextView = null;

    LocationManager lm ;
    Location location;

    double longitude ;
    double latitude ;
    Marker marker;

    JSONObject res;

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
        setContentView(R.layout.activity_profilo);

        final SharedPreferences settings = getSharedPreferences("memoria", 0);
        final String session_id = settings.getString("session_id","session_idNonRicevuta");
        final SharedPreferences.Editor editor = settings.edit();

        final TextView nome_utente = findViewById(R.id.nome_utente);
        final TextView ultimo_messaggio = findViewById(R.id.ultimo_messaggio);

        bottone_amici = (FloatingActionButton) findViewById(R.id.amici);
        bottone_amici.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)  {
            Log.d("MainActivity", "Permission granted");
            permissionGranted = true;
        } else {
            Log.d("MainActivity", "Permission NOT granted");
            // Non l'ha ancora fatto, allora mostra il metodo onRequestPermissionsResult method che fa la domanda
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
        }

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, base_url+tipo+"session_id="+session_id, null,  new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response){
                    //Log.d("aaaa Response", response);

                    if (response.toString() != "") {

                        try {

                            res = response;

                            nome_utente.setText(response.getString("username"));

                            if (response.getString("msg") != "null") {

                                ultimo_messaggio.setText(response.getString("msg"));
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        }

                    }else{
                        Toast.makeText(getApplicationContext(), "Problemi con il collegamento al server", Toast.LENGTH_LONG).show();
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

            queue.add(jsObjRequest);


        //String nome = settings.getString("username", "Nessun Username");
        //String password = settings.getString("password", "Nessuna Password");

        // Acchiappa l'intent che ha cominciato l'attività
        //String nome_utente = getIntent().getExtras().getString("nome");
        //String password = getIntent().getExtras().getString("password");

        Button button_logout = (Button)findViewById(R.id.button_logout);
        button_logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), login.class);
                editor.putBoolean("isLoggato", false);
                final String url = base_url + "logout?session_id=" + session_id;

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url , new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            Intent i = new Intent(getApplicationContext(), login.class);
                            editor.putBoolean("isLoggato", true);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                            URL requestURL = new URL(url);

                            HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();

                            Log.d("MainActivity", url);

                            conn.setRequestMethod("GET");
                            conn.setDoOutput(true);
                            conn.setDoInput(true);

                            editor.commit();

                        } catch (MalformedURLException e) {
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

                startActivity(i);
                finish();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //Controlla che i GooglePlayService siano disponibili sul dispositivo
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if(status == ConnectionResult.SUCCESS) {
            Log.d("MainActivity", "GooglePlayServices available");
        } else {
            Log.d("MainActivity", "GooglePlayServices UNAVAILABLE");
            if(googleApiAvailability.isUserResolvableError(status)) {
                Log.d("MainActivity", "Ask the user to fix the problem");
                //Se l'errore è risolvibile, viene chiesto all'utente di installare i GooglePlayServices, viene reinstallata l'app e quando l'utente torna indietro riparte da onStart
                googleApiAvailability.getErrorDialog(this, status, 2404).show();
            } else {
                //Se l'errore non è risolvibile, niente...
                Log.d("MainActivity", "The problem cannot be fixed");
            }
        }

        // Instanziamento e connessione al GooglePlayServices
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("MainActivity", "GoogleApiClient connected");
        googleApiClientReady = true;
        checkAndStartLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("MainActivity", "GoogleApiClient suspended");
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        Log.d("MainActivity", "RequestCode: "+ requestCode);

        switch (requestCode) {
            case LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;

                    checkAndStartLocationUpdate();

                } else {
                    // Se il permesso è negato disabilità le funzionalità che dipendono da questo permesso.
                    mTextView.setText("");
                    Log.d("MainActivity", "L'applicazione ha bisogno di accedere alla posizione");
                }
                return;
            }
        }

    }

    private void checkAndStartLocationUpdate() {
        if (permissionGranted && googleApiClientReady) {
            Log.d("MainActivity", "Start updating location");
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            try {

                lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);

                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("MainActivity", "GoogleApiClient failed");
    }

    public void onLocationChanged(Location location) {

    }

    //Prova Mappa
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);

        LatLng posizione = null;

        try {

            if (res.getString("lon") != "null") {

                //Log.d("MainActivity", res.getString("lon") + " sono stronzo");

                posizione = new LatLng(Double.parseDouble(res.getString("lat")), Double.parseDouble(res.getString("lon")));

                marker = googleMap.addMarker(new MarkerOptions().position(posizione).title("Ultima posizione di " + res.getString("username")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                googleMap.moveCamera(CameraUpdateFactory.newLatLng(posizione));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

            }

        }catch (JSONException e) {
            e.printStackTrace();
        }

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
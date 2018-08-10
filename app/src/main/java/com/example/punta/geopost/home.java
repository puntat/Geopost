package com.example.punta.geopost;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class home extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

    //final lista_di_utenti listaUtenti = new lista_di_utenti();
    insieme_di_utenti insieme = new insieme_di_utenti();
    insieme_di_utenti.Utente[] followed;

    SharedPreferences settings = null;
    SharedPreferences.Editor editor = null;
    String nome;

    FloatingActionMenu menu_piu;
    FloatingActionButton bottone_profilo, bottone_ricerca, bottone_amici, bottone_stato;

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

    //Inizializzazione del menù opzioni
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_opzioni, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        settings = getSharedPreferences("memoria", 0);
        editor = settings.edit();

        //Controlla se l'utente ha già permesso l'utilizzo di FINE_LOCATION
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)  {
            Log.d("MainActivity", "Permission granted");
            permissionGranted = true;
        } else {
            Log.d("MainActivity", "Permission NOT granted");
            // Non l'ha ancora fatto, allora mostra il metodo onRequestPermissionsResult method che fa la domanda
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
        }

        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location == null) {

            location = new Location("");
            location.setLongitude(0.0);
            location.setLatitude(0.0);

        }

        //Log.d("aaa MainActivity", lm.getLastKnownLocation(LocationManager.GPS_PROVIDER).toString());
        //Log.d("aaa MainActivity", Double.toString(location.getLongitude()));

        //Gestione del menù +
        menu_piu = (FloatingActionMenu) findViewById(R.id.menu_floating);
        bottone_profilo = (FloatingActionButton) findViewById(R.id.profilo);
        bottone_ricerca = (FloatingActionButton) findViewById(R.id.ricerca);
        bottone_stato = (FloatingActionButton) findViewById(R.id.aggiornamento_stato);
        bottone_amici = (FloatingActionButton) findViewById(R.id.lista_amici);

        bottone_profilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), profilo.class);

                menu_piu.close(true);
                startActivity(i);

                /*Snackbar.make(view, "Snackbar Amici", Snackbar.LENGTH_LONG)
                        //Mostra la SNACKBAR
                        .setAction("Action", null).show();*/

            }
        });

        bottone_ricerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ricerca.class);

                menu_piu.close(true);
                startActivity(i);


            }
        });

        bottone_stato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), stato.class);

                menu_piu.close(true);
                startActivity(i);
            }
        });

        bottone_amici.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), amici.class);

                menu_piu.close(true);
                startActivity(i);
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

                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

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

        settings = getSharedPreferences("memoria", 0);
        editor = settings.edit();

        //Log.d("aaa MainActivity", location.toString());
        //Log.d("aaa MainActivity", Double.toString(location.getLongitude()));

        Log.d("MainActivity", "Location update received: " + location.toString());

        longitude = location.getLongitude();
        latitude = location.getLatitude();

        Log.d("MainActivity", latitude + " " + longitude);

        LatLng posizione = new LatLng(latitude, longitude);

        editor.putString("lon", Double.toString(longitude));
        editor.putString("lat", Double.toString(latitude));
        editor.commit();

        if (mMap != null) {

            marker.setPosition(posizione);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(posizione));

        }

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        settings = getSharedPreferences("memoria", 0);
        editor = settings.edit();

        //editor.putString("lat", Double.toString(0));
        //editor.putString("lon", Double.toString(0));

        String session_id = settings.getString("session_id","session_idNonRicevuta");

        final String base_url = "https://ewserver.di.unimi.it/mobicomp/geopost/";
        final String tipo = "followed";
        final String dati = "?session_id="+session_id;

        mMap = googleMap;

        mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);

        nome = settings.getString("username", "Nessun Username");

        Log.d("MainActivity", "Map is ready!");

        RequestQueue coda = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest richiestaJson = new JsonObjectRequest(Request.Method.GET, base_url+tipo+dati, null,  new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response){
                //Log.d("aaaa Response", response);

                Gson gson = new Gson();
                insieme = gson.fromJson(response.toString(),insieme_di_utenti.class);
                followed = insieme.getFollowed();

                for (insieme_di_utenti.Utente u : followed) {

                    //listaUtenti.add(u);
                    LatLng posizione_utente = new LatLng(u.getLat(), u.getLon());
                    //Log.d("aaa MainActivity", Double.toString(u.getLon()));

                    try{

                        googleMap.addMarker(new MarkerOptions().position(posizione_utente).title(u.getUsername()).snippet(u.getMsg()));

                        //u.distanza= DistanceOperator.calculateDistance(myLat,myLon,u.getLat(),u.getLon(),"k");
                        Log.d("MainActivity","u vale "+u.getUsername()+" "+u.getMsg()+" "+posizione_utente);

                        //uAdapter.notifyDataSetChanged();
                    }
                    catch (Exception e){
                        googleMap.addMarker(new MarkerOptions().position(posizione_utente));

                    }

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

        coda.add(richiestaJson);

        LatLng posizione = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d("aaa MainActivity", Double.toString(location.getLongitude()));
        marker = googleMap.addMarker(new MarkerOptions().position(posizione).title("Posizione attuale di " + nome).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posizione, 17.0f));

        editor.putString("lon", Double.toString(location.getLongitude()));
        editor.putString("lat", Double.toString(location.getLatitude()));
        editor.commit();

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

    @Override
    public void onBackPressed() {
        // NON FAR NULLA
    }

}
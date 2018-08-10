package com.example.punta.geopost;

import android.util.Log;

public class insieme_di_utenti {
    private Utente[] followed;

    public class Utente {
        public String username,msg;
        public double lat,lon;
        public double distanza;

        public Utente(){}

        public double getLat() {
            //Log.d("aaa MainActivity", Double.toString(lat));
            return lat;
        }

        public double getLon() {
            return lon;
        }

        public String getMsg() {
            return msg;
        }

        public String getUsername() {
            return username;
        }
        public double getDistanza() {
            return distanza;
        }

    }

    public String getUtenti() {
        String ciao="";
        for(Utente u:followed)
        {
            ciao+= u.getUsername()+" "+u.getMsg()+" "+u.getLat()+" "+u.getLon()+"\n";
        }
        return ciao;
    }

    public  int getFollowedLength()
    {
        return followed.length;
    }

    public insieme_di_utenti() {}

    public Utente[] getFollowed()
    {
        return followed;
    }

}

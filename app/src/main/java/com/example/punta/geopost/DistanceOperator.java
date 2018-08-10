package com.example.punta.geopost;

import java.util.Comparator;

public class DistanceOperator implements Comparator<insieme_di_utenti.Utente>{

    @Override
    public int compare(insieme_di_utenti.Utente u1, insieme_di_utenti.Utente u2) {
        if(u1.getDistanza()<u2.getDistanza()){

            return -1;

        }else if(u1.getDistanza()>u2.getDistanza()){

            return 1;
        }

        return 0;
    }

}

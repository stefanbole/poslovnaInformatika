package controllers;

import models.Korisnik;

public class Security extends Secure.Security {
    
    static boolean authenticate(String email, String sifra) {
        Korisnik korisnik = Korisnik.find("byEmail", email).first();
        return korisnik != null && korisnik.sifra.equals(sifra);
    }
}
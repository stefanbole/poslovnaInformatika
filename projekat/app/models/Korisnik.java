package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Korisnik extends Model{

	public String ime;
	public String email;
	public String sifra;
	
	public Korisnik(String ime, String email, String sifra) {
		super();
		this.ime = ime;
		this.email = email;
		this.sifra = sifra;
	}
	

	
}

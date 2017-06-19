package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Max;
import play.data.validation.MaxSize;
import play.data.validation.Min;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;

/*
 * 
 * 
 * MALA NAPOMENA:
 * Poslovni partner isporucuje narudzbe, otkomentarisati listu dole kad se doda klasa Narudzba,
 *  i dodati vezu iz Narudzba ka PoslovniPartner
 * 
 * 
 * 
 * 
 * */

@Entity
public class PoslovniPartner extends Model {

	@Required
	@MinSize(2)
	@MaxSize(40)
	@Column(columnDefinition = "varchar(40)")
	public String naziv;

	@MinSize(2)
	@MaxSize(40)
	@Column(columnDefinition = "varchar(40)")
	public String mesto;

	@Required
	@MinSize(2)
	@MaxSize(40)
	@Column(columnDefinition = "varchar(40)")
	public String adresa;

	@Column(columnDefinition = "varchar(2)")
	public String vrsta;

	public String telefon;

	@Required
	@MinSize(8)
	@MaxSize(8)
	@Min(11111111)
	@Max(99999999)
	public int pib;

	@Required
	@Column(columnDefinition = "char(18)")
	public String tekuciRacun;

	 @OneToMany(mappedBy = "poslovniPartner")
	 public List<Narudzba> narudzbe;

	@OneToMany(mappedBy = "poslovniPartner")
	public List<Faktura> fakture;

	@ManyToOne
	public Preduzece preduzece;

	public PoslovniPartner(String naziv, String mesto, String adresa, String vrsta, String telefon, int pib,
			String tekuciRacun) {
		super();
		this.naziv = naziv;
		this.mesto = mesto;
		this.adresa = adresa;
		this.vrsta = vrsta;
		this.telefon = telefon;
		this.pib = pib;
		this.tekuciRacun = tekuciRacun;
	}

}

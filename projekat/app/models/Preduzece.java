package models;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Preduzece extends Model {

	@Required
	@MinSize(3)
	@MaxSize(1024)
	@Column(columnDefinition = "varchar(1024)")
	public String naziv;

	@Required
	@MinSize(8)
	@MaxSize(8)
	@Column(columnDefinition = "varchar(8)")
	public String pib;

	@MinSize(2)
	@MaxSize(40)
	@Column(columnDefinition = "varchar(40)")
	public String mesto;

	@Required
	@MinSize(3)
	@MaxSize(40)
	@Column(columnDefinition = "varchar(40)")
	public String adresa;

	@MaxSize(15)
	@Column(columnDefinition = "varchar(15)")
	public String telefon;

	@Required
	@MinSize(13)
	@MaxSize(13)
	@Column(columnDefinition = "varchar(13)")
	public String maticniBroj;

	@Required
	@MinSize(3)
	@MaxSize(18)
	@Column(columnDefinition = "character(18)")
	public String tekuciRacun;

	@OneToMany(mappedBy = "preduzece")
	public List<PoslovnaGodina> poslovneGodine;

	@OneToMany(mappedBy = "preduzece")
	public List<Grupa> grupe;

	@OneToMany(mappedBy = "preduzece")
	public List<PoslovniPartner> poslovniPartneri;

	@OneToMany(mappedBy = "preduzece")
	public List<Faktura> fakture;
	
	@OneToMany(mappedBy = "preduzece")
	public List<Narudzba> narudzbe;

	public Preduzece(String naziv, String pib, String mesto, String adresa, String telefon, String maticniBroj,
			String tekuciRacun) {
		super();
		this.naziv = naziv;
		this.pib = pib;
		this.mesto = mesto;
		this.adresa = adresa;
		this.telefon = telefon;
		this.maticniBroj = maticniBroj;
		this.tekuciRacun = tekuciRacun;
	}

}

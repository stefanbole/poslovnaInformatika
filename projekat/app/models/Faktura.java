package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.data.validation.Required;
import play.db.jpa.Model;

/*
 * 
 * MALA NAPOMENA:
 * Dodati vezu 1:1 sa klasom Naruzba
 * 
 * 
 * */

@Entity
public class Faktura extends Model {

	@Required
	@Column(columnDefinition = "varchar(10)")
	public String datumFakture;

	public int brojFakture;

	@Required
	@Column(columnDefinition = "varchar(10)")
	public String datumValute;

	public float ukupnoOsnovica;

	public float ukupnoPDV;

	public float ukupnoZaPlacanje;

	@OneToMany(mappedBy = "faktura")
	public List<StavkaFakture> stavkeFakture;

	@ManyToOne
	public PoslovniPartner poslovniPartner;

	@ManyToOne
	public PoslovnaGodina poslovnaGodina;

	@ManyToOne
	public Preduzece preduzece;
	
	@ManyToOne 
	public Narudzba narudzba;

	public Faktura(String datumFakture, int brojFakture, String datumValute, float ukupnoOsnovica, float ukupnoPDV,
			float ukupnoZaPlacanje) {
		super();
		this.datumFakture = datumFakture;
		this.brojFakture = brojFakture;
		this.datumValute = datumValute;
		this.ukupnoOsnovica = ukupnoOsnovica;
		this.ukupnoPDV = ukupnoPDV;
		this.ukupnoZaPlacanje = ukupnoZaPlacanje;
	}

}

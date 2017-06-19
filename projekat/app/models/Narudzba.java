package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Narudzba extends Model {

	@Required
	@Column(columnDefinition = "varchar(10)")
	public String datumNarudzbe;
	
	@Required
	public int brojNarudzbe;
	
	@Required
	@Column(columnDefinition = "varchar(10)")
	public String datumValute;
	
	@Required
	public double ukupnoOsnovica;
	
	@Required
	public float ukupnoPDV;
	
	@Required
	public float ukupnoZaPlacanje;
	
	public String fakturisana;
	
	@OneToMany(mappedBy = "narudzba")
	public List<StavkaNarudzbe> stavkeNarudzbe;
	
	@OneToMany(mappedBy = "narudzba")
	public List<Faktura> faktura;
	
	@ManyToOne
	public PoslovniPartner poslovniPartner;
	
	@ManyToOne
	public PoslovnaGodina poslovnaGodina;
	
	@ManyToOne 
	public Preduzece preduzece;
	
	
	
	
	
	public Narudzba(String datumNarudzbe, int brojNarudzbe, String datumValute, double ukupnoOsnovica, 
			float ukupnoPDV,
			float ukupnoZaPlacanje,String fakturisana) {
		super();
		this.datumNarudzbe = datumNarudzbe;
		this.brojNarudzbe = brojNarudzbe;
		this.datumValute = datumValute;
		this.ukupnoOsnovica = ukupnoOsnovica;
		this.ukupnoPDV = ukupnoPDV;
		this.ukupnoZaPlacanje = ukupnoZaPlacanje;
		this.fakturisana = fakturisana;
	}
	
}

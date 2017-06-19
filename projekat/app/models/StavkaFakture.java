package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Min;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class StavkaFakture extends Model {

	@Required
	@Min(1)
	public int kolicina;

	public float cena;

	public float vrednost;

	@Required
	@Min(1)
	public float rabat;

	public float iznosRabata;

	public float osnovicaZaPDV;

	public float stopaPDVa;

	public float iznosPDVa;

	public float ukupno;

	@ManyToOne
	public Faktura faktura;

	@ManyToOne
	public KatalogRobeIUsluga katalogRobeIUsluga;

	public StavkaFakture(int kolicina, float cena, float vrednost, float rabat, float iznosRabata, float osnovicaZaPDV,
			float stopaPDVa, float iznosPDVa, float ukupno) {
		super();
		this.kolicina = kolicina;
		this.cena = cena;
		this.vrednost = vrednost;
		this.rabat = rabat;
		this.iznosRabata = iznosRabata;
		this.osnovicaZaPDV = osnovicaZaPDV;
		this.stopaPDVa = stopaPDVa;
		this.iznosPDVa = iznosPDVa;
		this.ukupno = ukupno;
	}

}

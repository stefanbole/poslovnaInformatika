package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.MaxSize;
import play.data.validation.Min;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class StavkaCenovnika extends Model {

	@Required
	@Column(columnDefinition = "decimal")
	@Min(1)
	public double cena;

	@ManyToOne
	public Cenovnik cenovnik;
	
	@ManyToOne
	public KatalogRobeIUsluga katalogRobeIUsluga;

	public StavkaCenovnika(double cena) {
		super();
		this.cena = cena;
	}

}

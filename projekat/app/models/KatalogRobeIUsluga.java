package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class KatalogRobeIUsluga extends Model {

	@Required
	@MinSize(3)
	@MaxSize(40)
	@Column(columnDefinition = "varchar(40)")
	public String nazivStavkeKataloga;

	@Required
	@MinSize(3)
	@MaxSize(1024)
	@Column(columnDefinition = "varchar(1024)")
	public String opisStavkeKataloga;

	@ManyToOne
	public Podgrupa podgrupa;

	@OneToMany(mappedBy = "katalogRobeIUsluga")
	public List<StavkaCenovnika> stavkeCenovnika;

	@OneToMany(mappedBy = "katalogRobeIUsluga")
	public List<StavkaFakture> stavkeFakture;

	public KatalogRobeIUsluga(String nazivStavkeKataloga, String opisStavkeKataloga) {
		super();
		this.nazivStavkeKataloga = nazivStavkeKataloga;
		this.opisStavkeKataloga = opisStavkeKataloga;
	}

}

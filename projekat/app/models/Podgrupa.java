package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Podgrupa extends Model {

	@Required
	@Column(columnDefinition = "varchar(40)")
	public String nazivPodgrupe;

	@ManyToOne
	public Grupa grupa;

	@OneToMany(mappedBy = "podgrupa")
	public List<KatalogRobeIUsluga> kataloziRobeIUsloga;
	
	

	public Podgrupa(String nazivPodgrupe) {
		super();
		this.nazivPodgrupe = nazivPodgrupe;
	}

}

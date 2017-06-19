package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Grupa extends Model {

	@Required
	@Column(columnDefinition = "varchar(40)")
	public String nazivGrupe;

	@ManyToOne
	public VrstaPDVa vrstaPDVa;

	@ManyToOne
	public Preduzece preduzece;

	@OneToMany(mappedBy = "grupa")
	public List<Podgrupa> podgrupe;

	public Grupa(String nazivGrupe) {
		super();
		this.nazivGrupe = nazivGrupe;
	}

}

package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.data.validation.Min;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class StopaPDVa extends Model {

	@Required
	@Column(columnDefinition = "varchar(10)")
	public String datumKreiranja;

	@Min(1)
	public float procenatPDVa;

	@ManyToOne
	public VrstaPDVa vrstaPDVa;

	public StopaPDVa(int procenatPDVa, String datumKreiranja) {
		super();
		this.procenatPDVa = procenatPDVa;
		this.datumKreiranja = datumKreiranja;
	}

}

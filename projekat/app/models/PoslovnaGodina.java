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
public class PoslovnaGodina extends Model {

	@Required
	@MinSize(4)
	@MaxSize(4)
	@Column(columnDefinition = "varchar(4)")
	public String brojGodine;

	@Column(columnDefinition = "character(1)")
	public Character aktivna;

	@ManyToOne
	public Preduzece preduzece;

	@OneToMany(mappedBy = "poslovnaGodina")
	public List<Faktura> fakture;
	
	@OneToMany(mappedBy = "poslovnaGodina")
	public List<Narudzba> narudzbe;

	public PoslovnaGodina(String brojGodine, Character aktivna, Preduzece preduzece) {
		super();
		this.brojGodine = brojGodine;
		this.aktivna = aktivna;
		this.preduzece = preduzece;
	}

}

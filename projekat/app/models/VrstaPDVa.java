package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class VrstaPDVa extends Model {

	@Required
	@MinSize(3)
	@MaxSize(40)
	@Column(columnDefinition = "varchar(40)")
	public String nazivVrstePDva;

	@OneToMany(mappedBy = "vrstaPDVa")
	public List<StopaPDVa> stopePDVa;

	@OneToMany(mappedBy = "vrstaPDVa")
	public List<Grupa> grupe;

	public VrstaPDVa(String nazivVrstePDva) {
		super();
		this.nazivVrstePDva = nazivVrstePDva;
	}

}

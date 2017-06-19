package controllers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.Cenovnik;
import models.KatalogRobeIUsluga;
import models.StavkaCenovnika;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
@Check("administrator")
public class StavkeCenovnika extends Controller {

	/**
	 * Metoda se pokrece pri prvom ucitavanju stranice. Stranica se vraca u
	 * pocetno stanje. Brisu se sesije, i iscitavaju se svi podaci.
	 */
	public static void show() {
		validation.clear();
		clearSession();

		session.put("idKataloga", "null");
		session.put("idCenovnika", "null");

		session.put("mode", "edit");
		String mode = session.get("mode");

		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<Cenovnik> cenovnici = Cenovnici.checkCache();
		List<StavkaCenovnika> stavkeCenovnika = checkCache();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		render(kataloziRobeIUsluga, cenovnici, stavkeCenovnika, nadredjeneForme, mode);

	}

	/**
	 * Metoda pomocu koje se vrsi promena stanja.
	 * 
	 * @param mode
	 *            U renderTemplate ga je obavezno proslediti, jer se na osnovu
	 *            njega na view delu menja action.
	 */
	public static void changeMode(String mode) {
		clearSession();

		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		session.put("mode", mode);

		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<Cenovnik> cenovnici = Cenovnici.checkCache();
		List<StavkaCenovnika> stavkeCenovnika = fillList();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		renderTemplate("StavkeCenovnika/show.html", kataloziRobeIUsluga, cenovnici, stavkeCenovnika, nadredjeneForme,
				mode);

	}

	public static void create(StavkaCenovnika stavkaCenovnika, Long cenovnik, Long katalogRobeIUsluga) {
		validation.clear();
		clearSession();

		validation.valid(stavkaCenovnika);

		session.put("mode", "add");
		String mode = session.get("mode");

		List<StavkaCenovnika> stavkeCenovnika = null;
		List<Cenovnik> cenovnici = Cenovnici.checkCache();
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		if (!validation.hasErrors()) {
			stavkeCenovnika = StavkaCenovnika.findAll();

			// kada je disable- ovan combobox ne pokupi vrednost
			Cenovnik findCenovnik = null;
			if (cenovnik == null) {
				Long id = Long.parseLong(session.get("idCenovnika"));
				findCenovnik = Cenovnik.findById(id);
			} else {
				findCenovnik = Cenovnik.findById(cenovnik);
			}

			KatalogRobeIUsluga findKatalog = null;
			if (katalogRobeIUsluga == null) {
				Long id = Long.parseLong(session.get("idKataloga"));
				findKatalog = KatalogRobeIUsluga.findById(id);
			} else {
				findKatalog = KatalogRobeIUsluga.findById(katalogRobeIUsluga);
			}

			stavkaCenovnika.cenovnik = findCenovnik;
			stavkaCenovnika.katalogRobeIUsluga = findKatalog;

			stavkaCenovnika.save();
			stavkeCenovnika.add(stavkaCenovnika);
			Cache.set("stavkeCenovnika", stavkeCenovnika);

			Long idd = stavkaCenovnika.id;

			stavkeCenovnika.clear();
			stavkeCenovnika = fillList();

			validation.clear();

			renderTemplate("StavkeCenovnika/show.html", stavkeCenovnika, cenovnici, kataloziRobeIUsluga,
					nadredjeneForme, idd, mode);
		} else {
			validation.keep();

			stavkeCenovnika = fillList();

			session.put("cenaSC", null);

			renderTemplate("StavkeCenovnika/show.html", stavkeCenovnika, cenovnici, kataloziRobeIUsluga,
					nadredjeneForme, mode);
		}

	}

	public static void edit(StavkaCenovnika stavkaCenovnika, Long cenovnik, Long katalogRobeIUsluga) {
		validation.clear();
		clearSession();

		validation.valid(stavkaCenovnika);

		session.put("mode", "add");
		String mode = session.get("mode");

		List<StavkaCenovnika> stavkeCenovnika = null;
		List<Cenovnik> cenovnici = Cenovnici.checkCache();
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		if (!validation.hasErrors()) {
			stavkeCenovnika = StavkaCenovnika.findAll();

			// kada je disable- ovan combobox ne pokupi vrednost
			Cenovnik findCenovnik = null;
			if (cenovnik == null) {
				Long id = Long.parseLong(session.get("idCenovnika"));
				findCenovnik = Cenovnik.findById(id);
			} else {
				findCenovnik = Cenovnik.findById(cenovnik);
			}

			KatalogRobeIUsluga findKatalog = null;
			if (katalogRobeIUsluga == null) {
				Long id = Long.parseLong(session.get("idKataloga"));
				findKatalog = KatalogRobeIUsluga.findById(id);
			} else {
				findKatalog = KatalogRobeIUsluga.findById(katalogRobeIUsluga);
			}

			stavkaCenovnika.cenovnik = findCenovnik;
			stavkaCenovnika.katalogRobeIUsluga = findKatalog;

			for (StavkaCenovnika tmp : stavkeCenovnika) {
				if (tmp.id == stavkaCenovnika.id) {
					tmp.cena = stavkaCenovnika.cena;
					tmp.cenovnik = stavkaCenovnika.cenovnik;
					tmp.katalogRobeIUsluga = stavkaCenovnika.katalogRobeIUsluga;
					tmp.save();
					break;
				}
			}

			Cache.set("stavkeCenovnika", stavkeCenovnika);

			stavkeCenovnika.clear();
			stavkeCenovnika = fillList();

			validation.clear();
		} else {
			validation.keep();

			stavkeCenovnika = fillList();

			session.put("idSC", null);
			session.put("cenaSC", null);
		}

		renderTemplate("StavkeCenovnika/show.html", stavkeCenovnika, cenovnici, kataloziRobeIUsluga, nadredjeneForme,
				mode);
	}

	public static void filter(StavkaCenovnika stavkaCenovnika) {
		List<StavkaCenovnika> stavkeCenovnika = StavkaCenovnika.find("byCena", stavkaCenovnika.cena).fetch();

		List<Cenovnik> cenovnici = Cenovnici.checkCache();
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		session.put("mode", "edit");
		String mode = session.get("mode");

		renderTemplate("StavkeCenovnika/show.html", stavkeCenovnika, cenovnici, kataloziRobeIUsluga, nadredjeneForme,
				mode);
	}

	public static void delete(Long id) {
		String mode = session.get("mode");

		List<StavkaCenovnika> stavkeCenovnika = checkCache();
		List<Cenovnik> cenovnici = Cenovnici.checkCache();
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		StavkaCenovnika stavkaCenovnika = StavkaCenovnika.findById(id);
		Long idd = null;

		for (int i = 1; i < stavkeCenovnika.size(); i++) {
			if (stavkeCenovnika.get(i).id == id) {
				StavkaCenovnika prethodni = stavkeCenovnika.get(i - 1);
				idd = prethodni.id;
			}
		}
		stavkaCenovnika.delete();

		Cache.set("stavkeCenovnika", stavkeCenovnika);

		stavkeCenovnika.clear();
		stavkeCenovnika = fillList();

		renderTemplate("StavkeCenovnika/show.html", stavkeCenovnika, cenovnici, kataloziRobeIUsluga, nadredjeneForme,
				idd, mode);
	}

	/** Prelazak na nadredjenu formu */
	public static void pickup(String forma) {
		if (forma.equals("cenovnik")) {
			Cenovnici.show("edit");
		} else if (forma.equals("katalogRobeIUsluga")) {
			KataloziRobeIUsluga.show();
		}
	}

	public static void refresh() {
		List<Cenovnik> cenovnici = Cenovnici.checkCache();
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<StavkaCenovnika> stavkeCenovnika = fillList();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		String mode = session.get("mode");

		renderTemplate("StavkeCenovnika/show.html", stavkeCenovnika, cenovnici, kataloziRobeIUsluga, nadredjeneForme,
				mode);
	}

	/** Pomocna metoda za brisanje podataka iz sesije. */
	public static boolean clearSession() {
		session.put("idSC", null);
		session.put("cenaSC", null);
		return true;
	}

	/**
	 * Pomocna metoda za proveru da li su zeljeni podaci (koji treba da budu
	 * dostupni kroz vise zahteva) i dalje na Cache-u.
	 */
	public static List<StavkaCenovnika> checkCache() {
		List<StavkaCenovnika> stavkeCenovnika = (List<StavkaCenovnika>) Cache.get("stavkeCenovnika");

		if ((stavkeCenovnika == null) || (stavkeCenovnika.size() == 0)) {
			stavkeCenovnika = StavkaCenovnika.findAll();
			Cache.set("stavkeCenovnika", stavkeCenovnika);
		}

		return stavkeCenovnika;
	}

	/**
	 * Pomocna metoda koja popunjava listu stavki cenovnika. Vrsi se provera da
	 * li se radi nextForm mehanizam ili normalno ucitavanje stranice. Ukoliko
	 * se radi nextForm, potrebno je vratiti samo one stavnke cenovnika u okviru
	 * izabranog kataloga.
	 */
	public static List<StavkaCenovnika> fillList() {
		List<StavkaCenovnika> stavkeCenovnika = null;

		if (!session.get("idKataloga").equals("null")) {
			Long id = Long.parseLong(session.get("idKataloga"));
			stavkeCenovnika = KataloziRobeIUsluga.findStavkeCenovnika(id);
		} else if (!session.get("idCenovnika").equals("null")) {
			Long id = Long.parseLong(session.get("idCenovnika"));
			stavkeCenovnika = Cenovnici.findStavkeCenovnika(id);
		} else {
			stavkeCenovnika = checkCache();
		}

		return stavkeCenovnika;
	}

	/**
	 * Pomocna metoda koja vraca listu nadredjenih formi.
	 * 
	 * @see <a href=
	 *      "http://tutorials.jenkov.com/java-reflection/annotations.html"> Java
	 *      Reflection - Annotations</a>
	 */
	public static List<String> getForeignKeysFieldsManyToOne() {
		Class stavkaCenovnikaClass = StavkaCenovnika.class;
		Field[] fields = stavkaCenovnikaClass.getFields();

		List<String> povezaneForme = new ArrayList<String>();

		for (int i = 0; i < fields.length; i++) {
			Annotation annotation = fields[i].getAnnotation(ManyToOne.class);
			if (annotation instanceof ManyToOne) {
				povezaneForme.add(fields[i].getName());
			}
		}

		return povezaneForme;
	}

}

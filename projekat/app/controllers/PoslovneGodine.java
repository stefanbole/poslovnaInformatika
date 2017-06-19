package controllers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.Faktura;
import models.Narudzba;
import models.PoslovnaGodina;
import models.PoslovniPartner;
import models.Preduzece;
import play.cache.Cache;
import play.mvc.Controller;

public class PoslovneGodine extends Controller {

	/**
	 * Metoda se pokrece pri prvom ucitavanju stranice. Stranica se vraca u
	 * pocetno stanje. Brisu se sesije, i iscitavaju se svi podaci.
	 */
	public static void show() {
		validation.clear();
		clearSession();

		session.put("idPreduzeca", "null");

		String mode = "edit";

		session.put("mode", "edit");
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<PoslovnaGodina> poslovneGodine = checkCache();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		List<String> povezaneForme = getForeignKeysFields();

		render(preduzeca, poslovneGodine, nadredjeneForme, povezaneForme, mode);
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

		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<PoslovnaGodina> poslovneGodine = fillList();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		List<String> povezaneForme = getForeignKeysFields();

		renderTemplate("PoslovneGodine/show.html", preduzeca, poslovneGodine, nadredjeneForme, povezaneForme, mode);
	}

	public static void edit(PoslovnaGodina poslovnaGodina, Long preduzece) {
		validation.clear();
		validation.valid(poslovnaGodina);

		clearSession();

		session.put("mode", "edit");
		String mode = session.get("mode");

		List<PoslovnaGodina> poslovneGodine = null;
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		List<String> povezaneForme = getForeignKeysFields();

		if (!validation.hasErrors()) {
			poslovneGodine = PoslovnaGodina.findAll();

			// kada je disable- ovan combobox ne pokupi vrednost
			Preduzece findPreduzece = null;
			if (preduzece == null) {
				Long id = Long.parseLong(session.get("idPreduzeca"));
				findPreduzece = Preduzece.findById(id);
			} else {
				findPreduzece = Preduzece.findById(preduzece);
			}

			for (PoslovnaGodina tmp : poslovneGodine) {
				if (tmp.id == poslovnaGodina.id) {
					tmp.brojGodine = poslovnaGodina.brojGodine;
					tmp.aktivna = poslovnaGodina.aktivna;
					tmp.preduzece = findPreduzece;
					tmp.save();
					break;
				}
			}

			Cache.set("poslovneGodine", poslovneGodine);

			poslovneGodine.clear();
			poslovneGodine = fillList();

			validation.clear();

		} else {
			validation.keep();

			poslovneGodine = fillList();

			session.put("idPG", poslovnaGodina.id);
			session.put("brojGodine", poslovnaGodina.brojGodine);

		}
		renderTemplate("PoslovneGodine/show.html", poslovneGodine, preduzeca, nadredjeneForme, povezaneForme, mode);
	}

	public static void create(PoslovnaGodina poslovnaGodina, Long preduzece) {
		validation.clear();
		clearSession();

		validation.valid(poslovnaGodina);

		session.put("mode", "add");
		String mode = session.get("mode");

		List<PoslovnaGodina> poslovneGodine = null;
		List<Preduzece> preduzeca = Preduzeca.checkCache();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		List<String> povezaneForme = getForeignKeysFields();

		if (!validation.hasErrors()) {
			poslovneGodine = PoslovnaGodina.findAll();

			// kada je disable- ovan combobox ne pokupi vrednost
			Preduzece findPreduzece = null;
			if (preduzece == null) {
				Long id = Long.parseLong(session.get("idPreduzeca"));
				findPreduzece = Preduzece.findById(id);
			} else {
				findPreduzece = Preduzece.findById(preduzece);
			}

			poslovnaGodina.preduzece = findPreduzece;

			poslovnaGodina.save();
			poslovneGodine.add(poslovnaGodina);
			Cache.set("poslovneGodine", poslovneGodine);

			Long idd = poslovnaGodina.id;

			poslovneGodine.clear();
			poslovneGodine = fillList();

			validation.clear();

			renderTemplate("PoslovneGodine/show.html", poslovneGodine, preduzeca, nadredjeneForme, povezaneForme, idd,
					mode);
		} else {
			validation.keep();

			poslovneGodine = fillList();

			session.put("brojGodine", poslovnaGodina.brojGodine);

			renderTemplate("PoslovneGodine/show.html", poslovneGodine, preduzeca, nadredjeneForme, povezaneForme, mode);
		}
	}

	public static void delete(Long id) {
		String mode = session.get("mode");

		List<PoslovnaGodina> poslovneGodine = checkCache();
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		List<String> povezaneForme = getForeignKeysFields();

		PoslovnaGodina poslovnaGodina = PoslovnaGodina.findById(id);
		Long idd = null;

		for (int i = 1; i < poslovneGodine.size(); i++) {
			if (poslovneGodine.get(i).id == id) {
				PoslovnaGodina prethodni = poslovneGodine.get(i - 1);
				idd = prethodni.id;
			}
		}
		poslovnaGodina.delete();

		Cache.set("poslovneGodine", poslovneGodine);

		poslovneGodine.clear();
		poslovneGodine = fillList();

		renderTemplate("PoslovneGodine/show.html", poslovneGodine, preduzeca, nadredjeneForme, povezaneForme, idd,
				mode);
	}

	public static void filter(PoslovnaGodina poslovnaGodina) {
		List<PoslovnaGodina> poslovneGodine = PoslovnaGodina
				.find("byBrojGodineLikeAndAktivna", "%" + poslovnaGodina.brojGodine + "%", poslovnaGodina.aktivna)
				.fetch();

		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		List<String> povezaneForme = getForeignKeysFields();

		session.put("mode", "edit");
		String mode = session.get("mode");

		renderTemplate("PoslovneGodine/show.html", poslovneGodine, preduzeca, nadredjeneForme, povezaneForme, mode);
	}

	public static void refresh() {
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<PoslovnaGodina> poslovneGodine = fillList();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		List<String> povezaneForme = getForeignKeysFields();

		String mode = session.get("mode");

		renderTemplate("PoslovneGodine/show.html", preduzeca, poslovneGodine, nadredjeneForme, povezaneForme, mode);
	}

	/**
	 * Prelazak na nadredjenu formu
	 * 
	 * @param forma
	 *            Izabrana forma na koju se prelazi
	 */
	public static void pickup(String forma) {
		if (forma.equals("preduzece")) {
			Preduzeca.show("edit");
		}
	}

	/**
	 * Metoda koja na osnovu oznacene poslovneGodine, prelazi na odabarnu formu,
	 * i prikazuje samo podatke izabrane forme u okviru te poslovneGodine.
	 * 
	 * @param id
	 *            ID oznacene poslovneGodine
	 * @param forma
	 *            Izabrana forma na koju se prelazi
	 */
	public static void nextForm(Long id, String forma) {
		session.put("idPoslovneGodine", id);
		clearSession();

		if (forma.equals("fakture")) {
			List<Preduzece> preduzeca = Preduzeca.checkCache();
			List<PoslovnaGodina> poslovneGodine = checkCache();
			List<PoslovniPartner> poslovniParneri = PoslovniPartneri.checkCache();

			List<String> nadredjeneForme = Fakture.getForeignKeysFieldsManyToOne();
			List<String> povezaneForme = Fakture.getForeignKeysFields();

			List<Faktura> fakture = findFakture(id);
			renderTemplate("Fakture/show.html", poslovneGodine, preduzeca, fakture, povezaneForme, poslovniParneri,
					nadredjeneForme);
		}
	}

	/**
	 * Pomocna metoda za proveru da li su zeljeni podaci (koji treba da budu
	 * dostupni kroz vise zahteva) i dalje na Cache-u.
	 */
	public static List<PoslovnaGodina> checkCache() {
		List<PoslovnaGodina> poslovneGodine = (List<PoslovnaGodina>) Cache.get("poslovneGodine");

		if ((poslovneGodine == null) || (poslovneGodine.size() == 0)) {
			poslovneGodine = PoslovnaGodina.findAll();
			Cache.set("poslovneGodine", poslovneGodine);
		}

		return poslovneGodine;
	}

	/** Pomocna metoda za brisanje podataka iz sesije. */
	public static boolean clearSession() {
		session.put("idPG", null);
		session.put("brojGodine", null);
		return true;
	}

	/**
	 * Pomocna metoda koja popunjava listu poslovnih godina. Vrsi se provera da
	 * li se radi nextForm mehanizam ili normalno ucitavanje stranice. Ukoliko
	 * se radi nextForm, potrebno je vratiti samo one poslovne godine u okviru
	 * izabranog preduzeca.
	 */
	public static List<PoslovnaGodina> fillList() {
		List<PoslovnaGodina> poslovneGodine = null;
		if (!session.get("idPreduzeca").equals("null")) {
			Long id = Long.parseLong(session.get("idPreduzeca"));
			poslovneGodine = Preduzeca.findPoslovneGodine(id);
		} else {
			poslovneGodine = checkCache();
		}

		return poslovneGodine;
	}

	public static List<Faktura> findFakture(Long idPoslovneGodine) {
		List<Faktura> faktureAll = Faktura.findAll();
		List<Faktura> fakture = new ArrayList<>();

		for (Faktura sc : faktureAll) {
			if (sc.poslovniPartner.id == idPoslovneGodine) {
				fakture.add(sc);
			}
		}

		return fakture;
	}

	/**
	 * Pomocna metoda koja vraca listu povezanih formi.
	 * 
	 * @see <a href=
	 *      "http://tutorials.jenkov.com/java-reflection/annotations.html"> Java
	 *      Reflection - Annotations</a>
	 */
	public static List<String> getForeignKeysFields() {
		Class poslovnaGodinaClass = PoslovnaGodina.class;
		Field[] fields = poslovnaGodinaClass.getFields();

		List<String> povezaneForme = new ArrayList<String>();

		for (int i = 0; i < fields.length; i++) {
			Annotation annotation = fields[i].getAnnotation(OneToMany.class);
			if (annotation instanceof OneToMany) {
				povezaneForme.add(fields[i].getName());
			}
		}

		return povezaneForme;
	}

	/**
	 * Pomocna metoda koja vraca listu nadredjenih formi.
	 * 
	 * @see <a href=
	 *      "http://tutorials.jenkov.com/java-reflection/annotations.html"> Java
	 *      Reflection - Annotations</a>
	 */
	public static List<String> getForeignKeysFieldsManyToOne() {
		Class poslovnaGodinaClass = PoslovnaGodina.class;
		Field[] fields = poslovnaGodinaClass.getFields();

		List<String> povezaneForme = new ArrayList<String>();

		for (int i = 0; i < fields.length; i++) {
			Annotation annotation = fields[i].getAnnotation(ManyToOne.class);
			if (annotation instanceof ManyToOne) {
				povezaneForme.add(fields[i].getName());
			}
		}

		return povezaneForme;
	}

	public static List<PoslovnaGodina> findAktivnePoslovneGodine() {
		List<PoslovnaGodina> poslovneGodineAll = PoslovnaGodina.findAll();
		List<PoslovnaGodina> aktivneGodine = new ArrayList<>();

		for (PoslovnaGodina pg : poslovneGodineAll) {
			if (pg.aktivna.equals('D')) {
				aktivneGodine.add(pg);
			}
		}

		return aktivneGodine;
	}
	
public static List<Narudzba> findNarudzbe(Long idPoslovneGodine){
		
		List<Narudzba> narudzbeAll = Narudzba.findAll();
		List<Narudzba> narudzbe = new ArrayList<>();
		
		for (Narudzba narudzba : narudzbeAll) {
			if (narudzba.poslovnaGodina.id == idPoslovneGodine) {
				narudzbe.add(narudzba);
			}
		}
		
		return narudzbe;
	}

}

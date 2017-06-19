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

/*
 * 
 * Kad bude odradjen model Narudzbe, odradi sve isto za narudzbu kao za fakturu.
 * 
 * 
 * 
 * */

public class PoslovniPartneri extends Controller {

	public static void show() {
		validation.clear();
		clearSession();

		// za next mehanizam
		session.put("idPoslovnogPartnera", "null");

		session.put("idPreduzeca", "null"); // ManyToOne
		session.put("mode", "edit");
		String mode = session.get("mode");

		if (mode == null || mode.equals("")) {
			mode = "edit";
		}

		session.put("mode", mode);

		List<PoslovniPartner> poslovniPartneri = checkCache();
		List<String> povezaneForme = getForeignKeysFields();
		List<Preduzece> preduzeca = Preduzeca.checkCache();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		render(poslovniPartneri, povezaneForme, mode, preduzeca, nadredjeneForme);
	}

	public static void changeMode(String mode) {

		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		session.put("mode", mode);
		clearSession();

		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<PoslovniPartner> poslovniPartneri = fillList();
		List<String> povezaneForme = getForeignKeysFields();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		renderTemplate("PoslovniPartneri/show.html", preduzeca, povezaneForme, poslovniPartneri, nadredjeneForme, mode);
	}

	public static List<String> getForeignKeysFields() {
		Class poslovniPartnerClass = PoslovniPartner.class;
		Field[] fields = poslovniPartnerClass.getFields();

		List<String> povezaneForme = new ArrayList<String>();

		for (int i = 0; i < fields.length; i++) {
			Annotation annotation = fields[i].getAnnotation(OneToMany.class);
			if (annotation instanceof OneToMany) {
				povezaneForme.add(fields[i].getName());
			}
		}

		return povezaneForme;
	}

	public static List<PoslovniPartner> checkCache() {
		List<PoslovniPartner> poslovniPartneri = (List<PoslovniPartner>) Cache.get("poslovniPartneri");

		if ((poslovniPartneri == null) || (poslovniPartneri.size() == 0)) {
			poslovniPartneri = PoslovniPartner.findAll();
			Cache.set("poslovniPartneri", poslovniPartneri);
		}

		return poslovniPartneri;
	}

	public static void edit(PoslovniPartner poslovniPartner, Long preduzece) {

		validation.clear();
		validation.valid(poslovniPartner);
		clearSession();

		session.put("mode", "edit");
		String mode = session.get("mode");
		List<String> povezaneForme = getForeignKeysFields();

		List<Preduzece> preduzeca = Preduzeca.checkCache();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		List<PoslovniPartner> poslovniPartneri = null;
		if (!validation.hasErrors()) {
			poslovniPartneri = PoslovniPartner.findAll();

			Preduzece findPreduzece = null;
			if (preduzece == null) {
				Long id = Long.parseLong(session.get("idPreduzeca"));
				findPreduzece = Preduzece.findById(id);
			} else {
				findPreduzece = Preduzece.findById(preduzece);
			}

			for (PoslovniPartner tmp : poslovniPartneri) {
				if (tmp.id == poslovniPartner.id) {
					tmp.naziv = poslovniPartner.naziv;
					tmp.adresa = poslovniPartner.adresa;
					tmp.mesto = poslovniPartner.mesto;
					tmp.pib = poslovniPartner.pib;
					tmp.preduzece = findPreduzece;
					tmp.tekuciRacun = poslovniPartner.tekuciRacun;
					tmp.telefon = poslovniPartner.telefon;
					tmp.vrsta = poslovniPartner.vrsta;

					tmp.save();
					break;
				}
			}

			Cache.set("poslovniPartneri", poslovniPartneri);

			poslovniPartneri.clear();
			poslovniPartneri = fillList();
			validation.clear();

		} else {
			poslovniPartneri = checkCache();

			validation.keep();

			session.put("idPP", poslovniPartner.id);
			session.put("nazivPP", poslovniPartner.naziv);
			session.put("adresaPP", poslovniPartner.adresa);
			session.put("mestoPP", poslovniPartner.mesto);
			session.put("pibPP", poslovniPartner.pib);
			session.put("tekuciRacunPP", poslovniPartner.tekuciRacun);
			session.put("telefonPP", poslovniPartner.telefon);

		}

		renderTemplate("poslovniPartneri/show.html", poslovniPartneri, povezaneForme, nadredjeneForme, preduzeca, mode);
	}

	public static void create(PoslovniPartner poslovniPartner, Long preduzece) {
		validation.clear();
		validation.valid(poslovniPartner);
		clearSession();

		session.put("mode", "add");
		String mode = session.get("mode");

		List<PoslovniPartner> poslovniPartneri = null;
		List<Preduzece> preduzeca = Preduzeca.checkCache();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		List<String> povezaneForme = getForeignKeysFields();

		if (!validation.hasErrors()) {
			poslovniPartneri = PoslovniPartner.findAll();

			Preduzece findPreduzece = null;
			if (preduzece == null) {
				Long id = Long.parseLong(session.get("idPreduzeca"));
				findPreduzece = Preduzece.findById(id);
			} else {
				findPreduzece = Preduzece.findById(preduzece);
			}

			poslovniPartner.preduzece = findPreduzece;
			System.out.println("preduzece: " + poslovniPartner.preduzece.naziv);

			poslovniPartner.save();
			poslovniPartneri.add(poslovniPartner);
			Cache.set("poslovniPartneri", poslovniPartneri);

			// potrebno da bi se selektovao dodati red na view delu
			Long idd = poslovniPartner.id;

			poslovniPartneri.clear();
			poslovniPartneri = fillList();

			validation.clear();

			renderTemplate("poslovniPartneri/show.html", poslovniPartneri, povezaneForme, nadredjeneForme, preduzeca,
					idd, mode);
		} else {
			validation.keep();

			poslovniPartneri = fillList();
			// potrebno da bi se ispisla greska
			session.put("nazivPP", poslovniPartner.naziv);
			session.put("adresaPP", poslovniPartner.adresa);
			session.put("mestoPP", poslovniPartner.mesto);
			session.put("pibPP", poslovniPartner.pib);
			session.put("tekuciRacunPP", poslovniPartner.tekuciRacun);
			session.put("telefonPP", poslovniPartner.telefon);

			renderTemplate("poslovniPartneri/show.html", poslovniPartneri, povezaneForme, nadredjeneForme, preduzeca,
					mode);
		}
	}

	public static void filter(PoslovniPartner poslovniPartner) {
		List<PoslovniPartner> poslovniPartneri = PoslovniPartner.find(
				"byNazivLikeAndVrstaLikeAndMestoLikeAndAdresaLikeAndTelefonLike", "%" + poslovniPartner.naziv + "%",
				"%" + poslovniPartner.vrsta + "%", "%" + poslovniPartner.mesto + "%",
				"%" + poslovniPartner.adresa + "%", "%" + poslovniPartner.telefon + "%"

		).fetch();

		session.put("mode", "edit");
		String mode = session.get("mode");

		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<String> povezaneForme = getForeignKeysFields();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		renderTemplate("poslovniPartneri/show.html", poslovniPartneri, preduzeca, povezaneForme, nadredjeneForme, mode);
	}

	public static boolean clearSession() {
		session.put("idPP", null);
		session.put("nazivPP", null);
		session.put("mestoPP", null);
		session.put("adresaPP", null);
		session.put("telefonPP", null);
		session.put("pibPP", null);
		session.put("tekuciRacunPP", null);

		return true;

	}

	public static List<PoslovniPartner> fillList() {
		List<PoslovniPartner> poslovniPartneri = null;
		if (!session.get("idPreduzeca").equals("null")) {
			Long id = Long.parseLong(session.get("idPreduzeca"));
			poslovniPartneri = Preduzeca.findPoslovniPartneri(id);
		} else {
			poslovniPartneri = checkCache();
		}

		return poslovniPartneri;
	}

	public static void nextForm(Long id, String forma) {
		session.put("idPoslovnogPartnera", id);
		session.put("idPreduzeca", "null");
		session.put("idPoslovneGodine", "null");

		clearSession();

		if (forma.equals("fakture")) {
			List<PoslovniPartner> poslovniPartneri = checkCache();
			List<Faktura> fakture = findFakture(id);
			List<Preduzece> preduzeca = Preduzeca.checkCache();
			List<PoslovnaGodina> poslovneGodine = PoslovneGodine.checkCache();

			List<String> nadredjeneForme = Fakture.getForeignKeysFieldsManyToOne();
			List<String> povezaneForme = Fakture.getForeignKeysFields();

			renderTemplate("Fakture/show.html", poslovniPartneri, fakture, preduzeca, poslovneGodine, povezaneForme,
					nadredjeneForme);
		}

		// DODATI ZA NARUDZBU
	}

	public static List<Faktura> findFakture(Long idPoslovnogPartnera) {
		List<Faktura> faktureAll = Faktura.findAll();
		List<Faktura> fakture = new ArrayList<>();

		for (Faktura sc : faktureAll) {
			if (sc.poslovniPartner.id == idPoslovnogPartnera) {
				fakture.add(sc);
			}
		}

		return fakture;
	}

	public static void delete(Long id) {
		String mode = session.get("mode");

		List<PoslovniPartner> poslovniPartneri = checkCache();
		List<String> povezaneForme = getForeignKeysFields();
		List<Preduzece> preduzeca = Preduzeca.checkCache();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		PoslovniPartner poslovniPartner = PoslovniPartner.findById(id);
		Long idd = null;

		for (int i = 1; i < poslovniPartneri.size(); i++) {
			if (poslovniPartneri.get(i).id == id) {
				PoslovniPartner prethodni = poslovniPartneri.get(i - 1);
				idd = prethodni.id;
			}
		}
		poslovniPartner.delete();

		poslovniPartneri = PoslovniPartner.findAll();
		Cache.set("poslovniPartneri", poslovniPartneri);

		renderTemplate("poslovniPartneri/show.html", poslovniPartneri, preduzeca, idd, povezaneForme, nadredjeneForme,
				mode);
	}

	public static void refresh() {
		String mode = session.get("mode");

		List<PoslovniPartner> poslovniPartneri = checkCache();
		List<String> povezaneForme = getForeignKeysFields();
		List<Preduzece> preduzeca = Preduzeca.checkCache();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		renderTemplate("poslovniPartneri/show.html", preduzeca, poslovniPartneri, povezaneForme, nadredjeneForme, mode);
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
	 * Pomocna metoda koja vraca listu nadredjenih formi.
	 * 
	 * @see <a href=
	 *      "http://tutorials.jenkov.com/java-reflection/annotations.html"> Java
	 *      Reflection - Annotations</a>
	 */
	public static List<String> getForeignKeysFieldsManyToOne() {
		Class klasa = PoslovniPartner.class;
		Field[] fields = klasa.getFields();

		List<String> povezaneForme = new ArrayList<String>();

		for (int i = 0; i < fields.length; i++) {
			Annotation annotation = fields[i].getAnnotation(ManyToOne.class);
			if (annotation instanceof ManyToOne) {
				povezaneForme.add(fields[i].getName());
			}
		}

		return povezaneForme;
	}

	public static List<PoslovniPartner> findKupci() {
		List<PoslovniPartner> poslovniPartneriAll = PoslovniPartner.findAll();
		List<PoslovniPartner> kupci = new ArrayList<>();

		for (PoslovniPartner pp : poslovniPartneriAll) {
			if (pp.vrsta.equals("K") || pp.vrsta.equals("KD")) {
				kupci.add(pp);
			}
		}

		return kupci;
	}
	
public static List<Narudzba> findNarudzbe(Long idPoslovnogPartnera){
		
		List<Narudzba> narudzbeAll = Narudzba.findAll();
		List<Narudzba> narudzbe = new ArrayList<>();
		
		for (Narudzba narudzba : narudzbeAll) {
			if (narudzba.poslovniPartner.id == idPoslovnogPartnera) {
				narudzbe.add(narudzba);
			}
		}
		
		return narudzbe;
	}
}

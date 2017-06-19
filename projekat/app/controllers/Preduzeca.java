package controllers;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.OneToMany;

import models.Faktura;
import models.Grupa;
import models.Narudzba;
import models.PoslovnaGodina;
import models.PoslovniPartner;
import models.Preduzece;
import models.VrstaPDVa;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import play.Play;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
@Check("administrator")
public class Preduzeca extends Controller {

	/**
	 * Stranica se vraca u pocetno stanje. Iz sesije se ukljanja ID, koji je bio
	 * potreban za nextForm mehanizam. Vrsi se ponovno ucitavanje podataka.
	 */
	public static void show(String mode) {
		validation.clear();
		clearSession();

		// potrebno za nextForm mehanizam
		session.put("idPreduzeca", "null");

		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		session.put("mode", mode);

		List<Preduzece> preduzeca = checkCache();
		List<String> povezaneForme = getForeignKeysFields();

		render(preduzeca, povezaneForme, mode);
	}

	public static void edit(Preduzece preduzece) {
		validation.clear();
		validation.valid(preduzece);
		clearSession();

		session.put("mode", "edit");
		String mode = session.get("mode");
		List<String> povezaneForme = getForeignKeysFields();

		List<Preduzece> preduzeca = null;

		if (!validation.hasErrors()) {
			preduzeca = Preduzece.findAll();

			for (Preduzece tmp : preduzeca) {
				if (tmp.id == preduzece.id) {
					tmp.naziv = preduzece.naziv;
					tmp.pib = preduzece.pib;
					tmp.mesto = preduzece.mesto;
					tmp.adresa = preduzece.adresa;
					tmp.telefon = preduzece.telefon;
					tmp.maticniBroj = preduzece.maticniBroj;
					tmp.tekuciRacun = preduzece.tekuciRacun;
					tmp.save();
					break;
				}
			}

			Cache.set("preduzeca", preduzeca);

			validation.clear();

		} else {
			preduzeca = checkCache();

			validation.keep();

			session.put("idPred", preduzece.id);
			session.put("nazivPred", preduzece.naziv);
			session.put("pibPred", preduzece.pib);
			session.put("mestoPred", preduzece.mesto);
			session.put("adresaPred", preduzece.adresa);
			session.put("telefonPred", preduzece.telefon);
			session.put("maticniBrojPred", preduzece.maticniBroj);
			session.put("tekuciRacunPred", preduzece.tekuciRacun);
		}

		renderTemplate("preduzeca/show.html", preduzeca, povezaneForme, mode);
	}

	public static void create(Preduzece preduzece) {
		validation.clear();
		validation.valid(preduzece);

		session.put("mode", "add");
		String mode = session.get("mode");
		List<String> povezaneForme = getForeignKeysFields();

		List<Preduzece> preduzeca = checkCache();

		if (!validation.hasErrors()) {
			preduzece.save();
			preduzeca.add(preduzece);
			Cache.set("preduzeca", preduzeca);

			// potrebno da bi se selektovao dodati red na view delu
			Long idd = preduzece.id;

			validation.clear();
			clearSession();

			renderTemplate("preduzeca/show.html", preduzeca, povezaneForme, idd, mode);
		} else {
			validation.keep();

			// potrebno da bi se ispisla greska
			session.put("nazivPred", preduzece.naziv);
			session.put("pibPred", preduzece.pib);
			session.put("mestoPred", preduzece.mesto);
			session.put("adresaPred", preduzece.adresa);
			session.put("telefonPred", preduzece.telefon);
			session.put("maticniBrojPred", preduzece.maticniBroj);
			session.put("tekuciRacunPred", preduzece.tekuciRacun);

			renderTemplate("preduzeca/show.html", preduzeca, povezaneForme, mode);
		}
	}

	public static void filter(Preduzece preduzece) {
		List<Preduzece> preduzeca = Preduzece
				.find("byNazivLikeAndPibLikeAndMestoLikeAndAdresaLikeAndTelefonLikeAndMaticniBrojLikeAndTekuciRacunLike",
						"%" + preduzece.naziv + "%", "%" + preduzece.pib + "%", "%" + preduzece.mesto + "%",
						"%" + preduzece.adresa + "%", "%" + preduzece.telefon + "%", "%" + preduzece.maticniBroj + "%",
						"%" + preduzece.tekuciRacun + "%")
				.fetch();

		session.put("mode", "edit");
		String mode = session.get("mode");

		List<String> povezaneForme = getForeignKeysFields();

		renderTemplate("preduzeca/show.html", preduzeca, povezaneForme, mode);
	}

	public static void delete(Long id) {
		String mode = session.get("mode");

		List<Preduzece> preduzeca = checkCache();
		List<String> povezaneForme = getForeignKeysFields();

		Preduzece preduzece = Preduzece.findById(id);
		Long idd = null;

		for (int i = 1; i < preduzeca.size(); i++) {
			if (preduzeca.get(i).id == id) {
				Preduzece prethodni = preduzeca.get(i - 1);
				idd = prethodni.id;
			}
		}
		preduzece.delete();

		preduzeca = Preduzece.findAll();
		Cache.set("preduzeca", preduzeca);

		renderTemplate("preduzeca/show.html", preduzeca, idd, povezaneForme, mode);
	}

	/**
	 * Metoda koja na osnovu oznacenog preduzeca, prelazi na odabarnu formu, i
	 * prikazuje samo podatke izabrane forme u okviru tog preduzeca.
	 * 
	 * @param id
	 *            ID oznacenog Preduzeca
	 * @param forma
	 *            Izabrana forma na koju se prelazi
	 */
	public static void nextForm(Long id, String forma) {

		session.put("idPreduzeca", id);
		clearSession();

		if (forma.equals("poslovneGodine")) {
			List<Preduzece> preduzeca = checkCache();

			List<PoslovnaGodina> poslovneGodine = findPoslovneGodine(id);
			List<String> nadredjeneForme = PoslovneGodine.getForeignKeysFieldsManyToOne();
			List<String> povezaneForme = PoslovneGodine.getForeignKeysFields();

			renderTemplate("PoslovneGodine/show.html", poslovneGodine, preduzeca, nadredjeneForme, povezaneForme);
		} else if (forma.equals("grupe")) {
			List<Grupa> grupe = Grupe.checkCache();
			grupe= findGrupe(id);
			List<Preduzece> preduzeca = checkCache();
			List<VrstaPDVa> vrstePDVa = VrstePDVa.checkCache();

			List<String> povezaneForme = Grupe.getForeignKeysFields();
			List<String> nadredjeneForme = Grupe.getForeignKeysFieldsManyToOne();

			renderTemplate("Grupe/show.html", grupe, preduzeca, vrstePDVa, povezaneForme, nadredjeneForme);
		} else if (forma.equals("poslovniPartneri")) {

			List<Preduzece> preduzeca = checkCache();
			List<String> nadredjeneForme = PoslovniPartneri.getForeignKeysFieldsManyToOne();
			List<String> povezaneForme = PoslovniPartneri.getForeignKeysFields();

			renderTemplate("PoslovniPartneri/show.html", preduzeca, nadredjeneForme, povezaneForme);
		} else if (forma.equals("fakture")) {
			List<Preduzece> preduzeca = checkCache();
			List<PoslovnaGodina> poslovneGodine = PoslovneGodine.checkCache();
			List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.checkCache();

			List<Faktura> fakture = findFakture(id);
			List<String> nadredjeneForme = Fakture.getForeignKeysFieldsManyToOne();
			List<String> povezaneForme = Fakture.getForeignKeysFields();

			renderTemplate("Fakture/show.html", fakture, preduzeca, nadredjeneForme, poslovneGodine, povezaneForme,
					poslovniPartneri);
		}else if (forma.equals("narudzbe")) {
			List<Preduzece> preduzeca = checkCache();
			List<PoslovnaGodina> poslovneGodine = PoslovneGodine.checkCache();
			List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.checkCache();

			List<Narudzba> narudzbe = findNarudzbe(id);
			List<String> nadredjeneForme = Fakture.getForeignKeysFieldsManyToOne();
			List<String> povezaneForme = Fakture.getForeignKeysFields();

			renderTemplate("Narudzbe/show.html", narudzbe, preduzeca, nadredjeneForme, poslovneGodine, povezaneForme,
					poslovniPartneri);
		}

	}

	public static void refresh() {
		String mode = session.get("mode");

		List<Preduzece> preduzeca = checkCache();
		List<String> povezaneForme = getForeignKeysFields();

		renderTemplate("preduzeca/show.html", preduzeca, povezaneForme, mode);
	}

	public void exportToPdf(Long id) {
		try {

			Preduzece p = Preduzece.findById(id);
			String nazivPreduzeca = p.naziv;

			Map parametri = new HashMap<>();
			parametri.put("nazivPreduzeca", nazivPreduzeca);

			String file = imeIzvestaja("KIF.jasper");
			JasperPrint jp = JasperFillManager.fillReport(file, parametri, play.db.DB.getConnection());
			JasperExportManager.exportReportToPdfFile(jp, imeIzvestaja("KIF") + id + ".pdf");

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			List<Preduzece> preduzeca = checkCache();
			List<String> povezaneForme = getForeignKeysFields();
			String mode = "edit";
			renderTemplate("Preduzeca/show.html", preduzeca, povezaneForme, mode);
		}
	}

	public static String imeIzvestaja(String ime) {
		return Play.applicationPath + File.separator + "jaspers" + File.separator + ime;
	}

	/** Pomocna metoda za brisanje podataka iz sesije. */
	private static boolean clearSession() {
		session.put("idPred", null);
		session.put("nazivPred", null);
		session.put("pibPred", null);
		session.put("mestoPred", null);
		session.put("adresaPred", null);
		session.put("telefonPred", null);
		session.put("maticniBrojPred", null);
		session.put("tekuciRacunPred", null);

		return true;
	}

	/**
	 * Pomocna metoda za proveru da li su zeljeni podaci (koji treba da budu
	 * dostupni kroz vise zahteva) i dalje na Cache-u.
	 */
	public static List<Preduzece> checkCache() {
		List<Preduzece> preduzeca = (List<Preduzece>) Cache.get("preduzeca");

		if (preduzeca == null) {
			preduzeca = Preduzece.findAll();
			Cache.set("preduzeca", preduzeca);
		}

		return preduzeca;
	}

	/**
	 * Pomocna metoda za nextForm mehanizam. Nalazi sve poslovne godine u okviru
	 * jednog izabranog preduzeca.
	 * 
	 * @param idPreduzeca
	 *            ID izabranog preduzeca
	 */
	public static List<PoslovnaGodina> findPoslovneGodine(Long idPreduzeca) {
		List<PoslovnaGodina> poslovneGodineAll = PoslovnaGodina.findAll();
		List<PoslovnaGodina> poslovneGodine = new ArrayList<>();

		for (PoslovnaGodina pg : poslovneGodineAll) {
			if (pg.preduzece.id == idPreduzeca) {
				poslovneGodine.add(pg);
			}
		}

		return poslovneGodine;
	}

	/**
	 * Pomocna metoda koja vraca listu povezanih formi.
	 * 
	 * @see <a href=
	 *      "http://tutorials.jenkov.com/java-reflection/annotations.html"> Java
	 *      Reflection - Annotations</a>
	 */
	public static List<String> getForeignKeysFields() {
		Class preduzeceClass = Preduzece.class;
		Field[] fields = preduzeceClass.getFields();

		List<String> povezaneForme = new ArrayList<String>();

		for (int i = 0; i < fields.length; i++) {
			Annotation annotation = fields[i].getAnnotation(OneToMany.class);
			if (annotation instanceof OneToMany) {
				povezaneForme.add(fields[i].getName());
			}
		}

		return povezaneForme;
	}

	public static List<PoslovniPartner> findPoslovniPartneri(Long idPreduzeca) {
		List<PoslovniPartner> poslovniPartneriAll = PoslovniPartner.findAll();
		List<PoslovniPartner> poslovniPartneri = new ArrayList<>();

		for (PoslovniPartner sp : poslovniPartneriAll) {
			if (sp.preduzece.id == idPreduzeca) {
				poslovniPartneri.add(sp);
			}
		}

		return poslovniPartneri;
	}

	public static List<Faktura> findFakture(Long idPreduzeca) {
		List<Faktura> faktureAll = Faktura.findAll();
		List<Faktura> fakture = new ArrayList<>();

		for (Faktura f : faktureAll) {
			if (f.preduzece.id == idPreduzeca) {
				fakture.add(f);
			}
		}

		return fakture;
	}
	
	public static List<Grupa> findGrupe(Long idPreduzeca){
		List<Grupa> grupeAll = Grupa.findAll();
		List<Grupa> grupe = new ArrayList<>();
		
		for(Grupa grupa:grupeAll){
			if(grupa.preduzece.id == idPreduzeca){
				grupe.add(grupa);
			}
		}
		
		return grupe;
	}
	
	public static List<Narudzba> findNarudzbe(Long idPreduzeca){
		
		List<Narudzba> narudzbeAll = Narudzba.findAll();
		List<Narudzba> narudzbe = new ArrayList<>();
		
		for (Narudzba narudzba : narudzbeAll) {
			if (narudzba.preduzece.id == idPreduzeca) {
				narudzbe.add(narudzba);
			}
		}
		
		return narudzbe;
	}

}

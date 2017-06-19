package controllers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.OneToMany;

import models.Cenovnik;
import models.Grupa;
import models.KatalogRobeIUsluga;
import models.PoslovnaGodina;
import models.Preduzece;
import models.StavkaCenovnika;
import models.StopaPDVa;
import models.VrstaPDVa;
import play.cache.Cache;
import play.mvc.Controller;

public class VrstePDVa extends Controller {

	public static void show(String mode) {
		validation.clear();
		clearSession();

		// potrebno za nextForm mehanizam
		session.put("idVrstePDVa", "null");

		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		session.put("mode", mode);

		List<VrstaPDVa> vrstePDVa = checkCache();
		List<String> povezaneForme = getForeignKeysFields();

		render(vrstePDVa, povezaneForme, mode);
	}

	public static void edit(VrstaPDVa vrstaPDVa) {
		validation.clear();
		validation.valid(vrstaPDVa);
		clearSession();

		session.put("mode", "edit");
		String mode = session.get("mode");
		List<String> povezaneForme = getForeignKeysFields();

		List<VrstaPDVa> vrstePDVa = null;

		if (!validation.hasErrors()) {
			vrstePDVa = VrstaPDVa.findAll();

			for (VrstaPDVa tmp : vrstePDVa) {
				if (tmp.id == vrstaPDVa.id) {
					tmp.nazivVrstePDva = vrstaPDVa.nazivVrstePDva;
					tmp.save();
					break;
				}
			}

			Cache.set("vrstePDVa", vrstePDVa);

			validation.clear();

		} else {
			vrstePDVa = checkCache();

			validation.keep();

			session.put("idVPDVa", vrstaPDVa.id);
			session.put("nazivVPDVa", vrstaPDVa.nazivVrstePDva);

		}

		renderTemplate("vrstePDVa/show.html", vrstePDVa, povezaneForme, mode);
	}

	public static void create(VrstaPDVa vrstaPDVa) {
		validation.clear();
		validation.valid(vrstaPDVa);

		session.put("mode", "add");
		String mode = session.get("mode");

		List<String> povezaneForme = getForeignKeysFields();

		List<VrstaPDVa> vrstePDVa = checkCache();

		if (!validation.hasErrors()) {
			vrstaPDVa.save();
			vrstePDVa.add(vrstaPDVa);
			Cache.set("vrstePDVa", vrstePDVa);

			// potrebno da bi se selektovao dodati red na view delu
			Long idd = vrstaPDVa.id;

			validation.clear();
			clearSession();

			renderTemplate("vrstePDVa/show.html", vrstePDVa, povezaneForme, idd, mode);
		} else {
			validation.keep();

			// potrebno da bi se ispisla greska
			session.put("nazivVPDVa", vrstaPDVa.nazivVrstePDva);

			renderTemplate("vrstePDVa/show.html", vrstePDVa, povezaneForme, mode);
		}
	}

	public static void filter(VrstaPDVa vrstaPDVa) {
		List<VrstaPDVa> vrstePDVa = VrstaPDVa.find("byNazivVrstePDvaLike", "%" + vrstaPDVa.nazivVrstePDva).fetch();
		session.put("mode", "edit");
		String mode = session.get("mode");

		List<String> povezaneForme = getForeignKeysFields();

		renderTemplate("vrstePDVa/show.html", vrstePDVa, povezaneForme, mode);
	}

	public static void delete(Long id) {

		String mode = session.get("mode");

		List<VrstaPDVa> vrstePDVa = checkCache();
		List<String> povezaneForme = getForeignKeysFields();

		VrstaPDVa vrstaPDVa = VrstaPDVa.findById(id);
		Long idd = null;

		for (int i = 1; i < vrstePDVa.size(); i++) {
			if (vrstePDVa.get(i).id == id) {
				VrstaPDVa prethodni = vrstePDVa.get(i - 1);
				idd = prethodni.id;
			}
		}
		vrstaPDVa.delete();

		vrstePDVa = VrstaPDVa.findAll();
		Cache.set("vrstePDVa", vrstePDVa);

		renderTemplate("vrstePDVa/show.html", vrstePDVa, idd, povezaneForme, mode);

	}

	// imam listu stopa i listu grupa
	public static void nextForm(Long id, String forma) {
		session.put("idVrstePDVa", id);
		session.put("idPreduzeca", "null"); // jer ima ManyToOne u Grupa
		clearSession();

		if (forma.equals("stopePDVa")) {
			List<VrstaPDVa> vrstePDVa = checkCache();
			List<StopaPDVa> stopePDVa = findStopePDVa(id);

			List<String> nadredjeneForme = StopePDVa.getForeignKeysFieldsManyToOne();

			renderTemplate("StopePDVa/show.html", stopePDVa, vrstePDVa, nadredjeneForme);

		}

		if (forma.equals("grupe")) {
			List<VrstaPDVa> vrstePDVa = checkCache();
			List<Grupa> grupe = findGrupe(id);
			List<Preduzece> preduzeca = Preduzeca.checkCache();

			renderTemplate("Grupe/show.html", grupe, vrstePDVa, preduzeca);

		}

	}

	public static void refresh() {

		String mode = session.get("mode");
		List<VrstaPDVa> vrstePDVa = checkCache();
		List<String> povezaneForme = getForeignKeysFields();

		renderTemplate("vrstePDVa/show.html", vrstePDVa, povezaneForme, mode);
	}

	/**
	 * Pomocne metode.
	 */
	public static List<VrstaPDVa> checkCache() {
		List<VrstaPDVa> vrstePDVa = (List<VrstaPDVa>) Cache.get("vrstePDVa");

		if (vrstePDVa == null) {
			vrstePDVa = VrstaPDVa.findAll();
			Cache.set("vrstePDVa", vrstePDVa);
		}

		return vrstePDVa;
	}

	private static boolean clearSession() {
		session.put("idVPDVa", null);
		session.put("nazivVPDVa", null);

		return true;
	}

	public static List<StopaPDVa> findStopePDVa(Long idVrstePDVa) {
		List<StopaPDVa> stopePDVaAll = StopaPDVa.findAll();
		List<StopaPDVa> stopePDVa = new ArrayList<>();

		for (StopaPDVa sp : stopePDVaAll) {
			if (sp.vrstaPDVa.id == idVrstePDVa) {
				stopePDVa.add(sp);
			}
		}

		return stopePDVa;
	}

	public static List<Grupa> findGrupe(Long idVrstePDVa) {
		List<Grupa> grupeAll = Grupa.findAll();
		List<Grupa> grupe = new ArrayList<>();

		for (Grupa gr : grupeAll) {
			if (gr.vrstaPDVa.id == idVrstePDVa) {
				grupe.add(gr);
			}
		}
		return grupe;
	}

	/**
	 * Pomocna metoda koja vraca listu povezanih formi.
	 * 
	 * @see <a href=
	 *      "http://tutorials.jenkov.com/java-reflection/annotations.html"> Java
	 *      Reflection - Annotations</a>
	 */
	public static List<String> getForeignKeysFields() {
		Class vrstaPDVaClass = VrstaPDVa.class;
		Field[] fields = vrstaPDVaClass.getFields();

		List<String> povezaneForme = new ArrayList<String>();

		for (int i = 0; i < fields.length; i++) {
			Annotation annotation = fields[i].getAnnotation(OneToMany.class);
			if (annotation instanceof OneToMany) {
				povezaneForme.add(fields[i].getName());
			}
		}

		return povezaneForme;
	}

}

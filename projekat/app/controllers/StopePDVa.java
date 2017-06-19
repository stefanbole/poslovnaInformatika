package controllers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.ManyToOne;

import models.Cenovnik;
import models.Faktura;
import models.PoslovnaGodina;
import models.StopaPDVa;
import models.VrstaPDVa;
import play.cache.Cache;
import play.mvc.Controller;

public class StopePDVa extends Controller {

	public static void show() {
		validation.clear();
		clearSession();

		session.put("idVrstePDVa", "null"); // ManyToOne u klasi VrstaPDVa
		session.put("mode", "edit");
		String mode = session.get("mode");

		List<VrstaPDVa> vrstePDVa = VrstePDVa.checkCache();
		List<StopaPDVa> stopePDVa = checkCache();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		render(vrstePDVa, stopePDVa, nadredjeneForme, mode);
	}

	public static void changeMode(String mode) {
		clearSession();

		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		session.put("mode", mode);

		List<VrstaPDVa> vrstePDVa = VrstePDVa.checkCache();
		List<StopaPDVa> stopePDVa = fillList();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		renderTemplate("StopePDVa/show.html", vrstePDVa, stopePDVa, nadredjeneForme, mode);
	}

	public static void edit(StopaPDVa stopaPDVa, Long vrstaPDVa) {
		validation.clear();
		clearSession();

		validation.valid(stopaPDVa);

		session.put("mode", "edit");
		String mode = session.get("mode");

		List<StopaPDVa> stopePDVa = null;
		List<VrstaPDVa> vrstePDVa = VrstePDVa.checkCache();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		if (!validation.hasErrors()) {
			stopePDVa = StopaPDVa.findAll();

			VrstaPDVa findVrstaPDVa = null;
			if (vrstaPDVa == null) {
				Long id = Long.parseLong(session.get("idVrstePDVa"));
				findVrstaPDVa = VrstaPDVa.findById(id);
			} else {
				findVrstaPDVa = VrstaPDVa.findById(vrstaPDVa);
			}

			for (StopaPDVa tmp : stopePDVa) {
				if (tmp.id == stopaPDVa.id) {
					tmp.vrstaPDVa = findVrstaPDVa;
					tmp.procenatPDVa = stopaPDVa.procenatPDVa;
					tmp.datumKreiranja = stopaPDVa.datumKreiranja;
					tmp.save();
					break;
				}
			}

			Cache.set("stopePDVa", stopePDVa);

			stopePDVa.clear();
			stopePDVa = fillList();
			validation.clear();

		} else {
			validation.keep();
			stopePDVa = fillList();

			session.put("idSPDVa", stopaPDVa.id);
			session.put("procenatSPDVa", stopaPDVa.procenatPDVa);

		}

		renderTemplate("StopePDVa/show.html", stopePDVa, vrstePDVa, nadredjeneForme, mode);
	}

	public static void create(StopaPDVa stopaPDVa, Long vrstaPDVa) {
		validation.clear();
		clearSession();

		validation.valid(stopaPDVa);

		session.put("mode", "add");
		String mode = session.get("mode");

		List<StopaPDVa> stopePDVa = null;
		List<VrstaPDVa> vrstePDVa = VrstePDVa.checkCache();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		if (!validation.hasErrors()) {

			stopePDVa = StopaPDVa.findAll();

			VrstaPDVa findVrstaPDVa = null;
			if (vrstaPDVa == null) {
				Long id = Long.parseLong(session.get("idVrstePDVa"));
				findVrstaPDVa = VrstaPDVa.findById(id);
			} else {
				findVrstaPDVa = VrstaPDVa.findById(vrstaPDVa);
			}

			stopaPDVa.vrstaPDVa = findVrstaPDVa;

			stopaPDVa.save();
			stopePDVa.add(stopaPDVa);
			Cache.set("stopePDVa", stopePDVa);

			Long idd = stopaPDVa.id;

			stopePDVa.clear();
			stopePDVa = fillList();
			validation.clear();

			renderTemplate("StopePDVa/show.html", stopePDVa, vrstePDVa, idd, nadredjeneForme, mode);
		} else

		{
			validation.keep();

			stopePDVa = fillList();

			session.put("procenatSPDVa", stopaPDVa.procenatPDVa);

			renderTemplate("StopePDVa/show.html", stopePDVa, vrstePDVa, nadredjeneForme, mode);
		}
	}

	public static void filter(StopaPDVa stopaPDVa) {

		List<StopaPDVa> stopePDVa = StopaPDVa.find("byProcenatPDVa", stopaPDVa.procenatPDVa).fetch();
		// List<StopaPDVa> stopePDVa = StopaPDVa.find("procenatPDVa = ?",
		// stopaPDVa.procenatPDVa).fetch();
		List<VrstaPDVa> vrstePDVa = VrstePDVa.checkCache();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		session.put("mode", "edit");
		String mode = session.get("mode");

		renderTemplate("StopePDVa/show.html", stopePDVa, vrstePDVa, mode, nadredjeneForme);
	}

	public static void delete(Long id) {
		String mode = session.get("mode");

		List<VrstaPDVa> vrstePDVa = VrstePDVa.checkCache();
		List<StopaPDVa> stopePDVa = checkCache();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		StopaPDVa stopaPDVa = StopaPDVa.findById(id);
		Long idd = null;

		for (int i = 1; i < stopePDVa.size(); i++) {
			if (stopePDVa.get(i).id == id) {
				StopaPDVa prethodni = stopePDVa.get(i - 1);
				idd = prethodni.id;
			}
		}
		stopaPDVa.delete();

		Cache.set("stopePDVa", stopePDVa);

		stopePDVa.clear();
		stopePDVa = fillList();
		renderTemplate("StopePDVa/show.html", stopePDVa, vrstePDVa, idd, mode, nadredjeneForme);

	}

	public static void refresh() {
		List<StopaPDVa> stopePDVa = checkCache();
		List<VrstaPDVa> vrstePDVa = VrstePDVa.checkCache();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		String mode = session.get("mode");
		renderTemplate("StopePDVa/show.html", stopePDVa, vrstePDVa, nadredjeneForme, mode);

	}

	/** Prelazak na nadredjenu formu */
	public static void pickup(String forma) {
		if (forma.equals("vrstaPDVa")) {
			VrstePDVa.show("edit");
		}
	}

	/**
	 * Pomocne metode.
	 */
	public static List<StopaPDVa> checkCache() {
		List<StopaPDVa> stopePDVa = (List<StopaPDVa>) Cache.get("stopePDVa");

		if ((stopePDVa == null) || (stopePDVa.size() == 0)) {
			stopePDVa = StopaPDVa.findAll();
			Cache.set("stopePDVa", stopePDVa);
		}

		return stopePDVa;
	}

	private static boolean clearSession() {
		session.put("idSPDVa", null);
		session.put("procenatSPDVa", null);

		return true;
	}

	public static List<StopaPDVa> fillList() {
		List<StopaPDVa> stopePDVa = null;
		if (!session.get("idVrstePDVa").equals("null")) {
			Long id = Long.parseLong(session.get("idVrstePDVa"));
			stopePDVa = VrstePDVa.findStopePDVa(id);
		} else {
			stopePDVa = checkCache();
		}

		return stopePDVa;
	}

	/**
	 * Pomocna metoda koja vraca listu nadredjenih formi.
	 * 
	 * @see <a href=
	 *      "http://tutorials.jenkov.com/java-reflection/annotations.html"> Java
	 *      Reflection - Annotations</a>
	 */
	public static List<String> getForeignKeysFieldsManyToOne() {
		Class klasa = StopaPDVa.class;
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

}

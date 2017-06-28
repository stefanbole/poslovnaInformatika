package controllers;



import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.Cenovnik;
import models.Faktura;
import models.PoslovnaGodina;
import models.PoslovniPartner;
import models.Preduzece;
import models.StavkaCenovnika;
import models.StavkaFakture;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import play.Play;
import play.cache.Cache;
import play.mvc.Controller;

public class Fakture extends Controller {

	public static void show() {
		validation.clear();
		clearSession();

		session.put("idFakture", "null"); // za next mehanizam

		session.put("idPoslovnogPartnera", "null"); // ManyToOne
		session.put("idPoslovneGodine", "null");// ManyToOne
		session.put("idPreduzeca", "null"); // ManyToOne

		session.put("mode", "edit");
		String mode = session.get("mode");

		if (mode == null || mode.equals("")) {
			mode = "edit";
		}

		session.put("mode", mode);

		List<String> povezaneForme = getForeignKeysFields();
		List<Faktura> fakture = Fakture.checkCache();

		List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.findKupci();
		List<PoslovnaGodina> poslovneGodine = PoslovneGodine.findAktivnePoslovneGodine();
		List<Preduzece> preduzeca = Preduzeca.checkCache();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		render(povezaneForme, fakture, mode, poslovniPartneri, poslovneGodine, preduzeca, nadredjeneForme);

	}

	public static void changeMode(String mode) {
		if (mode == null || mode.equals("")) {
			mode = "edit";
		}

		session.put("mode", mode);

		List<String> povezaneForme = getForeignKeysFields();
		List<Faktura> fakture = Fakture.checkCache();
		List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.findKupci();
		List<PoslovnaGodina> poslovneGodine = PoslovneGodine.findAktivnePoslovneGodine();
		List<Preduzece> preduzeca = Preduzeca.checkCache();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		renderTemplate("Fakture/show.html", povezaneForme, fakture, mode, poslovniPartneri, nadredjeneForme,
				poslovneGodine, preduzeca);

	}

	public static void edit(Faktura faktura, Long poslovniPartner, Long poslovnaGodina, Long preduzece) {
		validation.clear();
		validation.valid(faktura);
		clearSession();

		session.put("mode", "edit");
		String mode = session.get("mode");

		List<String> povezaneForme = getForeignKeysFields();
		List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.findKupci();
		List<PoslovnaGodina> poslovneGodine = PoslovneGodine.findAktivnePoslovneGodine();
		List<Preduzece> preduzeca = Preduzeca.checkCache();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		List<Faktura> fakture = null;

		if (!validation.hasErrors()) {
			fakture = Faktura.findAll();

			Preduzece findPreduzece = null;
			if (preduzece == null) {
				Long id = Long.parseLong(session.get("idPreduzeca"));
				findPreduzece = Preduzece.findById(id);
			} else {
				findPreduzece = Preduzece.findById(preduzece);
			}

			PoslovnaGodina findPoslovnaGodina = null;
			if (poslovnaGodina == null) {
				Long id = Long.parseLong(session.get("idPoslovneGodine"));
				findPoslovnaGodina = PoslovnaGodina.findById(id);
			} else {
				findPoslovnaGodina = PoslovnaGodina.findById(poslovnaGodina);
			}

			PoslovniPartner findPoslovniPartner = null;
			if (poslovniPartner == null) {
				Long id = Long.parseLong(session.get("idPoslovnogPartnera"));
				findPoslovniPartner = PoslovniPartner.findById(id);
			} else {
				findPoslovniPartner = PoslovniPartner.findById(poslovniPartner);
			}

			for (Faktura tmp : fakture) {
				if (tmp.id == faktura.id) {
					tmp.datumFakture = faktura.datumFakture;
					tmp.datumValute = faktura.datumValute;

					tmp.preduzece = findPreduzece;
					tmp.poslovnaGodina = findPoslovnaGodina;
					tmp.poslovniPartner = findPoslovniPartner;

					tmp.save();
					break;
				}
			}

			Cache.set("fakture", fakture);

			validation.clear();

			fakture.clear();
			fakture = fillList();
		} else {
			fakture = checkCache();

			validation.keep();

			session.put("idF", faktura.id);
			session.put("datumFakture", faktura.datumFakture);
			session.put("datumValute", faktura.datumValute);
			session.put("ukupnoOsnovica", faktura.ukupnoOsnovica);
			session.put("ukupnoPDV", faktura.ukupnoPDV);
			session.put("ukupnoZaPlacanje", faktura.ukupnoZaPlacanje);
		}

		renderTemplate("fakture/show.html", poslovniPartneri, povezaneForme, preduzeca, poslovneGodine, nadredjeneForme,
				fakture, mode);
	}

	public static void create(Faktura faktura, Long poslovniPartner, Long poslovnaGodina, Long preduzece)
			throws ParseException {
		validation.clear();
		validation.valid(faktura);
		clearSession();

		session.put("mode", "add");
		String mode = session.get("mode");
		List<String> povezaneForme = getForeignKeysFields();

		List<Faktura> fakture = null;
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.findKupci();
		List<PoslovnaGodina> poslovneGodine = PoslovneGodine.findAktivnePoslovneGodine();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		if (!validation.hasErrors()) {

			fakture = Faktura.findAll();

			Preduzece findPreduzece = null;
			//if (preduzece == null) {
			//	Long id = Long.parseLong(session.get("idPreduzeca"));
				findPreduzece = Preduzece.findById(new Long(1));
			//} else {
			//	findPreduzece = Preduzece.findById(preduzece);
			//}

			PoslovnaGodina findPoslovnaGodina = null;
			if (poslovnaGodina == null) {
				Long id = Long.parseLong(session.get("idPoslovneGodine"));
				findPoslovnaGodina = PoslovnaGodina.findById(id);
			} else {
				findPoslovnaGodina = PoslovnaGodina.findById(poslovnaGodina);
			}

			PoslovniPartner findPoslovniPartner = null;
			if (poslovniPartner == null) {
				Long id = Long.parseLong(session.get("idPoslovnogPartnera"));
				findPoslovniPartner = PoslovniPartner.findById(id);
			} else {
				findPoslovniPartner = PoslovniPartner.findById(poslovniPartner);
			}

			faktura.brojFakture = incrementBrojFakture();
			faktura.preduzece = findPreduzece;
			faktura.poslovnaGodina = findPoslovnaGodina;
			faktura.poslovniPartner = findPoslovniPartner;

			//faktura.save();
			List<StavkaFakture> stavkeFakture = faktura.stavkeFakture;
			faktura.ukupnoOsnovica = 0;
			faktura.ukupnoPDV = 0;
			faktura.ukupnoZaPlacanje = 0;

			if (stavkeFakture != null) {
				for (StavkaFakture sf : stavkeFakture) {
					faktura.ukupnoOsnovica += sf.osnovicaZaPDV;
					faktura.ukupnoPDV += sf.iznosPDVa;
					faktura.ukupnoZaPlacanje += sf.ukupno;
				}

			}

			faktura.save();
			fakture.add(faktura);
			Cache.set("fakture", fakture);

			fakture.clear();
			fakture = fillList();
			validation.clear();

			nextForm(faktura.id, "stavkeFakture");
		} else {
			validation.keep();

			fakture = fillList();

			// potrebno da bi se ispisala greska
			session.put("idF", faktura.id);
			session.put("datumFakture", faktura.datumFakture);
			session.put("datumValute", faktura.datumValute);
			session.put("ukupnoOsnovica", faktura.ukupnoOsnovica);
			session.put("ukupnoPDV", faktura.ukupnoPDV);
			session.put("ukupnoZaPlacanje", faktura.ukupnoZaPlacanje);

			renderTemplate("fakture/show.html", poslovniPartneri, fakture, poslovneGodine, povezaneForme, preduzeca,
					nadredjeneForme, mode);
		}
	}

	public static void filter(Faktura faktura) {
		List<Faktura> fakture = Faktura.find("byDatumFaktureLikeAndDatumValute", "%" + faktura.datumFakture + "%",
				"%" + faktura.datumValute + "%").fetch();

		session.put("mode", "edit");
		String mode = session.get("mode");

		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<PoslovnaGodina> poslovneGodine = PoslovneGodine.findAktivnePoslovneGodine();
		List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.findKupci();

		List<String> povezaneForme = getForeignKeysFields();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		renderTemplate("Fakture/show.html", fakture, preduzeca, povezaneForme, poslovniPartneri, poslovneGodine, mode,
				nadredjeneForme);

	}

	public static void delete(Long id) {
		String mode = session.get("mode");

		List<Faktura> fakture = checkCache();
		List<String> povezaneForme = getForeignKeysFields();
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.findKupci();
		List<PoslovnaGodina> poslovneGodine = PoslovneGodine.findAktivnePoslovneGodine();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		Faktura faktura = Faktura.findById(id);
		Long idd = null;

		for (int i = 1; i < fakture.size(); i++) {
			if (fakture.get(i).id == id) {
				Faktura prethodni = fakture.get(i - 1);
				idd = prethodni.id;
			}
		}
		faktura.delete();

		fakture = Faktura.findAll();
		Cache.set("fakture", fakture);

		renderTemplate("fakture/show.html", fakture, poslovneGodine, poslovniPartneri, preduzeca, nadredjeneForme, idd,
				povezaneForme, mode);
	}

	public static void nextForm(Long id, String forma) throws ParseException {
		session.put("idFakture", id);
		session.put("idPreduzeca", "null");
		session.put("idPoslovneGodine", "null");
		session.put("idPoslovnogPartnera", "null");

		if (forma.equals("stavkeFakture")) {
			List<Faktura> fakture = checkCache();
			List<StavkaFakture> stavkeFakture = findStavkeFakture(id);
			List<Preduzece> preduzeca = Preduzeca.checkCache();
			List<PoslovnaGodina> poslovneGodine = PoslovneGodine.findAktivnePoslovneGodine();
			List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.findKupci();

			List<String> nadredjeneForme = StavkeFakture.getForeignKeysFieldsManyToOne();

			List<StavkaCenovnika> stavkeCenovnika = findStavkeCenovnika(id);

			String mode = "edit";
			session.put("mode", mode);

			StavkeFakture.clearSession();

			renderTemplate("StavkeFakture/show.html", fakture, stavkeFakture, preduzeca, poslovneGodine,
					poslovniPartneri, nadredjeneForme, stavkeCenovnika, mode);
		}

		// DODATI ZA NARUDZBU
	}

	public static void refresh() {
		String mode = session.get("mode");

		List<Faktura> fakture = checkCache();
		List<String> povezaneForme = getForeignKeysFields();
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.findKupci();
		List<PoslovnaGodina> poslovneGodine = PoslovneGodine.findAktivnePoslovneGodine();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		renderTemplate("fakture/show.html", fakture, poslovneGodine, preduzeca, poslovniPartneri, nadredjeneForme,
				povezaneForme, mode);
	}

	/** Prelazak na nadredjenu formu */
	public static void pickup(String forma) {
		if (forma.equals("poslovniPartner")) {
			PoslovniPartneri.show();
		} else if (forma.equals("poslovnaGodina")) {
			PoslovneGodine.show();
		} else if (forma.equals("preduzece")) {
			Preduzeca.show("edit");
		}
	}

	public void exportToPdf1(Long id) {
		try {

			Map parametri = new HashMap<>();
			parametri.put("idFakture", id);

			String file = imeIzvestaja1("faktura.jasper");

			if (play.db.DB.getConnection() == null) {
				System.out.println("NULL JE");
			}

			JasperPrint jp = JasperFillManager.fillReport(file, parametri, play.db.DB.getConnection());
			JasperExportManager.exportReportToPdfFile(jp, imeIzvestaja1("faktura") + id + ".pdf");

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			List<Faktura> fakture = checkCache();
			List<String> povezaneForme = getForeignKeysFields();
			String mode = "edit";
			renderTemplate("Fakture/show.html", fakture, povezaneForme, mode);
		}
	}

	public static String imeIzvestaja1(String ime) {
		return Play.applicationPath + File.separator + "jaspers" + File.separator + ime;
	}

	/** Pomocna metoda za brisanje podataka iz sesije. */
	private static boolean clearSession() {
		session.put("idF", null);
		session.put("datumFakture", null);
		session.put("datumValute", null);
		session.put("ukupnoOsnovica", null);
		session.put("ukupnoPDV", null);
		session.put("ukupnoZaPlacanje", null);

		return true;
	}

	public static List<Faktura> fillList() {
		List<Faktura> fakture = null;
		if (!session.get("idPreduzeca").equals("null")) {
			Long id = Long.parseLong(session.get("idPreduzeca"));
			fakture = Preduzeca.findFakture(id);
		} else if (!session.get("idPoslovnogPartnera").equals("null")) {
			Long id = Long.parseLong(session.get("idPoslovnogPartnera"));
			fakture = PoslovniPartneri.findFakture(id);
		} else if (!session.get("idPoslovneGodine").equals("null")) {
			Long id = Long.parseLong(session.get("idPoslovneGodine"));
			fakture = PoslovneGodine.findFakture(id);
		} else {
			fakture = checkCache();
		}

		return fakture;
	}

	public static int incrementBrojFakture() {
		List<Faktura> fakture = Faktura.findAll();
		int brojFakture = 0;
		if (fakture.size() > 0) {
			brojFakture = fakture.get(fakture.size() - 1).brojFakture;
			brojFakture++;
		} else {
			brojFakture = 1;
		}

		return brojFakture;
	}

	public static Date convertToDate(String receivedDate) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Date date = formatter.parse(receivedDate);
		return date;
	}

	public static List<StavkaCenovnika> findStavkeCenovnika(Long idFakture) throws ParseException {
		Faktura faktura = Faktura.findById(idFakture);
		String datumFakture = faktura.datumFakture;
		Date datumFaktureDate = convertToDate(datumFakture);

		List<Cenovnik> cenovniciSaDatumima = new ArrayList<>();
		List<Cenovnik> cenovnici = Cenovnici.checkCache();
		for (Cenovnik tmp : cenovnici) {
			String datumCenovnika = tmp.datumVazenja;
			Date datumCenovnikaDate = convertToDate(datumCenovnika);

			if (!datumCenovnikaDate.after(datumFaktureDate)) {
				cenovniciSaDatumima.add(tmp);
			}
		}

		List<Date> datumi = new ArrayList<>();
		// trazim cenovnik sa najvisim datumom
		for (Cenovnik tmp : cenovniciSaDatumima) {
			Date d = convertToDate(tmp.datumVazenja);
			datumi.add(d);
		}

		Collections.sort(datumi, new Comparator<Date>() {
			@Override
			public int compare(Date arg0, Date arg1) {
				return arg0.compareTo(arg1);
			}
		});

		// trazim stavke cenovnika
		List<StavkaCenovnika> stavkeCenovnika = new ArrayList<>();
		for (Cenovnik tmp : cenovniciSaDatumima) {
			String string = new SimpleDateFormat("MM/dd/yyyy").format(datumi.get(datumi.size() - 1));
			if (tmp.datumVazenja.equals(string)) {
				stavkeCenovnika = tmp.stavkeCenovnika;
			}
		}

		return stavkeCenovnika;
	}

	public static List<StavkaFakture> findStavkeFakture(Long idFakture) {
		List<StavkaFakture> stavkeFaktureAll = StavkaFakture.findAll();
		List<StavkaFakture> stavkeFakture = new ArrayList<>();

		for (StavkaFakture sc : stavkeFaktureAll) {
			if (sc.faktura.id == idFakture) {
				stavkeFakture.add(sc);
			}
		}

		return stavkeFakture;
	}

	public static List<String> getForeignKeysFields() {
		Class fakturaClass = Faktura.class;
		Field[] fields = fakturaClass.getFields();

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
		Class faktureClass = Faktura.class;
		Field[] fields = faktureClass.getFields();

		List<String> povezaneForme = new ArrayList<String>();

		for (int i = 0; i < fields.length; i++) {
			Annotation annotation = fields[i].getAnnotation(ManyToOne.class);
			if (annotation instanceof ManyToOne) {
				povezaneForme.add(fields[i].getName());
			}
		}

		return povezaneForme;
	}

	public static List<Faktura> checkCache() {

		List<Faktura> fakture = (List<Faktura>) Cache.get("fakture");

		if ((fakture == null) || (fakture.size() == 0)) {
			fakture = Faktura.findAll();
			Cache.set("fakture", fakture);
		}

		return fakture;
	}
	
	public static void exportToXml(Long id){
		
		System.out.println("STA JE DOSLO:"+id+"nesto");
		if(id != null){
			Faktura faktura = Faktura.findById(id);
			List<StavkaFakture> stavkeFakture = findStavkeFakture(id);
			
			saveToXml(faktura,stavkeFakture);
			
		}
		
		show();
	}
	
	private static void saveToXml(Faktura faktura, List<StavkaFakture> stavkeFakture){
		String brojFakture = Integer.toString(faktura.brojFakture);
		String poslovniPartner = faktura.poslovniPartner.naziv;
		String poslovnaGodina = faktura.poslovnaGodina.brojGodine;
		String nazivPreduzeca = faktura.preduzece.naziv;
		String datumFakture = faktura.datumFakture;
		String datumValute = faktura.datumValute;
		String ukupnoOsnovica = Float.toString(faktura.ukupnoOsnovica);
		String ukupnoPDV = Float.toString(faktura.ukupnoPDV);
		String ukupnoZaPlacanje = Float.toString(faktura.ukupnoZaPlacanje);
		
		try{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.newDocument();
	         
	        Element topElement = doc.createElement("faktura");
	         doc.appendChild(topElement);

	         Element rootElement = doc.createElement("podaci_Faktura");
	         topElement.appendChild(rootElement);
	         
	         Element preduzece = doc.createElement("preduzece");
	         Attr tipAtributa1 = doc.createAttribute("type");
	         tipAtributa1.setValue("faktura_preduzece");
	         preduzece.setAttributeNode(tipAtributa1);
	         preduzece.appendChild(doc.createTextNode(nazivPreduzeca));
	         rootElement.appendChild(preduzece);
	         
	         Element partner = doc.createElement("partner");
	         Attr tipAtributa2 = doc.createAttribute("type");
	         tipAtributa2.setValue("poslovni_partner");
	         partner.setAttributeNode(tipAtributa2);
	         partner.appendChild(doc.createTextNode(poslovniPartner));
	         rootElement.appendChild(partner);
	         
	         Element godina = doc.createElement("godina");
	         Attr tipAtributa3 = doc.createAttribute("type");
	         tipAtributa3.setValue("poslovna_godina");
	         godina.setAttributeNode(tipAtributa3);
	         godina.appendChild(doc.createTextNode(poslovnaGodina));
	         rootElement.appendChild(godina);
	         
	         Element broj = doc.createElement("broj");
	         Attr tipAtributa4 = doc.createAttribute("type");
	         tipAtributa4.setValue("broj_fakture");
	         broj.setAttributeNode(tipAtributa4);
	         broj.appendChild(doc.createTextNode(brojFakture));
	         rootElement.appendChild(broj);
	         
	         Element valuta = doc.createElement("valutaDatum");
	         Attr tipAtributa5 = doc.createAttribute("type");
	         tipAtributa5.setValue("datum_Valute");
	         valuta.setAttributeNode(tipAtributa5);
	         valuta.appendChild(doc.createTextNode(datumValute));
	         rootElement.appendChild(valuta);
	         
	         Element datum = doc.createElement("fakturaDatum");
	         Attr tipAtributa6 = doc.createAttribute("type");
	         tipAtributa6.setValue("datum_Fakture");
	         datum.setAttributeNode(tipAtributa6);
	         datum.appendChild(doc.createTextNode(datumFakture));
	         rootElement.appendChild(datum);
	         
	         Element osnovica = doc.createElement("ukupnaOsnovica");
	         Attr tipAtributa7 = doc.createAttribute("type");
	         tipAtributa7.setValue("ukupno_Osnovica");
	         osnovica.setAttributeNode(tipAtributa7);
	         osnovica.appendChild(doc.createTextNode(ukupnoOsnovica));
	         rootElement.appendChild(osnovica);
	         
	         Element pdv = doc.createElement("ukupnoPDVe");
	         Attr tipAtributa8 = doc.createAttribute("type");
	         tipAtributa8.setValue("ukupno_PDVe");
	         pdv.setAttributeNode(tipAtributa8);
	         pdv.appendChild(doc.createTextNode(ukupnoPDV));
	         rootElement.appendChild(pdv);
	         
	         Element ukupno = doc.createElement("ukupnoPlacanje");
	         Attr tipAtributa9 = doc.createAttribute("type");
	         tipAtributa9.setValue("ukupno_Placanje");
	         ukupno.setAttributeNode(tipAtributa9);
	         ukupno.appendChild(doc.createTextNode(ukupnoZaPlacanje));
	         rootElement.appendChild(ukupno);
	         
	         if(stavkeFakture!=null){
	        	 for(StavkaFakture sf: stavkeFakture){
	        		 Element stavka =  doc.createElement("stavka");
					 Attr attrStavka = doc.createAttribute("kolicina");
					 Attr attrStavkaUkupno = doc.createAttribute("ukupno");
					 Attr attrStavkaCena = doc.createAttribute("cena");
					 attrStavka.setValue(Integer.toString(sf.kolicina));
					 attrStavkaUkupno.setValue(Float.toString(sf.ukupno));
					 attrStavkaCena.setValue(Float.toString(sf.cena));
					 stavka.setAttributeNode(attrStavka);
					 stavka.setAttributeNode(attrStavkaUkupno);
					 stavka.setAttributeNode(attrStavkaCena);
					 topElement.appendChild(stavka);
	        	 }
	         }
	         
	         
	         TransformerFactory transformerFactory = TransformerFactory.newInstance();
	         Transformer transformer = transformerFactory.newTransformer();
	         DOMSource source = new DOMSource(doc);
	         StreamResult result = new StreamResult(new File("fakture.xml"));
	         transformer.transform(source, result);
	         
		} catch (Exception e) {
	         e.printStackTrace();
		 }
		
	}

}

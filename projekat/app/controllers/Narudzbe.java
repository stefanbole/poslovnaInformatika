package controllers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.Cenovnik;
import models.Faktura;
import models.KatalogRobeIUsluga;
import models.Narudzba;
import models.PoslovnaGodina;
import models.PoslovniPartner;
import models.Preduzece;
import models.StavkaCenovnika;
import models.StavkaFakture;
import models.StavkaNarudzbe;
import play.cache.Cache;
import play.mvc.Controller;

public class Narudzbe extends Controller {
	
	

	public static void show() {
		validation.clear();
		
		session.put("idNarudzbe", "null"); // 

		session.put("idPoslovnogPartnera", "null"); 
		session.put("idPoslovneGodine", "null");
		session.put("idPreduzeca", "null"); 
		session.put("idKataloga", "null");
		session.put("mode", "edit");
		session.put("fakturisna","null");
		String mode = session.get("mode");

		if (mode == null || mode.equals("")) {
			mode = "edit";
		}

	
		
		session.put("mode", mode);
		
		

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		List<String> povezaneForme = getForeignKeysFields();
		
		List<Narudzba> narudzbe = checkCache();
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<PoslovnaGodina> poslovneGodine = PoslovneGodine.findAktivnePoslovneGodine();
		List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.findKupci();
		
		
		render(povezaneForme, narudzbe, mode, poslovniPartneri, poslovneGodine, preduzeca, nadredjeneForme);

	}

	public static void changeMode(String mode) {
		if (mode == null || mode.equals("")) {
			mode = "edit";
		}

		session.put("mode", mode);

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		List<String> povezaneForme = getForeignKeysFields();
		
		List<Narudzba> narudzbe = checkCache();
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<PoslovnaGodina> poslovneGodine = PoslovneGodine.findAktivnePoslovneGodine();
		List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.findKupci();

		renderTemplate("Narudzbe/show.html", povezaneForme, narudzbe, mode, poslovniPartneri, nadredjeneForme,
				poslovneGodine, preduzeca);

}

	public static void create(Narudzba narudzba,Long preduzece, Long poslovnaGodina,Long poslovniPartner) throws ParseException{
		validation.clear();
		validation.valid(narudzba);
		clearSession();
		
		session.put("mode", "add");
		String mode = session.get("mode");
		
		System.out.println("Narudzba: "+narudzba.id);
		System.out.println("Godina: "+ poslovnaGodina);
		System.out.println("Preduzece: "+ preduzece);
		System.out.println("PoslovniPARTENR: "+ poslovniPartner);
		
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		List<String> povezaneForme = getForeignKeysFields();
		
		List<Narudzba> narudzbe = null;
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<PoslovnaGodina> poslovneGodine = PoslovneGodine.findAktivnePoslovneGodine();
		List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.checkCache();
		
		if(!validation.hasErrors()){
			
			narudzbe = Narudzba.findAll();
			
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
			
			narudzba.brojNarudzbe = incrementBrojNarudzbe();
			narudzba.preduzece = findPreduzece;
			narudzba.poslovnaGodina = findPoslovnaGodina;
			narudzba.poslovniPartner = findPoslovniPartner;
			
			narudzba.save();
			
			List<StavkaNarudzbe> stavkenarudzbe = narudzba.stavkeNarudzbe;
			
			narudzba.ukupnoOsnovica = 0;
			narudzba.ukupnoPDV = 0;
			narudzba.ukupnoZaPlacanje = 0;
			
			
			System.out.println("narudzba.brojNarudzbe = " + narudzba.brojNarudzbe);
			
			narudzba.fakturisana = "ne";
			
			if(stavkenarudzbe != null){
				for(StavkaNarudzbe sn:stavkenarudzbe){
					narudzba.ukupnoOsnovica += sn.osnovicaZaPDV;
					narudzba.ukupnoPDV += sn.iznosPDVa;
					narudzba.ukupnoZaPlacanje += sn.ukupno;
				}
			}
			

			narudzba.save();
			narudzbe.add(narudzba);
			Cache.set("narudzbe", narudzbe);
			
			narudzbe.clear();
			narudzbe = fillList();
			validation.clear();
			session.put("idNarudzbe", narudzba.id);
			
			
			//renderTemplate("Narudzbe/show.html",povezaneForme, narudzbe, mode, poslovniPartneri, poslovneGodine, preduzeca, nadredjeneForme);
			//nextForm(narudzba.id,"stavkeNarudzbe");
			continueStavkeNarudzbe(narudzba.id,"stavkeNarudzbe");
			
			
		} else {
			
			validation.keep();
			
			narudzbe = fillList();
			
			session.put("idN", narudzba.id);
			session.put("datumNarudzbe", narudzba.datumNarudzbe);
			session.put("datumValute", narudzba.datumValute);
			session.put("ukupnoOsnovica", narudzba.ukupnoOsnovica);
			session.put("ukupnoPDV", narudzba.ukupnoPDV);
			session.put("ukupnoZaPlacanje", narudzba.ukupnoZaPlacanje);
			
			renderTemplate("Narudzbe/show.html",povezaneForme, narudzbe, mode, poslovniPartneri, poslovneGodine, preduzeca, nadredjeneForme);
			
		}
		
		
		
		
	}
	
	public static void edit(Narudzba narudzba,Long preduzece, Long poslovnaGodina,Long poslovniPartner){
		
		validation.clear();
		validation.valid(narudzba);
		clearSession();
		
		session.put("mode", "edit");
		String mode = session.get("mode");
		
	
		List<String> povezaneForme = getForeignKeysFields();
		List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.findKupci();
		List<PoslovnaGodina> poslovneGodine = PoslovneGodine.findAktivnePoslovneGodine();
		List<Preduzece> preduzeca = Preduzeca.checkCache();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		
		List<Narudzba> narudzbe = null;
		
		if(!validation.hasErrors()){
			narudzbe = Narudzba.findAll();
			
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
			
			for (Narudzba tmp : narudzbe) {
				if (tmp.id == narudzba.id) {
					tmp.datumNarudzbe = narudzba.datumNarudzbe;
					tmp.datumValute = narudzba.datumValute;

					tmp.preduzece = findPreduzece;
					tmp.poslovnaGodina = findPoslovnaGodina;
					tmp.poslovniPartner = findPoslovniPartner;

					tmp.save();
					break;
				}
			}
			
			Cache.set("narudzbe", narudzbe);

			

			narudzbe.clear();
			narudzbe = fillList();
			validation.clear();
			
			
		} else {
			narudzbe = checkCache();
			
			validation.keep();
			
			session.put("idN", narudzba.id);
			session.put("datumNarudzbe", narudzba.datumNarudzbe);
			session.put("datumValute", narudzba.datumValute);
			session.put("ukupnoOsnovica", narudzba.ukupnoOsnovica);
			session.put("ukupnoPDV", narudzba.ukupnoPDV);
			session.put("ukupnoZaPlacanje", narudzba.ukupnoZaPlacanje);
			
			
		}
		
		renderTemplate("Narudzbe/show.html",povezaneForme, narudzbe, mode, poslovniPartneri, poslovneGodine, preduzeca, nadredjeneForme);
	}
	
	public static void fillter(Narudzba narudzba){
		List<Narudzba> narudzbe = Narudzba.find("byDatumNarudzbeLikeAndDatumValute", "%" + narudzba.datumNarudzbe + "%" + narudzba.datumValute + "%").fetch();
		
		session.put("mode", "edit");
		String mode = session.get("mode");
		
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		List<String> povezaneForme = getForeignKeysFields();
		
	
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<PoslovnaGodina> poslovneGodine = PoslovneGodine.findAktivnePoslovneGodine();
		List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.findKupci();

		renderTemplate("Narudzbe/show.html", povezaneForme, narudzbe, mode, poslovniPartneri, nadredjeneForme,
				poslovneGodine, preduzeca);
	}
	
	public static void exportToFaktura(Long id){
		Narudzba narudzba = Narudzba.findById(id);
		
		if(narudzba.faktura==null || narudzba.faktura.size() ==0){
			List<Faktura> fakture = Faktura.findAll();
			int brojFakture = 0;
			if (fakture.size() > 0) {
				brojFakture = fakture.get(fakture.size() - 1).brojFakture;
				brojFakture++;
			} else {
				brojFakture = 1;
			}
			
			float ukupnoOsnovica = (float)narudzba.ukupnoOsnovica;
			List<StavkaFakture> listaStavkifakture = new ArrayList<StavkaFakture>();
			
			Faktura faktura = new Faktura(narudzba.datumNarudzbe,brojFakture,narudzba.datumValute,ukupnoOsnovica,narudzba.ukupnoPDV,narudzba.ukupnoZaPlacanje);
			
			faktura.poslovnaGodina = narudzba.poslovnaGodina;
			faktura.poslovniPartner = narudzba.poslovniPartner;
			faktura.preduzece = narudzba.preduzece;
			faktura.narudzba = narudzba;
			
			
			narudzba.save();
			
			List<Narudzba> narudzbe = null;
			
			narudzbe = Narudzba.findAll();
			
			
			for (Narudzba tmp : narudzbe) {
				if (tmp.id == narudzba.id) {
					tmp.datumNarudzbe = narudzba.datumNarudzbe;
					tmp.datumValute = narudzba.datumValute;

					String fakturisan = "da";
					narudzba.fakturisana = fakturisan;
					
					tmp.save();
					break;
				}
			}
			
			Cache.set("narudzbe",narudzbe);
			
			faktura.save();
			
			for(StavkaNarudzbe sn:narudzba.stavkeNarudzbe){
				StavkaFakture sf = new StavkaFakture(sn.kolicina,sn.cena,0,0,0,sn.osnovicaZaPDV,sn.stopaPDVa,sn.iznosPDVa,sn.ukupno);
				sf.katalogRobeIUsluga = sn.katalogRobeIUsluga;
				sf.faktura = faktura;
				sf.save();
				listaStavkifakture.add(sf);
			}
			
			faktura.stavkeFakture = listaStavkifakture;
			
			faktura.save();
			
			fakture.add(faktura);
			Cache.set("fakture",fakture);
			
			
		}
		
		Fakture.show();
	}
	
	
	
	public static List<Narudzba> fillList(){
		List<Narudzba> narudzbe = null;
		
		
		if (!session.get("idPreduzeca").equals("null")) {
			Long id = Long.parseLong(session.get("idPreduzeca"));
			narudzbe = Preduzeca.findNarudzbe(id);
		} else if (!session.get("idPoslovnogPartnera").equals("null")) {
			Long id = Long.parseLong(session.get("idPoslovnogPartnera"));
			narudzbe = PoslovniPartneri.findNarudzbe(id);
		} else if (!session.get("idPoslovneGodine").equals("null")) {
			Long id = Long.parseLong(session.get("idPoslovneGodine"));
			narudzbe = PoslovneGodine.findNarudzbe(id);
		} else {
			narudzbe = checkCache();
		}


		
		return narudzbe;
	}

	public static void pickup(String forma) {
		if (forma.equals("poslovniPartner")) {
			PoslovniPartneri.show();
		} else if (forma.equals("poslovnaGodina")) {
			PoslovneGodine.show();
		} else if (forma.equals("preduzece")) {
			Preduzeca.show("edit");
		}
	}

	public static void delete(Long id) {
		String mode = session.get("mode");

		List<Narudzba> narudzbe = checkCache();
		List<String> povezaneForme = getForeignKeysFields();
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.findKupci();
		List<PoslovnaGodina> poslovneGodine = PoslovneGodine.findAktivnePoslovneGodine();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		Narudzba narudzba = Narudzba.findById(id);
		Long idd = null;
		
	if(narudzba.stavkeNarudzbe != null){
			for(StavkaNarudzbe sn:narudzba.stavkeNarudzbe){
				sn.delete();
				System.out.println("JEL BRISAO STAVKE");
			}
		} 
		
	
		List<Faktura> fakture = findFakture(id);
	//	fakture.get(0).narudzba = null;
		for(Faktura f: fakture){
			f.narudzba = null;
			f.save();
		}
		narudzba.faktura = null;
		narudzba.save();
		
		for (int i = 1; i < narudzbe.size(); i++) {
			if (narudzbe.get(i).id == id) {
				Narudzba prethodni = narudzbe.get(i - 1);
				idd = prethodni.id;
			}
		}
		narudzba.delete();

		narudzbe = Narudzba.findAll();
		Cache.set("narudzbe", narudzbe);

		renderTemplate("narudzbe/show.html", narudzbe, poslovneGodine, poslovniPartneri, preduzeca, nadredjeneForme, idd,
				povezaneForme, mode);
	}
	
	public static void refresh(){
		String mode = session.get("mode");

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		List<String> povezaneForme = getForeignKeysFields();
		
		List<Narudzba> narudzbe = checkCache();
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<PoslovnaGodina> poslovneGodine = PoslovneGodine.findAktivnePoslovneGodine();
		List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.findKupci();
		
		
		renderTemplate("narudzbe/show.html", narudzbe, poslovneGodine, poslovniPartneri, preduzeca, nadredjeneForme,
				povezaneForme, mode);
		
	}
	
	public static void continueStavkeNarudzbe(Long id,String forma) throws ParseException{
		System.out.println("JEL POSTOJI: "+id);
		Narudzba n = Narudzba.findById(id);
		if(n.fakturisana.equals("da")){
		//	session.put("fakturisna", id);
		}
		if (forma.equals("stavkeNarudzbe")) {
			List<Narudzba> narudzbe = checkCache();
			
			List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		
			List<StavkaNarudzbe> stavkeNarudzbe = findStavkeNarudzbe(id);
		
			
			List<String> nadredjeneForme = StavkeNarudzbe.getForeignKeysFieldsManyToOne();
			List<StavkaCenovnika> stavkeCenovnika = findStavkeCenovnika(id);
			
			String mode = "edit";
			session.put("mode", "edit");

			renderTemplate("StavkeNarudzbe/show.html",kataloziRobeIUsluga, narudzbe, stavkeNarudzbe ,nadredjeneForme,mode,stavkeCenovnika,mode);
		}
	}
	
	public static void nextForm(Long id,String forma) throws ParseException{
		System.out.println("JEL POSTOJI: "+id);
		session.put("idNarudzbe", id);
		session.put("idPreduzeca", "null");
		session.put("idPoslovneGodine", "null");
		session.put("idPoslovnogPartnera", "null");
		
		Narudzba n = Narudzba.findById(id);
		if(n.fakturisana.equals("da")){
			session.put("fakturisna", 1);
		}

		if (forma.equals("stavkeNarudzbe")) {
			List<Narudzba> narudzbe = checkCache();
			
			List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		
			List<StavkaNarudzbe> stavkeNarudzbe = findStavkeNarudzbe(id);
		
			
			List<String> nadredjeneForme = StavkeNarudzbe.getForeignKeysFieldsManyToOne();
			List<StavkaCenovnika> stavkeCenovnika = findStavkeCenovnika(id);
			
			String mode = "edit";
			session.put("mode", "edit");

			renderTemplate("StavkeNarudzbe/show.html",kataloziRobeIUsluga, narudzbe, stavkeNarudzbe ,nadredjeneForme,mode,stavkeCenovnika,mode);
		}
		else if(forma.equals("faktura") /* &&  n.fakturisana=="da" */ ){
			
			System.out.println("JESMO LI ODJE AAA");
			List<Narudzba> narudzbe = checkCache();
			List<Preduzece> preduzeca = Preduzeca.checkCache();
			List<PoslovnaGodina> poslovneGodine = PoslovneGodine.checkCache();
			List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.checkCache();

			List<Faktura> fakture = findFakture(id);
			List<String> nadredjeneForme = Fakture.getForeignKeysFieldsManyToOne();
			List<String> povezaneForme = Fakture.getForeignKeysFields();

			renderTemplate("Fakture/show.html", fakture, narudzbe, nadredjeneForme, poslovneGodine, povezaneForme,poslovniPartneri);
		}
		else
			show();
	}
	
	public static List<Narudzba> checkCache(){
		List<Narudzba> narudzbe = (List<Narudzba>) Cache.get("narudzbe");

		if ((narudzbe == null) || (narudzbe.size() == 0)) {
			narudzbe = Narudzba.findAll();
			Cache.set("narudzbe", narudzbe);
		}

		return narudzbe;
	}
	
	public static boolean clearSession(){
		return true;
	}


	public static List<String> getForeignKeysFields() {
		Class narudzbaClass = Narudzba.class;
		Field[] fields = narudzbaClass.getFields();

		List<String> povezaneForme = new ArrayList<String>();

		for (int i = 0; i < fields.length; i++) {
			Annotation annotation = fields[i].getAnnotation(OneToMany.class);
			if (annotation instanceof OneToMany) {
				povezaneForme.add(fields[i].getName());
			}
		}

		return povezaneForme;
	}

	
	
	public static List<String> getForeignKeysFieldsManyToOne() {
		Class narudzbeClass = Narudzba.class;
		Field[] fields = narudzbeClass.getFields();

		List<String> povezaneForme = new ArrayList<String>();

		for (int i = 0; i < fields.length; i++) {
			Annotation annotation = fields[i].getAnnotation(ManyToOne.class);
			if (annotation instanceof ManyToOne) {
				povezaneForme.add(fields[i].getName());
			}
		}

		return povezaneForme;
	}
	
	public static Date convertToDate(String receivedDate) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Date date = formatter.parse(receivedDate);
		return date;
	}
	
	public static List<StavkaCenovnika> findStavkeCenovnika(Long idNarudzbe) throws ParseException {
		
		System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
		
		Narudzba narudzba = Narudzba.findById(idNarudzbe);
		String datumNarudzbe = narudzba.datumNarudzbe;
		System.out.println("DATUM NARUDZBE: "+datumNarudzbe);
		Date datumNarudzbeDate = convertToDate(datumNarudzbe);

		List<Cenovnik> cenovniciSaDatumima = new ArrayList<>();
		List<Cenovnik> cenovnici = Cenovnici.checkCache();
		for (Cenovnik tmp : cenovnici) {
			String datumCenovnika = tmp.datumVazenja;
			Date datumCenovnikaDate = convertToDate(datumCenovnika);

			if (!datumCenovnikaDate.after(datumNarudzbeDate)) {
				cenovniciSaDatumima.add(tmp);
			}
		}

		List<Date> datumi = new ArrayList<>();
		// trazim cenovnik sa najvisim datumom
		for (Cenovnik tmp : cenovniciSaDatumima) {
			
			Date d = convertToDate(tmp.datumVazenja);
			datumi.add(d);
			System.out.println("IMA LI I JEDAN" + tmp.datumVazenja);
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
			System.out.println(string);
			if (tmp.datumVazenja.equals(string)) {
				
				stavkeCenovnika = tmp.stavkeCenovnika;
				System.out.println("YES"+stavkeCenovnika.get(0).cena);
				break;
			}
		}
		
		if(stavkeCenovnika==null){
			System.out.println("WHY?");
		}

		return stavkeCenovnika;
	}
	
	public static List<StavkaNarudzbe> findStavkeNarudzbe(Long idNarudzbe) {
		List<StavkaNarudzbe> stavkeNarudzbeAll = StavkaNarudzbe.findAll();
		List<StavkaNarudzbe> stavkeNarudzba = new ArrayList<>();

		for (StavkaNarudzbe sc : stavkeNarudzbeAll) {
			if(sc.narudzba != null){
				if (sc.narudzba.id== idNarudzbe) {
					stavkeNarudzba.add(sc);
				}
			}
		}

		return stavkeNarudzba;
	}
	
	public static int incrementBrojNarudzbe() {
		List<Narudzba> narudzbe = Narudzba.findAll();
		System.out.println("narudzbe.size = " + narudzbe.size());
		
		int brojNarudzbe = 0;
		if (narudzbe.size() > 0) {
			System.out.println("narudzbe.get(narudzbe.size() - 1)" + narudzbe.get(narudzbe.size() - 1));
			Narudzba narudzba = narudzbe.get((narudzbe.size()) - 1);
			brojNarudzbe = narudzba.brojNarudzbe;
			System.out.println("narudzba.brojNarudzbe" + narudzba.brojNarudzbe);
			brojNarudzbe = brojNarudzbe + 1;
			System.out.println("narudzbe.size > 0: brojNarudzbe posle ++" + brojNarudzbe);
		} else {
			brojNarudzbe = 1;
		}

		return brojNarudzbe;
}
	
public static List<Faktura> findFakture(Long idNarudzbe){
		
		List<Faktura> faktureAll = Faktura.findAll();
		List<Faktura> fakture = new ArrayList<>();
		
		for (Faktura faktura : faktureAll) {
			if(faktura.narudzba != null){
				if (faktura.narudzba.id == idNarudzbe) {
					fakture.add(faktura);
				}
			}
		}
		
		return fakture;
	}
	
	
}

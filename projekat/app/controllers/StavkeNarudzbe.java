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

import models.Cenovnik;
import models.Faktura;
import models.KatalogRobeIUsluga;
import models.Narudzba;
import models.StavkaCenovnika;
import models.StavkaNarudzbe;
import models.StopaPDVa;
import models.VrstaPDVa;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
@Check("administrator")
public class StavkeNarudzbe extends Controller {
	
	
	public static void show() throws ParseException{
		validation.clear();
		clearSession();

		session.put("idKataloga", "null");
		session.put("idNarudzbe", "null");

		session.put("mode", "edit");
		String mode = session.get("mode");
		
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<Narudzba> narudzbe = Narudzbe.checkCache();
		List<StavkaNarudzbe> stavkeNarudzbe = checkCache();
		List<StavkaCenovnika> stavkeCenovnika = fillListStavkeCenovnika();
		
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		render(kataloziRobeIUsluga, narudzbe, stavkeNarudzbe ,nadredjeneForme,mode,stavkeCenovnika);
	}
	
	public static void changeMode(String mode) throws ParseException {
		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		session.put("mode", mode);

		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<Narudzba> narudzbe = Narudzbe.checkCache();
		List<StavkaNarudzbe> stavkeNarudzbe = fillList();
		
		List<StavkaCenovnika> stavkeCenovnika = fillListStavkeCenovnika();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		renderTemplate("StavkeNarudzbe/show.html",kataloziRobeIUsluga, narudzbe, stavkeNarudzbe ,nadredjeneForme,mode,stavkeCenovnika);

	}
	
	public static Date convertToDate(String receivedDate) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Date date = formatter.parse(receivedDate);
		return date;
	}
	
	public static void create(StavkaNarudzbe stavkaNarudzbe, Long narudzba, Long katalogRobeIUsluga) throws ParseException{
		validation.clear();
		clearSession();

		validation.valid(stavkaNarudzbe);
		
		if(session.get("idNarudzbe")==null)
			System.out.println("NULL");
		System.out.println("Naziv: "+stavkaNarudzbe.kolicina+"idNaruzdbe: "+narudzba+"IdNarudzbe: ");
		Long idn = Long.parseLong(session.get("idNarudzbe"));
		System.out.println("ID SESESIJE NARUDZBE JE: "+idn);
		
		System.out.println("CENA CENA CENA 1 :"+stavkaNarudzbe.cena);
		session.put("mode", "add");
		String mode = session.get("mode");
		
		List<StavkaNarudzbe> stavkeNarudzbe = null;
		List<Narudzba> narudzbe = Narudzbe.checkCache();
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
	//	List<StavkaCenovnika> stavkeCenovnika = fillListStavkeCenovnika();
		
		Narudzba nn = Narudzba.findById(idn);
		String datumNarudzbe = nn.datumNarudzbe;
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
			System.out.println("ETO NIJE JESTA EN ZNAM");
			
		}
		
		for (StavkaCenovnika sc : stavkeCenovnika) {
			System.out.println("StvakCenovnikaIDKataloga: "+sc.katalogRobeIUsluga.id);
		}
		
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		
		if(!validation.hasErrors()){
			stavkeNarudzbe = StavkaNarudzbe.findAll();
			
			Narudzba findNarudzba = null;
			if(narudzba==null){
				Long id = Long.parseLong(session.get("idNarudzbe"));
				findNarudzba = Narudzba.findById(id);
			} else{
				findNarudzba = Narudzba.findById(narudzba);
			}
			
			KatalogRobeIUsluga katalogRobeIUslugaFind = KatalogRobeIUsluga.findById(katalogRobeIUsluga);
			stavkaNarudzbe.katalogRobeIUsluga = katalogRobeIUslugaFind;
			System.out.println("stavkaNarudzbe.katalogRobeIUsluga: "+stavkaNarudzbe.katalogRobeIUsluga.nazivStavkeKataloga+stavkaNarudzbe.katalogRobeIUsluga.id);
	/*		for (StavkaCenovnika sc : stavkeCenovnika) {
				System.out.println("StvakCenovnikaIDKataloga: "+sc.katalogRobeIUsluga.id);
				if (sc.katalogRobeIUsluga.id == stavkaNarudzbe.katalogRobeIUsluga.id) {
					System.out.println("NASAO JE ISTE KATALOGE CENOCNIKE I LI VEC STA");
					stavkaNarudzbe.cena = (float) sc.cena;
				}
			}
			System.out.println("Cena Stavke je: "+stavkaNarudzbe.cena); */
			
			
			
			
			
			
			
			
			System.out.println("CENA CENA CENA 2 :"+stavkaNarudzbe.cena);
			
			
			
			System.out.println("Stopa pdva je je: : "+stavkaNarudzbe.stopaPDVa);
			
			List<StavkaCenovnika> stavkeCenovnika1 = stavkaNarudzbe.katalogRobeIUsluga.stavkeCenovnika;
			for(StavkaCenovnika sc : stavkeCenovnika1) {
				if(sc.katalogRobeIUsluga == stavkaNarudzbe.katalogRobeIUsluga) {
					
					stavkaNarudzbe.cena =  (float) sc.cena;
				}
			}
			
			stavkaNarudzbe.osnovicaZaPDV = (float)(stavkaNarudzbe.kolicina * stavkaNarudzbe.cena);
			System.out.println("Osnovica za pdv je: : "+stavkaNarudzbe.osnovicaZaPDV);
			stavkaNarudzbe.stopaPDVa = findStopaPDVa(findNarudzba.id,
					stavkaNarudzbe.katalogRobeIUsluga.podgrupa.grupa.vrstaPDVa).procenatPDVa;
			
			
			System.out.println("CENA CENA CENA 3 :"+stavkaNarudzbe.cena);
			List<StopaPDVa> stopePDVa = stavkaNarudzbe.katalogRobeIUsluga.podgrupa.grupa.vrstaPDVa.stopePDVa;
			
			for(StopaPDVa sp : stopePDVa) {
				if(sp.vrstaPDVa == stavkaNarudzbe.katalogRobeIUsluga.podgrupa.grupa.vrstaPDVa) {
					stavkaNarudzbe.stopaPDVa = sp.procenatPDVa;
				}
			}
			
			stavkaNarudzbe.iznosPDVa = (stavkaNarudzbe.osnovicaZaPDV * stavkaNarudzbe.stopaPDVa) / 100;
			stavkaNarudzbe.ukupno = stavkaNarudzbe.osnovicaZaPDV  + stavkaNarudzbe.iznosPDVa;
			
			stavkaNarudzbe.narudzba = findNarudzba;
			
			stavkaNarudzbe.narudzba.ukupnoOsnovica += stavkaNarudzbe.osnovicaZaPDV;
			stavkaNarudzbe.narudzba.ukupnoPDV += stavkaNarudzbe.iznosPDVa;
			stavkaNarudzbe.narudzba.ukupnoZaPlacanje += stavkaNarudzbe.ukupno;
			
			stavkaNarudzbe.save();
			stavkaNarudzbe.narudzba.save();
			
			List<Narudzba> narudzbeAll = Narudzba.findAll();
			Cache.set("narudzbe", narudzbeAll);

			stavkeNarudzbe.add(stavkaNarudzbe);
			Cache.set("stavkeNarudzbe", stavkeNarudzbe);
			
			Long idd = stavkaNarudzbe.id;
			
			stavkeNarudzbe.clear();
			stavkeNarudzbe = fillList();
			
			validation.clear();
			
			renderTemplate("StavkeNarudzbe/show.html",kataloziRobeIUsluga, narudzbe, stavkeNarudzbe ,nadredjeneForme,mode,idd,stavkeCenovnika);

			
		}else{
			validation.keep();
			stavkeNarudzbe = fillList();
			session.put("idSN", null);
			renderTemplate("StavkeNarudzbe/show.html",kataloziRobeIUsluga, narudzbe, stavkeNarudzbe ,nadredjeneForme,mode,stavkeCenovnika);
		}
		
		
	}
	
	public static void edit(StavkaNarudzbe stavkaNarudzbe, Long narudzba, Long katalogRobeIUsluga) throws ParseException{
		validation.clear();
		clearSession();

		validation.valid(stavkaNarudzbe);
		
		if(session.get("idNarudzbe")==null)
			System.out.println("NULL");
		System.out.println("Naziv: "+stavkaNarudzbe.kolicina+"idNaruzdbe: "+narudzba+"IdNarudzbe: ");
		Long idn = Long.parseLong(session.get("idNarudzbe"));
		System.out.println("ID SESESIJE NARUDZBE JE: "+stavkaNarudzbe.id);
		
		StavkaNarudzbe given = StavkaNarudzbe.findById(stavkaNarudzbe.id);
		float staraOsnovica = given.osnovicaZaPDV;
		float stariIznosPDVa = given.iznosPDVa;
		float staroUkupno = given.ukupno;
		
		System.out.println("CENA CENA CENA 1 :"+stavkaNarudzbe.cena);
		session.put("mode", "add");
		String mode = session.get("mode");
		
		List<StavkaNarudzbe> stavkeNarudzbe = null;
		List<Narudzba> narudzbe = Narudzbe.checkCache();
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
	//	List<StavkaCenovnika> stavkeCenovnika = fillListStavkeCenovnika();
		
		Narudzba nn = Narudzba.findById(idn);
		String datumNarudzbe = nn.datumNarudzbe;
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
			System.out.println("ETO NIJE JESTA EN ZNAM");
			
		}
		
		for (StavkaCenovnika sc : stavkeCenovnika) {
			System.out.println("StvakCenovnikaIDKataloga: "+sc.katalogRobeIUsluga.id);
		}
		
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		
		if(!validation.hasErrors()){
			stavkeNarudzbe = StavkaNarudzbe.findAll();
			
			Narudzba findNarudzba = null;
			if(narudzba==null){
				Long id = Long.parseLong(session.get("idNarudzbe"));
				findNarudzba = Narudzba.findById(id);
			} else{
				findNarudzba = Narudzba.findById(narudzba);
			}
			
			KatalogRobeIUsluga katalogRobeIUslugaFind = KatalogRobeIUsluga.findById(katalogRobeIUsluga);
			stavkaNarudzbe.katalogRobeIUsluga = katalogRobeIUslugaFind;
			System.out.println("stavkaNarudzbe.katalogRobeIUsluga: "+stavkaNarudzbe.katalogRobeIUsluga.nazivStavkeKataloga+stavkaNarudzbe.katalogRobeIUsluga.id);
	/*		for (StavkaCenovnika sc : stavkeCenovnika) {
				System.out.println("StvakCenovnikaIDKataloga: "+sc.katalogRobeIUsluga.id);
				if (sc.katalogRobeIUsluga.id == stavkaNarudzbe.katalogRobeIUsluga.id) {
					System.out.println("NASAO JE ISTE KATALOGE CENOCNIKE I LI VEC STA");
					stavkaNarudzbe.cena = (float) sc.cena;
				}
			}
			System.out.println("Cena Stavke je: "+stavkaNarudzbe.cena); */
			
			
			
			
			
			
			
			
			System.out.println("CENA CENA CENA 2 :"+stavkaNarudzbe.cena);
			
			
			
			System.out.println("Stopa pdva je je: : "+stavkaNarudzbe.stopaPDVa);
			
			List<StavkaCenovnika> stavkeCenovnika1 = stavkaNarudzbe.katalogRobeIUsluga.stavkeCenovnika;
			for(StavkaCenovnika sc : stavkeCenovnika1) {
				if(sc.katalogRobeIUsluga == stavkaNarudzbe.katalogRobeIUsluga) {
					
					stavkaNarudzbe.cena =  (float) sc.cena;
				}
			}
			
			stavkaNarudzbe.osnovicaZaPDV = (float)(stavkaNarudzbe.kolicina * stavkaNarudzbe.cena);
			System.out.println("Osnovica za pdv je: : "+stavkaNarudzbe.osnovicaZaPDV);
			stavkaNarudzbe.stopaPDVa = findStopaPDVa(findNarudzba.id,
					stavkaNarudzbe.katalogRobeIUsluga.podgrupa.grupa.vrstaPDVa).procenatPDVa;
			
			
			System.out.println("CENA CENA CENA 3 :"+stavkaNarudzbe.cena);
			List<StopaPDVa> stopePDVa = stavkaNarudzbe.katalogRobeIUsluga.podgrupa.grupa.vrstaPDVa.stopePDVa;
			
			for(StopaPDVa sp : stopePDVa) {
				if(sp.vrstaPDVa == stavkaNarudzbe.katalogRobeIUsluga.podgrupa.grupa.vrstaPDVa) {
					stavkaNarudzbe.stopaPDVa = sp.procenatPDVa;
				}
			}
			
			stavkaNarudzbe.iznosPDVa = (stavkaNarudzbe.osnovicaZaPDV * stavkaNarudzbe.stopaPDVa) / 100;
			stavkaNarudzbe.ukupno = stavkaNarudzbe.osnovicaZaPDV  + stavkaNarudzbe.iznosPDVa;
			
			stavkaNarudzbe.narudzba = findNarudzba;
			
			stavkaNarudzbe.narudzba.ukupnoOsnovica -= staraOsnovica;
			stavkaNarudzbe.narudzba.ukupnoPDV -= stariIznosPDVa;
			stavkaNarudzbe.narudzba.ukupnoZaPlacanje -= staroUkupno;
			
			stavkaNarudzbe.narudzba.ukupnoOsnovica += stavkaNarudzbe.osnovicaZaPDV;
			stavkaNarudzbe.narudzba.ukupnoPDV += stavkaNarudzbe.iznosPDVa;
			stavkaNarudzbe.narudzba.ukupnoZaPlacanje += stavkaNarudzbe.ukupno;
			
			for(StavkaNarudzbe sn: stavkeNarudzbe){
				if(sn.id==stavkaNarudzbe.id){
					stavkaNarudzbe.save();
					stavkaNarudzbe.narudzba.save();
				}
			}
			
			
			
			
			List<Narudzba> narudzbeAll = Narudzba.findAll();
			Cache.set("narudzbe", narudzbeAll);

			stavkeNarudzbe.add(stavkaNarudzbe);
			Cache.set("stavkeNarudzbe", stavkeNarudzbe);
			
			Long idd = stavkaNarudzbe.id;
			
			stavkeNarudzbe.clear();
			stavkeNarudzbe = fillList();
			
			validation.clear();
			
			renderTemplate("StavkeNarudzbe/show.html",kataloziRobeIUsluga, narudzbe, stavkeNarudzbe ,nadredjeneForme,mode,idd,stavkeCenovnika);

			
		}else{
			validation.keep();
			stavkeNarudzbe = fillList();
			session.put("idSN", null);
			renderTemplate("StavkeNarudzbe/show.html",kataloziRobeIUsluga, narudzbe, stavkeNarudzbe ,nadredjeneForme,mode,stavkeCenovnika);
		}
		
		renderTemplate("StavkeNarudzbe/show.html",kataloziRobeIUsluga, narudzbe, stavkeNarudzbe ,nadredjeneForme,mode,stavkeCenovnika);
	}
	
	public static void filter(StavkaNarudzbe stavkaNarudzbe) throws ParseException{
		List<StavkaNarudzbe> stavkeNarudzbe = StavkaNarudzbe.find("byKolicina", stavkaNarudzbe.kolicina).fetch();
		
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<Narudzba> narudzbe = Narudzbe.checkCache();
	
		List<StavkaCenovnika> stavkeCenovnika = fillListStavkeCenovnika();
		
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		
		session.put("mode", "edit");
		String mode = session.get("mode");
		
		renderTemplate("StavkeNarudzbe/show.html",kataloziRobeIUsluga, narudzbe, stavkeNarudzbe ,nadredjeneForme,mode,stavkeCenovnika);
		
	}
	
	public static void delete(Long id) throws ParseException{
		String mode = session.get("mode");
		
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<Narudzba> narudzbe = Narudzbe.checkCache();
		List<StavkaNarudzbe> stavkeNarudzbe = checkCache();
		List<StavkaCenovnika> stavkeCenovnika = fillListStavkeCenovnika();
		
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		
		StavkaNarudzbe stavkaNarudzbe = StavkaNarudzbe.findById(id);
		Long idd = null;
		
		stavkaNarudzbe.narudzba.ukupnoOsnovica -= stavkaNarudzbe.osnovicaZaPDV;
		stavkaNarudzbe.narudzba.ukupnoPDV -= stavkaNarudzbe.iznosPDVa;
		stavkaNarudzbe.narudzba.ukupnoZaPlacanje -= stavkaNarudzbe.ukupno;
		stavkaNarudzbe.narudzba.save();
		
		for (int i = 1; i < stavkeNarudzbe.size(); i++) {
			if (stavkeNarudzbe.get(i).id == id) {
				StavkaNarudzbe prethodni = stavkeNarudzbe.get(i - 1);
				idd = prethodni.id;
			}
		}
		stavkaNarudzbe.delete();
		stavkaNarudzbe.narudzba.save();
		
		narudzbe = Narudzba.findAll();
		Cache.set("narudzbe",narudzbe);

		Cache.set("stavkeNarudzbe", stavkeNarudzbe);
		
		stavkeNarudzbe.clear();
		stavkeNarudzbe = fillList();
		
		
		renderTemplate("StavkeNarudzbe/show.html",kataloziRobeIUsluga, narudzbe, stavkeNarudzbe ,nadredjeneForme,mode,idd,stavkeCenovnika);
		
	}
	
	public static void refresh() throws ParseException{
		List<Narudzba> narudzbe = Narudzbe.checkCache();
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<StavkaNarudzbe> stavkeNarudzbe = fillList();
		List<StavkaCenovnika> stavkeCenovnika = fillListStavkeCenovnika();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		String mode = session.get("mode");
		
		renderTemplate("StavkeNarudzbe/show.html",kataloziRobeIUsluga, narudzbe, stavkeNarudzbe ,nadredjeneForme,mode,stavkeCenovnika);
	}
	
	public static void pickup(String forma){
		if (forma.equals("narudzba")) {
			Narudzbe.show();
		} else if (forma.equals("katalogRobeIUsluga")) {
			KataloziRobeIUsluga.show();
		}
		
	}
	
	public static List<StavkaNarudzbe> fillList(){
		
		List<StavkaNarudzbe> stavkeNarudzbe = null;	
		

		if (!session.get("idKataloga").equals("null")) {
			Long id = Long.parseLong(session.get("idKataloga"));
			stavkeNarudzbe = KataloziRobeIUsluga.findStavkeNarudzbe(id);
		} else if (!session.get("idNarudzbe").equals("null")) {
			Long id = Long.parseLong(session.get("idNarudzbe"));
			System.out.println("A JELI OVDE PEODJE: "+id);
			stavkeNarudzbe = Narudzbe.findStavkeNarudzbe(id);
		} else {
			stavkeNarudzbe = checkCache();
		}

		return stavkeNarudzbe;
	}
	
	public static List<StavkaNarudzbe> checkCache(){
		List<StavkaNarudzbe> stavkeNarudzbe = (List<StavkaNarudzbe>) Cache.get("stavkeNarudzbe");

		if ((stavkeNarudzbe == null) || (stavkeNarudzbe.size() == 0)) {
			stavkeNarudzbe = StavkaNarudzbe.findAll();
			Cache.set("stavkeNarudzbe", stavkeNarudzbe);
		}

		return stavkeNarudzbe;
	}
	
	public static boolean clearSession(){
		session.put("idSF", null);
		return true;
	}
	
	public static List<StavkaCenovnika> fillListStavkeCenovnika() throws ParseException {
		List<StavkaCenovnika> stavkeCenovnika = null;
		System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAA");
		if (!session.get("idNarudzbe").equals("null")) {
			Long id = Long.parseLong(session.get("idNarudzbe"));
			System.out.println("WHERE YOU HERE?");
			stavkeCenovnika = Narudzbe.findStavkeCenovnika(id);
		}
		
		if(stavkeCenovnika==null){
			System.out.println("WHY HERE?");
		}

		return stavkeCenovnika;
	}
	
	public static List<String> getForeignKeysFieldsManyToOne() {
		Class stavkaFaktureClass = StavkaNarudzbe.class;
		Field[] fields = stavkaFaktureClass.getFields();

		List<String> povezaneForme = new ArrayList<String>();

		for (int i = 0; i < fields.length; i++) {
			Annotation annotation = fields[i].getAnnotation(ManyToOne.class);
			if (annotation instanceof ManyToOne) {
				povezaneForme.add(fields[i].getName());
			}
		}

		return povezaneForme;
	}
	
	public static StopaPDVa findStopaPDVa(Long idNarudzbe, VrstaPDVa vrstaPDVa) throws ParseException {
		Narudzba narudzba = Narudzba.findById(idNarudzbe);
		String datumNarudzbe = narudzba.datumNarudzbe;
		Date datumNarudzbeDate = Fakture.convertToDate(datumNarudzbe);

		List<StopaPDVa> stopePDVaSaDatumima = new ArrayList<>();
		List<StopaPDVa> stopePDVa = StopePDVa.checkCache();
		for (StopaPDVa tmp : stopePDVa) {
			String datumStopePDVa = tmp.datumKreiranja;
			Date datumStopePDVaDate = Fakture.convertToDate(datumStopePDVa);

			if (!datumStopePDVaDate.after(datumNarudzbeDate) && (tmp.vrstaPDVa.id == vrstaPDVa.id)) {
				stopePDVaSaDatumima.add(tmp);
			}
		}

		List<Date> datumi = new ArrayList<>();
		for (StopaPDVa tmp : stopePDVaSaDatumima) {
			Date d = Fakture.convertToDate(tmp.datumKreiranja);
			datumi.add(d);
		}

		Collections.sort(datumi, new Comparator<Date>() {
			@Override
			public int compare(Date arg0, Date arg1) {
				return arg0.compareTo(arg1);
			}
		});

		// trazim stopuPDVa
		StopaPDVa stopaPDVa = null;
		for (StopaPDVa tmp : stopePDVaSaDatumima) {
			String string = new SimpleDateFormat("MM/dd/yyyy").format(datumi.get(datumi.size() - 1));
			if (tmp.datumKreiranja.equals(string)) {
				stopaPDVa = tmp;
			}
		}

		return stopaPDVa;
	}
	
	private static List<StavkaCenovnika> getListeStvkeCenovnika(Long narudzba) throws ParseException{
		List<StavkaCenovnika> stavkeCenovnika = null;

		
		stavkeCenovnika = Narudzbe.findStavkeCenovnika(narudzba);
		
		
		

		return stavkeCenovnika;
	}

	
	public static void saveStavke() {
		Long id = Long.parseLong(session.get("idNarudzbe"));
		Narudzba narudzba = Narudzba.findById(id);

		if (narudzba.stavkeNarudzbe.size() == 0) {
			Narudzbe.delete(id);
		} else {
			Narudzbe.show();
		}
	}
}

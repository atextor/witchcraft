package de.rccc.java.witchcraft;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.util.*;

/**
 * Die Definition eines Feindes, der "Bauplan" fuer einen Feind
 */
public class TFeindDef {
	/**
	 * Liste der verfügbaren Feinddefinitionen (static)
	 */
	protected static Map<String, TFeindDef> ffeinddefliste =
		new HashMap<String, TFeindDef>();

	/**
	 * Welche Animation hat der Feind?
	 */
	protected String fdarstellung = null;
	
	/**
	 * Wieviel HP hat der Feind
	 */
	protected int flebenspunkte = 1;

	/**
	 * Welche Waffe hat der Feind?
	 */
	protected TWaffe fwaffe = null;

	/**
	 * Wiviele Punkte gibt der Feind
	 */
	protected int fpunkte = 0;

	/**
	 * Welchem Bewegungsmuster folgt der Feind
	 */
	protected int fmuster = 0;

	/**
	 * Wo wird die Waffe ausgeworfen?
	 */
	protected TVektor fwaffenausgang = null;

	/**
	 * Welcher Sound soll geloopt werden, wenn der Gegner kommt?
	 * Bei Boss-Gegnern brauchbar, damit damit eine bedrohliche 
	 * Kulisse geschaffen werden kann.
	 */
	protected String fdauerSound = null;

	/**
	 * Konstruktor
	 *
	 * @param darstellung Welche Animation für diesen Feind
	 * @param lebenspunkte Wieviele lebenspunkte hat der Feind
	 * @param waffe Welche Waffe hat der Feind
	 * @param punkte Weiviele Punkte gibt es für den Abschuss dieses Gegners
	 * @param muster Welchem Muster folgt der Feind
	 * @param waffenausgang Wo kommt die Waffe raus?
	 * @param dauersound Welcher Sound soll angefangen werden zu loopen, wenn
	 * der Gegner aktiviert wird?
	 */
	TFeindDef(String darstellung, int lebenspunkte, String waffe,
		int punkte, int muster, TVektor waffenausgang, String dauersound) {

		fdarstellung = darstellung;
		flebenspunkte = lebenspunkte;
		fwaffe = TWaffe.getWaffe(waffe);
		fpunkte = punkte;
		fmuster = muster;
		fwaffenausgang = waffenausgang;
		fdauerSound = dauersound;

		// Noch was korrigieren. Die Mitte des Bildes 
		// muss bei waffenausgang sein
		String waffenbild = fwaffe.getBild();
		if ((waffenbild != null) && (!waffenbild.equals(""))) {
			TVektor groesse = TSharedObjects.getNewBild(waffenbild).groesse();
			groesse.mult(0.5);
			fwaffenausgang.sub(groesse);
		}
	};

	/**
	 * Gibt die Lebenspunkte dieses Feindes zurück
	 */
	public int getLebenspunkte() {
		return flebenspunkte;
	}

	/**
	 * Gibt die Darstellung zurück
	 */
	public String getDarstellung() {
		return fdarstellung;
	}

	/**
	 * Gibt die Waffe zurück
	 */
	public TWaffe getWaffe() {
		return fwaffe;
	}

	/**
	 * Ginbt die Punkte des Feindes zurück
	 */
	public int getPunkte() {
		return fpunkte;
	}

	/**
	 * Gibt das Bewegungsmuster des Feindes zurück
	 */
	public int getMuster() {
		return fmuster;
	}

	/**
	 * Gibt die Startkoordinaten der Gesschosse zurück
	 */
	public TVektor getWaffenausgang() {
		return fwaffenausgang;
	}

	/**
	 * Gibt den Sound zurueck, der mit dem Aktivieren des Gegners
	 * gestartet werden soll
	 */
	public String getDauersound() {
		return fdauerSound;
	}

	/**
	 * Fügt eine Feinddefinition in die List hinzu
	 * @param id Unter welcher ID soll die Definition abgelegt werden
	 * @param feinddef Diese Definition soll gespeichert werden
	 */
	public static void addFeindDef(String id, TFeindDef feinddef) {
		ffeinddefliste.put(id, feinddef);
	}

	/**
	 * Holt eine Feinddefinition zu einer ID
	 * @param id Die ID, unter der nachgeschaut werden soll
	 */
	public static TFeindDef getFeindDef(String id) {
		return ffeinddefliste.get(id);
	}

	/**
	 * Leert die Liste (wenn neuer Level)
	 */
	public static void leereListe() {
		ffeinddefliste.clear();
	}

	/**
	 * Erzeugt eine neue Feinddefiniton aus den Daten eines XML-Nodes
	 * @param n Der Node, aus dem die Feinddefinition gebaut werden soll
	 */
	public static boolean addFeindDefAusNode(Node n) {
		String bild = null;
		String dauersound = null;
		int lebenspunkte = -1;
		String waffenName = null;
		String id = null;
		int punkte = -1;
		int muster = -1;
		TVektor waffenausgang = new TVektor();

		// in die Liste eintauchen
		NodeList nds = n.getChildNodes(); 
		// wie aktuell ;)
		Node a; 

		// Einlesen der Daten
		for (int i = 0; i < nds.getLength(); i++) {
			a = nds.item(i);
			
			if (a.getNodeName().equals("bild")) {
				bild = a.getTextContent();
			} else if (a.getNodeName().equals("ID")) {
				id = a.getTextContent();
			} else if (a.getNodeName().equals("leben")) {
				lebenspunkte = Integer.parseInt(a.getTextContent());
			} else if (a.getNodeName().equals("waffe")) {
				waffenName = a.getTextContent();
			} else if (a.getNodeName().equals("punkte")) {
				punkte = Integer.parseInt(a.getTextContent());
			} else if (a.getNodeName().equals("muster")) {
				muster = Integer.parseInt(a.getTextContent());
			} else if (a.getNodeName().equals("waffenausgang")) {
				waffenausgang.leseAusXmlNode(a);
			} else if (a.getNodeName().equals("dauersound")) {
				dauersound = a.getTextContent();
			}
		}

		// Plausibilitaetspruefung
		if ((bild == null) || (lebenspunkte <= 0) || (waffenName == null) ||
			(punkte < 0) || (muster == -1)){
			System.out.println("Fehlende Angaben (" + bild + ", " + 
				lebenspunkte + ", " + waffenName + ", " + punkte +
				", " + muster + ")");
			return false;
		}
		
		if (!TSharedObjects.bildInListe(bild)) {
			System.out.println("Bild des Feindes \"" + id + "\": \"" +
				bild + "\" nicht gefunden.");
			return false;
		}

		if (TWaffe.getWaffe(waffenName) == null) {
			System.out.println("Waffe des Feindes \"" + id + "\": \"" +
				waffenName + "\" nicht gefunden.");
			return false;
		}

		System.out.println(" Lade: " + id);
		addFeindDef(id, new TFeindDef(bild, lebenspunkte, waffenName, punkte,
			muster, waffenausgang, dauersound));
		return true;
	}
}

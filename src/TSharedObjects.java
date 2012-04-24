package de.rccc.java.witchcraft;

import java.util.Map;
import java.util.HashMap;
import java.util.Random;

/**
 * TSharedObject enthält Referenzen zu allen Objekten, die von vielen
 * Klassen benötigt werden
 */
public class TSharedObjects {
	/**
	 * statische Liste aller Bilder
	 */
	protected static Map<String, TAnimation> bildliste =
		new HashMap<String, TAnimation>();

	/**
	 * Sound-Objekt
	 */
	protected static TSound fsound = null;

	/**
	 * Partikel-Verwaltung
	 */
	protected static TPartikelVerwaltung fpverw = null;

	/**
	 * Spiel-Anzeige
	 */
	protected static TAnzeige fanzeige = null;
	
	/**
	 * die Haupt-Spielklasse registiert sich fuer alle erreichbar,
	 * damit zB Menueeintraege disabled werden koennen
	 */
	protected static Main fmain = null;

	/**
	 * Zufallsgenerator
	 */
	protected static Random frandom = new Random();

	/**
	 * Gibt an, ob gerade Musik spielt (und ob beim Spielstart
	 * Musik direkt anfangen soll oder nicht)
	 */
	protected static boolean fmusik = false;

	/**
	 * Fuer alle zugaenglich: Die Breite des Spielfensters
	 */
	public final static int FENSTER_BREITE = 640;

	/**
	 * Fuer alle zugaenglich: Die Hoehe des Spielfensters
	 */
	public final static int FENSTER_HOEHE = 480;

	/**
	 * Liefert ein Zufalls-Int
	 */
	public static int rndInt(int r) {
		return frandom.nextInt(r);
	}

	/**
	 * Liefert ein Zufalls-Double
	 */
	public static double rndDouble() {
		return frandom.nextDouble();
	}

	/**
	 * Fügt ein neues Bild in die Bilderlsite an
	 */
	static public void addBild(String id, TAnimation bild) {
		bildliste.put(id, bild);
	}

	/**
	 * Gibt eine Kopie eine Bildes aus der Bilderliste zurück
	 * Beim Kopieren der TAnimation werden die Animationsframes
	 * nicht kopiert, sondern referenziert. Alle Objekte, die eigene
	 * Animationsframes haben (also Gegner etc. - nicht Geschosse, da hier
	 * die Animationsframes die Drehphasen darstellen) verwenden daher
	 * diese Methode anstelle von getBild()
	 *
	 * @param bild Das Bild
	 */
	public static TAnimation getNewBild(String bild) {
		return (bildInListe(bild) ? new TAnimation(bildliste.get(bild)) : null);
	}

	/**
	 * Gibt eine Refernz auf ein Bild aus der Bilderliste zurück
	 *
	 * @param bild Das Bild
	 */
	public static TAnimation getBild(String bild) {
		return bildliste.get(bild);
	}

	/**
	 * Liefert die Information darueber, ob ein Bild in der Bildliste
	 * vorhanden ist
	 * @param bild Das Bild, das in der Liste gesucht wird
	 * @return Ist das Bild in der Liste vorhanden?
	 */
	public static boolean bildInListe(String bild) {
		return (bildliste != null) && (bildliste.get(bild) != null);
	}

	/**
	 * Setzt die Partikelverwaltung
	 */
	public static void setPartikelVerwaltung(TPartikelVerwaltung pverw) {
		fpverw = pverw;
	}

	/**
	 * Gibt die aktuell Verwendete Partikelverwaltung zurück
	 * (besser als die Aufrufe durchzureichen wie beim Sound)
	 */
	public static TPartikelVerwaltung getPartikelVerwaltung() {
		return fpverw;
	}

	/**
	 * Setzt die Anzeige
	 * @param az Die zu setzende Anzeige
	 */
	public static void setAnzeige(TAnzeige az) {
		fanzeige = az;
	}

	/**
	 * Liefert die Anzeige zurueck
	 */
	public static TAnzeige getAnzeige() {
		return fanzeige;
	}

	/**
	 * Setzt die Haupt-Spielklasse - von allen zugreifbar, damit
	 * z.B. Menueintraege disabled werden koennen
	 */
	public static void setMain(Main main) {
		fmain = main;
	}

	/**
	 * Liefert die Haupt-Spielklasse
	 */
	public static Main getMain() {
		return fmain;
	}

	/**
	 * Liefert, ob Musik gerade laeuft (beim Spielstart: ob direkt
	 * beim Spielstart die Musik anfangen soll)
	 */ 
	public static boolean getMusik() {
		return fmusik;
	}

	/**
	 * Schaltet die Musik ein oder aus und aendert alles notwendige
	 */
	public static void setMusik(boolean musik) {
		fmusik = musik;
		if (musik) {
			TSound.playMusik("MUSIK");
			fmain.aendereMenuItem("optionen_musik", "Musik abschalten");
		} else {
			TSound.stoppe("MUSIK");
			fmain.aendereMenuItem("optionen_musik", "Musik anschalten");
		}
	}

	/**
	 * Methode, die das Spiel beendet sofern moeglich,
	 * andernfalls zumindest das Fenster schliesst
	 */
	public static void endGame() {
		try {
			System.exit(0);
		} catch (java.security.AccessControlException e) {
			// Wir koennen im Applet das Programm nicht beenden -
			// aber wir koennen das Fenster schliessen :)
			if (fmusik) {
				setMusik(false);
				TSound.stoppe("MUSIK");
			}

			fmain.getHauptFenster().dispose();
			System.out.println("Kann Programm nicht beenden.");
			fanzeige.pause();
		}
	}
}

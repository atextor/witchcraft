package de.rccc.java.witchcraft;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.util.*;
import java.awt.Font;
import java.awt.*;

/**
 * Waffendefinitionen, die im Spiel verwendet werden.
 * Hier sind nur die "Definitionen" - die sind pro Waffe einamlig.
 * Die Bildschirmobjekte werden in TGeschoss gespeichert.
 */ 
public class TWaffe {
	/**
	 * Die zur Verfügung stehenden Behandlungen.
	 * Alle Geschosse haben eine dieser Arten, die die Behandlung
	 * (anzeige, bewegung, etc) vorgibt.
	 */
	static public enum Waffen {
		/**
		 * Not in List. Fehlwert.
		 */
		NIL, 
		/**
		 * Raketenart.
		 * Ein Objekt, das sich nach dem ausstoß in die Zielrichtung
		 * dreht, und dann dahinfliegt, bzw. das ein Ziel verfolgt
		 */
		Rakete, 
		/**
		 * "Instant hit" Waffe.
		 * Das Ziel wird sofort getroffen, und es wird ein strahl dahingemalt
		 */
		Rail, 
		/**
		 * DumbFire.
		 * Standardtyp. Die Waffe fliegt in eine Richtung. Ohne sich
		 * zu drehen, etc.
		 */
		Dumbfire
	};

	/**
	 * Liste der verfügbaren Waffen (static)
	 */
	protected static Map<String, TWaffe> fwaffenliste =
		new HashMap<String, TWaffe>();

	/**
	 *  Die benoetigte Magie
	 */
	protected int fmagie = 0;
	
	/**
	 * Den Schaden, den die Waffe ausrichtet
	 */
	protected int fschaden = 0;

	/**
	 * Wie viele Ticks soll der Bildschirm wackeln bei
	 * Abschuss eines Gegners mit dieser Waffe
	 */
	protected int fbeben = 0;

	/**
	 * Um welche Art handelt es sich
	 */
	protected Waffen fwaffe;

	/**
	 * Rate of fire
	 */
	protected int frof;

	/**
	 * Wie heisst diese Waffe. Nur bei spielerauswählbaren waffen wichtig
	 */
	protected String fbez;

	/**
	 * ID der Waffe - diese wird auch ins Savegame geschrieben
	 */
	protected String fid;

	/**
	 * Mit welchem Bild soll das dargestellt werden?
	 */
	protected String fbild;

	/**
	 * Mit welcher Geschwindig fliegt das?
	 */
	protected int fgeschw;

	/**
	 * Welcher Sound soll beim abfeuern dieser Waffe gespielt werden?
	 */
	protected String fstartsound;

	/**
	 * Welcher Sound soll beim auftreffen (Einschlag) dieser Waffe
	 * gespielt werden?
	 */
	protected String fhitsound;

	/**
	 * Welcher Sound soll beim töten mit dieser Waffe gespielt werden?
	 */
	protected String ftotsound;

	/**
	 * Welche Partikel sollen beim Treffer mit dieser Waffe erzeugt werden?
	 */
	protected TPartikelVerwaltung.Partikel fpartikeltreff =
		TPartikelVerwaltung.Partikel.NIL;

	/**
	 * Welche Partikel sollen bei Zerstörung mit dieser Waffe erzeugt werden?
	 */
	protected TPartikelVerwaltung.Partikel fpartikeltot =
		TPartikelVerwaltung.Partikel.NIL;

	/**
	 * Statistik: Wie oft wurde diese Waffe abgefeuert
	 */
	protected int fstatAbgefeuert = 0;

	/**
	 * Statistik: Wie oft wurde mit dieser Waffe getroffen
	 */
	protected int fstatTreffer = 0;

	/**
	 * Statistik: Die Anzahl der abgefeuerten eröhen
	 */
	public void statIncAbgefeuert() {
		fstatAbgefeuert++;
	}

	/**
	 * Statistik: Die Anzahl der getroffenen erhöhen
	 */
	public void statIncTreffer() {
		fstatTreffer++;
	}

	/**
	 * Statistik: Gib die Anzahl der abgefeuerten Schüsse zurück
	 */
	public int getStatAbgefeuert() {
		return fstatAbgefeuert;
	}

	/**
	 * Statistik: Gib die Anzahl der Treffer zurück
	 */
	public int getStatTreffer() {
		return fstatTreffer;
	}

	/**
	 * Statistik: Reset
	 */
	public static void resetStatistik() {
		for (String i : fwaffenliste.keySet()) {
			TWaffe akt = fwaffenliste.get(i);
			akt.fstatAbgefeuert = 0;
			akt.fstatTreffer = 0;
		}
	}

	/**
	 * Statisitk setzen (für das Laden des Levels)
	 *
	 * @param statAbgefeuert Wie viele Schuesse wurden mit dieser
	 * Waffe abgefeuert
	 * @param statTreffer Und wie viele davon waren Treffer
	 */
	public void setStatistik(int statAbgefeuert, int statTreffer) {
		fstatAbgefeuert = statAbgefeuert;
		fstatTreffer = statTreffer;
	}

	/**
	 * Holt eine Waffe zu der ID
	 *
	 * @param id Die ID der zu suchenden Waffe
	 */
	public static TWaffe getWaffe(String id) {
		return fwaffenliste.get(id);
	}

	/**
	 * Fügt eine Waffe in der Waffenliste hinzu
	 *
	 * @param id Die ID der zuzufuegenden Waffe
	 * @param waffe Die Waffe, die der Liste zugefuegt werden soll
	 */
	public static void addWaffe(String id, TWaffe waffe) {
		fwaffenliste.put(id, waffe);
	}
	
	/**
	 * Konstruktor
	 *
	 * @param magie Die Magie, die die Waffe verbraucht (bei dem
	 * Spielerwaffen)
	 * @param schaden Den Schaden, den die Waffe anrichtet.
	 * @param waffe Was für ein Typ ist die Waffe. Siehe das enum
	 * @param rof rate of fire - mit welchen Tick-abstand soll die
	 * Waffe abgefeuert werden?
	 * @param bez Wie die Waffe heisst. Nur für den Spieler
	 * @param id Der ID-Name der Waffe (der auch im Savegame abgespeichert
	 * wird ggf.)
	 * @param bild Unter welchem Namen ist die Darstellung dieser Waffe
	 * bekannt?
	 * @param geschw Die Geschwindigkeit der Waffe
	 * @param partikeltreff Abzuspielender Partikeleffekt beim treffen mit 
	 * dieser Waffe
	 * @param partikeltot Abzuspielender Partikeleffekt beim Töten mit 
	 * dieser Waffe
	 * @param startsound Welcher Sound soll beim starten dieser Waffe
	 * gespielt werden?
	 * @param hitsound Welcher Sound soll beim Aufschlag mit dieser Waffe
	 * gespielt werden?
	 * @param totsound Welcher sound soll beim töten mit dieser Waffe
	 * abgespielt werden?
	 * @param beben Wie viele Ticks soll der Bildschirm wackeln, wenn
	 * ein Gegner mit dieser Waffe abgeschossen wurde?
	 */
	TWaffe(int magie, int schaden, Waffen waffe, int rof, String bez,
		String id, String bild, int geschw,
		TPartikelVerwaltung.Partikel partikeltreff,
		TPartikelVerwaltung.Partikel partikeltot,
		String startsound, String hitsound,
		String totsound, int beben) {

		fmagie         = magie;
		fschaden       = schaden;
		fwaffe         = waffe;
		frof           = rof;
		fbez           = bez;
		fid            = id;
		fbild          = bild;
		fgeschw        = geschw;
		fpartikeltreff = partikeltreff;
		fpartikeltot   = partikeltot;
		fstartsound    = startsound;
		fhitsound      = hitsound;
		ftotsound      = totsound;
		fbeben         = beben;

		if ((bild != null) && (!bild.equals(""))) {
			TAnimation ani = TSharedObjects.getBild(bild);
			if (ani != null) {
				try {
					ani.rotiereFrame();
				} catch (Exception e) {
					System.out.println("Fehler in TWaffe: " + e);
				}
			}
		}
	}

	/**
	 * Erzeugt eine neue Waffe aus den Daten eines XML-Nodes
	 *
	 * @param n Der Node, aus dem die Waffe erzeugt werden soll
	 */
	public static TWaffe WaffeAusNode(Node n) {
		int magie         = 0;
		int schaden       = -1;
		int beben         = 0;
		Waffen waffe      = Waffen.NIL;
		int rof           = -1;
		String bez        = "";
		String id         = "";
		String bild       = null;
		int geschw        = -1;
		String startsound = null;
		String hitsound   = null;
		String totsound   = null;
		TPartikelVerwaltung.Partikel partikeltreff =
			TPartikelVerwaltung.Partikel.NIL;
		TPartikelVerwaltung.Partikel partikeltot =
			TPartikelVerwaltung.Partikel.NIL;

		// in die Liste eintauchen
		NodeList nds = n.getChildNodes(); 
		Node a; 

		for (int i = 0; i < nds.getLength(); i++) {
			a = nds.item(i);
			
			if (a.getNodeName().equals("magie")) {
				magie = Integer.parseInt(a.getTextContent()); 
			} else if (a.getNodeName().equals("ID")) {
				id = a.getTextContent();
			} else if (a.getNodeName().equals("schaden")) {
				schaden = Integer.parseInt(a.getTextContent());
			} else if (a.getNodeName().equals("beben")) {
				beben = Integer.parseInt(a.getTextContent());
			} else if (a.getNodeName().equals("rof")) {
				rof = Integer.parseInt(a.getTextContent());
			} else if (a.getNodeName().equals("bezeichnung")) {
				bez = a.getTextContent();
			} else if (a.getNodeName().equals("bild")) {
				bild = a.getTextContent();
			} else if (a.getNodeName().equals("startsound")) {
				startsound = a.getTextContent();
			} else if (a.getNodeName().equals("hitsound")) {
				hitsound = a.getTextContent();
			} else if (a.getNodeName().equals("totsound")) {
				totsound = a.getTextContent();
			} else if (a.getNodeName().equals("partikeltreff")) {
				partikeltreff = TPartikelVerwaltung.Partikel.valueOf(
				a.getTextContent());
			} else if (a.getNodeName().equals("partikeltot")) {
				partikeltot = TPartikelVerwaltung.Partikel.valueOf(
				a.getTextContent());
			} else if (a.getNodeName().equals("geschwindigkeit")) {
				geschw = Integer.parseInt(a.getTextContent());
			} else if (a.getNodeName().equals("waffe")) {
				waffe = Waffen.valueOf(a.getTextContent());
			}
		}

		if ((waffe == Waffen.NIL) || (schaden < 0) || (rof < 0) ||
			(bild == null) || (geschw < 0)) {
			System.out.println("Fehlende Angaben (" + waffe + ", " +
				schaden + ", " + rof + ", " + bild + ", " + geschw + ")");
			return null;
		}

		if ((bild != "") && (!TSharedObjects.bildInListe(bild))) {
			System.out.println("Bild der Waffe \"" +waffe + "\": \"" +
				bild + "\" nicht gefunden.");
			return null;
		}

		if ((startsound != null) && !TSound.inListe(startsound)) {
			System.out.println("Fehler in der Waffe \"" + bez + "\": Startsound \"" +
				startsound + "\" nicht gefunden.");
			// Hindert nicht Waffe an sich zu funktionieren, also weiter
			startsound = null;
		}

		if ((hitsound != null) && !TSound.inListe(hitsound)) {
			System.out.println("Fehler in der Waffe \"" + bez + "\": Hitsound \"" +
				hitsound + "\" nicht gefunden.");
			// Hindert nicht Waffe an sich zu funktionieren, also weiter
			hitsound = null;
		}

		if ((totsound != null) && !TSound.inListe(totsound)) {
			System.out.println("Fehler in der Waffe \"" + bez + "\": Totsound \"" +
				totsound + "\" nicht gefunden.");
			// Hindert nicht Waffe an sich zu funktionieren, also weiter
			totsound = null;
		}

		return new TWaffe(magie, schaden, waffe, rof, bez, id, bild, geschw,
			partikeltreff, partikeltot, startsound, hitsound, totsound, beben);
	}

	/**
	 * Liefert den Magiewert, den die Waffe absaugt
	 */
	public int getMagie() {
		return fmagie;
	}

	/**
	 * Um welche Waffenart handelt es sich (aus Enum)
	 */
	public Waffen getWaffe() {
		return fwaffe;
	}

	/**
	 * Liefert den Schaden, den die Waffe verursacht
	 */
	public int getSchaden() {
		return fschaden;
	}

	/**
	 * Liefert die Schussfrequenz der Waffe
	 */
	public int getRof() {
		return frof;
	}
	
	/**
	 * Liefert die ID es Bildes
	 */
	public String getBild() {
		return fbild;
	}

	/**
	 * Liefert die Fluggeschwindigkeit der Geschosse dieser Waffe
	 */
	public int getGeschw() {
		return fgeschw;
	}

	/**
	 * Liefert den Typ des Partikeleffekts, den ein Treffer eines
	 * Geschosses dieser Waffe hervorrufen soll
	 */
	public TPartikelVerwaltung.Partikel getPartikelTreff() {
		return fpartikeltreff;
	}

	/**
	 * Liefert den Typ des Partikeleffekts, den ein Abschuss eines
	 * Gegners mit einem Geschoss dieser Waffe hervorrufen soll
	 */
	public TPartikelVerwaltung.Partikel getPartikelTot() {
		return fpartikeltot;
	}

	/**
	 * Liefert den Name der Waffe, fuer HUD
	 */
	public String toString() {
		return fbez;
	}

	/**
	 * Liefert die ID der Waffe, fuer Savegames
	 */
	public String getID() {
		return fid;
	}

	/**
	 * Liefert die ID des Sounds, der beim Abfeuern eines
	 * Geschosses mit dieser Waffe gespielt werden soll
	 */
	public String getStartSound() {
		return fstartsound;
	}
	
	/**
	 * Liefert die ID des Sounds, der beim Treffen eines
	 * eines Gegners mit einem Geschoss dieser Waffe gespielt werden soll
	 */
	public String getHitSound() {
		return fhitsound;
	}

	/**
	 * Liefert die ID des Sounds, der beim Abschiessen eines
	 * eines Gegners mit einem Geschoss dieser Waffe gespielt werden soll
	 */
	public String getTotSound() {
		return ftotsound;
	}

	/**
	 * Liefert die Anzahl der Ticks, die der Bildschirm wackeln soll,
	 * wenn ein Gegner mit dieser Waffe abgeschossen wurde
	 */
	public int getBeben() {
		return fbeben;
	}
}	

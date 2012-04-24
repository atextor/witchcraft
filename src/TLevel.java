package de.rccc.java.witchcraft;

import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 * Klasse, die fuer das Laden, repraesentieren (Modell) und Darstellen
 * des Levels sowie eventueller grafischen Meldungen zustaendig ist.
 */
public class TLevel {
	/**
	 * Klasse fuer ein einzelnes Levelsegment: der kleinste unteilbare
	 * Teil eines Levels
	 */
	private class TLevelSegment {
		/**
		 * Bild fuer das Levelsegment
		 */
		private TAnimation fdarstellung;

		/**
		 * Meldung fuer den Spieler, die beim Aktivieren des Segments
		 * einmalig angezeigt werden soll
		 */
		private String fmeldung = null;

		/**
		 * Wurden die Feinde aus der lokalen Liste schon aktiviert?
		 */
		private boolean faktiv;

		/**
		 * Ambiente-Sounds
		 */
		private String fsound = null;

		/**
		 * Liste der Feinde, die mit diesem
		 * Segment erscheinen sollen. Diese "schlafen" hier bis
		 * das Segment sichtbar wird.
		 */
		private List<TFeind> ffeinde = new LinkedList<TFeind>();

		/**
		 * Konstruktor
		 * @param darstellung Das Bild fuer das Levelsegment
		 */
		TLevelSegment(TAnimation darstellung) {
			fdarstellung = darstellung;
			faktiv = false;
		}

		/**
		 * Lebewesen zur Liste zufuegen
		 */
		public void addFeind(TFeind f) {
			ffeinde.add(f);
		}

		/**
		 * Liefert die Liste der Lebewesen
		 */
		public List<TFeind> getFeinde() {
			return ffeinde;
		}

		/**
		 * Zeichnet das Levelsegment
		 */
		public void zeichne(java.awt.Graphics g, int offset) {
			g.drawImage(fdarstellung.getFrame(), offset, 0, null);
		}

		/**
		 * Fuege die lokalen, schlafenden Feinde in die
		 * eigentliche Feinde-Liste ein und aktiviere eine eventuell
		 * in diesem Segment vorhandene Meldung
		 */
		public void aktiviere() {
			TAnzeige anzeige = TSharedObjects.getAnzeige();
			if (!faktiv) {
				faktiv = true;
				anzeige.getLebewesen().addAll(ffeinde);

				// irgendwelche geloopten Sounds starten
				for (TFeind feind: ffeinde) {
					if (feind.getFeindDef().getDauersound() != null) {
						TSound.playMusik(feind.getFeindDef().getDauersound());
					}
				}

				// Meldung anzeigen und Spiel pausieren, falls vorhanden
				if (fmeldung != null) {
					setAktuelleMeldung(fmeldung);
					anzeige.pause();
				}

				// Ambientesound spielen
				TSound.play(fsound);
			}
		}

		/**
		 * Setze die Meldung, die beim Aktivieren des Segments
		 * einmal angezeigt werden soll
		 */
		public void setMeldung(String meldung) {
			fmeldung = meldung;
		}

		/**
		 * Setzen den Sound, der abgespielt werden soll.
		 * Als "Ambiente"
		 */
		public void setSound(String sound) {
			fsound = sound;
		}
	}

	/**
	 * Name des Levels
	 */
	private String fname;

	/**
	 * Liste aller Segmente, die das Level ausmachen
	 */
	private LinkedList<TLevelSegment> flevel =
		new LinkedList<TLevelSegment>();

	/**
	 * Da immer nur eine Meldung aktiv sein kann, kann sich das
	 * Level direkt darum kuemmern
	 */
	private TBildObjekt faktuelleMeldung;

	/**
	 * Breite in Pixel eines Segments. Da diese fuer alle Segmente
	 * gleich ist, braucht die Unterklasse TLevelSegment davon nichts
	 * zu wissen
	 */
	private static int fsegmentBreite = 80;

	/**
	 * Die Anzahl der Segmente, die nebeneinander auf der Spielflaeche
	 * Platz haben
	 */
	private static int fanzahlSegmente;

	/**
	 * Wie weit verschoben ist das linkeste sichtbare Levelsegment
	 */
	private int foffset = 0;

	/**
	 * Nummer des Levels, das geladen wurde
	 */
	private int flevelNummer;

	/**
	 * Zaehlt die Anzahl der Segmente, an denen der Spieler schon
	 * vorbeigeflogen ist. Diese Zahl wird in das Savegame gespeichert
	 */
	private int fgeloeschteSegmente = 0;

	/**
	 * Anzahl der "aufgefuellten" Segmente - diese sollen beim
	 * erneuten Speichern nicht nochmal mitgespeichert werden
	 */
	private int fpadSegmente = 0;

	/**
	 * Map fuer die Segmentgrafiken (gleiche Segmente koennen das
	 * selbe TAnimation-Objekt zur Darstellung verwenden)
	 */
	private Map<String, TAnimation> segmentGrafiken = 
		new HashMap<String, TAnimation>();

	/**
	 * Konstruktor mit Level-Nummer
	 * @param nummer Die Nummer des Levels, das geladen werden soll
	 */
	public TLevel(int nummer) throws Exception {
		this(nummer, 0, false);
	}

	/**
	 * Konstruktor mit Level-Nummer und Anzahl der Segmente, die
	 * nicht geladen werden sollen
	 * @param nummer Die Nummer des Levels, das geladen werden soll
	 * @param ueberspring Ignoriere diese Anzahl von Segmenten im Level
	 * @param pad Wurde das Level geladen und soll vorne aufgefuellt
	 * werden: (Gegner sollen an vorgesehenen Positionen
	 * starten, nicht alle auf einmal, ausserdem steckt der Spieler
	 * nicht direkt im Gemetzel)
	 */
	public TLevel(int nummer, int ueberspring, boolean pad)
		throws Exception {

		faktuelleMeldung = null;
		fanzahlSegmente = TSharedObjects.FENSTER_BREITE / fsegmentBreite;
		flevelNummer = nummer;

		String levelDatei = "/level/level" + nummer + ".xml";
		java.net.URL pfad = TLevel.class.getResource(levelDatei);

		System.out.println("Lade Level " + nummer + ": "+levelDatei);

		if (pfad == null) {
			throw new Exception(levelDatei + " nicht gefunden");
		}

		//vorbereiten: Jeder Level hat seine eigenen Feinde
		TFeindDef.leereListe();

		String nodeName;
		Document config = DocumentBuilderFactory
						.newInstance().newDocumentBuilder()
						.parse(pfad.toURI().toString());
		NodeList nds = config.getChildNodes();

		if (!nds.item(0).getNodeName().equals("level")) {
			throw new Exception("Fehlerhafte Leveldatei: " + levelDatei +
					" (<level> als Root-Element erwartet)");
		}

		// Level wurde geladen, wir wollen erstmal auffuellen
		if (pad) {
			fpadSegmente = fanzahlSegmente;
			segmentGrafiken.put("1", new TAnimation("level" +
				nummer + "seg1.gif"));
			for (int i = 1; i <= fanzahlSegmente; i++) {
				flevel.add(new TLevelSegment(segmentGrafiken.get("1")));
			}
		}

		// jetzt Nodes verarbeiten
		nds = nds.item(0).getChildNodes(); 
		for (int i = 0; i < nds.getLength(); i++) {
			if (nds.item(i) instanceof Element) {
				nodeName = nds.item(i).getNodeName();
				if (nodeName.equals("name")) {
					fname = nds.item(i).getTextContent();
				} else if (nodeName.equals("struktur")) {
					ladeSegmente(nds.item(i).getChildNodes(), nummer, ueberspring);
				} else if (nodeName.equals("spieler")) {
					ladeSpielerDaten(nds.item(i).getChildNodes());
				} else if (nodeName.equals("feinde")) {
					ladeFeindDefs(nds.item(i).getChildNodes());
				}
			}
		}

		if (!pad) {
			// die ersten Segemente auf dem Bildschirm aktivieren
			for (int i = 0; i < fanzahlSegmente; i++) {
				flevel.get(i).aktiviere();
			}
		}
	}

	/**
	 * Setzt die aktuell anzuzeigende Meldung an einer zentrierten Position
	 * @param meldung Der Eintrag der Bilderliste, der angezeigt werden soll
	 */
	public void setAktuelleMeldung(String meldung) {
		// Wenn Spieler Tot, dann keine weitere (Level-)Meldung
		if (TSharedObjects.getAnzeige().fspieler == null) {
			return; // Nur mt Protest!
		}
		TAnimation bild = TSharedObjects.getBild(meldung);
		int breite = (int)bild.groesse().x;
		int hoehe = (int)bild.groesse().y;
		faktuelleMeldung = new TBildObjekt(
			new TVektor((TSharedObjects.FENSTER_BREITE / 2) - (breite / 2),
						(TSharedObjects.FENSTER_HOEHE / 2) - (hoehe / 2)),
			null, null, meldung);
	}

	/**
	 * Laedt ein Level aus einer Nodeliste (struktur)
	 * @param nds Die Nodeliste der Segmente
	 * @param nummer Die Nummer des Levels
	 * @param ueberspring Die Anzahl der Segmente, die ignoriert werden sollen
	 */
	private void ladeSegmente(NodeList nds, int nummer,
		int ueberspring) throws Exception {

		String nodeName;

		for (int i = 0; i < nds.getLength(); i++) {
			String segmentInhalt  = null;
			String segmentMeldung = null;
			String segmentSound   = null;

			if (nds.item(i) instanceof Element) {
				nodeName = nds.item(i).getNodeName();
				// Wir interessieren uns nur fuer Segmente, und nur dann,
				// wen wir schon genug uebersprugngen haben (beim Laden)
				if ((nodeName.equals("segment")) && (--ueberspring < 0)) {
					for (int j = 0; j < nds.item(i).getChildNodes().getLength(); j++) {
						if (nds.item(i).getChildNodes().item(j)
							.getNodeName().equals("nummer")) {
							segmentInhalt = nds.item(i).getChildNodes()
								.item(j).getTextContent();
						} else if (nds.item(i).getChildNodes().item(j)
								.getNodeName().equals("meldung")) {
							segmentMeldung = nds.item(i).getChildNodes()
								.item(j).getTextContent();
						} else if (nds.item(i).getChildNodes().item(j)
								.getNodeName().equals("sound")) {
							segmentSound = nds.item(i).getChildNodes()
								.item(j).getTextContent();
						}
					}

					// Wenn wir das Segment noch nicht kennen, erzeugen wir es...
					if (segmentGrafiken.get(segmentInhalt) == null) {
						segmentGrafiken.put(segmentInhalt,
							new TAnimation("level" + nummer + "seg" +
							segmentInhalt + ".gif"));
					}

					TLevelSegment tmpseg = new TLevelSegment(
						segmentGrafiken.get(segmentInhalt));
					tmpseg.setMeldung(segmentMeldung);
					tmpseg.setSound(segmentSound);

					// ...ermitteln seine Feinde...
					verarbeiteSegment(nds.item(i).getChildNodes(), nummer, tmpseg);
					// ...und fuegen es dem Level zu
					flevel.add(tmpseg);
				} else if (!nodeName.equals("segment")) {
					throw new Exception("Fehler beim Laden von Level " +
						"level/level" + nummer + ".xml (<segment> erwartet)");
				}
			}
		}
	}

	/**
	 * Lädt Spielerangaben
	 *
	 * @param nds Die Nodeliste des Spielers
	 */
	private void ladeSpielerDaten(NodeList nds) throws Exception {
		for (int j = 0; j < nds.getLength(); j++) {
			Node a = nds.item(j);
			if (a.getNodeName().equals("waffe")) {
				TSharedObjects.getAnzeige().fspieler.addWaffe(a.getTextContent());
			}
		}
		
	}
	
	/**
	 * Lädt die Feinddefinitionen
	 *
	 * @param nds Die Nodeliste des Spielers
	 */
	private void ladeFeindDefs(NodeList nds) throws Exception {
		System.out.println("Lade FeindDefinition");
		for (int j = 0; j < nds.getLength(); j++) {
			Node a = nds.item(j);
			
			if (a.getNodeName().equals("feind")) {
				if (!TFeindDef.addFeindDefAusNode(a)) {
					throw new Exception("Fehler beim Laden der Feinddefinitionen");
				}
			}

		}
	}
	

	/**
	 * verarbeiteSegment fuegt die in der NodeList nds gefundenen Feinde
	 * an die Liste des TLevelSegment seg an
	 * @param nds Die zu verarbeitende NodeListe
	 * @param nummer Die Nummer des Levels (fuer Fehlerausgaben)
	 * @param seg Das Segment, bei dem die Gegner zugefuegt werden sollen
	 */
	private void verarbeiteSegment(NodeList nds, int nummer,
		TLevelSegment seg) throws Exception {

		String levelDatei = "level/level" + nummer + ".xml";
		String nodeName;

		// alle Feinde durchgehen
		for (int i = 0; i < nds.getLength(); i++) {
			if ((nds.item(i) instanceof Element) &&
					(nds.item(i).getNodeName().equals("feind"))) {
				
				String id = null; 
				int startpunkt = -1;
				TItem item = null;

				for (int j = 0; j < nds.item(i).getChildNodes().getLength(); j++) {
					Node a = nds.item(i).getChildNodes().item(j);
					if (a.getNodeName().equals("startpunkt")) {
						startpunkt = Integer.parseInt(a.getTextContent());
					} else if (a.getNodeName().equals("ID")) {
						id = a.getTextContent();
					} else if (a.getNodeName().equals("item")) {
						if (item != null) {
							throw new Exception("Fehler in Leveldatei " + levelDatei +
								" (Ein Feind kann nur ein Item haben)");
						}
						item = TItem.itemAusNode(a.getChildNodes());
					}
				}
				
				// Alle Angaben fuer den Feind muessen gemacht sein
				if ((id == null) || (startpunkt == -1)) {
					throw new Exception("Fehler in Leveldatei " + levelDatei +
						" (id oder  startpunkt " +
						"fehlt bei feind in segment)");
				}

				// Ausserdem muss das Bild bereits geladen sein
				TFeindDef feinddef = TFeindDef.getFeindDef(id);
				if (feinddef != null) {
					seg.addFeind(new TFeind(feinddef, startpunkt, 1, item));
				} else {
					throw new Exception("Fehler in Leveldatei " + levelDatei +
						" (Feinddef '" + id + "' ist nicht definiert)");
				}
			}
		}
	}

	/**
	 * Level einen Tick weiter scrollen
	 */
	public void weiterScrollen() {
		foffset -= 4;
		if (foffset < -fsegmentBreite) {
			foffset = 0;
			if (flevel.size() > (fanzahlSegmente + 1)) {
				flevel.get(fanzahlSegmente + 1).aktiviere();
				flevel.removeFirst();
				fgeloeschteSegmente++;
				if (fpadSegmente > 0) {
					fpadSegmente--;
				}
			} else {
				// Wenn Level zuende ist, dann das vorderste hinten einketten.
				TLevelSegment vorne = flevel.get(0);
				flevel.removeFirst();
				flevel.add(vorne);
			}
		}
	}

	/**
	 * Ist das Level schon am Ende?
	 *
	 * @return true, wenn das Level am Ende ist, und nur noch wiederholt wird
	 */
	public boolean levelZuEnde() {
		return flevel.size() <= (fanzahlSegmente + 1);
	}

	/**
	 * Level zeichnen
	 */
	public void zeichne(java.awt.Graphics g) {
		int count = 0;
		for (TLevelSegment ls: flevel) {
			ls.zeichne(g, (count * fsegmentBreite) + foffset);

			count++;
			if (count >= (fanzahlSegmente + 2))
				return;
		}
	}

	/**
	 * Zeichnet die eventuell aktive Segmentmeldung
	 */
	public void zeichneMeldung(java.awt.Graphics g) {
		if (faktuelleMeldung != null) {
			faktuelleMeldung.zeichne(g);
		}
	}

	/**
	 * Deaktiviere alle eventuell aktiven Meldungen
	 */
	public void deaktiviereMeldung() {
		faktuelleMeldung = null;
	}

	/**
	 * Liefert die Nummer des aktuell geladenen Levels
	 */
	public int getLevelNummer() {
		return flevelNummer;
	}

	/**
	 * Liefert die Anzahl der Segmente, an denen der Spieler schon
	 * vorbeigeflogen ist - ausgenommen diese, die extra nach dem
	 * Laden eines Savegames aufgefuellt wurden
	 */
	public int getGeloeschteSegmente() {
		int segs = fgeloeschteSegmente - fpadSegmente +
			fanzahlSegmente;
		return (segs < 0) ? 0 : segs;
	}
}


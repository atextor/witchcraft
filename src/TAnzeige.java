package de.rccc.java.witchcraft;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 * TAnzeige ist die eigentliche Hauptklasse des Spiels: Hier gibt es
 * Listen fuer bewegte Objekte, Objekte fuer Spieler, Fadenkreuz usw,
 * sowie die Maus- und Tastaturlistener
 */
class TAnzeige extends JPanel {
	/**
	 * Am LevelEnde soll das Bild noch so lange stehen bleiben
	 */
	protected final static int fLEVELSTEHEN = 100;

	/**
	 * Gibt an, ob das Spiel als Applet ausgefuehrt wird (beeinflusst
	 * zB, ob Laden und Speichern moeglich sein soll
	 */
	protected boolean fapplet;

	/**
	 * Vektor, der die Dimension des Fenster enthaelt
	 */
	protected TVektor fdim;
	
	/**
	 * Liste der Geschosse, die auf der Spielflaeche rumfliegen
	 */
	protected java.util.List<TGeschoss> fgeschosse =
		new ArrayList<TGeschoss>();

	/**
	 * Liste der Lebewesen (Spieler und Gegner)
	 */
	protected java.util.List<TLebewesen> flebewesen =
		new ArrayList<TLebewesen>();

	/**
	 * Liste von Objekten ohne Spieleinfluss. Diese werden auch
	 * nicht bewegt, z.B. die Textobjekte, die die Sonderpunkte
	 * anzeigen.
	 */
	protected java.util.List<TObjekt> fobjekte =
		new ArrayList<TObjekt>();

	/**
	 * Liste von Items (vom Spieler einsammelbare Gegenstaende)
	 */
	protected java.util.List<TItem> fitems =
		new ArrayList<TItem>();

	/**
	 * Der spielablaufsteuernde Timer
	 */
	protected javax.swing.Timer fzeitgeber;

	/**
	 * Der Timer, der fuer das Neuzeichnen des Fensters verantwortlich ist
	 */
	protected javax.swing.Timer fupdater;

	/**
	 * Der Spieler
	 */
	protected TSpieler fspieler = null;

	/**
	 * Das Fadenkreuz
	 */
	protected TBildObjekt ffadenkreuz = null;

	/**
	 * Unsichtbarer Cursor (damit das Fadenkreuz nicht von einem haesslichen
	 * Pfeil ueberlagert wird)
	 */
	protected Cursor finvisiCursor = null;

	/**
	 * Das Objekt, das den Level darstellt
	 */
	protected TLevel flevel = null;

	/**
	 * Hat einen Wert zwischen 0 und 1, der angibt, wie sichtbar stark
	 * das Spielfeld ausgefaded ist (verdunkelt)
	 */
	protected double ffader = 1.;

	/**
	 * Gibt an, ob gerade gefaded werden soll: -1 einfaden, 0 nicht faden,
	 * 1 ausfaden
	 */
	protected int ffade = 0;

	/**
	 * Die Farbe, zu der hin gefaded werden soll
	 */
	protected Color ffadeColor = new Color(0, 0, 0);

	/**
	 * Ist hohes Grafikdetail eingestellt? (Betrifft Partikelsystem
	 * und Hintergrund)
	 */
	protected boolean fhdetail = true;

	/**
	 * Einzeilige Textmeldung, die ueber allen anderen Sachen gezeichnet wird.
	 * Fuer Meldungen wie "Spiel gespeichert." usw.
	 */
	protected TText fmeldung = null;

	/**
	 * Gibt an, ob bereits ein Spiel gestartet wurde (mittels Neu/Laden)
	 */
	protected boolean fspielGestartet = false;

	/**
	 * Gibt an, ob das Spiel gerade pausiert ist
	 */
	protected boolean fpausiert = false;

	/**
	 * Gibt an, ob das Level gerade gestartet wurde, und noch
	 * die Stats weiter angezeigt werden sollen (d.h., dieser
	 * Wert darf nur true werden, wenn wir schon ein Level gespielt
	 * haben)
	 */
	protected boolean flevelGestartet = false;

	/**
	 * Gibt den aktuellen Schwierigkeitsgrad an
	 */
	protected TSpieler.Schwierigkeitsgrade fschwierigkeit =
		TSpieler.Schwierigkeitsgrade.normal;

	/**
	 * Ist dieser Wert != 0, so "wackelt" das Bild entsprechend
	 * (bei Explosionen)
	 */
	protected int fbeben = 0;

	/**
	 * Nachdem das Level vorbei ist, soll es noch etwas angezeigt werden.
	 */
	protected int flevelwarte = fLEVELSTEHEN;

	/**
	 * Getter fuer die Lebewesen-Liste
	 * @return Liste der Lebewesen
	 */
	public java.util.List<TLebewesen> getLebewesen() {
		return flebewesen;
	}

	/**
	 * Methode, die bestimmt, wie gross das Fenster dargestellt wird
	 * @return Dimension-Objekt, das die Fenstergroesse repraesentiert
	 */
	public java.awt.Dimension getPreferredSize() {
		return new java.awt.Dimension((int)fdim.x, (int)fdim.y);
	}

	/**
	 * Startet das Einfaden (aufhellen)
	 */
	public void fadeIn() {
		ffader = 1.;
		ffade = -1;
	}
	
	/**
	 * Startet das Ausfaden (abdunkeln)
	 */
	public void fadeOut() {
		ffader = 0.;
		ffade = 1;
	}

	/**
	 * Bringt den Alphawert fuer das Faden auf den neuesten Stand
	 * (faded ein oder aus, je nach momentaner Einstellung)
	 */
	public void fadeUpdate() {
		if (ffade != 0) {
			ffader += ((double)ffade / 10);
			if (ffader < 0) {
				ffader = 0;
				ffade = 0;
			} else if (ffader > 1) {
				ffader = 1;
				ffade = 0;
			}
		}
	}

	/**
	 * Methode, die beim Zeichnen des Fensters aufgerufen wird.
	 * Diese Methode steuert alle Sichtbarkeiten und ruft ggf.
	 * Zeichenmethoden von Unterobjekten auf
	 * @param g Die Zeichenflaeche, auf der gezeichnet werden soll
	 */
	public void paintComponent(java.awt.Graphics g) {
		super.paintComponent(g);
		if (fhdetail) {
			g.drawImage(TSharedObjects.getBild("bg").getFrame(), 0, 0, null);
		}
		
		int beben = 0;
		if (fbeben > 0) {
			beben = TSharedObjects.rndInt(fbeben*2) - fbeben;
			g.translate(beben, beben);
		}

		flevel.zeichne(g);

		for (TLebewesen l: flebewesen) {
			l.zeichne(g);
		}

		for (TGeschoss ge: fgeschosse) {
			ge.zeichne(g);
		}
		
		for (TItem i: fitems) {
			i.zeichne(g);
		}

		for (TObjekt o: fobjekte) {
			o.zeichne(g);
		}

		TSharedObjects.getPartikelVerwaltung().zeichne(g);

		if (beben != 0) {
			g.translate(-beben, -beben);
			fbeben--;
		}
		ffadenkreuz.zeichne(g);

		fadeUpdate();
		if (ffader > 0.001) {
			Graphics2D g2 = (Graphics2D)g;
			Composite originalComposite = g2.getComposite();
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				(float)ffader / 2));
			g2.setColor(ffadeColor);
			g2.fillRect(0, 0, (int)fdim.x, (int)fdim.y);
			g2.setComposite(originalComposite);
		}

		flevel.zeichneMeldung(g);
		fmeldung.zeichne(g);
		if ((flevelwarte < fLEVELSTEHEN) || flevelGestartet) {
			// Der Spieler kann dann noch abkratzen...
			if (fspieler != null) {
				fspieler.zeichneStats(g);
			}
		}
	}

	/**
	 * Geschosse bewegen und auf Kollision abfragen
	 */
	private void bewegeGeschosse() throws Exception {
		for (Iterator<TGeschoss> i = fgeschosse.iterator();i.hasNext();) {
			TGeschoss ge = i.next();
			ge.bewege();
			boolean weiter = true;
			boolean weg = false;
			for (Iterator<TLebewesen> j = flebewesen.iterator();
				j.hasNext() && weiter;) {

				TLebewesen l = j.next();
				if (ge.beruehrt(l)) {
					ge.ende(true);
					if (l.treffer(ge.getTrefferp())) {
						l.ende(true, ge.getWaffe());
						j.remove();

						// der Spieler hat ausgespielt
						if (fspieler == l) {
							setGameOver();
							fspieler = null; // NACH der Meldung!
							TSound.play("GAMEOVER");
						}
					}
					// Die Rail z.B. soll etwas stehen bleiben
					if (ge.getTtl() < 0) {
						weg = true;
					}
					// Ein Einschlag reicht
					weiter = false;
				}
			}

			if ((ge.ausserhalbBildschirm()) || ge.tot()) {
				if (weg) {
					// Das Geschoss ist aufgeschlagen
					ge.ende(false);
				}
				weg = true;
			}
			if (weg) {
				i.remove();
			}
		}
	}

	/**
	 * Zeigt die Gameover-Meldung und die aktuelle Punktzahl
	 */
	private void setGameOver() {
		flevel.setAktuelleMeldung("GAMEOVER");
		addObjekt(new TText(new TVektor(150, 100),
			"Erreichte Punkte: " + fspieler.getScore(),
			new Font("Arial", Font.BOLD, 28)));
	}

	/**
	 * Die Methode, die fuer das Bewegen aller Objekte zustaendig ist
	 */
	public void bewege() throws Exception {
		flevel.weiterScrollen();
		 
		for (Iterator<TLebewesen> i = flebewesen.iterator(); i.hasNext();) {
			TLebewesen l = i.next();
			l.bewege();
			// Wenn das Lebewesen außerhalb des Bildschirms ist, weg damit
			if (l.ausserhalbBildschirm()) {
				l.ende(false, null);
				i.remove();
			}
		}

		bewegeGeschosse();

		// Die Items durchiterieren..
		// Auf Kollission ueberpruefen nur, wenn der Spieler noch existiert
		for (Iterator<TItem> i = fitems.iterator(); i.hasNext();) {
			TItem akt = i.next();
			akt.bewege();
			if (fspieler != null) {
				if (fspieler.beruehrt(akt)) {
					fspieler.addItem(akt);
					i.remove();
					akt.ende(true);
				}
			}
			if ((akt.ausserhalbBildschirm())) {
				i.remove();
				akt.ende(false);
			}
		}
		
		TSharedObjects.getPartikelVerwaltung().update();

		// Level geschafft?
		if ((fspieler != null) && (flebewesen.size() == 1) &&
				(fitems.size() == 0) && (flebewesen.get(0) == fspieler) &&
				(flevel.levelZuEnde())) {
			// einmal reicht nicht: im alten Level die Meldung setzen
			// (vor dem Laden), und auch im neu geladenen level
			flevel.setAktuelleMeldung("LEVELPASSED");
			flevelwarte--;
			if (flevelwarte <= 0) {
				naechsterLevel();
			}
		}
	}

	/**
	 * Laedt den naechsten Level und zeigt eine entsprechende Meldung
	 */
	private void naechsterLevel() {
		TLevel oldlevel = flevel;
		reset();
		try {
			if (flevel == null) {
				flevel = new TLevel(1);
			} else {
				flevel = new TLevel(flevel.getLevelNummer() + 1);
			}
			// einmal reicht nicht: im alten Level die Meldung setzen
			// (vor dem Laden), und auch im neu geladenen Level,
			// weil die Meldung ueber Levelgrenzen hinweg stehen soll
			flevel.setAktuelleMeldung("LEVELPASSED");
			flevelwarte = fLEVELSTEHEN;
			flevelGestartet = true;
			pause("Mit Leertaste weiterspielen");
		} catch (Exception e) {
			// wir haben keine Level mehr...
			// damit das nicht schlecht aussieht, muss jedes level
			// mit einer Bildschirmbreite Einheitssegmente enden
			// Es könnten zwar auch andere Fehler auftreten, aber die
			// Reaktion darauf soll die Gleiche bleiben.
			System.out.println(e);
			flevel = oldlevel;
			setGameOver();
			fspieler = null;
		}
	}

	/**
	 * Liefert das Lebewesen, das sich unter dem Fadenkreuz befindet,
	 * sofern vorhanden
	 * @param x Die X-Koordinate, die ueberprueft werden soll
	 * @param y Die Y-Koordinate, die ueberprueft werden soll
	 * @param nichtseite Das Lebewesen soll nicht dieser Seite angehören
	 * (z.b. Soll der Spieler sich nicht selbst anvisieren)
	 * @return null oder das Lebewesen unter dem Fadenkreuz
	 */
	public TLebewesen lebewesenUnterMaus(int x, int y, int nichtseite) {
		TLebewesen ret = null;

		for (TLebewesen l: flebewesen) {
			if ((l.getSeite() != nichtseite) &&
				(l.innerhalb(new TVektor(x, y)))) {

				ret = l;
			} 
		}

		return ret;
	}

	/**
	 * Das Spiel soll pausiert werden, weil jemand eins der Zusatz-
	 * Fenster geoeffnet hat (mit entsprechender Meldung)
	 */
	public void pauseDurchMenu() {
		pause(fspielGestartet ? "Spiel pausiert - bitte Info-Fenster " +
			"schliessen zum Weiterspielen" : " ");
	}

	/**
	 * Pausiert das Spiel, mit Default-Meldung
	 */
	public void pause() {
		pause(fspielGestartet ? "PAUSE - Leertaste drücken zum " +
			"Weiterspielen" : " ");
	}

	/**
	 * Pausiert das Spiel, mit konfigurierbarer Meldung
	 * @param m Die Meldung, die auf der abgedunkelten Spielflaeche
	 * stehen soll
	 */
	public void pause(String m) {
		// Wenn der Spieler nicht mehr ist, gibt es auch keine Pause mehr
		if ((fspieler != null) && !fpausiert) {
			fpausiert = true;
			fadeOut();
			fmeldung.setText(m);
			fzeitgeber.stop();
		}
	}

	/**
	 * Startet den Zeitgeber (neu) und loescht evtl vorhandene
	 * Pause-Meldungen
	 */
	public void start() {
		// Um ein Laggen am Anfang zu vermeiden, rufen wir
		// den Garbage-Collector von Hand auf
		System.gc();

		if ((fspieler != null) && flevelGestartet) {
			fspieler.statsRuecksetzen();
		}

		flevelGestartet = false;
		fpausiert = false;
		fmeldung.setText(null);
		fadeIn();

		if (fspielGestartet) {
			fzeitgeber.start();
			fupdater.start();
		}
	}

	/**
	 * Fuegt ein Objekt zur allgemeinen Objektliste zu
	 * @param o Das Objekt, das zur Objektliste zugefuegt werden soll
	 */
	public void addObjekt(TObjekt o) {
		fobjekte.add(o);
	}

	/**
	 * Fuegt ein Geschoss zur Geschossliste zu
	 * @param g Das Geschoss, das zur Geschossliste zugefuegt werden soll
	 */
	public void addGeschoss(TGeschoss g) {
		fgeschosse.add(g);
	}

	/**
	 * Fuegt ein Lebewesen (Spieler oder Feind) zur Lebewesenliste zu
	 * @param l Das Lebewesen, das zur Lebewesenliste zugfuegt werden soll
	 */
	public void addLebewesen(TLebewesen l) {
		flebewesen.add(l);
	}
	
	/**
	 * Fuegt ein Item (vom Spieler einsammelbares Objekt) zur Itemliste zu
	 * @param i Das Item, das zur Itemliste zugefuegt werden soll
	 */
	public void addItem(TItem i) {
		fitems.add(i);
	}

	/**
	 * Wird aufgerufen, wenn die Maus bewegt wurde, und dieses
	 * an den Spieler wietergegeben werden muss.
	 * @param x X-Koodiante der Maus
	 * @param y Y-Koodiante der Maus
	 */
	public void aktionmouseMoved(int x, int y) {
		if (fspieler != null) {
			fspieler.zielgeaendert(lebewesenUnterMaus(x, y,
				fspieler.getSeite()), x, y);
		}
	}

	/**
	 * Setzten den Wert von fbeben und loest damit ein Wackeln
	 * des Bildschirms aus
	 * @param beben das Beben
	 */
	public void setBeben(int beben) {
		fbeben = beben;
	}

	/**
	 * Versucht, das Spiel zu speichern, und gibt eine Erfolgsmeldung
	 * aus, sofern angebracht
	 */
	public void speichere() {
		if (fspieler != null) {
			try {
				TSavegame.speichere(fspieler, flevel);
				fmeldung.setTimeoutText("Spiel gespeichert.");
				TSharedObjects.getMain().enableMenuItem("spiel_laden", true);
			} catch (Exception e) {
				fmeldung.setTimeoutText("Spiel speichern fehlgeschlagen!");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Bereitet alles auf das Laden eines Spieles vor, und laedt
	 * anschliessend das Spiel
	 */
	public void lade(int spielnummer) {
		try {
			fspielGestartet = true;
			pause();
			fupdater.stop();
			reset();
			fspieler.setScore(0);
			fspieler.leereWaffen();
			fspieler.statsRuecksetzen();
			flevel = TSavegame.lade(spielnummer, fspieler);
			flevelwarte = fLEVELSTEHEN;
			fupdater.start();
			fmeldung.setText("Spiel geladen - Leertaste drücken zum " +
				"Weiterspielen");
		} catch (Exception e) {
			fmeldung.setTimeoutText("Spiel laden fehlgeschlagen!");
			e.printStackTrace();
		}
	}

	/**
	 * Setzt alles (Geschosse, Gegner, etc) zurueck, damit ein
	 * neues oder geladenes Spiel sauber anfaengt
	 */
	protected void reset() {
		setCursor(finvisiCursor);
		if (fspieler == null) {
			fspieler = new TSpieler(fschwierigkeit);
		} else {
			fspieler.setSchwierigkeit(fschwierigkeit);
			fspieler.leereWaffenListe();
		}

		TSharedObjects.getPartikelVerwaltung().reset();
		for(TLebewesen lw: flebewesen) {
			lw.ende(false, null);
		}
		flebewesen.clear();
		fgeschosse.clear();
		fitems.clear();
		fobjekte.clear();
		flebewesen.add(fspieler);
		fspieler.setKoord(new TVektor(5, 200));
		if (!fapplet) {
			TSharedObjects.getMain().enableMenuItem("spiel_speichern", true);
		}
		TWaffe.resetStatistik();
	}

	/**
	 * Wird aufgerufen, wenn der Spieler im Menue "Spiel - Neu" auswaehlt
	 * und sich im Optionen-Dialog durchgeklickt hat
	 * @param name Der Name des Spielers
	 * @param schwierigkeit Der Schwierigkeitsgrad
	 */
	public void neuesSpiel(String name,
		TSpieler.Schwierigkeitsgrade schwierigkeit) {

		pause();
		fschwierigkeit = schwierigkeit;
		reset();
		try {
			flevel = new TLevel(1);
			flevelwarte = fLEVELSTEHEN;
		} catch (Exception e) {
			fmeldung.setTimeoutText("Level laden fehlgeschlagen!");
			e.printStackTrace();
		}
		fspielGestartet = true;
		fspieler.setName(name);
		flevel.deaktiviereMeldung();
		start();
	}

	/**
	 * Setzt das Grafikdetail und gibt eine Meldung aus ueber
	 * die neue Einstellung
	 * @param d true fuer hohes Detail, false fuer niedriges Detail
	 */
	public void setHohesDetail(boolean d) {
		String meldung;

		fhdetail = d;
		if (d) {
			meldung = "Hohes Detail ausgewählt.";
		} else {
			meldung = "Niedriges Detail ausgewählt.";
		}
		
		// nur melden wenn wir nicht grade pausieren
		if (!fspielGestartet || fzeitgeber.isRunning()) {
			fmeldung.setTimeoutText(meldung);
		}
	}

	/**
	 * Setzt einen Info-Text
	 * @param text Der zu setzende Text
	 */
	public void setMeldung(String text) {
		fmeldung.setTimeoutText(text);
	}
	
	/**
	 * Bugfix: Wenn man eine Taste gedrueckt haelt und dann
	 * auf das Menu klickt, "rastet" die Taste ein, dh ein
	 * ReleaseEvent wird nicht mehr erfasst; daher rufen
	 * alle Menukomponenten diese Methode bei mouseEntered
	 * Event auf
	 */
	public void tastenLoslassen() {
		if (fspieler != null) {
			fspieler.bewegen(TSpieler.Bewegung.rechts, false);
			fspieler.bewegen(TSpieler.Bewegung.links, false);
			fspieler.bewegen(TSpieler.Bewegung.unten, false);
			fspieler.bewegen(TSpieler.Bewegung.oben, false);
		}
	}

	/**
	 * Startet oder stoppt den Timer, der fuer das Neuzeichnen
	 * des Fensters verantwortlich ist
	 * @param update true fuer Anschalten, false fuer Abschalten
	 */
	public void updateEnable(boolean update) {
		if (update) {
			fupdater.start();
		} else {
			fupdater.stop();
		}
	}

	/**
	 * Konstruktor.
	 * Dieser laedt Ressourcen (Grafiken, Sounds, Config), erzeugt
	 * ein Levelobjekt (das seinerseits eine Leveldatei laedt) und erzeugt
	 * Maus- und Tastaturlistener, deren Methoden den Spielablauf steuern
	 * @param applet Ist das Spiel im Applet gestartet (true) oder als 
	 * normale Anwendung (false)
	 */
	public TAnzeige(boolean applet) {
		// generelle Einstellungen
		fapplet = applet;
		fdim = new TVektor(TSharedObjects.FENSTER_BREITE,
			TSharedObjects.FENSTER_HOEHE);
		fmeldung = new TText(new TVektor(40, 20), null,
			new Font("Arial", Font.BOLD, 13), 10);

		// unsichtbaren Cursor generieren. Den setzen wir aber erst, wenns
		// losgeht, weil es sonst verwirrend ist, wenn man keinen auf dem
		// Titelscreen sieht
		finvisiCursor = java.awt.Toolkit.getDefaultToolkit().createCustomCursor(
			new java.awt.image.BufferedImage(1, 1,
			java.awt.image.BufferedImage.TYPE_4BYTE_ABGR),
			new java.awt.Point(0, 0), "NOCURSOR");

		// wir registrieren uns als Chef
		TSharedObjects.setAnzeige(this);

		// Spieltimer erzeugen
		fzeitgeber = new javax.swing.Timer(45, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					bewege();
				} catch (Exception e) {
					System.out.println("Fehler im Spiel: " + e);
					e.printStackTrace();
					System.out.println("Beende");
					TSharedObjects.endGame();
				}
			}
		});

		// Laden der Ressourcen
		try {
			TConfig.ladeRessourcen();
			// Einige Sachen laden, damit die "Engine" läuft
			// Die werden sowieso wieder weggeschmissen
			fspieler = new TSpieler(
				TSpieler.Schwierigkeitsgrade.normal);
			flevel = new TLevel(1);
			flevel.setAktuelleMeldung("TITEL");
			//TSound.play("STARTSOUND");
		} catch(Exception e) {
			e.printStackTrace();
			TSharedObjects.endGame();
		}
		
		// Fadenkreuz erzeugen
		ffadenkreuz = new TBildObjekt(new TVektor(50, 50), null,
			new TVektor(0, 0), "FADENKREUZ");

		// Partikelverwaltung erzeugen und registrieren
		TSharedObjects.setPartikelVerwaltung(new TPartikelVerwaltung());
		TSharedObjects.getPartikelVerwaltung().setHohesDetail(fhdetail);

		// Fenstereinstellungen
		setFocusable(true);
		setBackground(new Color(70, 70, 70));
	
		// Fenster-neu-zeichnen-Timer
		fupdater = new javax.swing.Timer(40, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				repaint();
			}
		});

		// Mouse-Listener
		addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {
				ffadenkreuz.setKoord(new TVektor(e.getX()-23, e.getY()-23));
				aktionmouseMoved(e.getX(),e.getY());
			}

			public void mouseDragged(MouseEvent e) {
				ffadenkreuz.setKoord(new TVektor(e.getX()-23, e.getY()-23));
				aktionmouseMoved(e.getX(),e.getY());
			}
		});
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {}

			public void mousePressed(MouseEvent e) {
				if (fspieler != null) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						fspieler.clickaktiv();
					}
					if (e.getButton() == MouseEvent.BUTTON3) {
						fspieler.nextWaffe();
					}
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (fspieler != null) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						fspieler.clicknichtaktiv();
					}
				}
			}

			// Bugfix: Wenn man mit dem Cursor das Fenster betritt
			// oder verlaesst, soll der Spieler anhalten - die Tasten
			// sollen "losgelassen" werden
			public void mouseEntered(MouseEvent e) {
				tastenLoslassen();
			}

			public void mouseExited(MouseEvent e) {
				tastenLoslassen();
			}
		});

		// Keylistener
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int kc = e.getKeyCode();

				// Nur wenn ein Spieler da ist, kann der Spieler reagieren
				if ((fspieler != null) && fspielGestartet) {
					if ((kc == KeyEvent.VK_RIGHT) ||
						(kc == KeyEvent.VK_D)) {
						fspieler.bewegen(TSpieler.Bewegung.rechts, true);
					}

					if ((kc == KeyEvent.VK_LEFT) ||
					    (kc == KeyEvent.VK_A)) {
						fspieler.bewegen(TSpieler.Bewegung.links, true);
					}

					if ((kc == KeyEvent.VK_UP) ||
						(kc == KeyEvent.VK_W)) {
						fspieler.bewegen(TSpieler.Bewegung.oben, true);
					}

					if ((kc == KeyEvent.VK_DOWN) ||
						(kc == KeyEvent.VK_S)) {
						fspieler.bewegen(TSpieler.Bewegung.unten, true);
					}

					if (kc == KeyEvent.VK_N) {
						fspieler.nextWaffe();
					}
				}
			}

			public void keyReleased(KeyEvent e) {
				int kc = e.getKeyCode();

				if (kc == KeyEvent.VK_SPACE) {
					if (fspielGestartet) {
						if (fzeitgeber.isRunning()) {
							pause();
						} else {
							flevel.deaktiviereMeldung();
							start();
						}
					}
				}

				// Nur wenn ein Spieler da ist, kann der Spieler reagieren
				if ((fspieler != null) && fspielGestartet) {

					if ((kc == KeyEvent.VK_RIGHT) ||
						(kc == KeyEvent.VK_D)) {
						fspieler.bewegen(TSpieler.Bewegung.rechts, false);
					}

					if ((kc == KeyEvent.VK_LEFT) ||
						(kc == KeyEvent.VK_A)) {
						fspieler.bewegen(TSpieler.Bewegung.links, false);
					}

					if ((kc == KeyEvent.VK_UP) ||
						(kc == KeyEvent.VK_W)) {
						fspieler.bewegen(TSpieler.Bewegung.oben, false);
					}

					if ((kc == KeyEvent.VK_DOWN) ||
						(kc == KeyEvent.VK_S)) {
						fspieler.bewegen(TSpieler.Bewegung.unten, false);
					}
				}
			}
		});

		System.out.println("Temporäre Daten löschen...");
		// Garbage-Collector aufrufen, dadurch verhindern wir
		// das Laggen am Anfang
		System.gc();
	}
}


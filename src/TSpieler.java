package de.rccc.java.witchcraft;

import java.util.*;
import java.awt.*;

/**
 * Dieses hier ist das Objekt des Spielers.
 * Daher werden hier viele Sonderkekse gebacken.
 */
public class TSpieler extends TLebewesen {
	/**
	 * Mögliche bewegungen des Spielers. Die Eintraege einzeln
	 * zu kommentieren ist reichlich sinnlos...
	 */
	public static enum Bewegung {
		oben,
		unten,
		rechts,
		links
	};

	/**
	 * Die Schwierigkeitsgrade, die hier unterstüzt werden
	 */
	public static enum Schwierigkeitsgrade {
		/** 
		 * leicht (250 leben, 150 magie)
		 */
		leicht,
		/**
		 * normal (100 leben, 100 magie)
		 */
		normal,
		/**
		 * schwer (50 leben, 75 magie)
		 */
		schwer
	};

	/**
	 * Magie des Lebewesens
	 */
	protected double fmagie;

	/** 
	 * Maximale Magie des Lebewesens
	 */
	protected double fmaxmagie;

	/**
	 * Taste nach oben gedrückt
	 */
	protected boolean foben = false;

	/**
	 * Taste nach unten gedrückt
	 */
	protected boolean funten = false;

	/**
	 * Taste nach rechts gedrückt
	 */
	protected boolean frechts = false;

	/**
	 * Taste nach links gedrückt
	 */
	protected boolean flinks = false;

	/**
	 * Maximales Inkrement fuer beide Richtungen (Bewegungsweite)
	 */
	protected int fdmax = 5;

	/**
	 * Wie viele Punkte hat der Spieler
	 */
	protected int fscore;

	/**
	 * Koordinaten fuer den fraktenauswurf, relativ zum Spieler
	 */
	final protected static TVektor fraktenauswurf = new TVektor(50, 100);

	/**
	 * Startkoordinaten fuer den Rail-Strahl, relativ zum Spieler
	 */
	final protected static TVektor frailauswurf = new TVektor(50, 50);

	/**
	 * Koordinaten fuer den Auswurf der Dumb-Fire Waffen
	 * (Frosch-Zauber), relativ zum Spieler
	 */
	final protected static TVektor dfauswurf = new TVektor(80, 30);

	/**
	 * Ende des Besens, für die Partikel, relativ zum Spieler
	 */
	final protected static TVektor fbesenende = new TVektor( 0, 70);

	/**
	 * Merkvariablen für das  Dauefeuer: Ist Das Feuer aktiv?
	 */
	protected boolean ffaueraktiv = false;
	/**
	 * Merkvariablen für das  Dauefeuer: Dieses Lebewesen ist unser Ziel
	 */
	protected TLebewesen fziel;
	/**
	 * Merkvariablen für das  Dauefeuer: Dieses sind unsere Zielkoordinaten
	 */
	protected TVektor fzielv = new TVektor();

	/**
	 * Eine Liste aller Waffen, die der Spieler hat 
	 */
	protected java.util.List<TWaffe> fwaffenliste = new ArrayList<TWaffe>();

	/**
	 * Aktuell ausgewaehlte Waffe
	 */
	protected int faktWaffe = -1;

	/**
	 * Rate of Fire
	 * Zähler. Wenn 0, dann darf das nächste Mal gefeuert werden.
	 * Wird bei bewege() runtergezählt
	 */
	protected int frof = 0;

	/**
	 * Zeigt die aktive Waffe an
	 */
	protected TText fhudText1 = null;

	/**
	 * Zeigt die Score des Spielers an
	 */
	protected TText fhudText2 = null;

	/**
	 * Der Name des Spielers (fuer eine Highscore)
	 */
	protected String fname = "UnnamedPlayer";

	/**
	 * Gibt den aktuellen Schwierigkeitsgrad an
	 */
	protected Schwierigkeitsgrade fschwierigkeit = Schwierigkeitsgrade.normal;

	/**
	 * Countdown für die Statistik am Levelende
	 */
	protected int fLevelEnde;

	/**
	 * Array der Texte, die am Levelende angezeigt werden
	 */
	protected java.util.List<TText> fLevelEndeTextListe =
		new ArrayList<TText>();

	/**
	 * Konstruktor
	 *
	 * @param schwer Der Schwirigkeitsgrad
	 */
	public TSpieler(Schwierigkeitsgrade schwer) {

		// Lebenspunkte sind temporär, das wird bei "setSchwierigkeit" gesetzt
		super(new TVektor(1,1), null, new TVektor(0,0), "SPIELER", 10, 0);

		fgeschw.set(0,0);

		setSchwierigkeit(schwer);

		Font hudFont = new Font("Arial", Font.BOLD, 13);
		fhudText1 = new TText(new TVektor(5, 470), null, hudFont);
		fhudText2 = new TText(new TVektor(500, 470), "Punkte: 0", hudFont);
		fLevelEnde = 0;
	}

	/**
	 * Bewege den Spieler einen Schritt
	 */
	public void bewege() {
		super.bewege();

		// Starten wir ein paar Funken
		if (TSharedObjects.rndInt(3) == 0) {
			TSharedObjects.getPartikelVerwaltung().startEffekt(
				TPartikelVerwaltung.Partikel.Funken2, fbesenende.newAdd(fkoord), 
				new TVektor(-3, 0));
		}

		if (fmagie < fmaxmagie) {
			fmagie += 1;
		}

		if (fkoord.x<0) {
			fkoord.x = 0;
			fru.x = fdim.x;
		} else if ((fkoord.x+fdim.x) > TSharedObjects.FENSTER_BREITE) {
			fkoord.x = TSharedObjects.FENSTER_BREITE - fdim.x;
			fru.x = TSharedObjects.FENSTER_BREITE;
		}
		if (fkoord.y<0) {
			fkoord.y = 0;
			fru.y = fdim.y;
		} else if ((fkoord.y+fdim.y) > TSharedObjects.FENSTER_HOEHE) {
			fkoord.y = TSharedObjects.FENSTER_HOEHE - fdim.y;
			fru.y = TSharedObjects.FENSTER_HOEHE;
		}

		if (frof > 0) {
			frof -= 1;
		}

		// Und jetzt feuern
		if (ffaueraktiv && (frof<=0)) {
			feuer();
		}

		// Partikel-Effekt des Besens
		if (TSharedObjects.rndInt(3) == 0) {
			TSharedObjects.getPartikelVerwaltung().startEffekt(
				TPartikelVerwaltung.Partikel.Funken2, 
				fbesenende.newAdd(fkoord), 
				new TVektor(-3,0));
		}
	}

	/**
	 * Der Spieler will sich bewegen
	 *
	 * @param bewegung wohin bewegen?
	 * @param gedrueckt Ist die Taste gedrueckt
	 */
	public void bewegen(Bewegung bewegung,  boolean gedrueckt) {
		boolean xakt = false;
		boolean yakt = false;
		if (bewegung == Bewegung.oben) {
			foben = gedrueckt;
			yakt = true;
		} else if (bewegung == Bewegung.unten) {
			funten = gedrueckt;
			yakt = true;
		} else if (bewegung == Bewegung.rechts) {
			frechts = gedrueckt;
			xakt = true;
		} else if (bewegung == Bewegung.links) {
			flinks = gedrueckt;
			xakt = true;
		}

		if (xakt) {
			if (frechts == flinks) {
				// keine, oder beide gedrückt
				fgeschw.x = 0;
			} else if (frechts) {
				fgeschw.x = fdmax;
			} else if (flinks) {
				fgeschw.x = -fdmax;
			}
		}

		if (yakt) {
			if (foben == funten) {
				// keine, oder beide gedrückt
				fgeschw.y = 0;
			} else if (foben) {
				fgeschw.y = -fdmax;
			} else if (funten) {
				fgeschw.y = fdmax;
			}
		}
		
	}

	/**
	 * Die Maustaste wurde gedrückt.
	 * Jetzt dafür sorgen, dass in "bewege" gefeuert wird
	 */
	public void clickaktiv() {
		ffaueraktiv = true;
	}

	/**
	 * Die Maustaste wurde losgelassen.
	 * Feuern einstellen
	 */
	public void clicknichtaktiv() {
		ffaueraktiv = false;
	}

	/**
	 * Die Maus wurde bewegt.
	 * Jetzt die Zielkoordianten ändern
	 */
	public void zielgeaendert(TLebewesen ziel, int x, int y) {
		fziel = ziel;
		fzielv.set(x,y);
	}

	/**
	 * auf das Ziel feuern
	 */
	private void feuer() {
		if (faktWaffe >= fwaffenliste.size()) {
			System.out.println("Problem: Waffe ausgewählt die gibt es " +
				"nicht. Aktiviere die letzte Waffe");
			faktWaffe = fwaffenliste.size() - 1;
		}

		// Keine Waffe ausgewaehlt? - passiert z.b., wenn im Level
		// vergessen wurde, den Spieler zu bewaffnen.
		if (faktWaffe < 0) {
			return;
		}

		TWaffe aktw = fwaffenliste.get(faktWaffe);
		TAnzeige anzeige = TSharedObjects.getAnzeige();
		// frof wurde schon geprüft (in bewege)
		if (fmagie>=aktw.getMagie()) {
			fmagie -= aktw.getMagie();
			frof = aktw.getRof();

			// Prüfen, ob das aktuelle Ziel nicht schon tot ist
			if ((fziel != null) && (fziel.getLeben()<0)) {
				fziel = null;
			}

			if (aktw.getWaffe() == TWaffe.Waffen.Dumbfire) {
				// Dumbfire Waffe. Einmal in die Richtung feuern

				// Zielvekoor errechnen. Das ist der Zielpunkt
				TVektor zielp = new TVektor(fzielv);
				// Minus meinen Koordinaten
				zielp.sub(fkoord);
				// Minus den "auswurf"-koordinaten
				zielp.sub(dfauswurf);
				// Geschwindigkeit achten
				zielp.setlaenge(aktw.getGeschw());
				anzeige.addGeschoss(new TGeschoss(fkoord.newAdd(dfauswurf), null,
					zielp, fseite, aktw));
			} else if (aktw.getWaffe() == TWaffe.Waffen.Rail) {
				// "Strahl" waffe
				anzeige.addGeschoss(new TRail(fkoord.newAdd(frailauswurf), fziel, 
					new TVektor(fzielv), fseite, aktw));
			} else if (aktw.getWaffe() == TWaffe.Waffen.Rakete) {
				TRakete rak1;
				TRakete rak2;
				TVektor zielp = null;

				if (fziel == null) {
					// Raketenmodus "DumbFire"
					zielp = new TVektor(fzielv);
					zielp.sub(getRU());
					rak1 = new TRakete(fkoord.newAdd(fraktenauswurf),
						null, new TVektor(fgeschw.x, 2), 
						fseite, fwaffenliste.get(faktWaffe), 60,
						null, TVektor.genormt.winkel(zielp),
						fwaffenliste.get(faktWaffe).getGeschw());
					rak2 = new TRakete(fkoord.newAdd(fraktenauswurf),
						null, new TVektor(fgeschw.x, 2), 
						fseite, fwaffenliste.get(faktWaffe), 120,
						null, TVektor.genormt.winkel(zielp),
						fwaffenliste.get(faktWaffe).getGeschw());
				} else {
					rak1 = new TRakete(fkoord.newAdd(fraktenauswurf),
						null, new TVektor(fgeschw.x, 2), 
						fseite, fwaffenliste.get(faktWaffe), 60, fziel, 0,
						fwaffenliste.get(faktWaffe).getGeschw());
					rak2 = new TRakete(fkoord.newAdd(fraktenauswurf),
						null, new TVektor(fgeschw.x, 2), 
						fseite, fwaffenliste.get(faktWaffe), 120, fziel, 0,
						fwaffenliste.get(faktWaffe).getGeschw());
					fziel.addAbhaengige(rak1);
					fziel.addAbhaengige(rak2);
				}

				anzeige.addGeschoss(rak1);
				anzeige.addGeschoss(rak2);
			}
		}
	}

	/**
	 * Es wurde ein Mausklick (rechte Maustaste) gemeldet.
	 *
	 * @param ziel Ggf. ein Lebewesen, das anvisiert wurde
	 * @param x X-Koordinate des Klicks
	 * @param y Y-Koordinate des Klicks
	 */
	public void clickrechts(TLebewesen ziel, int x, int y) {
		nextWaffe();
	}

	/**
	 * Eine weitere Waffe zur Liste hinzufugen
	 *
	 * @param waffe ID der Waffe
	 */
	public void addWaffe(String waffe) throws Exception {
		TWaffe neu = TWaffe.getWaffe(waffe);

		if (neu == null) {
			throw new Exception("Unbekannte Waffe: \"" + waffe + "\"");
		}

		if (!fwaffenliste.contains(neu)) {
			fwaffenliste.add(neu);
			TSharedObjects.getAnzeige().setMeldung("Waffe erhalten: " + neu);
			setAktiveWaffe(fwaffenliste.size() - 1);
		}
	}

	/**
	 * Alle Waffen, die der Spieler bis jetzt eingesammelt hat,
	 * zuruecksetzen (zB bei einem neuen Levelstart)
	 */
	public void leereWaffen() {
		fwaffenliste.clear();
	}

	/**
	 * Die naechste verfuegbare Waffe auswaehlen
	 */
	public void nextWaffe() {
		setAktiveWaffe(faktWaffe + 1);
	}

	/**
	 * Setzt die aktive Waffe des Spielers
	 */
	public void setAktiveWaffe(int w) {
		faktWaffe = w;
		if (faktWaffe >= fwaffenliste.size()) {
			if (fwaffenliste.size() == 0) {
				// Spieler hat keine Waffe
				faktWaffe = -1;
			} else {
				faktWaffe = 0;
			}
		}

		fhudText1.setText("aktive Waffe: " + fwaffenliste.get(faktWaffe));
	}

	/**
	 * Den Spieler und die zum Spieler gehoerigen Sachen (zB den Magiebalken)
	 * zeichnen
	 * @param g Die Zeichenflaeche, auf der gezeichnet werden soll
	 */
	public void zeichne(java.awt.Graphics g) {
		super.zeichne(g);

		// Magie-Balken
		g.setColor(java.awt.Color.BLACK);
		g.fillRect(0, TSharedObjects.FENSTER_HOEHE-10,
			TSharedObjects.FENSTER_BREITE, 6);
		g.setColor(java.awt.Color.BLUE);
		g.fillRect(2, TSharedObjects.FENSTER_HOEHE-8,
			(int)((TSharedObjects.FENSTER_BREITE-2)*(fmagie/fmaxmagie)), 2);
		fhudText1.zeichne(g);
		fhudText2.zeichne(g);
	}

	/**
	 * Zeichnet die Statistik am Ende des Levels
	 */
	protected void zeichneStats(java.awt.Graphics g) {
		if (fLevelEnde == 0) {
			Font font = new Font("Arial", Font.BOLD, 13);
			fLevelEndeTextListe.add(new TText(new TVektor(10, 50),
				"Waffe", font));
			fLevelEndeTextListe.add(new TText(new TVektor(200, 50),
				"Abgefeuert", font));
			fLevelEndeTextListe.add(new TText(new TVektor(290, 50),
				"Treffer", font));
			fLevelEndeTextListe.add(new TText(new TVektor(360, 50),
				"Sonderpunkte", font));
		} else if (fLevelEnde % 10 == 0) {
			Font font = new Font("Arial", Font.BOLD, 13);
			int i = fLevelEnde / 10 -1;

			// Die nächste Waffe anzeigen
			if (i < fwaffenliste.size()) {
				TWaffe w = fwaffenliste.get(i);
				fLevelEndeTextListe.add(new TText(new TVektor(10, 80+i*20), 
					w.toString(), font));
				fLevelEndeTextListe.add(new TText(new TVektor(200, 80+i*20),
					""+w.getStatAbgefeuert(), font));
				fLevelEndeTextListe.add(new TText(new TVektor(290, 80+i*20),
					""+w.getStatTreffer(), font));

				// Sonderpunkte ausrechnen
				// Wenn nicht genügend abgefeuert wurden, kann man
				// nichts vergeben
				double trefferrate = (double)
					w.getStatTreffer() / w.getStatAbgefeuert();
				trefferrate *= 100;
				if (w.getStatAbgefeuert() < 20) {
					fLevelEndeTextListe.add(new TText(new TVektor(360, 80+i*20),
						"Nicht genügend Schüsse", font));
				} else if (trefferrate < 50) {
					fLevelEndeTextListe.add(new TText(new TVektor(360, 80+i*20),
						"Zu niedrige Trefferrate: " + (int)trefferrate + "%", font));
				} else if (trefferrate < 80) {
					fLevelEndeTextListe.add(new TText(new TVektor(360, 80+i*20),
						"Trefferrate: " + (int)trefferrate +
						"%   Gut: +1000 Score", font));
					addScore(1000);
					TSound.play("GELD");
				} else {
					fLevelEndeTextListe.add(new TText(new TVektor(360, 80+i*20),
						"Trefferrate: " + (int)trefferrate +
						"%   Sehr Gut! +2000 Score", font));
					addScore(2000);
					TSound.play("GELD");
				}
			}
		}
		fLevelEnde++;

		// Und nun zeichnen
		for (TText i:fLevelEndeTextListe) {
			i.zeichne(g);
		}
	}

	/**
	 * Die Statusmeldungen auf den Anfangszustand setzen
	 * (beim naechsten Zeichnen ein Neu-Aufbauen der Liste erzwingen)
	 */
	public void statsRuecksetzen() {
		fLevelEndeTextListe.clear();
		fLevelEnde = 0;
	}

	/**
	 * Addiert die Punktezahl des Spielers
	 */
	public void addScore(int score) {
		fscore += score;
		fhudText2.setText("Punkte: " + fscore);
	}

	/**
	 * Setzt die Punkte des Spielers
	 */
	public void setScore(int score) {
		fscore = score;
		addScore(0); // Zum zeichnen
	}
	
	/**
	 * Wird aufgerufen, wenn der Spieler ein Item aufsammelt
	 */
	public void addItem(TItem item) throws Exception{
		int faktor;

		switch (item.getItem()) {
			case Heilung:
				if (fschwierigkeit == Schwierigkeitsgrade.leicht) {
					faktor = 2;
				} else {
					faktor = 1;
				}
				treffer(-item.getiParam() * faktor);
				break;
			case Waffe:
				addWaffe(item.getsParam());
				break;
		}
	}

	/**
	 * Setzt den Namen des Spielers
	 */
	public void setName(String name) {
		fname =  name;
	}

	/**
	 * Liefert die Anzahl der Punkte, die der Spieler gemacht hat
	 */
	public int getScore() {
		return fscore;
	}

	/**
	 * Liefert die aktuell ausgewaehlte Waffe des Spielers
	 */
	public int getAktiveWaffe() {
		return faktWaffe;
	}

	/**
	 * Liefert den Namen des Spielers zurueck
	 */
	public String getName() {
		return fname;
	}

	/**
	 * Liefert den Schwierigkeitsgrad zurueck
	 */
	public Schwierigkeitsgrade getSchwierigkeit() {
		return fschwierigkeit;
	}

	/**
	 * Setzt den Schwierigkeitsgrad
	 */
	public void setSchwierigkeit(Schwierigkeitsgrade s) {
		fschwierigkeit = s;
		switch (s) {
			case leicht:
				flebenmax = 250;
				fmaxmagie = 150;
				break;
			case normal:
				flebenmax = 100;
				fmaxmagie = 100;
				break;
			case schwer:
				flebenmax = 50;
				fmaxmagie = 75;
				break;
		}

		// Das wird nur bei einem neuen Spiel aufgerufen
		fmagie = fmaxmagie;
		flebenspunkte = flebenmax;

		// Den Balken neu berechnen
		treffer(0);
	}

	/**
	 * Liefert die Liste der Waffen, die der Spieler zur Verfuegung hat
	 */
	public java.util.List<TWaffe> getWaffenListe() {
		return fwaffenliste;
	}

	/**
	 * Leer die Waffenliste
	 */
	public void leereWaffenListe() {
		fwaffenliste.clear();
	}

}

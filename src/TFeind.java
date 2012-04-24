package de.rccc.java.witchcraft;

/**
 * Ein Feind-Objekt.
 * Ein Feind ist eine autonome bewegende und feuernde Einheit,
 * könnte aber auch eine befreundete Einheit sein.
 * Nutzt TFeindDef.
 */
public class TFeind extends TLebewesen {
	/**
	 * Die Feinddefiniton, auf dem dieser Feind aufbaut
	 */
	protected TFeindDef ffeinddef = null;

	/**
	 * Die Waffe, die dieser Feind verwendet.
	 * (Abkürzung, um einen Zugiff zu sparen)
	 */
	protected TWaffe fwaffe = null;

	/**
	 * Das Item, das dieser Feind fallenlässt
	 */
	protected TItem fitem;

	/**
	 * Das Bewegungsmuster, dem der Feind folgt
	 */
	protected int fmuster = -1;

	/**
	 * Der Parameter zu dem Bewegungsmuster: Berechnungsvariable 
	 */
	protected double fmusterparamberech = 0;

	/**
	 * Der Parameter zu dem Bewegungsmuster: Laufvariable
	 */
	protected double fmusterparamlauf = 0;

	/**
	 * Liefert den "Bauplan" dieses Feindes
	 */
	public TFeindDef getFeindDef() {
		return ffeinddef;
	}

	/**
	 * Konstruktor.
	 * Feinde koennen sich nach einem bestimmten Bewegungsmuster
	 * fortbegegen, das die Flugbahn und die Geschwindigkeit bestimmt.
	 * Mögliche Muster:
	 *                             langsam  schnell  extraschnell
	 * geradeaus                        1      2        19
	 * sinus flach                      3      5        20
	 * sinus hoch                       4      6        21
	 * hoch langsam                     7      8
	 * hoch schnell                     9     10
	 * runter langsam                  11     12
	 * runter schnell                  13     14
	 * zickzack, hoch                  15     16
	 * zickzack, runter                17     18
	 * reinkommend, dann hoch, runter  22     23
	 * Kreis                          101
	 *
	 * @param feinddef Die Feinddefinition (der Bauplan) fuer diesen Feind
	 * @param startpunkt der Startpunkt des Feindes
	 * @param item Welches Item lässt dieser Feind nach der Zerstörung
	 * liegen? (TItem/null)
	 */
	TFeind(TFeindDef feinddef, int startpunkt, int seite, TItem item) {

		super(null, null, new TVektor(), feinddef.getDarstellung(),
			feinddef.getLebenspunkte(), seite);
		fwaffe = feinddef.getWaffe();
		
		ffeinddef = feinddef;
		fitem = item;

		// damit nicht alle sofort schiessen, wenn die auf den Bildschirm
		// erscheinen
		frof = TSharedObjects.rndInt(fwaffe.getRof());

		if ((startpunkt >= 0) && (startpunkt <= 40)) {
			this.fkoord = new TVektor(630, 10*startpunkt);
		// Spezialmuster
		} else if (startpunkt == -2) {
			// Das UFO fliegt weg
			this.fkoord = new TVektor(400, 50);
		} else {
			System.out.println("Fehler: Startpunkt " + startpunkt +
				" unbekannt");
			this.fkoord = new TVektor(100, 100);
		}

		// langsam geradeaus
		fmuster = feinddef.getMuster();
		if (fmuster == 1) {
			this.fgeschw.set(-2, 0);
		} else if (fmuster == 2) {
			this.fgeschw.set(-5, 0);
		} else if (fmuster == 3) {
			this.fgeschw.set(-2, 0);
			fmusterparamberech = 3;
		} else if (fmuster == 4) {
			this.fgeschw.set(-2, 0);
			fmusterparamberech = 8;
			fmuster = 3; // gleiche berechnungsroutine
		} else if (fmuster == 5) {
			this.fgeschw.set(-5, 0);
			fmusterparamberech = 3;
			fmuster = 3; // gleiche berechnungsroutine
		} else if (fmuster == 6) {
			this.fgeschw.set(-5, 0);
			fmusterparamberech = 3;
			fmuster = 3; // gleiche berechnungsroutine
		} else if (fmuster == 7) {
			this.fgeschw.set(-2, -1);
		} else if (fmuster == 8) {
			this.fgeschw.set(-5, -1);
		} else if (fmuster == 9) {
			this.fgeschw.set(-2, -3);
		} else if (fmuster == 10) {
			this.fgeschw.set(-5, -3);
		} else if (fmuster == 11) {
			this.fgeschw.set(-2, 1);
		} else if (fmuster == 12) {
			this.fgeschw.set(-5, 1);
		} else if (fmuster == 13) {
			this.fgeschw.set(-2, 3);
		} else if (fmuster == 14) {
			this.fgeschw.set(-5, 3);
		} else if (fmuster == 15) {
			this.fgeschw.set(-2, -3);
		} else if (fmuster == 16) {
			this.fgeschw.set(-5, -3);
			fmuster = 15; // gleiche routine
		} else if (fmuster == 17) {
			this.fgeschw.set(-2, 3);
			fmuster = 15; // gleiche routine
		} else if (fmuster == 18) {
			this.fgeschw.set(-5, 3);
			fmuster = 15; // gleiche routine
		} else if (fmuster == 19) {
			this.fgeschw.set(-8, 0);
		} else if (fmuster == 20) {
			this.fgeschw.set(-8, 0);
			fmusterparamberech = 3;
		} else if (fmuster == 21) {
			this.fgeschw.set(-8, 0);
			fmusterparamberech = 8;
			fmuster = 3;
		} else if (fmuster == 22) {
			this.fgeschw.set(-2, 0);
		} else if (fmuster == 23) {
			this.fgeschw.set(-5, 0);
			fmuster = 22;
		} else if (fmuster == 101) {
			// zuerst geradeaus, dann im Kreis
			this.fgeschw.set(-8,0);
			fmusterparamlauf = -50;
		} else if (fmuster == -2) {
			// Das UFO fliegt weg
			this.fgeschw = new TVektor(8, -1);
		} else {
			System.out.println("Fehler: Muster " + fmuster + " unbekannt");
			this.fgeschw = new TVektor(0, 0);
		}
		fru = fkoord.newAdd(fdim);
	}

	/**
	 * Bewegt das Objekt einen Schritt, abhaengig von seiner Geschwindigkeit
	 * Außerdem wird hier die Waffe abgefeuert
	 */
	public void bewege(){
		frof -= 1;
		TAnzeige a = TSharedObjects.getAnzeige();

		// Ballern, wenn waffe bereit, und wenn Spieler noch existens
		if ((frof <= 0) && (a.fspieler != null)) {
			frof = fwaffe.getRof();
			TVektor waffenausgang = ffeinddef.getWaffenausgang().newAdd(fkoord);

			// Zielpunkt1: Mitte des Spielers
			TVektor zielp1 = new TVektor(a.fspieler.fdim);
			zielp1.mult(0.5);
			zielp1.add(a.fspieler.fkoord);
			// Da wir ja einen Vektor brauchen, der auf das Ziel zeigt:
			// gleiche, wie der Auswurfpunkt!
			zielp1.sub(waffenausgang);
			// Und jetzt dem Ding eine Geschwindigkeit geben. 
			// Sonst ist der sofort da
			zielp1.setlaenge(fwaffe.getGeschw());

			if (fwaffe.getWaffe() == TWaffe.Waffen.Rakete) {
				TRakete rak = new TRakete(waffenausgang, null,
						new TVektor(fgeschw.x,fgeschw.x), fseite,
						fwaffe, 0, null, TVektor.genormt.winkel(zielp1),
						fwaffe.getGeschw());
				a.addGeschoss(rak);
			} else if (fwaffe.getWaffe() == TWaffe.Waffen.Dumbfire) {
				a.addGeschoss(new TGeschoss(waffenausgang, null,
					zielp1, fseite, fwaffe));
			} else if (fwaffe.getWaffe() == TWaffe.Waffen.Rail) {
				// Das ist etwas unfair. Daher random..
				TVektor ziel = a.fspieler.getMitte();
				ziel.add(new TVektor(TSharedObjects.rndInt(200)-100,
						TSharedObjects.rndInt(200)-100));
				a.addGeschoss(new TRail(waffenausgang, a.fspieler, ziel,
					 fseite, fwaffe));
			}
		}

		// Bei bestimmten Mustern jetzt die Bewegung aktualisieren
		if (fmuster == 3) {
			fmusterparamlauf += 0.1;
			fgeschw.y = (Math.sin(fmusterparamlauf) * fmusterparamberech);
		} else if (fmuster == 15) {
			fmusterparamlauf += 1;
			if (fmusterparamlauf > 40) {
				fmusterparamlauf = 0;
				fgeschw.y *= -1;
			}
		} else if (fmuster == 22) {
			if (fgeschw.y == 0) {
				// Fährt noch rein
				if (fkoord.x < (TSharedObjects.FENSTER_BREITE-fdim.x)) {
					// Ist weit genug drinnen
					fgeschw.swap();
				}
			} else {
				// Hoch / runter
				if ((fkoord.y < 0) && (fgeschw.y < 0)) {
					fgeschw.y *= -1;
				} else if ((fkoord.y > TSharedObjects.FENSTER_HOEHE-fdim.y) && (fgeschw.y > 0)) {
					fgeschw.y *= -1;
				}
			}
		} else if (fmuster == 101) {
			if (fmusterparamlauf >= 0) {
				// Ist weit genug drinnen. Jetzt kreis
				fmusterparamlauf  += 0.02;
				fgeschw.set(Math.cos(fmusterparamlauf)*4,-Math.sin(fmusterparamlauf)*4);
			} else {
				fmusterparamlauf++;
				if (fmusterparamlauf >= 0) {
					fmusterparamlauf = Math.PI;
				}
			}
		}

		super.bewege();
	}

	/**
	 * Wird aufgerufen, wenn das Objekt gelöscht (aus den Listen) wird.
	 * So kann jedes erbende Objekt ein letztes Röcheln abgeben.
	 *
	 * @param tot True, wenn das Objekt wirklich stirbt.
	 * False, wenn das Objekt nur "getötet" wird, weil es
	 * außerhalb des Bildschirms ist
	 * @param waffe Mit welcher Waffe wurde das Lebewesen eliminiert?
	 * (TWaffe/null)
	 */ 
	public void ende(boolean tot, TWaffe waffe) {
		// Nur Todesröcheln, wenn er stirbt...
		if (tot) {
			TPartikelVerwaltung partverwaltung =
				TSharedObjects.getPartikelVerwaltung();
			TAnzeige anzeige = TSharedObjects.getAnzeige();

			if (anzeige.fspieler != null) {
				anzeige.fspieler.addScore(ffeinddef.getPunkte());
			}

			partverwaltung.startEffekt(waffe.getPartikelTot(), getMitte());
			partverwaltung.startEffekt(TPartikelVerwaltung.Partikel.Score,
				getMitte(), "" + ffeinddef.getPunkte());
			if (waffe.getBeben() > 0) {
				anzeige.setBeben(waffe.getBeben());
			}
			TSound.play(waffe.getTotSound());

			if (fitem != null) {
				fitem.setKoord(getMitte());
				anzeige.addItem(fitem);
			}

			if (ffeinddef.getDauersound() != null) {
				TSound.stoppe(ffeinddef.getDauersound());
			}
		}
		super.ende(tot, waffe);
	}
	
}

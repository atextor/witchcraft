package de.rccc.java.witchcraft;

/**
 * Eine Rakete ist ein Selbstlenkgeschoss.
 * Die Rakete hat zwei Modi: DumbFire oder Verfolgung.
 * im DumbFire dreht sich die Rakete nach dem Start auf den Zielvektor,
 * und fliegt dann diese Richtung weiter. Im Verfolgermodus verfolgt
 * die Rakete ein Ziel, indem die sich immer in Zielrichtung dreht
 * und dann beschleunigt.
 */
public class TRakete extends TGeschoss {
	/**
	 * Maximaler Drehwinkel pro Tick
	 */
	private static final double MAX_DREHW = 0.15;

	/**
	 * Maximale Beschleunigung pro Tick
	 */
	private static final double BESCHL = 1;

	/**
	 * Das Zielobjekt, auf das die Rakete zufliegt
	 */
	protected TObjekt fziel = null;
	
	/**
	 * Wie ist der Zielwinkel, auf dem die Rakete einschwenken soll,
	 * wenn kein ZielObjekt da ist.
	 */
	protected double fzielw;

	/**
	 * Mitte der Rakete (wird fuer die Kollision verwendet)
	 */
	protected TVektor fmitte;

	/**
	 * Maixmalgeschwindigkeit
	 */
	protected int fmaxgeschw;

	/**
	 * Konstruktor fuer bekanntes Zielobjekt
	 *
	 * @param koord Startkoordinaten der Rakete
	 * @param dim Groessenvektor
	 * @param geschw Geschwindigkeitsvektor
	 * @param seite Fuer welche Seite fliegt die Rakete
	 * @param waffe Die Waffe, aus der die Rakete erzeugt wurde
	 * @param ausrichtung Anfaenglicher Drehwinkel
	 * @param ziel Das Zielobjekt der Lenkrakete
	 * @param zielw Der ZielVektor, der Rakete. ACHTUNG! ENTWEDER ziel
	 * ODER zielw! (Soll viele fast-identische Konstruktoren vermeiden)
	 * @param maxgeschw Maximale Geschw. der Rakete
	 */
	TRakete(TVektor koord, TVektor dim, TVektor geschw, int seite,
		TWaffe waffe, double ausrichtung, TObjekt ziel,
		double zielw, int maxgeschw) {

		super(koord, dim, geschw, seite, waffe);
		fausrichtung = Math.toRadians(ausrichtung);
		fziel  = ziel;
		fzielw = zielw;
		fmitte = new TVektor(fdim.x/2 , fdim.y/2);
		fmaxgeschw = maxgeschw;
	}

	/**
	 * Die Rakete bekommt ein neues Ziel zugewiesen
	 */
	public void setZiel(TObjekt ziel) {
		fziel = ziel;
	}

	/**
	 * Die Rakete wird auf das Ziel ausgerichtet, sofern eines
	 * vorhanden ist, anschliessend in diese Richtung bewegt
	 */
	public void bewege() {
		double winkelsoll;
		double winkeldiff;

		if (fziel != null) {
			// Dazu den benötigen Vektor finden, der zum Ziel führt
			TVektor zielmitte = new TVektor(fziel.fdim.x/2, fziel.fdim.y/2);
			TVektor ziel      = new TVektor(fziel.fkoord);
			ziel.add(zielmitte);

			TVektor raketenmitte = fkoord.newAdd(fmitte);

			TVektor zielv = ziel.newSub(raketenmitte);
			TVektor aktiv = new TVektor(Math.sin(fausrichtung),
			Math.cos(fausrichtung));
			winkelsoll = aktiv.winkel(zielv);
			winkeldiff =  winkelsoll;
		} else {
			// Der Winkel ist der wirkliche
			winkelsoll = fzielw;
			winkeldiff = (fausrichtung - winkelsoll) * -1;
		}

		// Nun hätten wir also den benötigten Winkel. Aber die Rakete kann
		// sich nicht so schnell drehen
		// Die Ausrichtung der Rakete korrigieren
		if (winkeldiff > MAX_DREHW) {
			winkeldiff = MAX_DREHW;
		} else if (winkeldiff < -MAX_DREHW) {
			winkeldiff = -MAX_DREHW;
		}
		fausrichtung += winkeldiff;
		
		// winkeldiff nicht neu rechnen. Könnte einen guten Effekt bringen

		// Zu dem Winkel einen Vektor generieren
		// Und jetzt darf die Rakete Gas geben
		double faktor=1;

		if (winkeldiff > Math.PI/2) {
			// Wenn die sich vom Ziel abwendet, dann kein Gas geben
			faktor = 0;
		} else if (winkeldiff > Math.PI/4) {
			// Hier anteilig Gas geben
			faktor = 1 / ((Math.PI/4) * winkeldiff);
		} else {
			// Vollgas
			faktor = 1;
		}
		if (fziel != null) {
			// Falls das Ziel zerstört wird
			fzielw = fausrichtung;
		}

		TVektor dazu = new TVektor(Math.sin(fausrichtung)*BESCHL*faktor,
			Math.cos(fausrichtung)*BESCHL*faktor);
		fgeschw.add(dazu);

		if (fgeschw.laenge() > fmaxgeschw) {
			// Objekt ist zu schnell. Abbremsen
			// Aber nur langsam
			double setgeschw = fmaxgeschw;/*fgeschw.laenge()-BESCHL;
			if (setgeschw<MAX_GESCHW) {
				setgeschw=MAX_GESCHW;
			}*/
			fgeschw.setlaenge(setgeschw);
		}

		// Der Winkel ist der wirkliche
		super.bewege();

		// Und nu die Richtigen Bilder organisieren
		berechneBildangaben();

		// Rauch
		if (TSharedObjects.rndInt(5) == 0) {
			TVektor akt = new TVektor(fgeschw);
			akt.mult(-1);
			// Und nu, so lang machen, wie die Rakete maxGeschw ist
			akt.mult(fmaxgeschw/akt.laenge());
			akt.add(fgeschw);

			TSharedObjects.getPartikelVerwaltung().startEffekt(
				TPartikelVerwaltung.Partikel.Rauch, fkoord, 
				akt);
		}
	}
	
	/**
	 * Ist das Objekt ausserhalb des Bildschirms UND hat kein Ziel?
	 */
	public boolean ausserhalbBildschirm() {
		return (fziel == null) && super.ausserhalbBildschirm();
	}
}

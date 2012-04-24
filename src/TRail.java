package de.rccc.java.witchcraft;

/**
 * Rail ist ein "instant Hit" Geschoss.
 * Das Ziel wird sofort getroffen.
 * Außerdem wird kein Bild gezeichnet, sondern eine Linie.
 */
public class TRail extends TGeschoss {
	/**
	 * Von wo wurde es abgeschossen?
	 */
	protected TVektor fquelle;

	/**
	 * Wohin wurde geschossen?
	 */
	protected TVektor fzielv;

	/**
	 * Welches Objekt ist das Ziel?
	 */
	protected TObjekt fziel;

	/**
	 * Konstruktor
	 *
	 * @param quelle Woher kommt der der Schuss
	 * @param ziel Welches Lebewesen ist das Ziel?
	 * @param zielkoord Wo sind die Zielkoordinaten?
	 * @param seite Welcher Seite gehört das Geschoss an?
	 * @param waffe Mit welcher WAffe wurde geschossen?
	 */
	TRail(TVektor quelle, TLebewesen ziel, TVektor zielkoord, int seite,
		TWaffe waffe) {

		super(zielkoord, new TVektor(), new TVektor(), seite, waffe);
		fquelle = quelle;
		fzielv = zielkoord;
		fziel  = ziel;
		// Instant hit
		fttl=10;
	}

	/**
	 * Zeichnet den Rail an seinen Koordinaten auf der Zeichenflaeche g
	 *
	 * @param g Die Zeichenflaeche, auf der das Objekt gezeichnet werden soll
	 */
	public void zeichne(java.awt.Graphics g) {
		// Hier NICHT: super.zeichne(g);
		g.setColor(java.awt.Color.BLUE);
		g.drawLine((int)fquelle.x,(int)fquelle.y,
			(int)fzielv.x,(int)fzielv.y);
	}
	
	/**
	 * Ueberprueft, ob das Objekt mit einem Anderen kollidiert
	 *
	 * @param anderes Das andere Objekt, mit dem auf Kollision geprueft
	 * werden soll
	 * @return Besteht eine Kollision (ueberschneiden sich die Objekte)?
	 */
	public boolean beruehrt(TLebewesen anderes) {
		// Nur EINMAL!
		if ((anderes == fziel) && (anderes.innerhalb(fzielv))) {
			fziel = null;
			return true;
		} else {
			return false;
		}
	}
}

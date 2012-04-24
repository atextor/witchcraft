package de.rccc.java.witchcraft;

import java.util.Map;
import java.awt.image.*;

/**
 * TObjekt stellt ein auf der Zeichenflaeche darstellbares Objekt dar.
 * Es hat Koordinaten-, Groessen-, und Geschwindigkeitsvektoren. Eine
 * Darstellung hat TObjekt nicht, darum muessen sich erbende Klassen
 * kuemmern.
 */
abstract public class TObjekt {
	/**
	 * Der Koordinatenvektor
	 */
	protected TVektor fkoord;

	/**
	 * Der Groessenvektor
	 */
	protected TVektor fdim;

	/**
	 * Der Vektor, der "Rechts Unten" anzeigt.
	 * So müssen wir nicht mit Objekten um uns scheissen.
	 * (neue TVektor-Objekte, die bei jedem mal Anfragen
	 * von "rechts unten" erzeugt werden muessten)
	 */
	protected TVektor fru;

	/** 
	 * Der Geschwindigkeitsvektor
	 */
	protected TVektor fgeschw;

	/**
	 * Konstruktor
	 *
	 * @param koord Der Koordinatenvektor
	 * @param dim Der Groessenvektor
	 * @param geschw Der Geschwindigkeitsvektor
	 */
	TObjekt(TVektor koord, TVektor dim, TVektor geschw) {
		this.fkoord  = koord;
		this.fgeschw = geschw;
		this.fdim = dim;
		if ((fkoord != null) && (fdim != null)) {
			this.fru = fkoord.newAdd(fdim);
		}
	}

	/**
	 * Veraendere die Y-Komponente des Geschwindigkeitsvektors
	 * @param g Der Wert, der auf die Y-Komponente addiert werden soll
	 */
	public void gravitationAdd(double g) {
		this.fgeschw.y += g;
	}

	/**
	 * Multipliziere die Y-Komponente des Geschwindigkeitsvektors
	 * mit einem Faktor
	 * @param g Der Wert, mit dem die Y-Komponente multipliziert werden soll
	 */
	public void gravitationMult(double g) {
		this.fgeschw.mult(g);
	}

	/**
	 * Bewegt das Objekt einen Schritt, abhaengig von seiner Geschwindigkeit
	 */
	public void bewege() {
		fkoord.add(fgeschw);
		fru.add(fgeschw);
	}

	/**
	 * Setzt die Koordinaten des Objektes
	 */
	public void setKoord(TVektor koord) {
		this.fkoord = koord;
		// RU nachziehen
		this.fru = koord.newAdd(fdim);
	}

	/**
	 * Liefert den Koordinatenvektor des Objekts
	 */
	public TVektor getKoord() {
		return fkoord;
	}

	/**
	 * Liefert den Geschwindigkeitsvektor des Objekts
	 */
	public TVektor getGeschw() {
		return fgeschw;
	}

	/**
	 * Liefert die Mitte des Objektes
	 */
	public TVektor getMitte() {
		return new TVektor(fkoord.x + fdim.x / 2, fkoord.y + fdim.y / 2);
	}

	/**
	 * Liefert den Vektor auf die rechte untere Ecke der
	 * Boundingbox des Objekts
	 */
	public TVektor getRU() {
		// Eigentlich ist es Objektorientierter es so zu machen:
		// return fkoord.newAdd(fdim);
		// Aber so scheissen wir zu viele Objekte
		return fru;
	}

	/** 
	 * Sind die Koordinaten innerhalb der von mir gebrauchten Fläche?
	 *
	 * @param k Der Koordinatenvektor, der ueberprueft werden soll
	 * @return true: Die Koordianten liegen innerhalb
	 */
	public boolean innerhalb(TVektor k) {
		return (fkoord.x <= k.x) && (fkoord.y <= k.y) &&
			(fru.x >= k.x) && (fru.y >= k.y);
	}

	/**
	 * Zeichnet das Bild an seinen Koordinaten auf der Zeichenflaeche g
	 *
	 * @param g Die Zeichenflaeche, auf der das Objekt gezeichnet werden soll
	 */
	abstract public void zeichne(java.awt.Graphics g);

	/**
	 * Ueberprueft, ob das Objekt mit einem Anderen kollidiert
	 *
	 * @param anderes Das andere Objekt, mit dem auf Kollision geprueft
	 * werden soll
	 * @return Besteht eine Kollision (ueberschneiden sich die Objekte)?
	 */
	public boolean beruehrt(TObjekt anderes) {
		TVektor aru = anderes.getRU(); 
		return !((anderes.fkoord.x > fru.x) ||
				(anderes.fkoord.y > fru.y) ||
				(fkoord.x > aru.x) ||
				(fkoord.y > aru.y));
	}

	/**
	 * Überprüft, ob das Objekt nicht jetzt ausgespielt hat
	 *
	 * @return true, wenn das Ojekt nicht mehr gebraucht wird
	 */
	public boolean ausserhalbBildschirm() {
		int x = TSharedObjects.FENSTER_BREITE;
		int y = TSharedObjects.FENSTER_HOEHE;
		return (fru.x < 0) || (fru.y < 0) || (fkoord.x > x) || (fkoord.y > y);
	}

	/**
	 * Wird aufgerufen, wenn das Objekt gelöscht (aus den Listen) wird.
	 * So kann jedes erbende Objekt ein letztes Röcheln abgeben.
	 * Methode ist nicht abstrakt, weil nicht jedes Objekt dessen Klasse von
	 * TObjekt erbt, diese Methode benoetigt.
	 *
	 * @param tot True, wenn das Objekt wirklich stirbt.
	 * False, wenn das Objekt nur "getötet" wird, weil es außerhalb
	 * des Bildschirms ist
	 */ 
	public void ende(boolean tot) {}
}


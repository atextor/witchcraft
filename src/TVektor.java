package de.rccc.java.witchcraft;

import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 * Klasse fuer 2-dimensionale Vektoren. x- und y-Felder werden
 * von den nutzenden Klassen so oft gebraucht, dass auf separate
 * Get- und Set-Methoden verzichtet wurde. Die Klasse enthaelt
 * eine Reihe von Vektoroperationen, die fuer das Spiel nuetzlich
 * sind.
 */
public class TVektor {
	/**
	 * X-Komponente des Vektors
	 */
	public double x;

	/**
	 * Y-Komponente des Vektors
	 */
	public double y;

	/**
	 * Normierter Vektor e2.
	 * Wird zur Winkelberechnung benötigt
	 */
	static public TVektor genormt = new TVektor(0, 1);

	/**
	 * Defaultkonstruktor: Vektor wird mit zwei Nullwerten initialisiert
	 */
	TVektor() {
		this(0., 0.);
	}

	/**
	 * Konstruktor: Vektor mit zwei Zahlenwerten initialisieren
	 *
	 * @param x Die x-Komponente als double-Wert
	 * @param y Die y-Komponente als double-Wert
	 */
	TVektor(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Konstruktor: Vektor wird mit zwei Int-Werten initialisiert
	 *
	 * @param x Die x-Komponente als int-Wert
	 * @param y Die y-Komponente als int-Wert
	 */
	TVektor(int x, int y) {
		this.x = (double)x;
		this.y = (double)y;
	}

	/**
	 * Kopierkonstruktor
	 *
	 * @param v Der Vektor, der kopiert werden soll
	 */
	TVektor(TVektor v) {
		this.x = v.x;
		this.y = v.y;
	}
	
	/**
	 * Setzen der x,y Koordinaten
	 *
	 * @param x x-Korrdinate
	 * @param y y-Korrdinate
	 */
	public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Setzt den Vektor auf die Koordinaten des anderen Vektors.
	 * Gemacht, falls man nicht kopieren, bzw. zuweisen will
	 *
	 * @param vektor der andere Vektor
	 */
	public void set(TVektor vektor) {
		this.x = vektor.x;
		this.y = vektor.y;
	}

	/**
	 * Addiere einen Vektor
	 *
	 * @param v Der zu addierende Vektor
	 */
	public void add(TVektor v) {
		this.x += v.x;
		this.y += v.y;
	}

	/**
	 * Addiere einen Vektor und liefere das Ergebnis
	 * als neuen Vektor zurueck
	 *
	 * @param v Der zu addierende Vektor
	 * @return Der durch die Addition entstandene Vektor
	 */
	public TVektor newAdd(TVektor v) {
		return new TVektor(this.x + v.x, this.y + v.y);
	}

	/**
	 * Subtrahiere einen Vektor
	 *
	 * @param v Der zu subtrahierende Vektor
	 */
	public void sub(TVektor v) {
		this.x -= v.x;
		this.y -= v.y;
	}

	/**
	 * Substrahiert einen Vektor und liefere das Ergebnis
	 * als neuen Vektor zurueck
	 *
	 * @param v Der zu subtrahierende Vektor
	 * @return Der durch die Addition entstandene Vektor
	 */
	public TVektor newSub(TVektor v) {
		return new TVektor(this.x - v.x, this.y - v.y);
	}

	/**
	 * Multipliziert einen Skalar mit dem Vektor
	 *
	 * @param s Der Skalar, mit dem multipliziert werden soll
	 */
	public void mult(double s) {
		this.x *= s;
		this.y *= s;
	}

	/**
	 * Berechne das Skalarprodukt zweier Vektoren
	 *
	 * @param v Der Vektor, mit dem das SP berechnet werden soll
	 * @return Das berechnete Skalarprodukt
	 */
	public double skalarprodukt(TVektor v) {
		return (x * v.x) + (y * v.y);
	}

	/**
	 * Berechne die Laenge eines Vektors
	 *
	 * @return Die Laenge des Vektors
	 */
	public double laenge() {
		return Math.sqrt(x*x + y*y);
	}

	/**
	 * Berechne den Winkel zwischen zwei Vektoren, in Bogenmass
	 *
	 * @param v Der Vektor, zwischen dem der Winkel berechnet
	 * werden soll
	 * @return Der Winkel der Vektoren in Bogenmass
	 */
	public double winkel(TVektor v) {
		double ret = skalarprodukt(v) / (laenge() * v.laenge());
		if (ret>1) {
			// Manchmal ist ret ein kleines bisschen größer als 1.
			// Und das mag acos nicht
			ret=1;
		};
		ret = Math.acos(ret);

		// Jetzt rausfinden, wie die Vektoren zueinander liegen.
		// Dazu das Kreuzprodukt berechnen. Da aber die x und y
		// Achse garantiert 0 sind, reicht uns die z Achse
		double kreuz_z = x * v.y - y * v.x;

		if (kreuz_z > 0) {
			ret *= -1;
		}

		return ret;
	}

	/**
	 * Set den Vektor auf eine bestimmte Laenge setzen
	 *
	 * @param laenge Die neue Laenge des Vektors
	 */
	public void setlaenge(double laenge) {
		double aktlaenge = laenge();
		double faktor = laenge / aktlaenge;
		this.x *= faktor;
		this.y *= faktor;
	}

	/**
	 * Tauscht die beiden Kompanjenten aus
	 */
	public void swap() {
		double a = x;
		x = y;
		y = a;
	}

	/**
	 * Textuelle Ausgabe des Strings (Debug-Ausgabe)
	 *
	 * @return Formatierter String, der X- und Y-Komponenten enthaelt
	 */
	public String toString() {
		return "[" + x + ", " + y + "]";
	}

	/**
	 * Koordinaten aus einem XML-Node auslesen.
	 * Besonderheit: Ändert die eigenden Daten, KEIN neues Objekt
	 *
	 * @param n Der zu lesende Node
	 */
	public void leseAusXmlNode(Node n) {
		// in die Bilder eintauchen
		NodeList nds = n.getChildNodes();
		// wie aktuell ;)
		Node a;

		for (int i = 0; i < nds.getLength(); i++) {
				a = nds.item(i);
			if (a.getNodeName().equals("x")) {
				x = Integer.parseInt(a.getTextContent());
			} else if (a.getNodeName().equals("y")) {
				y = Integer.parseInt(a.getTextContent());
			}
		}
	}
}


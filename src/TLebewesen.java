package de.rccc.java.witchcraft;

import java.util.*;

/**
 * Lebewesen sind alle Einheiten, die Leben haben
 */
public class TLebewesen extends TBildObjekt {
	/**
	 * Anzahl der Lebenspunkt des Lebewesens
	 */
	protected double flebenspunkte;
	
	/**
	 * Maximale Lebendspunkte des Lebewesens.
	 * wichtig für die Berechnung des Balkens
	 */
	protected double flebenmax;

	/**
	 * Die Liste aller Objekte, die bearbeitet werden müssen,
	 * wenn das hier zerstört wird
	 */
	protected List<TObjekt> fabhaengige = new LinkedList<TObjekt>();

	/**
	 * Auf welcher Seite ist dieses Lebewesen? 0 = Spieler
	 */
	protected int fseite; 

	/**
	 * Balkenlaenge.
	 * Damit das nicht jedesmal bei der Anzeige berechnet werden muss
	 */
	protected int fbll;

	/**
	 * ROF (rate of fire) - die Schussfrequenz
	 */
	protected int frof;

	/**
	 * Konstruktor
	 *
	 * @param koord Die Koordinaten, auf dem das Lebewesen erscheint.
	 * @param dim Die Dimensionen des Lebewesen. bei null automatische
	 * Erkennung
	 * @param geschw die Bewegung des Lebewesens
	 * @param darstellung Welches Bild nutzt das Lebewesen
	 * @param lebenspunkte Die Lebendspunkte des Lebewesens
	 * @param seite welcher Seite gehört das Lebewesen an
	 */
	TLebewesen(TVektor koord, TVektor dim, TVektor geschw, String darstellung,
		double lebenspunkte, int seite) {

		super(koord, dim, geschw, darstellung);
		flebenspunkte = lebenspunkte;
		flebenmax = lebenspunkte;
		fseite = seite;
		// fbll berechnen
		treffer(0);
		frof = 0;
	}

	/**
	 * Zieht die Trefferpunkte von dem Leben ab, und gibt zurück, ob
	 * der gestorben ist 
	 *
	 * @param punkte Die Punkte, die abgezogen werden
	 * @return True, wenn der gestorben ist
	 */
	public boolean treffer(double punkte) {
		flebenspunkte -= punkte;
		// Falls der Spieler Heilung aufsammelt
		if (flebenspunkte > flebenmax) {
			flebenspunkte = flebenmax;
		}
		fbll = (int) ((fdim.x-2)*(flebenspunkte/flebenmax));
		return flebenspunkte < 0;
	}

	/**
	 * Zeichnet das Bild an seinen Koordinaten auf der Zeichenflaeche g.
	 * Zusätzlich wird noch ein Health Balken gemalt.
	 *
	 * @param g Die Zeichenflaeche, auf der das Objekt gezeichnet werden soll
	 */
	public void zeichne(java.awt.Graphics g) {
		super.zeichne(g);
		g.setColor(java.awt.Color.BLACK);
		g.fillRect((int)fkoord.x, (int)fkoord.y-3, (int)fdim.x, 3);
		g.setColor(java.awt.Color.RED);
		g.fillRect((int)fkoord.x+1, (int)fkoord.y-2, fbll, 2);
	}

	/**
	 * Fügt ein Objekt an die Liste der abhängigen Objekte hinzu
	 */
	public void addAbhaengige(TObjekt abhaengig){
		fabhaengige.add(abhaengig);
	}

	/**
	 * Löscht ein Objekt aus der Liste der abhängigen Objekte.
	 * Wenn z.B. eine Rakete eingeschlagen hat,
	 * ist es nicht mehr abhängig :)
	 */
	public void delAbhaengige(TObjekt abhaengig){
		fabhaengige.remove(abhaengig);
	}

	/**
	 * Gibt eine Liste der abhängigen Objetke zurück.
	 * Z.B. Werden die Raketen, die dieses Objekt als Ziel haben,
	 * hier gespeichert. Wenn dieses Objekt stirbt, werden die
	 * Raketen auf Dumb-Fire gestellt
	 */
	public List<TObjekt> getAbhaengige() {
		return fabhaengige;
	}

	/**
	 * Gibt die Seite zurück, der dieses Lebewesen angehört
	 */
	public int getSeite() {
		return fseite;
	}

	/**
	 * Wird aufgerufen, wenn das Objekt gelöscht (aus den Listen) wird.
	 * So kann jedes erbende Objekt ein letztes Röcheln abgeben.. :)
	 *
	 * @param tot True, wenn das Objekt wirklich stirbt.
	 * False, wenn das Objekt nur "getötet" wird, weil es außerhalb
	 * des Bildschirms ist
	 * @param waffe Mit welcher Waffe wurde das Lebewesen
	 * eliminiert? (TWaffe/null)
	 */
	public void ende(boolean tot, TWaffe waffe) {
		// Immer alle Abhängigen Objekte überarbeiten
		for (TObjekt o: fabhaengige) {
			if (o instanceof TRakete) {
				((TRakete)o).setZiel(null);
			}
		}
		fabhaengige.clear();
	}

	/**
	 * Liefert die Lebenspunkte des Lebewesens
	 */
	public double getLeben() {
		return flebenspunkte;
	}

	/**
	 * Liefert die maximale Anzahl der Lebenspunkte des Lebewesens
	 */
	public double getLebenMax() {
		return flebenmax;
	}

	/**
	 * Setzt die Lebenspunkte des Lebewesens
	 */
	public void setLeben(double leben) {
		flebenspunkte = leben;
		treffer(0);
	}

	/**
	 * Setzt die maximalen Lebenspunkte des Lebewesens
	 */
	public void setLebenMax(double lebenmax) {
		flebenmax = lebenmax;
	}
} 

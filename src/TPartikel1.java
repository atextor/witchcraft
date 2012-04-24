package de.rccc.java.witchcraft;

import java.awt.Color;

/**
 * Partikel, der einen Funken darstellt
 */
public class TPartikel1 extends TObjekt implements IPartikel {
	/**
	 * Konstruktor
	 *
	 * @param koord Der Koordinatenvektor
	 * @param dim Der Groessenvektor
	 * @param geschw Der Geschwindigkeitsvektor
	 */
	TPartikel1(TVektor koord, TVektor dim, TVektor geschw) {
		super(koord, dim, geschw);
	}

	/**
	 * Zeichnet das Bild an seinen Koordinaten auf der Zeichenflaeche g
	 *
	 * @param g Die Zeichenflaeche, auf der das Objekt gezeichnet werden soll
	 */
	public void zeichne(java.awt.Graphics g) {
		g.setColor(Color.YELLOW);
		g.fillOval((int)fkoord.x, (int)fkoord.y, (int)fdim.x, (int)fdim.y);
	}
}


package de.rccc.java.witchcraft;

import java.awt.Color;

/**
 * Stellt ein Rauchwoelkchen dar, das von einer Rakete verursacht wird
 */
public class TPartikelRauch extends TObjekt implements IPartikel {
	/**
	 * Konstruktor
	 *
	 * @param koord Der Koordinatenvektor
	 * @param geschw Der Geschwindigkeitsvektor
	 */
	TPartikelRauch(TVektor koord, TVektor geschw) {
		super(koord, new TVektor(8, 8), geschw);
	}

	/**
	 * Zeichnet das Bild an seinen Koordinaten auf der Zeichenflaeche g
	 *
	 * @param g Die Zeichenflaeche, auf der das Objekt gezeichnet werden soll
	 */
	public void zeichne(java.awt.Graphics g) {
		int x = (int)fkoord.x;
		int y = (int)fkoord.y;
		g.setColor(new Color(89, 89, 89));
		g.fillOval(x-3, y-3, 7, 7);
		g.setColor(new Color(121, 121, 121));
		g.fillOval(x, y, 2, 2);
	}
}


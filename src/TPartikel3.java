package de.rccc.java.witchcraft;

import java.awt.Color;

/**
 * Stellt einen Funken dar, der aus dem Besenende sprueht
 */
public class TPartikel3 extends TObjekt implements IPartikel {
	/**
	 * Konstruktor
	 *
	 * @param koord Der Koordinatenvektor
	 * @param geschw Der Geschwindigkeitsvektor
	 */
	TPartikel3(TVektor koord, TVektor geschw) {
		super(koord, new TVektor(10,10), geschw);
	}

	/**
	 * Zeichnet das Bild an seinen Koordinaten auf der Zeichenflaeche g
	 *
	 * @param g Die Zeichenflaeche, auf der das Objekt gezeichnet werden soll
	 */
	public void zeichne(java.awt.Graphics g) {
		int x = (int)fkoord.x;
		int y = (int)fkoord.y;

		g.setColor(new Color(132, 156, 172));
		g.fillRect(x + 4, y + 1, 3, 9);
		g.fillRect(x + 1, y + 4, 9, 3);
		g.setColor(new Color(255, 255, 255));
		g.fillRect(x + 6, y, 1, 11);
		g.fillRect(x, y + 6, 11, 1);
	}
}


package de.rccc.java.witchcraft;

import java.awt.Color;

/**
 * Stellt einen Explosions-Partikel dar, der durch
 * mehrere gefuellte Kreise mit jeweils kleinerem Radius
 * und abgestuften Farben dargestellt wird
 */
public class TPartikel2 extends TObjekt implements IPartikel {
	int foffset = 1;

	/**
	 * Konstruktor
	 *
	 * @param koord Der Koordinatenvektor
	 * @param dim Der Groessenvektor
	 * @param geschw Der Geschwindigkeitsvektor
	 * @param offset Um wieviel sollen die inneren Kreise jeweils
	 * verschoben sein
	 */
	TPartikel2(TVektor koord, TVektor dim, TVektor geschw, int offset) {
		super(koord, dim, geschw);
		this.foffset = offset;
	}

	/**
	 * Zeichnet das Bild an seinen Koordinaten auf der Zeichenflaeche g
	 * Die Farben sind auf den Default-Grau-Hintergrund, der im Niedrig-
	 * Detail-Modus verwendet wird, eingestellt, um wenigstens den
	 * Anschein von zusammenpassender Grafik zu erwecken
	 *
	 * @param g Die Zeichenflaeche, auf der das Objekt gezeichnet werden soll
	 */
	public void zeichne(java.awt.Graphics g) {
		int x = (int)fkoord.x;
		int y = (int)fkoord.y;
		int w = (int)fdim.x;
		int h = (int)fdim.y;
		g.setColor(new Color(91, 72, 61));
		g.fillOval(x, y, w, h);
		x += foffset; y += foffset; w -= foffset*2; h -= foffset*2;
		g.setColor(new Color(117, 79, 53));
		g.fillOval(x, y, w, h);
		x += foffset; y += foffset; w -= foffset*2; h -= foffset*2;
		g.setColor(new Color(149, 98, 50));
		g.fillOval(x, y, w, h);
		x += foffset; y += foffset; w -= foffset*2; h -= foffset*2;
		g.setColor(new Color(182, 131, 52));
		g.fillOval(x, y, w, h);
		x += foffset; y += foffset; w -= foffset*2; h -= foffset*2;
		g.setColor(new Color(233, 207, 70));
		g.fillOval(x, y, w, h);
	}
}


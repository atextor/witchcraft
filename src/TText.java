package de.rccc.java.witchcraft;

import java.awt.Color;
import java.awt.Font;

/**
 * Textobjekt, das auch fuer ein automatisches Ausblenden des
 * Textes nach einer bestimmten Anzahl von Darstellungen (Ticks)
 * sorgen kann
 */
public class TText extends TObjekt implements IPartikel {
	/**
	 * Der anzuzeigende String
	 */
	protected String ftext;
	
	/**
	 * Wie soll der String bei der Ausgabe formatiert werden
	 */
	protected Font ffont;

	/**
	 * Wie oft soll noch gezeichnet werden?
	 */
	protected int ftimeout = -1;

	/**
	 * Konstruktor
	 *
	 * @param koord Der Koordinatenvektor
	 * @param text Der anzuzeigende Text
	 * @param font Wie soll der String bei der Ausgabe formatiert werden
	 * @param timeout Wie viele Ticks soll der String angezeigt werden
	 */
	TText(TVektor koord, String text, Font font, int timeout) {
		this(koord, text, font);

		ftimeout = timeout;
	}

	/**
	 * Konstruktor
	 *
	 * @param koord Der Koordinatenvektor
	 * @param text Der anzuzeigende Text
	 * @param font Wie soll der String bei der Ausgabe formatiert werden
	 */
	TText(TVektor koord, String text, Font font) {
		super(koord, null, null);

		ftext = text;
		ffont = font;
	}

	/**
	 * Konstruktor
	 *
	 * @param koord Der Koordinatenvektor
	 * @param geschw Der Richtungsvektor
	 * @param text Der anzuzeigende Text
	 * @param font Wie soll der String bei der Ausgabe formatiert werden
	 */
	TText(TVektor koord, TVektor geschw, String text, Font font) {
		super(koord, new TVektor(1, 1), geschw);

		ftext = text;
		ffont = font;
	}

	/**
	 * Zeichnet das Bild an seinen Koordinaten auf der Zeichenflaeche g
	 *
	 * @param g Die Zeichenflaeche, auf der das Objekt gezeichnet werden soll
	 */
	public void zeichne(java.awt.Graphics g) {
		if (ftimeout > 0) {
			ftimeout--;
		}

		if ((ftimeout != 0) && (ftext != null)) {
			g.setFont(ffont);
			// Erst ein Bisschen versetzt schwarz zeichnen, dann mittig in
			// gelb. Gibt einen netten Umrandungseffekt
			g.setColor(Color.BLACK);
			g.drawString(ftext, (int)fkoord.x + 1, (int)fkoord.y + 1);
			g.drawString(ftext, (int)fkoord.x - 1, (int)fkoord.y - 1);
			g.drawString(ftext, (int)fkoord.x + 1, (int)fkoord.y - 1);
			g.drawString(ftext, (int)fkoord.x - 1, (int)fkoord.y + 1);
			g.setColor(Color.YELLOW);
			g.drawString(ftext, (int)fkoord.x, (int)fkoord.y);
		}
	}

	/**
	 * Setzt den Text, der dauerhaft angezeigt werden soll
	 */
	public void setText(String text) {
		ftext = text;
		ftimeout = -1;
	}

	/**
	 * Setzt einen Text, der nach 80 Ticks verschwindet
	 */
	public void setTimeoutText(String text) {
		ftext = text;
		ftimeout = 80;
	}

	/**
	 * Liefert den eingestellten Text zurueck
	 */
	public String getText() {
		return ftext;
	}
}


package de.rccc.java.witchcraft;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Alle Geschosse (reduzieren Leben) erben hiervon, oder sind dieses.
 * Geschosse sind die Repraesentation der Objekte, die rumfliegen.
 * Die Geschosse haben einen Link zu den definierten Waffen,
 * wo diese Geschosse genauer beschrieben sind.
 */ 
public class TGeschoss extends TBildObjekt {
	/**
	 * Die Ausrichtung des Geschosses, in Grad
	 */
	protected double fausrichtung;

	/**
	 * Für welche Seite fliegt das Geschoss?
	 */
	protected int fseite;
	
	/** 
	 * Time to life - wieviele ticks darf das 
	 * Geschoss überleben?
	 * Positiver fttl wird bei bewegen() runtergezählt
	 * -1 - ewig
	 * 0  - beim nächsten durchlauf löschen
	 */
	protected int fttl = -1;

	/**
	 * Das ist ein Geschoss der Waffe..
	 */
	protected TWaffe fwaffe = null;
	
	/**
	 * Sicherheitszone für die ausserhalbBildschirm.
	 * Notwendig, da der Kollsionsbereich nicht das gesammte
	 * Objekt bedekt, sondern ein Quadrat in der Mitte.
	 * Um zu verhindern, das das Geschoss dann am Rand einfach 
	 * verschwindet, wird das benuzt.
	 */
	private double fsicherheit = 0;

	/**
	 * Einmal das Bild holen reicht
	 */
	protected BufferedImage fimg = null;

	/**
	 * Offset für das Zeichnen
	 */
	protected TVektor foffset = null;

	/**
	 * Berechnet das notwendige zum Zeichnen des Bildes:
	 * img, offset
	 */
	protected void berechneBildangaben() {
		if (fbild != null) {
			// Rail z.B. hat kein Bild
			fimg = fbild.getFrame(fausrichtung);

			foffset = new TVektor((fimg.getWidth() - fdim.x) / 2,
				(fimg.getHeight() - fdim.y) / 2);
		}
	}

	/**
	 * Konstruktor des Geschosses
	 *
	 * @param koord Die Startkoordinaten
	 * @param dim Die Groesse der Bounding-Box
	 * @param geschw Der Geschwindigkeitsvektor
	 * @param seite Die Gesinnung
	 * @param waffe Das ist ein Geschoss der Waffe
	 */
	TGeschoss(TVektor koord, TVektor dim, TVektor geschw,
		int seite, TWaffe waffe) {

		super(koord, dim, geschw, waffe.getBild());

		// Der Kollsionsbereich ist ein kleines Rechteck in der Mitte
		// So hat man einen brauchbaren Kollisonsbereich, und es sieht
		// "cooler" aus.
		if (fbild != null) {
			// Rail hat kein Bild!
			fdim.set(fbild.groesse(0));
			if (fdim.x > fdim.y) {
				fsicherheit = fdim.x;
				fdim.x = fdim.y;
			} else {
				fsicherheit = fdim.y;
				fdim.y = fdim.x;
			}
		}
		
		fwaffe = waffe;
		fwaffe.statIncAbgefeuert();

		// Ausrichtung ausrechnen
		fausrichtung = TVektor.genormt.winkel(geschw);

		berechneBildangaben();
		fseite = seite;
		TSound.play(waffe.getStartSound());
	} 

	/**
	 * Methode zum Zeichnen des Geschosses, ueberschreibt Methode
	 * von TObjekt. Objekt wird automatisch in die Richtung gedreht,
	 * in die es fliegt
	 *
	 * @param g Das Grafik-Objekt, auf dem gezeichnet werden soll
	 */
	public void zeichne(java.awt.Graphics g) { 
		// Debug: zeichnet den Kollisionsbereich des Geschosses
		/*
		g.setColor(java.awt.Color.CYAN);
		g.fillRect((int)x,(int)y,(int)fimg.getWidth(),(int)fimg.getHeight());
		g.setColor(java.awt.Color.RED);
		g.fillRect((int)fkoord.x,(int)fkoord.y,(int)fdim.x,(int)fdim.y);
		*/
		
		g.drawImage(fimg, (int)(fkoord.x - foffset.x),
			(int)(fkoord.y - foffset.y), null);
	}

	/**
	 * Ein Geschoss soll ein Lebewesen nur beruehren koennen, wenn
	 * beide verfeindet sind
	 *
	 * @param anderes Das Lebewesen, das das Geschoss potentiell beruehrt
	 */
	public boolean beruehrt(TLebewesen anderes) {
		return (anderes.getSeite() != fseite) && super.beruehrt(anderes);
	}

	/**
	 * Bewegt das Objekt einen Schritt, abhaengig von seiner Geschwindigkeit
	 * Außerdem wird hier noch der TTL (Time to live) reduziert
	 */
	public void bewege() {
		super.bewege();
		if (fttl > 0) {
			fttl -= 1;
		}
	}

	/**
	 * Wird aufgerufen, wenn das Objekt gelöscht (aus den Listen) wird.
	 * So kann jedes erbende Objekt ein letztes Röcheln abgeben.
	 *
	 * @param tot True, wenn das Objekt wirklich stirbt.
	 * False, wenn das Objekt nur "getötet" wird, weil es außerhalb
	 * des Bildschirms ist
	 */
	public void ende(boolean tot) {
		if (tot) {
			TSound.play(fwaffe.getHitSound());
			TSharedObjects.getPartikelVerwaltung().startEffekt(
				fwaffe.getPartikelTreff(),
				getMitte(), getGeschw());
			fwaffe.statIncTreffer();
		}
	}
	
	/**
	 * Ist das Geschoss am Ende? (ttl=0)
	 *
	 * @return true - Geschoss aus der Liste nehmen
	 */
	public boolean tot() {
		return fttl == 0;
	}

	/**
	 * Gibt die Trefferpunkte des Geschosses zurück
	 */
	public int getTrefferp() {
		return fwaffe.getSchaden();
	}

	/**
	 * Gibt die TTL (time to life) zurück.
	 * Wenn =0, dann aus der Liste nehmen
	 */
	public int getTtl() {
		return fttl;
	}

	/**
	 * Gibt die Waffe zurück, aus dem das Geschoss gekommen ist 
	 */
	public TWaffe getWaffe() {
		return fwaffe;
	}

	/**
	 * Überprüft, ob das Objekt nicht jetzt ausgespielt hat.
	 * Hierhin verlagert, weil das Geschoss nur einen kleinen
	 * Kollisionsbereich in der Mitte hat,
	 * und es so einfach "verschwinden" würde.
	 * Daher hier etwas toleranz aufbauen... :)
	 *
	 * @return true, wenn das Ojekt nicht mehr gebraucht wird
	 */
	public boolean ausserhalbBildschirm () {
		int x = TSharedObjects.FENSTER_BREITE;
		int y = TSharedObjects.FENSTER_HOEHE;
		TVektor ru = getRU();
		return (ru.x < -fsicherheit) || (ru.y < -fsicherheit) ||
			(fkoord.x > x+fsicherheit) || (fkoord.y > y+fsicherheit);
	}

}

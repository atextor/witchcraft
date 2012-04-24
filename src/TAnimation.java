package de.rccc.java.witchcraft;

import java.awt.image.BufferedImage;
import java.util.*;
import javax.imageio.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

/**
 * TAnimation kapselt eine Liste von Bildern und eine
 * Animationsgeschwindigkeit in ein Objekt. Sie hat ausserdem
 * die Methode "rotiereFrame", die aufgerufen werden kann, wenn
 * das Objekt genau einen Frame enthaelt. Dieser wird dann in
 * mehrfach gedrehter Form als die Liste der Frames verwendet,
 * fuer Geschosse.
 */
public class TAnimation {
	/**
	 * Anzahl der Phasen fuer ein rotierendes Geschoss
	 */
	static private int rphasen = 32;

	/**
	 * Liste der Animationsphasen
	 */
	protected List<BufferedImage> fphasen = new ArrayList<BufferedImage>();

	/**
	 * Der Zaehler der angibt, wie oft der aktuelle Animationsframe
	 * bereits wiederholt wurde
	 */
	protected int faktuell = 0;

	/**
	 * Der Zaehler, der angibt, welcher der Animationsframes der aktuell
	 * anzuzeigende ist
	 */
	protected int fphase = 0;
	
	/**
	 * Wie oft soll jeder Animationsframe wiederholt werden
	 */
	protected int fwiederhol;

	/**
	 * Konstruktor fuer ein einzelnes Bild
	 *
	 * @param bildname Dateiname des Bildes
	 */
	TAnimation(String bildname) {
		fwiederhol = 0;
		fphasen.add(ladeBild(bildname));
	}

	/**
	 * Konstruktor fuer mehrere Animationsphasen
	 *
	 * @param bildmaske Dateinamen-maske der Phasenbilder, dieser muss
	 * im Format sein: bild%%.png (genau ein Vorkommen von '%%', dieses
	 * wird spaeter durch die Nummern der Einzelbilder ersetzt (1-n))
	 * @param anzahl Wie viele Einzelbilder sollen geladen werden. Ein Wert von
	 * 3 laedt mit der Maske "bild%%.png" die Dateien "bild1.png", "bild2.png"
	 * und "bild3.png"
	 * @param wiederhol Die Anzahl der Ticks, die jedes Einzelbild vor dem
	 * Wechsel angezeigt werden soll
	 */
	TAnimation(String bildmaske, int anzahl, int wiederhol) {
		fwiederhol = wiederhol;
		for (int i = 1; i <= anzahl; i++) {
			fphasen.add(ladeBild(bildmaske.replace("%%", "" + i)));
		}
	}

	/**
	 * Kopierkonstruktor.
	 * Beim Kopieren der TAnimation bekommt die Kopie eine Referenz
	 * (keine Kopie) der Liste der Animationsframes.
	 * @param t Die zu kopierende TAnimation
	 */
	TAnimation(TAnimation t) {
		this.fwiederhol = t.fwiederhol;
		this.fphasen = t.fphasen;
		this.faktuell = fwiederhol > 0 ? TSharedObjects.rndInt(fwiederhol) : 0;
		this.fphase = t.fphasen.size() > 0 ?
			TSharedObjects.rndInt(t.fphasen.size()) : 0;
	}

	/**
	 * Liefert den Groessenvektor des ersten Animationsframes zurueck
	 * (es wird davon ausgegangen, dass alle Frames einer Animation
	 * die selbe Groesse haben)
	 */
	public TVektor groesse() {
		return groesse(fphase);
	}

	/**
	 * Liefert den Groessenvektor eines beliebigen Frames zurueck.
	 * Diese Methode wird bei Geschossen verwendet, da hier die Frames
	 * unterschiedliche Groessen haben koennen.
	 * @param frame Die Nummer des Frames, dessen Groesse geliefert
	 * werden soll
	 */
	public TVektor groesse(int frame) {
		return new TVektor(fphasen.get(frame).getWidth(null),
			fphasen.get(frame).getHeight(null));
	}

	/**
	 * Liefert den naechsten anzuzeigenden Frame bei Animationen
	 * mit gleichmaessigem Tempo
	 * @return Der Animationsframe
	 */
	public BufferedImage getFrame() {
		faktuell++;
		if (faktuell > fwiederhol) {
			faktuell = 0;
			fphase++;
			if (fphase >= fphasen.size()) {
				fphase = 0;
			}
		}
		return fphasen.get(fphase);
	}

	/**
	 * Liefert einen der Frames bei Animationen, wo die Frames
	 * fuer die verschiedenen Ausrichtungen eines Objektes stehen,
	 * abhaengig von der gewuenschten Ausrichtung
	 *
	 * @param ausrichtung Die Ausrichten des Objektes in RAD
	 * @return Die Drehphase
	 */
	public BufferedImage getFrame(double ausrichtung) {
		// Welches Frame wird gebraucht?
		int x = (int)(rphasen - (rphasen / (2 * Math.PI)) *
			(ausrichtung - 3 * Math.PI)) % rphasen;
		return fphasen.get(x);
	}

	/**
	 * Laedt ein Bild aus einer Datei
	 */
	private BufferedImage ladeBild(String dateiname) {
		BufferedImage res = null;
		java.net.URL pfad =
			this.getClass().getClassLoader().getResource("bilder/" + dateiname);
		try {
			res = ImageIO.read(pfad);
		} catch (Exception e) {
			System.out.println("Konnte Bild " + dateiname +
				" nicht laden (Pfad: " + pfad + ")");
			TSharedObjects.endGame();
		}

		return res;
	}

	/**
	 * Diese Methode kann einmalig aufgerufen werden fuer TAnimations-Objekte,
	 * die genau einen Frame haben. 
	 * Sie dreht dann diesen Frame rphasen-1 mal (z.Zt. 31 mal),
	 * hinterher hat das TAnimation-Objekt 32 Frames, die jeweils um 11.25°
	 * (= 360/32) gedreht sind. Das macht Sinn fuer Geschosse, damit diese
	 * nicht bei jedem mal Zeichnen die teuere Transformations-Operation
	 * durchfuehren muessen.
	 */
	public void rotiereFrame() {
		// Wir wollen nur Frames erzeugen bei Animationen mit genau einem Frame
		// Allerdings verwenden mehrere Waffen die selben Animations-Objekte,
		// deswegen ignorieren wir einen erneuten Aufruf (kein Problem)

		if (fphasen.size() == 1) {
			BufferedImage orig = fphasen.get(0);

			// Die ursprüngliche Dimension merken:
			TVektor fdim = new TVektor(orig.getWidth(), orig.getHeight());

			int groesse = (int)fdim.x;
			if (groesse < fdim.y) {
				groesse = (int)fdim.y;
			}

			// Das Originalbild durch ein quadratisches Bild
			// ersetzen, damit das Drehen klappt
			BufferedImage neuOriginal = new BufferedImage(groesse, groesse,
				BufferedImage.TYPE_INT_ARGB_PRE);
			neuOriginal.createGraphics().drawImage(orig, null,
				(groesse - orig.getWidth()) / 2, (groesse - orig.getHeight()) / 2);
			orig = neuOriginal;

			double w = groesse / 2;
			double theta = (double)(Math.toRadians(360 / rphasen));
			int transformTyp = AffineTransformOp.TYPE_BICUBIC;
			AffineTransformOp transformer = null;

			// Es muss jedesmal ein neues Transformationsobjekt erzeugt werden.
			// Wuerden wir inkrementell drehen, d.h. den n-ten Frame basierend
			// auf dem (n-1)-ten, so wuerden die spaeteren Frames verwaschen
			// aussehen
			for (int i = 1; i < rphasen; i++) {
				transformer = new AffineTransformOp(
					AffineTransform.getRotateInstance(theta * i, w, w), transformTyp);
				BufferedImage neu = new BufferedImage(groesse, groesse,
					BufferedImage.TYPE_INT_ARGB_PRE);
				transformer.filter(orig, neu);
				fphasen.add(neu);
			}

			fwiederhol = 1;
		}
	}
}






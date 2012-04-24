package de.rccc.java.witchcraft;

import java.util.Map;
import java.awt.image.*;

/**
 * Ein Spielobjekt, das durch ein Bild oder eine Animation
 * dargestellt wird (TAnimation-Objekt als Darstellung)
 */
public class TBildObjekt extends TObjekt implements IPartikel {
	/**
	 * Darstellung des Objekts
	 */
	protected TAnimation fbild;

	/**
	 * Konstruktor
	 *
	 * @param koord Der Koordinatenvektor
	 * @param dim Der Groessenvektor
	 * @param geschw Der Geschwindigkeitsvektor
	 * @param darstellung Darstellung des Objekts
	 */
	TBildObjekt(TVektor koord, TVektor dim, TVektor geschw,
		String darstellung) {

		super(koord, dim, geschw);

		this.fbild = TSharedObjects.getNewBild(darstellung);
		if ((dim == null) && (fbild != null)) {
			this.fdim = fbild.groesse();
			if (fkoord != null) {
				this.fru = fkoord.newAdd(fdim);
			}
		} else {
			this.fdim = dim;
		}
	}

	/**
	 * Zeichnet das Bild an seinen Koordinaten auf der Zeichenflaeche g
	 *
	 * @param g Die Zeichenflaeche, auf der das Objekt gezeichnet werden soll
	 */
	public void zeichne(java.awt.Graphics g) {
		g.drawImage(fbild.getFrame(), (int)fkoord.x, (int)fkoord.y, null);
	}
}


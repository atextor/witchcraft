package de.rccc.java.witchcraft;

import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 * Klasse für alle vom Spieler aufsammelbaren Items.
 * Diese beschraenken sich auf ein "Health"-Objekt
 * und die verschiedenen Waffen, waere aber erweiterbar.
 */
public class TItem extends TBildObjekt {
	/**
	 * Die Items, die zur Verfuegung stehen.
	 * Heilung:  Heilt den Spieler
	 */
	static public enum Items {
	 	/**
		 * Not in List. Fehlwert
		 */
		NIL, 
		/**
		 * Heilung.
		 * Dieses Item heilt den Spieler
		 */
		Heilung,
		/**
		 * Waffe.
		 * Item ist eine Waffe
		 */
		Waffe
	};

	/**
	 * Das Item, das das Objekt ist
	 */
	protected Items fitem;

	/**
	 * Der int-Parameter zu dem Item.
	 * Z.B. heilt ein Heilung fiparam Punkte
	 */
	protected int fiparam = 0;

	/**
	 * Der String-Param zu diesem Item
	 */
	protected String fsparam = null;

	/**
	 * Konstuktor.
	 *
	 * @param koord Der Koordinatenvektor
	 * @param dim Der Groessenvektor
	 * @param geschw Der Geschwindigkeitsvektor
	 * @param darstellung Darstellung des Objekts
	 * @param item Das Item, das das Objekt ist
	 * @param iparam Integer-Parameter des Items
	 * @param sparam String-Parameter des Items
	 */
	TItem(TVektor koord, TVektor dim, TVektor geschw, String darstellung,
		Items item, int iparam, String sparam) {

		super(koord, dim, geschw, darstellung); 
		fitem = item;
		fiparam = iparam;
		fsparam = sparam;
	}

	/**
	 * Von welchem Typ ist dieses Item?
	 *
	 * @return Das Item
	 */
	public Items getItem() {
		return fitem;
	}

	/**
	 * Der Integer-Parameter des Item.
	 * z.B. Die Healthanzahl.
	 *
	 * @return Der Parameter
	 */
	public int getiParam() {
		return fiparam;
	}

	/**
	 * Der String-Parameter des Item.
	 * z.B. Die Waffe.
	 *
	 * @return Der Parameter
	 */
	public String getsParam() {
		return fsparam;
	}

	/**
	 * Generiert ein Item aus dem übergebenen XML-Node
	 *
	 * @param nds Die Nodeliste des Items
	 */
	public static TItem itemAusNode(NodeList nds) throws Exception {
		String bild = null;
		TItem.Items item = TItem.Items.NIL;
		int iparam = 0;
		String sparam = null;

		for (int j = 0; j < nds.getLength(); j++) {
			Node a = nds.item(j);
			if (a.getNodeName().equals("bild")) {
				bild = a.getTextContent();
			} else if (a.getNodeName().equals("item")) {
				item = TItem.Items.valueOf(a.getTextContent());
			} else if (a.getNodeName().equals("iparam")) {
				iparam = Integer.parseInt(a.getTextContent());
			} else if (a.getNodeName().equals("sparam")) {
				sparam = a.getTextContent();
			}
		}

		if ((bild==null) || (item == TItem.Items.NIL)) {
			throw new Exception("Einem Item fehlt was");
		}

		if (!TSharedObjects.bildInListe(bild)) {
			throw new Exception("Das ItemBild \"" + bild + "\" gibt es nicht.");
		}

		return new TItem(null, null, new TVektor(-4, 0), bild, item,
			iparam, sparam);
	}

	/**
	 * Überprüft, ob das Objekt nicht jetzt ausgespielt hat.
	 * Überschreiben, weil Items von rechts nach links durchlaufen,
	 * und wenn zufällig ein Feind außerhalb des Bildschirms eleminiert
	 * wird, dann würde das Item verschwinden.
	 *
	 * @return true, wenn das Ojekt nicht mehr gebraucht wird
	 */
	public boolean ausserhalbBildschirm () {
		int x = TSharedObjects.FENSTER_BREITE;
		int y = TSharedObjects.FENSTER_HOEHE;
		TVektor ru = getRU();
		return (ru.x < 0) || (ru.y < 0);
	}
}

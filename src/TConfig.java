package de.rccc.java.witchcraft;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.util.*;

/**
 * Klasse, die fuer das Laden von Ressourcen (Sound,
 * Bilder, Waffen) aus der Haupt-XML-Konfigurationsdatei
 * zustaendig ist
 */
public class TConfig {

	/**
	 * Lädt die Waffenkonfiguration
	 * @param n Aus diesem Node sollen die Waffen geladen werden
	 */
	private static void ladeWaffen(Node n) throws Exception {
		// in die Bilder eintauchen
		NodeList nds = n.getChildNodes(); 
		// wie aktuell ;)
		NodeList a; 
		String id;
		TWaffe w;
		System.out.println("Lade Waffen");
		for (int i = 0; i < nds.getLength(); i++) {
			if (nds.item(i) instanceof Element) {
				a = nds.item(i).getChildNodes();
				if (nds.item(i).getNodeName()!="waffe") {
					throw new Exception("Das Element " + a +
						" hat hier Nichts zu suchen!");
				} else {
					id = null;
					for (int j = 0; j < a.getLength(); j++) {
						if (a.item(j).getNodeName() == "ID") {
							id = a.item(j).getTextContent();
						}
					}
					if (id == null) {
						throw new Exception("Fehlendes ID Tag");
					}
					System.out.println(" Lade: " + id);
					w = TWaffe.WaffeAusNode(nds.item(i));
					if (w == null) {
						throw new Exception("Fehler beim Laden der Waffe.");
					}
					TWaffe.addWaffe(id,w);
				}
			}
		}
	}

	/**
	 * Läd die Sounds aus der übergebenen XML Liste
	 * @param n Aus diesem Node sollen die Sounds geladen werden
	 */
	private static void ladeSounds(Node n) throws Exception {
		System.out.println("Lese Sounds ein.. ");
		String id;
		String datei;
		boolean musik;
		// wie aktuell ;)
		NodeList a; 
		// in die Bilder eintauchen
		NodeList nds = n.getChildNodes();

		for (int i = 0; i < nds.getLength(); i++) {
			if (nds.item(i) instanceof Element) {
				a = nds.item(i).getChildNodes();
				id = null;
				datei = null;
				musik = false;

				for (int j = 0; j < a.getLength(); j++) {
					if (a.item(j).getNodeName() == "id") {
						id = a.item(j).getTextContent();
					} else if (a.item(j).getNodeName() == "datei") {
						datei = a.item(j).getTextContent();
					} else if (a.item(j).getNodeName() == "musik") {
						// Alleine das vorhandensein reicht aus
						musik = true;
					}
				}

				if (datei == null || id == null) {
					throw new Exception("Fehler beim Einlesen von witchcraft.xml: " +
							"Fehlendes Element");
				}

				System.out.println(" Lade: " + id + "/" + datei);
				TSound.addSound(id, datei, musik);
			}
		}
	}

	/**
	 * Lädt die Bilder aus der Übergebenen XML Liste
	 *
	 * @param n Der Node-Tree mit den bildern (der Punkt
	 * mit "<bildern></bildern>")
	 */
	private static void ladeBilder(Node n) throws Exception {
		System.out.println("Lese Bilder ein.. ");
		String id;
		String datei;
		int anzahl;
		int wiederhol;
		// wie aktuell ;)
		NodeList a; 
		// in die Bilder eintauchen
		NodeList nds = n.getChildNodes();

		for (int i = 0; i < nds.getLength(); i++) {
			if (nds.item(i) instanceof Element) {
				a = nds.item(i).getChildNodes();
				id = null;
				datei = null;
				anzahl = -1;
				wiederhol = -1;

				for (int j = 0; j < a.getLength(); j++) {
					if (a.item(j).getNodeName() == "ID") {
						id = a.item(j).getTextContent();
					} else if (a.item(j).getNodeName() == "datei") {
						datei = a.item(j).getTextContent();
					} else if (a.item(j).getNodeName() == "anzahl") {
						anzahl = Integer.parseInt(a.item(j).getTextContent());
					} else if (a.item(j).getNodeName() == "wiederhol") {
						wiederhol = Integer.parseInt(a.item(j).getTextContent());
					}
				}

				if (datei == null || id == null) {
					throw new Exception("Fehler beim Einlesen von witchcraft.xml: " +
							"Fehlendes Element");
				}

				System.out.println(" Lade: " + id + "/" + datei);

				if ((anzahl < 0) && (wiederhol < 0)) {
					TSharedObjects.addBild(id, new TAnimation(datei));
				} else {
					if ((anzahl < 0) || (wiederhol < 0)) {
						throw new Exception("Fehler beim Einlesen von witchcraft.xml: " + 
								"anzahl und wiederhol müssen zusammen angegeben werden.");
					} else {
						TSharedObjects.addBild(id, new TAnimation(datei,anzahl,wiederhol));
					}
				}
			}
		}
	}

	/**
	 * Haupt-Methode, die das Laden der Ressourcen startet und steuert
	 */
	public static void ladeRessourcen() throws Exception {
		java.net.URL pfad = TConfig.class.getResource("/config/witchcraft.xml");
		if (pfad == null) {
			throw new Exception("/config/witchcraft.xml nicht gefunden");
		}

		Document config = DocumentBuilderFactory
						.newInstance().newDocumentBuilder()
						.parse(pfad.toURI().toString());
		NodeList nds = config.getChildNodes();

		// Wir erwarten 1 Hauptelement, das "witchcraft" sein soll
		if ((nds.getLength() != 1) ||
			(nds.item(0).getNodeName() != "witchcraft")) {
			throw new Exception("Fehler: config-xml fehlerhaft formatiert");
		} else {
			// in witchcraft eintauchen
			nds = nds.item(0).getChildNodes(); 
			for (int i = 0; i < nds.getLength(); i++) {
				if (nds.item(i).getNodeName() == "bilder") {
					ladeBilder(nds.item(i));
				} if (nds.item(i).getNodeName() == "waffen") {
					ladeWaffen(nds.item(i));
				} if (nds.item(i).getNodeName() == "sounds") {
					ladeSounds(nds.item(i));
				}
			}
		}
	}
}


package de.rccc.java.witchcraft;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.transform.*;
import javax.xml.parsers.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.sax.*; 
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.text.DateFormat;

/**
 * Klasse die statische Methoden zum Laden und Speichern des 
 * aktuellen Spielstandes enthaelt
 */
public class TSavegame {
	/**
	 * Da die Java-Interne Liste ironischerweise das Interface
	 * ListModel nicht implementiert, benoetigen wir jetzt unsere
	 * eigene Version einer Liste, die auch mit JList
	 * zusammenarbeitet. 
	 */
	private static class TGenerischeListe implements ListModel {
		/**
		 * Die eigentliche Liste der Elemente
		 */
		private java.util.List<Object> felemente =
			new ArrayList<Object>();

		/**
		 * Nur fuer die Erfuellung des Interface benoetigt
		 */
		public void addListDataListener(ListDataListener _) {}

		/**
		 * Nur fuer die Erfuellung des Interface benoetigt
		 */
		public void removeListDataListener(ListDataListener _) {}

		/**
		 * Fuegt ein Objekt zur Liste hinzu
		 */
		public void add(Object o) {
			felemente.add(o);
		}

		/**
		 * Liefert das Objekt an der Stelle Index
		 */
		public Object getElementAt(int index) {
			return felemente.get(index);
		}

		/**
		 * Liefert die Groesse der Liste
		 */
		public int getSize() {
			return felemente.size();
		}
	}

	/**
	 * Gibt das Verzeichnis an, in dem die Savegames abgelegt
	 * werden sollen
	 */
	private static String home =
		System.getProperty("user.home") + "/.witchcraft/save";

	/**
	 * Methode, die ein Fenster zur Verfuegung stellt, mit dem man
	 * eines der vorhandenen Savegames zum Laden auswaehlen kann
	 */
	public static void savegameWaehlen() {
		final Map<String, Integer> eintraege = new HashMap<String, Integer>();
		final TGenerischeListe strings = new TGenerischeListe();
		final JDialog p = new JDialog(TSharedObjects.getMain().getHauptFenster(),
			"Bitte Savegame wählen", true);
		TSharedObjects.getAnzeige().updateEnable(false);
		TSharedObjects.getAnzeige().pause();

		// Liste von Eintraegen bauen
		try {
			int i = 1;
			while (new File(home + "/save" + i + ".xml").exists()) {
				File f = new File(home + "/save" + i + ".xml");
				Document datei = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(f);
				NodeList nds = datei.getChildNodes().item(0).getChildNodes();

				String levelnummer = null;
				String spielername = null;
				String punkte = null;

				for (int j = 0; j < nds.getLength(); j++) {
					String nodeName = nds.item(j).getNodeName();
					String inhalt = nds.item(j).getTextContent();
					if (nodeName.equals("levelnummer")) {
						levelnummer = inhalt;
					} else if (nodeName.equals("spielername")) {
						spielername = inhalt;
					} else if (nodeName.equals("punkte")) {
						punkte = inhalt;
					}
				}

				String angezeigt = "" + i + ":   " + "Spieler: " +
					spielername + "   Level: " + levelnummer +
					"  (" + punkte + " Punkte)  -- " +
					DateFormat.getDateTimeInstance()
						.format(new Date(f.lastModified()));
				strings.add(angezeigt);
				eintraege.put(angezeigt, i);
				i++;
			}
		} catch (Exception e) {
			TSharedObjects.getAnzeige().setMeldung("Savegame laden " + 
				"fehlgeschlagen");
			TSharedObjects.getAnzeige().updateEnable(true);
			p.dispose();
		}

		// Fenster und so weiter bauen
		final JList liste = new JList(strings);
		liste.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		final JScrollPane scrollPane = new JScrollPane(liste);
		scrollPane.setMinimumSize(new Dimension(650, 450));
		scrollPane.setSize(new Dimension(650, 450));
		scrollPane.setPreferredSize(new Dimension(650, 450));
		final JButton ok = new JButton("OK");

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _) {
				Integer ausgewaehlt = eintraege.get(liste.getSelectedValue());
				if (ausgewaehlt != null) {
					p.dispose();
					TSharedObjects.getAnzeige().lade(eintraege.get(
						liste.getSelectedValue()));
				}
			}
		});

		p.add(scrollPane, BorderLayout.CENTER);
		p.add(ok, BorderLayout.SOUTH);

		p.pack();
		p.setVisible(true);
		TSharedObjects.getAnzeige().updateEnable(true);
	}

	/**
	 * Gibt an, ob mindestens ein Savegame vorhanden ist
	 * @return true, falls mindestens ein Savegame gefunden wurde
	 * false falls keines gefunden wurde
	 */
	public static boolean mindEinSavegameVorhanden() {
		File f = new File(home + "/save1.xml");
		return f.exists();
	}

	/**
	 * Lädt und aktiviert ein Waffe aus dem Savegame
	 * @param n Die Node, aus der die Waffe erzeugt werden soll
	 * @param spieler Das Spielerobjekt, das die Waffe erhalten soll
	 */
	public static void ladeWaffe(Node n, TSpieler spieler) throws Exception {
		// in die Liste eintauchen
		NodeList nds = n.getChildNodes(); 
		Node a; 

		String id = null;
		int abgefeuert = 0;
		int treffer = 0;
		
		for (int i = 0; i < nds.getLength(); i++) {
			a = nds.item(i);

			if (a.getNodeName().equals("ID")) {
				id = a.getTextContent();
			} else if (a.getNodeName().equals("Abeschossen")) {
				abgefeuert = Integer.parseInt(a.getTextContent());
			} else if (a.getNodeName().equals("Treffer")) {
				treffer = Integer.parseInt(a.getTextContent());
			}
		}
		
		if (id == null) {
			throw new Exception("Fehler beim Levelladen: Waffen-ID nicht gesetzt");
		} else if (TWaffe.getWaffe(id) == null) {
			throw new Exception("Fehler beim Levelladen: Waffen " + id +
				" existiert nicht");
		}
		spieler.addWaffe(id);
		TWaffe.getWaffe(id).setStatistik(abgefeuert, treffer);
	}

	/**
	 * Laedt das Savegame mit der Nummer
	 * @param nummer Die Nummer des Savegames, das geladen werden soll
	 * @param spieler Das Spielerobjekt, dessen Werte aus dem Savegame geladen
	 * werden sollen
	 * @return Das Levelobjekt, an dessen Stelle das neue Level geladen
	 * werden soll
	 */
	public static TLevel lade(int nummer, TSpieler spieler) throws Exception {
		String dateiname = home + "/save" + nummer + ".xml";
		System.out.println("Lade " + dateiname + "...");

		Map<String, String> werte = new HashMap<String, String>();

		Document datei = DocumentBuilderFactory.newInstance()
			.newDocumentBuilder().parse(new File(dateiname));
		NodeList nds = datei.getChildNodes();
		
		if ((nds.getLength() != 1) || (nds.item(0).getNodeName() !=
			"savegame")) {
			throw new Exception("Fehlerhaftes Savegame: " + datei);
		} else {
			nds = nds.item(0).getChildNodes();
			for (int i = 0; i < nds.getLength(); i++) {
				if (nds.item(i).getNodeName().equals("waffe")) {
					//waffen.add(nds.item(i).getTextContent());
					ladeWaffe(nds.item(i), spieler);
				} else {
					werte.put(nds.item(i).getNodeName(),
						nds.item(i).getTextContent());
				}
			}
		}

		if ((werte.get("spielername") == null) ||
			(werte.get("spielerx") == null) ||
			(werte.get("spielery") == null) ||
			(werte.get("levelnummer") == null) ||
			(werte.get("levelfortschritt") == null) ||
			(werte.get("punkte") == null) ||
			(werte.get("ausgewähltewaffe") == null) ||
			(werte.get("leben") == null) ||
			(werte.get("lebenmax") == null) ||
			(werte.get("schwierigkeit") == null)) {
	
			throw new Exception("Fehlerhaftes Savegame: " + datei);
		}

		spieler.setKoord(new TVektor(Double.valueOf(werte.get("spielerx")),
			Double.valueOf(werte.get("spielery"))));
		spieler.setName(werte.get("spielername"));
		spieler.setAktiveWaffe(Integer.valueOf(werte.get("ausgewähltewaffe")));
		spieler.setScore(Integer.valueOf(werte.get("punkte")));
		spieler.setLeben(Double.valueOf(werte.get("leben")));
		spieler.setLebenMax(Double.valueOf(werte.get("lebenmax")));
		spieler.setSchwierigkeit(
			TSpieler.Schwierigkeitsgrade.valueOf(werte.get("schwierigkeit")));

		return new TLevel(Integer.valueOf(werte.get("levelnummer")),
			Integer.valueOf(werte.get("levelfortschritt")), true);
	}

	/**
	 * Speichert das Spiel. Es wird hier SAX zum Schreiben verwendet,
	 * weil das hiermit mehr "straightforward" geht.
	 * @param spieler Das Spielerobjekt, dessen Score usw.
	 * gespeichert werden soll
	 * @param level Das Levelobjekt, dessen Fortschritt gespeichert
	 * werden soll
	 */
	public static void speichere(TSpieler spieler,
		TLevel level) throws Exception {

		// Savegame-Verzeichnis anlegen, falls nicht vorhanden
		File f = new File(home);
		if (!f.exists()) {
			f.mkdirs();
		}

		// naechste freie Savegame-Nummer herausfinden
		int i = 1; 
		while (new File(home + "/save" + i + ".xml").exists()) {
			i++;
		}

		String datei = home + "/save" + i + ".xml";
		
		// was soll in das Savegame geschrieben werden?
		java.util.List<TPaar<String, String>> werte =
			new ArrayList<TPaar<String, String>>();
		werte.add(new TPaar<String, String>("spielername",
			spieler.getName()));
		werte.add(new TPaar<String, String>("spielerx",
			Double.toString(spieler.getKoord().x)));
		werte.add(new TPaar<String, String>("spielery",
			Double.toString(spieler.getKoord().y)));
		werte.add(new TPaar<String, String>("levelnummer",
			Integer.toString(level.getLevelNummer())));
		werte.add(new TPaar<String, String>("levelfortschritt",
			Integer.toString(level.getGeloeschteSegmente())));
		werte.add(new TPaar<String, String>("punkte",
			Integer.toString(spieler.getScore())));
		werte.add(new TPaar<String, String>("ausgewähltewaffe",
			Integer.toString(spieler.getAktiveWaffe())));
		werte.add(new TPaar<String, String>("leben",
			Double.toString(spieler.getLeben())));
		werte.add(new TPaar<String, String>("lebenmax",
			Double.toString(spieler.getLebenMax())));
		werte.add(new TPaar<String, String>("schwierigkeit",
			spieler.getSchwierigkeit().toString()));

		PrintWriter out = new PrintWriter(new FileOutputStream(datei));
		StreamResult streamResult = new StreamResult(out);
		SAXTransformerFactory tf = (SAXTransformerFactory)SAXTransformerFactory
			.newInstance();
		TransformerHandler hd = tf.newTransformerHandler();
		Transformer serializer = hd.getTransformer();
		// Uebler Hack fuer seltsames Verhalten unter Windows mit unbekanntem
		// Ursprung. Siehe Technische Dokumentation fuer weiter Erlaeuterung
		if (System.getProperty("os.name").equals("Windows XP")) {
			serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-15");
		} else {
			serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		}
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		hd.setResult(streamResult);
		hd.startDocument();
		hd.startElement("", "", "savegame", null);

		for(TPaar<String, String> wert: werte) {
			hd.startElement("", "", wert.eins, null);
			hd.characters(wert.zwei.toCharArray(), 0, wert.zwei.length());
			hd.endElement("", "", wert.eins);
		}
		// Die Waffen können wir nicht so elegant speichern
		for (TWaffe w: spieler.getWaffenListe()) {
			String id = w.getID();
			String abgeschossen = "" + w.getStatAbgefeuert();
			String treffer = "" + w.getStatTreffer();
			hd.startElement("", "", "waffe", null);
			hd.startElement("", "", "ID", null);
			hd.characters(id.toCharArray(), 0, id.length());
			hd.endElement("", "", "ID");
			hd.startElement("", "", "abgeschossen", null);
			hd.characters(abgeschossen.toCharArray(), 0, abgeschossen.length());
			hd.endElement("", "", "abgeschossen");
			hd.startElement("", "", "treffer", null);
			hd.characters(treffer.toCharArray(), 0, treffer.length());
			hd.endElement("", "", "treffer");
			hd.endElement("", "", "waffe");
		}

		hd.endElement("", "", "savegame");
		hd.endDocument();
	}
}

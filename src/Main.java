package de.rccc.java.witchcraft;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import name.panitz.java.ws0607.Game;

/**
 * Diese Klasse sorgt dafuer, dass ein neuer JFrame gezeichnet wird,
 * die Menueleiste zugefuegt und die TAnzeige (dh das eigentliche Spiel)
 * erzeugt, dargestellt und gestartet wird
 */
public class Main implements Game {
	/**
	 * Die TAnzeige, die das eigentliche Spiel enthaelt
	 */
	final protected TAnzeige anzeige;

	/**
	 * Das Fenster, in dem wir spielen wollen
	 */
	final protected JFrame fhauptFenster = new JFrame("Witchcraft");

	/**
	 * HashMap der Menueeintraege
	 */
	final protected Map<String, JMenuItem> fmenuItems =
		new HashMap<String, JMenuItem>();

	/**
	 * Ausgrauen oder enablen eines bestimmen Menueintrages
	 * @param item Beschreibung des menuItems, z.B. "spiel_neu"
	 * @param enabled true fuer enabled, false fuer disablen
	 */
	public void enableMenuItem(String item, boolean enabled) {
		fmenuItems.get(item).setEnabled(enabled);
	}

	/**
	 * Aendert den Text eines MenuItems
	 * @param item Beschreibung des menuItems, z.B. "optionen_musik"
	 * @param neuerText Der neue zu setzende Text
	 */
	public void aendereMenuItem(String item, String neuerText) {
		fmenuItems.get(item).setText(neuerText);
	}

	/**
	 * Liefert das Fenster zurueck - damit man es schliessen
	 * kann, wenn das Spiel als Applet laeuft (dort funktioniert
	 * System.exit() nicht)
	 */
	public JFrame getHauptFenster() {
		return fhauptFenster;
	}

	/**
	 * Defaultkonstruktor - um die Aufgabenstellung zu befriedigen
	 */
	public Main() {
		this(false);
	}

	/**
	 * Konstruktor.
	 * Hier wird das Fenster erzeugt, gefuellt und gestartet
	 * @param applet Laeuft das Spiel in einem Applet? Das
	 * beeinflusst z.B. ob es einen Menuepunkt "Datei - Beenden"
	 * gibt oder nicht (System.exit() nicht moeglich in Applets)
	 */
	public Main(final boolean applet) {
		// als Haupt-Spielklasse registrieren
		TSharedObjects.setMain(this);

		anzeige = new TAnzeige(applet);
		JMenuItem tmp;

		// Tasten-Releaseevents werden nicht erfasst, wenn wir
		// auf dem Menu rumklicken, daher mal praeventiv alle
		// Tasten loslassen - wir wollen nicht dass der Spieler
		// "weiterschlittert"
		final MouseMotionAdapter mma = new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent _) {
				anzeige.tastenLoslassen();
				anzeige.pause();
			}

			public void mouseMoved(MouseEvent _) {
				anzeige.tastenLoslassen();
				anzeige.pause();
			}
		};

		tmp = new JMenuItem("Neu");
		tmp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _) {
				new TOptionenFenster(applet);
		}});
		tmp.addMouseMotionListener(mma);
		fmenuItems.put("spiel_neu", tmp);
		
		tmp = new JMenuItem("Laden");
		tmp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _) {
				TSavegame.savegameWaehlen();
		}});
		tmp.addMouseMotionListener(mma);
		fmenuItems.put("spiel_laden", tmp);

		tmp = new JMenuItem("Speichern");
		tmp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _) {
				anzeige.speichere();
		}});
		tmp.addMouseMotionListener(mma);
		fmenuItems.put("spiel_speichern", tmp);

		tmp = new JMenuItem(applet ? "Fenster schliessen" : "Beenden");
		tmp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _) {
				TSharedObjects.endGame();
		}});
		tmp.addMouseMotionListener(mma);
		fmenuItems.put("spiel_beenden", tmp);

		tmp = new JMenuItem("Hohe Details (schneller PC)");
		tmp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _) {
				anzeige.setHohesDetail(true);
				TSharedObjects.getPartikelVerwaltung().setHohesDetail(true);
		}});
		tmp.addMouseMotionListener(mma);
		fmenuItems.put("optionen_hidetail", tmp);

		tmp = new JMenuItem("Niedrige Details (lahme Krücke)");
		tmp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _) {
				anzeige.setHohesDetail(false);
				TSharedObjects.getPartikelVerwaltung().setHohesDetail(false);
		}});
		tmp.addMouseMotionListener(mma);
		fmenuItems.put("optionen_lodetail", tmp);

		tmp = new JMenuItem(TSharedObjects.getMusik() ? "Musik abschalten" :
			"Musik anschalten");
		tmp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _) {
				TSharedObjects.setMusik(!TSharedObjects.getMusik());
		}});
		tmp.addMouseMotionListener(mma);
		fmenuItems.put("optionen_musik", tmp);

		tmp = new JMenuItem("Anleitung");
		tmp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _) {
				anzeige.pauseDurchMenu();
				TMenuHTMLzeig ueb = new TMenuHTMLzeig("anleitung.html");
		}});
		tmp.addMouseMotionListener(mma);
		fmenuItems.put("hilfe_anleitung", tmp);

		tmp = new JMenuItem("Über");
		tmp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _) {
				anzeige.pauseDurchMenu();
				TMenuHTMLzeig ueb = new TMenuHTMLzeig("ueber.html");
		}});
		tmp.addMouseMotionListener(mma);
		fmenuItems.put("hilfe_ueber", tmp);

		final JMenuBar hauptMenue = new JMenuBar();
		final JMenu menue_spiel = new JMenu("Spiel");
		final JMenu menue_optionen = new JMenu("Optionen");
		final JMenu menue_hilfe = new JMenu("Hilfe");

		enableMenuItem("spiel_speichern", false);
		enableMenuItem("spiel_laden", false);
		menue_spiel.add(fmenuItems.get("spiel_neu"));
		menue_spiel.addSeparator();
		menue_spiel.add(fmenuItems.get("spiel_laden"));
		menue_spiel.add(fmenuItems.get("spiel_speichern"));
		menue_spiel.addSeparator();
		menue_spiel.add(fmenuItems.get("spiel_beenden"));
		menue_spiel.addMouseMotionListener(mma);
		menue_optionen.add(fmenuItems.get("optionen_hidetail"));
		menue_optionen.add(fmenuItems.get("optionen_lodetail"));
		menue_optionen.addSeparator();
		menue_optionen.add(fmenuItems.get("optionen_musik"));
		menue_optionen.addMouseMotionListener(mma);
		menue_hilfe.add(fmenuItems.get("hilfe_anleitung"));
		menue_hilfe.add(fmenuItems.get("hilfe_ueber"));
		menue_hilfe.addMouseMotionListener(mma);
		hauptMenue.add(menue_spiel);
		hauptMenue.add(menue_optionen);
		hauptMenue.add(menue_hilfe);
		hauptMenue.addMouseMotionListener(mma);

		fhauptFenster.setJMenuBar(hauptMenue);
		fhauptFenster.setResizable(false);

		if (applet) {
			enableMenuItem("spiel_laden", false);
		} else {
			fhauptFenster.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			if (TSavegame.mindEinSavegameVorhanden()) {
				enableMenuItem("spiel_laden", true);
			}
		}

		fhauptFenster.add(anzeige);
		fhauptFenster.pack();
	}

	/**
	 * Um das geforderte Interface implementieren zu koennen:
	 * Macht das Spielfenster sichtbar und startet das Spiel im Panel
	 */
	public void start() {
		fhauptFenster.setVisible(true);
		anzeige.start();
	}

	/**
	 * Main-Funktion zum normalen Starten des Spiels
	 */
	public static void main(String [] _) {
		Game g = new Main();
		g.start();
	}
}

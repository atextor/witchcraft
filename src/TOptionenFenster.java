package de.rccc.java.witchcraft;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.event.*;

/**
 * Klasse, die ein Fenster darstellt, in dem der Spieler seinen Namen
 * eingeben und die Schwierigkeitsstufe auswahlen kann. Der Startknopf
 * ruft dann neuesSpiel() in TAnzeige auf.
 */
public class TOptionenFenster {
	/**
	 * Der Schwierigkeitsgrad, der ausgewaehlt werden soll,
	 * wird hier gespeichert
	 */
	protected TSpieler.Schwierigkeitsgrade fschwierig =
		TSpieler.Schwierigkeitsgrade.normal;

	/**
	 * Der Name des Spielers soll hier gespeichert werden
	 */
	protected String fname = null;

	/**
	 * Das Textfeld, in dem der Spieler seinen Namen eingeben kann.
	 * Kann nicht lokal im Konstruktor liegen, da unsere feine Klasse
	 * DL (d.h. der DocumentListener fuer das Feld) Zugriff braucht
	 */
	protected JTextField fnameFeld = null;

	/**
	 * DocumentListener ist dummerweise ein Interface,
	 * deswegen brauchen wir erst mal eine Klasse, die es
	 * implementiert. Der Listener wird aufgerufen, wenn sich
	 * das Name-Textfeld aendert. Das ist deswegen so umstaendlich
	 * (und nicht bspw. durch KeyListener), weil man eine
	 * Namensaenderung auch mitbekommen will, wenn man einen Text
	 * per Copy and Paste in das Textfeld fuegt...
	 */
	protected class DL implements DocumentListener {
		public void insertUpdate(DocumentEvent _) {
			fname = fnameFeld.getText();
		}

		public void removeUpdate(DocumentEvent _) {
			fname = fnameFeld.getText();
		}

		public void changedUpdate(DocumentEvent _) {
			fname = fnameFeld.getText();
		}
	}

	/**
	 * Das Fenster, das erscheint, wenn ein Spieler ein neues
	 * Spiel Starten will: Er wird aufgefordert seinen Namen einzugeben
	 * und die Schwierigkeitsstufe auszuwaehlen
	 *
	 * @param applet Laeuft das Spiel als Applet? Dann geht naemlich
	 * die Username-Ermittlung wieder nicht...
	 */
	public TOptionenFenster(boolean applet) {
		if (applet) {
			fname = "UnnamedPlayer";
		} else {
			// sinnvoller Default-wert
			fname = System.getProperty("user.name");
		}

		final JDialog p = new JDialog(TSharedObjects.getMain().getHauptFenster(),
			"Neues Spiel...", true);
		final Border margin = new EmptyBorder(8, 8, 8, 8);
		final JPanel mainPanel = new JPanel();

		final ActionListener startButtonAL = new ActionListener() {
			public void actionPerformed(ActionEvent _) {
				p.dispose();
				TSharedObjects.getAnzeige().neuesSpiel(fname, fschwierig);
			}
		};

		final JPanel panel1 = new JPanel();
		panel1.setBorder(new CompoundBorder(margin, new TitledBorder(null,
			"Spielername", TitledBorder.LEFT, TitledBorder.TOP)));
		fnameFeld = new JTextField(fname, 20);
		fnameFeld.getDocument().addDocumentListener(new DL());
		panel1.add(fnameFeld);

		final JPanel panel2 = new JPanel();
		panel2.setBorder(new CompoundBorder(margin, new TitledBorder(null,
			"Schwierigkeitsgrad", TitledBorder.LEFT, TitledBorder.TOP)));
		final ButtonGroup bg = new ButtonGroup();
		final JRadioButton rleicht = new JRadioButton("Leicht", false);
		final JRadioButton rmittel = new JRadioButton("Mittel", true);
		final JRadioButton rschwer = new JRadioButton("Schwer", false);

		final ActionListener radioButtonAL = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().toString().equals("0")) {
					fschwierig = 
						TSpieler.Schwierigkeitsgrade.leicht;
				} else if (e.getActionCommand().toString().equals("1")) {
					fschwierig = 
						TSpieler.Schwierigkeitsgrade.normal;
				} else if (e.getActionCommand().toString().equals("2")) {
					fschwierig = 
						TSpieler.Schwierigkeitsgrade.schwer;
				}
			}
		};

		rleicht.addActionListener(radioButtonAL);
		rleicht.setActionCommand("0");
		rmittel.addActionListener(radioButtonAL);
		rmittel.setActionCommand("1");
		rschwer.addActionListener(radioButtonAL);
		rschwer.setActionCommand("2");
		bg.add(rleicht);
		bg.add(rmittel);
		bg.add(rschwer);
		panel2.add(rleicht);
		panel2.add(rmittel);
		panel2.add(rschwer);

		final JPanel panel3 = new JPanel();
		panel3.setBorder(margin);
		final JButton startButton = new JButton("...und los!");
		startButton.addActionListener(startButtonAL);
		
		panel3.add(startButton);

		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(panel1);
		mainPanel.add(panel2);
		mainPanel.add(panel3);
		p.add(mainPanel);

		p.pack();
		p.setResizable(false);
		p.setVisible(true);
		p.repaint();
	}
}

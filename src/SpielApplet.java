package de.rccc.java.witchcraft;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Klasse, die das Spiel in ein JApplet wrappt
 */
public class SpielApplet extends JApplet {
	/**
	 * Konstruktor, der auch die gesamte funktionalitaet enthaelt
	 * Dargestellt wird ein Hinweislabel und ein Knopf, der dann
	 * das eigentliche Spiel in einem Neuen Fenster startet
	 */
	public SpielApplet() {
		final name.panitz.java.ws0607.Game g = new Main(true);

		final JPanel panel = new JPanel();
		final JButton startButton = new JButton("Spiel Starten");
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _) {
				g.start();
			}
		});
		final JLabel label1 = new JLabel(
			"Damit das Spiel funktioniert, wird ein Extra Fenster benötigt.");
		final JLabel label2 = new JLabel(
			"Drücken Sie den Knopf, um das Spiel in einem Fenster zu öffnen");

		panel.add(label1);
		panel.add(label2);
		panel.add(startButton);
		add(panel);
	}
}

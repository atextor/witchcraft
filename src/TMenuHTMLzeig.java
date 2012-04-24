package de.rccc.java.witchcraft;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * Klasse, um einen neuen Frame mit einer HTML-Datei als Inhalt
 * anzuzeigen
 */
public class TMenuHTMLzeig {
	/**
	 * Konstruktor, der die gesamte Funktionalitaet enthaelt
	 */
	public TMenuHTMLzeig(String htmldatei) {
		final JFrame p = new JFrame("Witchcraft - Info");

		try {
			java.net.URL url = TSound.class.getResource("/html/" + htmldatei);
			JEditorPane editorPane = new JEditorPane(url);
			editorPane.setEditable(false);

			JScrollPane hauptscroller = new JScrollPane(editorPane);
			hauptscroller.setMinimumSize(new Dimension(66, 400));
			hauptscroller.setSize(new Dimension(665, 400));
			hauptscroller.setPreferredSize(new Dimension(665, 400));

			p.add(hauptscroller, BorderLayout.CENTER);
			JButton weg = new JButton("Schließen");
			weg.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					p.dispose();
					TSharedObjects.getAnzeige().start();
				}
			});
			p.add(weg, BorderLayout.SOUTH);
			//p.setSize(650,400);
		} catch (Exception e) {
			p.add(new JLabel("Fehler: Konnte html nicht laden: " + e) );
		}

		p.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				TSharedObjects.getAnzeige().start();
			}
		});

		p.pack();
		p.setVisible(true);
	}
}

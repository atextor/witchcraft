package de.rccc.java.witchcraft;

import java.util.*;
import java.awt.Font;

/**
 * Partikel sind Objekte, die keine Kollision haben,
 * aber eine TTL und auf denen die Schwerkraft einwirkt
 */
public class TPartikelVerwaltung {
	/**
	 * Enum aller möglichen Partikelarten.
	 * Diese enthalten auch die jeweiligen Gravitation-
	 * und Reibungseinfluesse, die die Partikel erfahren sollen.
	 * Diese Werte gehoeren nicht in die Partikel, weil ein Partikel
	 * nicht ueber sich selbst entscheiden koennen soll, wie er sich 
	 * bewegt.
	 */
	static public enum Partikel {
		/**
		 * NIL - Not in List. Das ist ein Fehlwert
		 */
		NIL (0, 0),
		/**
		 * Funken Art 1. Kleine gelbe Punkte
		 */
		Funken1 (0.13, 0.990),
		/**
		 * Funken Art 2. Kreuze. Der "Beseneffekt"
		 */
		Funken2 (0.13, 0.990),
		/**
		 * Explosionspartikel
		 */
		Explosion (0.13, 0.990),
		/**
		 * Frosch. Ein Feind wurde zum Frosch verzaubert
		 */
		Frosch (0.13, 0.990),
		/**
		 * Rauch, von Raketen verursacht - dieser steigt nach oben
		 * mit linearer Geschwindigkeit
		 */
		Rauch (-0.15, 1),
		/**
		 * Score (die Punkte, die hochsteigen, wenn man einen
		 * Gegner abgeschossen hat)
		 */
		Score (-0.05, 1);

		/**
		 * Die Reibung die der Partikel erfaehrt, bremst ihn ab
		 */
		private final double freibung;

		/**
		 * Die Gravitation die der Partikel erfaehrt, laesst
		 * ihn nach unten fallen
		 */
		private final double fgravitation;

		/**
		 * Liest die Reibung fuer den Partikel 
		 */
		public double getReibung() {
			return freibung;
		}

		/**
		 * Liest die Gravition fuer den Partikel
		 */
		public double getGravitation() {
			return fgravitation;
		}

		/**
		 * Konstruktor
		 * @param reibung Welche Reibung soll der Partikel bei der
		 * Bewegung erfahren
		 * @param gravitation Welche Graviation soll der Partikel
		 * bei der Bewegung erfahren
		 */
		Partikel(double reibung, double gravitation) {
			freibung = reibung;
			fgravitation = gravitation;
		}
	};

	/**
	 * Liste der Partikel. Jeder besteht aus einem Tripel: IPartikel o
	 * und int i wobei o das entsprechende Objekt ist, und i die
	 * Anzahl der Ticks, die das Objekt noch zu leben hat, sowie Partikel
	 * der Enum-Wert, der fuer das Verhalten (Flugbahn) zustaendig ist
	 */
	List<TTripel<IPartikel, Integer, Partikel>> fpartikel =
		new LinkedList<TTripel<IPartikel, Integer, Partikel>>();

	/**
	 * Sollen BildeObjekte oder programmierte Partikel verwendet werden?
	 */
	private boolean fdetail = false;

	/**
	 * Konstruktor der PartikelVerwaltung 
	 */
	TPartikelVerwaltung() {
	}

	/**
	 * Methode zum Starten eines neuen Effektes, der keine initiale
	 * Richtung mitbekommen soll
	 *
	 * @param p Welcher Effekt soll gestartet werden
	 * @param pos An welcher Stelle soll der Effekt gestartet werden
	 */
	public void startEffekt(Partikel p, TVektor pos) {
		startEffekt(p, pos, new TVektor(0, 0));
	}

	/**
	 * Methode zum Starten eines neuen Effektes, der einen String-
	 * Parameter braucht - zB die Punkte, die erscheinen, wenn man
	 * einen Gegner abgeschossen hat
	 *
	 * @param p Welcher Effekt soll gestartet werden - momentan
	 * redunant, aber koennte auch noch mehr Effekte geben, die
	 * einen String-Parameter brauchen, als nur 'Score'
	 * @param pos An welcher Stelle soll der Effekt gestartet werden
	 * @param text Der Text-Parameter
	 */
	public void startEffekt(Partikel p, TVektor pos, String text) {
		// Score
		if (p == Partikel.Score) {
			TVektor richtung = new TVektor(0, -2);
			Font font = new Font("Arial", Font.BOLD, (Integer.valueOf(text)/10)+10);
			fpartikel.add(new TTripel<IPartikel, Integer, Partikel>(
				new TText(new TVektor(pos), richtung, text, font),
				40, Partikel.Score));
		}
	}

	/**
	 * Methode zum Starten eines neuen Effektes. Effektiv werden
	 * entsprechende Partikel mit Zufallsrichtungen (in entsprechendem
	 * Rahmen) an der gewuenschten Stelle erzeugt
	 *
	 * @param p Welcher Effekt soll gestartet werden
	 * @param pos An welcher Stelle soll der Effekt gestartet werden
	 * @param inigeschw Startgeschwindigkeit und -richtung, die die Partikel
	 * bekommen sollen
	 */
	public void startEffekt(Partikel p, TVektor pos, TVektor inigeschw) {
		// gelbe Treffer-Funken
		if (p == Partikel.Funken1) {
			for (int i = 1; i <= 5; i++) {
				TVektor richtung = new TVektor((TSharedObjects.rndDouble() * 6.0) -
					3.0 + inigeschw.x, (TSharedObjects.rndDouble()*2 - 2.5) +
					inigeschw.y);
				if (!fdetail) {
					fpartikel.add(new TTripel<IPartikel, Integer, Partikel>(
						new TPartikel1(new TVektor(pos), new TVektor(3, 3), richtung), 
						30 + TSharedObjects.rndInt(40), Partikel.Funken1));
				} else {
					fpartikel.add(new TTripel<IPartikel, Integer, Partikel>(
						new TBildObjekt(new TVektor(pos), new TVektor(7, 7), richtung,
						"partikel1"), 30 + TSharedObjects.rndInt(40), Partikel.Funken1));
				}
			}
		}

		// grosse gelbe Explosions-Leuchtdinger
		if (p == Partikel.Explosion) {
			for (int i = 1; i <= 15; i++) {
				TVektor richtung = new TVektor((TSharedObjects.rndDouble() * 6.0) -
					3.0 + inigeschw.x, (TSharedObjects.rndDouble()*2 - 5.0) +
					inigeschw.y);
				if (!fdetail) {
					fpartikel.add(new TTripel<IPartikel, Integer, Partikel>(
						new TPartikel2(new TVektor(pos), new TVektor(25, 25), richtung, 2),
						30 + TSharedObjects.rndInt(40), Partikel.Explosion));
				} else {
					fpartikel.add(new TTripel<IPartikel, Integer, Partikel>(
						new TBildObjekt(new TVektor(pos), new TVektor(25, 25), richtung,
						"partikel2"), 30 + TSharedObjects.rndInt(40), Partikel.Explosion));
				}
			}
		}

		// blaue Besen-Funken
		if (p == Partikel.Funken2) {
			TVektor richtung = new TVektor((TSharedObjects.rndDouble() * 3.0) - 1.5,
				(TSharedObjects.rndDouble()*2 - 1.5));
			richtung.add(inigeschw);
			if (!fdetail) {
				fpartikel.add(new TTripel<IPartikel, Integer, Partikel>(
					new TPartikel3(new TVektor(pos), richtung),
					20 + TSharedObjects.rndInt(40), Partikel.Funken2));
			} else {
				fpartikel.add(new TTripel<IPartikel, Integer, Partikel>(
					new TBildObjekt(new TVektor(pos), null, richtung,
					"partikel3"), 20 + TSharedObjects.rndInt(40), Partikel.Funken2));
			}
		}
		
		// Frosch. Ein Feind wurde zum Frosch verzaubert
		if (p == Partikel.Frosch) {
			TVektor richtung = new TVektor((TSharedObjects.rndDouble() * 3.0) - 1.5,
					(TSharedObjects.rndDouble()*2 - 1.5));
			richtung.add(inigeschw);
			fpartikel.add(new TTripel<IPartikel, Integer, Partikel>(
				new TBildObjekt(new TVektor(pos), null, richtung, "frosch"),
				999, Partikel.Frosch));
		}

		// Rauch (steigt von Raketen auf)
		if (p == Partikel.Rauch) {
			if (!fdetail) {
				fpartikel.add(new TTripel<IPartikel, Integer, Partikel>(
					new TPartikelRauch(new TVektor(pos), inigeschw),
					20 + TSharedObjects.rndInt(40), Partikel.Rauch));
			} else {
				fpartikel.add(new TTripel<IPartikel, Integer, Partikel>(
					new TBildObjekt(new TVektor(pos), null, inigeschw,
					"rauch"), 20 + TSharedObjects.rndInt(40), Partikel.Rauch));
			}
		}
	}

	/**
	 * Alle Effekte (Partikel) einen Tick weiterbewegen. Die TTL (Time To
	 * Live) der einzelnen Partikel wird heruntergesetzt, wenn dadurch
	 * ein Partikel stirbt, wird er aus der Partikelliste entfernt
	 */
	public void update() {
		for (Iterator<TTripel<IPartikel, Integer, Partikel>> i =
			fpartikel.iterator(); i.hasNext();) {

			TTripel<IPartikel, Integer, Partikel> p = i.next();
			p.eins.gravitationAdd(p.drei.getReibung());
			p.eins.gravitationMult(p.drei.getGravitation());
			p.eins.bewege();

			p.zwei = p.zwei - 1;
			if (p.zwei <= 0) {
				i.remove();
			} else if (p.eins.ausserhalbBildschirm()) {
				i.remove();
			}
		}
	}

	/**
	 * Loescht alle Partikel
	 */
	public void reset() {
		fpartikel.clear();
	}

	/**
	 * Aktuellen Stand des Partikelsystems zeichnen (d.h. alle Einzelpartikel
	 * an ihren aktuellen Positionen)
	 * @param g Die Zeichenflaeche, auf der gezeichnet werden soll
	 */
	public void zeichne(java.awt.Graphics g) { 
		for (TPaar<IPartikel, Integer> p: fpartikel) {
			p.eins.zeichne(g);
		}
	}

	/**
	 * Setzt, ob neue Partikel BildObjekte oder programmierte Partikel
	 * sein sollen
	 * @param d true fuer hohes, false fuer niedriges Detail
	 */
	public void setHohesDetail(boolean d) {
		fdetail = d;
	}
}

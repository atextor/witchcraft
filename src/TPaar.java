package de.rccc.java.witchcraft;

/**
 * Generisches Paar fuer zwei Werte
 */
public class TPaar<A, B> {
	// Getter und Setter koennen wir uns hier sparen
	A eins;
	B zwei;

	/** 
	 * Konstruktor
	 * @param a Wird in Feld 'eins' gefuellt
	 * @param b Wird in Feld 'zwei' gefuellt
	 */
	TPaar(A a, B b) {
		this.eins = a;
		this.zwei = b;
	}
}


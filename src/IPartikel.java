package de.rccc.java.witchcraft;

/**
 * Dieses Interface muss jede Klasse implementieren, die 
 * von der PartikelVerwaltung als Partikel angesehen werden will
 */
public interface IPartikel {
	/**
	 * Methode zur Darstellung
	 */
	public void zeichne(java.awt.Graphics g);
	
	/**
	 * Methode zum Aufaddieren eines Graviationsfaktors
	 * (alle Partikel sinken nach unten)
	 */
	public void gravitationAdd(double g);

	/**
	 * Methode zum Multiplizieren mit einem Gravitationsfaktor
	 * (alle Partikel beschleunigen nach unten)
	 */
	public void gravitationMult(double g);

	/**
	 * Methode um den Partikel einen Schritt weiterzubewegen
	 */
	public void bewege();

	/**
	 * Überprüft, ob der Partikel ausserhalb des Bildschirms ist
	 *
	 * @return true, wenn das Ojekt nicht mehr gebraucht wird
	 */
	public boolean ausserhalbBildschirm();
}

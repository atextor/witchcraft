package de.rccc.java.witchcraft;

import java.util.*;
import java.io.File;
import java.io.IOException;
import java.applet.*; 

/**
 * Soundklasse des Spiels.
 *
 * Idee:
 * Die Sound-daten sind statisch, die werden nur einmal gebraucht.
 * Wenn nun ein Sound abgespielt wird, wird eine neue Instanz dieses
 * Objektes erzeugt, und als Thread gestartet. Im Thread passiert
 * das Abspielen.
 *
 * Da das AudoClip aber nur eine Instanz ist, und wenn man den Sound
 * öfter Abspielt, das immer wieder abgebrochen und von vorne gespielt
 * wird, müssen wir tricksen:
 * - kopieren geht nicht :(  (Da Audioclip ein Interface ist)
 * - daher n-AudioClips mit dem gleichen Sound erzeugen, und dann beim
 * Abspielen dieses Round-Robin abspielen...
 */
public class TSound extends Applet implements Runnable{
	private static final int MEHRFACH = 4;

	/**
	 * Eine Liste der Sounds mit den IDs.
	 * Die Sounds aber als Array im "paar": Da wir sonst immer nur
	 * EINE instanz hätten, würde bei zweimalen abspielen der Sound
	 * abgebrochen, und neu gestartet. Daher einen "vorrat" an Sounds
	 * anlegen, die parralell spielen können. "eins" in "paar" zeigt
	 * auf das nächste zu nutzende Soundobjekt.
	 */
	private static Map<String, TPaar<Integer, AudioClip[]>> sounds =
		new HashMap<String, TPaar<Integer, AudioClip[]>>();

	/**
	 * Der Sound kann abgeschaltet werden.
	 * Wenn es z.B. Probleme (Weil geht nicht) gibt
	 */
	private static boolean faktiv = true;

	/**
	 * Ist das Musik. (soll geloopt werdern)
	 */
	private boolean fmusik = false;

	/**
	 * Die Auswahl des abzuspielenden Audioclip passiert im Create,
	 * NICHT im Thread!  Das so gemacht, weil ich nicht weiss, was
	 * passiert, wenn mehrere Threads gleichzeitig im TPaar.eins
	 * rumfummeln
	 */
	private AudioClip fsound;

	/**
	 * Konstruktor
	 *
	 * @param sound Der Sound, der abgespielt werden soll
	 */
	TSound(String sound, boolean musik) {
		// Das NICHT in den Thread! Keine Ahnung was passiert, wenn
		// zwei Threads gleichzeitig die Zahl vom Paar ändern wollen!
		TPaar<Integer, AudioClip[]> paar = sounds.get(sound);
		Integer x = paar.eins;
		fsound = paar.zwei[x];
		if (fsound == null) {
			// BUG! BUG! BUG!
			// Das war wohl eine Musikstück, das zweimal abgespielt wurde.
			fsound = paar.zwei[0];
		}
		x++;
		if (x >= MEHRFACH) {
			x = 0;
		}
		paar.eins = x;
		fmusik = musik;
	}

	/**
	 * Fügt einen Sound in die Liste hinzu
	 *
	 * @param id Die ID des Sounds
	 * @param datei die Datei
	 * @param musik ist das eine Musik-Date?
	 */
	public static void addSound(String id, String datei,
		boolean musik) throws Exception {

		java.net.URL url = TSound.class.getResource("/sounds/" + datei);

		if (url == null) {
			throw new Exception("Sounddatei \"" + datei + "\" nicht vorhanden.");
		}

		AudioClip[] sndarr = new AudioClip[MEHRFACH];

		if (musik) {
			// Musik wird nur einmal abgespielt, außerdem frisst
			// die Musik speicher
			sndarr[0] = Applet.newAudioClip(url);
			for (int x = 1; x < MEHRFACH; x++) {
				sndarr[x] = null;
			}
			
		} else {
			for (int x = 0; x < MEHRFACH; x++) {
				sndarr[x] = Applet.newAudioClip(url);
			}
		}
		TPaar<Integer, AudioClip[]> paar =
			new TPaar<Integer, AudioClip[]>(0, sndarr);
		sounds.put(id, paar);
	}

	/**
	 * Hier wird der Sound abgespielt.
	 *
	 * Das ist als Thread-Methode gemacht
	 */
	public void run() {
		if (faktiv) {
			try {
				if (fmusik) {
					fsound.loop();
					while (true) {
						Thread.sleep(100000);
					}
				} else {
					fsound.play();
					// Leider können wir nicht abfragen, ob der Sound
					// schon zuende ist, daher raten
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				System.out.println("Soundproblem: " + e + " bei " + fsound);
				e.printStackTrace();
				faktiv = false;
				System.out.println("Sound wird deaktiviert.");
			}
		}
	}
	
	/**
	 * Einen Sound abspielen
	 *
	 * @param sound Welcher Sound abgespielt werden soll
	 */
	static public void play(String sound) {
		// Null kann vorkommen und ist auch legitim (z.B. haben nicht
		// alle Waffen alle Sounds)
		if (sound != null) {
			TSound snd = new TSound(sound, false);
			Thread soundthread = new Thread(snd);
			soundthread.start();
		}
	}

	/**
	 * Musik abspielen
	 *
	 * @param sound Welche Musik abgespielt werden soll
	 */
	static public void playMusik(String sound) {
		TSound snd = new TSound(sound, true);
		Thread soundthread = new Thread(snd);
		soundthread.start();
	}

	/**
	 * Stoppt alle Sounds der ID
	 */
	static public void stoppe(String sound) {
		TPaar<Integer, AudioClip[]> paar = sounds.get(sound);
		AudioClip snd;
		for (int x = 0; x < MEHRFACH; x++) {
			snd = paar.zwei[x];
			if (snd != null) {
				// Die Musik hat nur einen Arrayeintrag!
				snd.stop();
			}
		}
	}

	/**
	 * Plausibilitaetspruefung:
	 * Prüft, ob ein Sound in der Liste vorhanden ist.
	 *
	 * @param sound der Sound, der geprüft werden soll
	 * @return true, wenn Sound in Liste
	 */
	static public boolean inListe(String sound) {
		return sounds.get(sound) != null;
	}
}


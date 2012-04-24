package de.rccc.java.witchcraft;

/**
 * Generisches Tripel fuer drei Werte
 */
public class TTripel<A, B, C> extends TPaar<A, B> {
	C drei;

	TTripel(A a, B b, C c) {
		super(a, b);
		this.drei = c;
	}
}


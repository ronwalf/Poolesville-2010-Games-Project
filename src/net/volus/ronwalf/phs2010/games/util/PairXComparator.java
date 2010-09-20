package net.volus.ronwalf.phs2010.games.util;

import java.util.Comparator;

public class PairXComparator<X extends Comparable<X>, Y> implements Comparator<Pair<X, Y>> {

	public int compare(Pair<X,Y> a1, Pair<X,Y> a2) {
		return a1.x.compareTo(a2.x);
	}
}

/*
Copyright (c) 2010 Ron Alford <ronwalf@volus.net>
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:
1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products
   derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package net.volus.ronwalf.phs2010.games.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A generic 2-D board
 * @author ronwalf
 *
 */
public class Board<E> implements Iterable<Board.Element<E>>{
	
	/** 
	 * An element of the board.
	 * @author ronwalf
	 *
	 * @param <E>
	 */
	public static class Element<E> {
		public final int x;
		public final int y;
		public final E elem;
		
		private Element(int x, int y, E elem) {
			this.x = x;
			this.y = y;
			this.elem = elem;
		}
		
		public boolean isSet() { return elem != null; } 
		
		@SuppressWarnings("unchecked")
		@Override
		public boolean equals( Object other ) {
			if ( !getClass().equals(other.getClass()) )
				return false;
			Element otherElem = (Element) other;
			if ( x != otherElem.x
					|| y != otherElem.y
					|| !elem.equals(otherElem.elem) )
				return false;
			
			return true;
		}
		
		@Override
		public int hashCode() {
			return x + y + elem.hashCode();
		}
		
	}
	
	final private int xmax, ymax;
	final private List<E> elements;
	
	/**
	 * Creates an empty board
	 * @param xmax
	 * @param ymax
	 */
	@SuppressWarnings("unchecked")
	public Board(final int xmax, final int ymax) {
		this(xmax, ymax, (List<E>) Collections.emptyList());
	}
	
	/** 
	 * Creates a board out of a list of elements
	 * @return
	 */
	public Board(final int xmax, final int ymax, final List<E> given) {
		this.xmax = xmax;
		this.ymax = ymax;
		
		elements = new ArrayList<E>(xmax*ymax);
		Iterator<E> givenIter = given.iterator();
		for (int i = 0; i < xmax*ymax; i++) {
			E elem = null;
			if (givenIter.hasNext()) {
				elem = givenIter.next();
			}
			elements.add(elem);
		}
		
	}
	
	/**
	 * Creates a shallow copy of the board.
	 */
	private Board(final Board<E> board) {
		xmax = board.getXSize();
		ymax = board.getYSize();
		elements = new ArrayList<E>(board.elements);
	}
	
	
	
	public int getXSize() { return xmax; }
	
	public int getYSize() { return ymax; }
	
	/**
	 * Creates a new board from this one with the given element changed.
	 */
	public Board<E> change(final int x, final int y, final E element) {
		Board<E> board = new Board<E>(this);
		board.elements.set(xmax*y + x, element);
		return board;
	};
	
	
	public Iterator<Element<E>> iterator() {
		return new Iterator<Element<E>>(){
			int i = 0;
			
			public boolean hasNext() {
				return i < elements.size();
			}

			public Element<E> next() {
				
				if ( i >= elements.size() ) {
					throw new IllegalStateException("No more elements");
				}
				
				Element<E> elem = new Element<E>(i%xmax, i/xmax, elements.get(i));
				i++;
				return elem;
			}

			public void remove() {
				throw new UnsupportedOperationException("Boards are immutable");
			}
			
		};
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		if (!getClass().equals(o.getClass())) {
			return false;
		}
		
		Board oe = (Board) o;
		if ( oe.getXSize() != getXSize()
				|| oe.getYSize() != getYSize()
				|| !oe.elements.equals(elements) ) {
			return false;
		}
			
		
		return true;
	}

	@Override
	public int hashCode() {
		return (xmax * 31 + ymax)*31 + elements.hashCode();
	}
}

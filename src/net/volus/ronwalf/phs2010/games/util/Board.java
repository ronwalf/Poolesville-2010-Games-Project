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
import java.util.Arrays;
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
		
		public Element(int x, int y, E elem) {
			this.x = x;
			this.y = y;
			this.elem = elem;
		}
		
		public boolean isSet() { return elem != null; } 
		
		@Override
		public String toString() {
			return "Element [" + x + ", " + y + ", " + elem + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((elem == null) ? 0 : elem.hashCode());
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Element other = (Element) obj;
			if (elem == null) {
				if (other.elem != null)
					return false;
			} else if (!elem.equals(other.elem))
				return false;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
		
	}
	
	final private int size;
	final private List<E> elements;
	
	/**
	 * Creates an empty board
	 * @param xmax
	 * @param ymax
	 */
	@SuppressWarnings("unchecked")
	public Board(final int size) {
		this(size, (List<E>) Collections.emptyList());
	}
	
	/**
	 * Creates a board out of an array of elements
	 */
	public Board(int size, final E... elems) {
		this(size, Arrays.asList(elems));
	}
	
	/** 
	 * Creates a board out of a list of elements
	 * @return
	 */
	public Board(final int size, final List<E> given) {
		this.size = size;
		
		elements = new ArrayList<E>(size*size);
		Iterator<E> givenIter = given.iterator();
		for (int i = 0; i < size*size; i++) {
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
		size = board.getSize();
		elements = new ArrayList<E>(board.elements);
	}
	
	/**
	 * Return the element at (i,j)
	 */
	public E get(int i, int j) {
		return elements.get(i + j*size);
	}
	
	public int getSize() { return size; }
	
	/**
	 * Creates a new board from this one with the given element changed.
	 */
	public Board<E> change(final int x, final int y, final E element) {
		Board<E> board = new Board<E>(this);
		board.elements.set(size*y + x, element);
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
				
				Element<E> elem = new Element<E>(i%size, i/size, elements.get(i));
				i++;
				return elem;
			}

			public void remove() {
				throw new UnsupportedOperationException("Boards are immutable");
			}
			
		};
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((elements == null) ? 0 : elements.hashCode());
		result = prime * result + size;
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Board other = (Board) obj;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		if (size != other.size)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		int y = 0;
		for (Element<E> elem : this) {
			if (y != elem.y) {
				y = elem.y;
				buffer.append('\n');
			}
			if (elem.isSet()) {
				buffer.append( elem.elem );
			} else {
				buffer.append( '.' );
			}
		}
		
		return buffer.toString();
		
	}

}

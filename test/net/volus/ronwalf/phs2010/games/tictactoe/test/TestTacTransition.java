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
package net.volus.ronwalf.phs2010.games.tictactoe.test;

import static net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell.O;
import static net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell.X;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacMove;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacState;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacTransition;
import net.volus.ronwalf.phs2010.games.util.Board;

import org.junit.Test;


public class TestTacTransition {
	
	private static TicTacState state(int turn, TicTacCell...cells) {
		int size = new Double(Math.ceil( Math.sqrt(cells.length) )).intValue();
		Board<TicTacCell> board = new Board<TicTacCell>(size, Arrays.asList(cells));
		return new TicTacState(turn, board);
	}
	
	
	@Test
	public void fullBoardTransition() {
		TicTacState state = state(1,
				X, O, X, 
				X, O, O,
				O, X, X);
		
		List<TicTacMove> moves = TicTacTransition.instance.enumerate(state);
		assertEquals(0, moves.size());
		assertTrue(Arrays.equals(new double[]{0,0}, TicTacTransition.instance.score(state)));
	}
	
	
	@Test
	public void lastBoardTransition1() {
		TicTacState state = state(0,
				X, O, X, 
				X, O, O,
				O, X, null);
		
		List<TicTacMove> moves = TicTacTransition.instance.enumerate(state);
		assertEquals(1, moves.size());
		assertEquals(new TicTacMove(2,2), moves.get(0));
	}
	
	@Test
	public void lastBoardTransition2() {
		TicTacState state = state(0,
				null, O, X, 
				X, O, O,
				O, X, X);
		
		List<TicTacMove> moves = TicTacTransition.instance.enumerate(state);
		assertEquals(1, moves.size());
		assertEquals(new TicTacMove(0,0), moves.get(0));
	}
	
	@Test
	public void finishedGameTransition1() {
		TicTacState state = state(1,
				X, X, X,
				O, O, null,
				null, null, null
				);
		
		List<TicTacMove> moves = TicTacTransition.instance.enumerate(state);
		assertEquals(0, moves.size());
		assertTrue(Arrays.equals(new double[]{1.0, -1.0}, TicTacTransition.instance.score( state )));
	}
	
	@Test
	public void finishedGameTransition2() {
		TicTacState state = state(0,
				X, X, null,
				O, O, O,
				X, null, null
				);
		
		List<TicTacMove> moves = TicTacTransition.instance.enumerate(state);
		assertEquals(0, moves.size());
		assertTrue(Arrays.equals(new double[]{-1.0, 1.0}, TicTacTransition.instance.score( state )));
	}
	
	@Test
	public void diagnolX1() {
		TicTacState state = state(0,
				X,O,null,
				O,X,null,
				null,null,X
				);
		
		List<TicTacMove> moves = TicTacTransition.instance.enumerate(state);
		assertEquals(0, moves.size());
		assertTrue(Arrays.equals(new double[]{1,-1}, TicTacTransition.instance.score( state )));
	}
	
	@Test
	public void diagnolO1() {
		TicTacState state = state(0,
				O,X,null,
				X,O,X,
				null,null,O
				);
		
		List<TicTacMove> moves = TicTacTransition.instance.enumerate(state);
		assertEquals(0, moves.size());
		assertTrue(Arrays.equals(new double[]{-1.0, 1.0}, TicTacTransition.instance.score( state )));
	}
	
	@Test
	public void diagnolX2() {
		TicTacState state = state(0,
				X,O,X,
				O,X,O,
				X,null,null
				);
		
		List<TicTacMove> moves = TicTacTransition.instance.enumerate(state);
		assertEquals(0, moves.size());
		assertTrue(Arrays.equals(new double[]{1,-1}, TicTacTransition.instance.score( state )));
	}
	
	@Test
	public void diagnolO2() {
		TicTacState state = state(0,
				X,X,O,
				X,O,X,
				O,null,O
				);
		
		List<TicTacMove> moves = TicTacTransition.instance.enumerate(state);
		assertEquals(0, moves.size());
		assertTrue(Arrays.equals(new double[]{-1.0, 1.0}, TicTacTransition.instance.score( state )));
	}
	
}

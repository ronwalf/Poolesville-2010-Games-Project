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
package net.volus.ronwalf.phs2010.games.reversi.test;

import static net.volus.ronwalf.phs2010.games.reversi.ReversiTransition.instance;
import static net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell.O;
import static net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell.X;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import net.volus.ronwalf.phs2010.games.reversi.ReversiState;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacMove;
import net.volus.ronwalf.phs2010.games.util.Board;

import org.junit.Test;

public class TestReversiTransition {

	@Test
	public void testStartMoves() {
		ReversiState state = ReversiState.othello;
		assertNull( instance.score( state ) );
		List<TicTacMove> moves = instance.enumerate(state);
		
		assertEquals(new HashSet<TicTacMove>(Arrays.asList(
				new TicTacMove(2,3),
				new TicTacMove(3,2),
				new TicTacMove(4,5),
				new TicTacMove(5,4))),
				new HashSet<TicTacMove>(moves));
	}
	
	@Test
	public void test_3_2_Moves() {
		ReversiState state = instance.apply(ReversiState.othello, new TicTacMove(3,2));
		assertEquals( 1, state.playerTurn() );
		assertNull( instance.score( state ) );
		List<TicTacMove> moves = instance.enumerate(state);
		
		assertEquals(new HashSet<TicTacMove>(Arrays.asList(
				new TicTacMove(2,2),
				new TicTacMove(2,4),
				new TicTacMove(4,2))),
				new HashSet<TicTacMove>(moves));
	}
	
	@Test
	public void scoreSmall1a() {
		ReversiState state = new ReversiState(0, new Board<TicTacCell>(2,
				X, X,
				O, null));
		assertArrayEquals(new double[]{ 1, -1 }, instance.score( state ), 0.001 );
	}
	
	@Test
	public void scoreSmall1b() {
		ReversiState state = new ReversiState(1, new Board<TicTacCell>(2,
				X, X,
				O, null));
		assertArrayEquals(new double[]{ 1, -1 }, instance.score( state ), 0.001 );
	}
	
	@Test
	public void scoreSmall2a() {
		ReversiState state = new ReversiState(0, new Board<TicTacCell>(2,
				O, X,
				O, null));
		assertArrayEquals(new double[]{ -1, 1 }, instance.score( state ), 0.001 );
	}
	
	@Test
	public void scoreSmall2b() {
		ReversiState state = new ReversiState(1, new Board<TicTacCell>(2,
				O, X,
				O, null));
		assertArrayEquals(new double[]{ -1, 1 }, instance.score( state ), 0.001 );
	}
	
	@Test
	public void scoreSmall3a() {
		ReversiState state = new ReversiState(0, new Board<TicTacCell>(2,
				O, X,
				O, X));
		assertArrayEquals(new double[]{ 0, 0 }, instance.score( state ), 0.001 );
	}
	
	@Test
	public void scoreSmall3b() {
		ReversiState state = new ReversiState(1, new Board<TicTacCell>(2,
				O, X,
				O, X));
		assertArrayEquals(new double[]{ 0, 0 }, instance.score( state ), 0.001 );
	}
	
	@Test
	public void scoreSmall4a() {
		ReversiState state = new ReversiState(0, new Board<TicTacCell>(2,
				O, X,
				null, null));
		assertArrayEquals(new double[]{ 0, 0 }, instance.score( state ), 0.001 );
	}
	
	@Test
	public void scoreSmall4b() {
		ReversiState state = new ReversiState(1, new Board<TicTacCell>(2,
				O, X,
				null, null));
		assertArrayEquals(new double[]{ 0, 0 }, instance.score( state ), 0.001 );
	}
}

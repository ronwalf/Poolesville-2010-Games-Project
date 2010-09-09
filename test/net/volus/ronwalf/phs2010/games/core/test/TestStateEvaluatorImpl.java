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
package net.volus.ronwalf.phs2010.games.core.test;

import static net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell.O;
import static net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell.X;
import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;

import net.volus.ronwalf.phs2010.games.core.StateEvaluator;
import net.volus.ronwalf.phs2010.games.core.StateEvaluatorFactory;
import net.volus.ronwalf.phs2010.games.core.impl.StateEvalFactoryImpl;
import net.volus.ronwalf.phs2010.games.reversi.ReversiState;
import net.volus.ronwalf.phs2010.games.reversi.ReversiTransition;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacState;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacTransition;
import net.volus.ronwalf.phs2010.games.util.Board;

import org.junit.Test;

public class TestStateEvaluatorImpl {

	private static final StateEvaluatorFactory factory = StateEvalFactoryImpl.instance;
	
	
	private static TicTacState state(int turn, TicTacCell...cells) {
		int size = new Double(Math.ceil( Math.sqrt(cells.length) )).intValue();
		Board<TicTacCell> board = new Board<TicTacCell>(size, Arrays.asList(cells));
		return new TicTacState(turn, board);
	}
	
	
	@Test
	public void testBlankTacToe() {
		TicTacState blank = state(0,
				null, null, null,
				null, null, null,
				null, null, null);
		StateEvaluator<TicTacState> eval = factory.create( TicTacTransition.instance );
		double[] score = eval.evaluate( blank );
		
		assertArrayEquals(new double[]{0,0}, score, 0.001);
	}
	
	@Test
	public void testWinXTacToe() {
		TicTacState state = state(0,
				X, O, O,
				null, X, null,
				null, null, null);
		StateEvaluator<TicTacState> eval = factory.create( TicTacTransition.instance );
		double[] score = eval.evaluate( state );
		
		assertArrayEquals(new double[]{1,-1}, score, 0.001);
	}
	
	
	@Test
	public void testWinOTacToe() {
		TicTacState state = state(0,
				O, X, X,
				O, O, null,
				null, X, null);
		StateEvaluator<TicTacState> eval = factory.create( TicTacTransition.instance );
		double[] score = eval.evaluate( state );
		
		assertArrayEquals(new double[]{-1,1}, score, 0.001);
	}
	
	@Test
	public void testSmallReversi() {
		ReversiState state = new ReversiState(0, new Board<TicTacCell>(4,
				null, null, null, null,
				null, X,    O,    null,
				null, O,    X,    null,
				null, null, null, null));
		StateEvaluator<ReversiState> eval = factory.create( ReversiTransition.instance );
		double[] score = eval.evaluate(state);
		System.out.print(score);
	}
	
	
}

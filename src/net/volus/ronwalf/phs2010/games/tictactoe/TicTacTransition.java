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
package net.volus.ronwalf.phs2010.games.tictactoe;

import static net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell.O;
import static net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell.X;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.volus.ronwalf.phs2010.games.core.GameTransition;
import net.volus.ronwalf.phs2010.games.util.Board;
import net.volus.ronwalf.phs2010.games.util.BoardPathIterator;
import net.volus.ronwalf.phs2010.games.util.Board.Element;

public class TicTacTransition implements GameTransition<TicTacState, TicTacMove> {
	public static final TicTacTransition instance = new TicTacTransition();
	
	private TicTacTransition() {}
	
	public TicTacState apply(TicTacState s, TicTacMove a) {
		TicTacCell cell = s.turnCell();
		Board<TicTacCell> newBoard = s.board.change(a.x, a.y, cell);
		return new TicTacState( s.playerTurn() + 1, newBoard);
	}

	public List<TicTacMove> enumerate(TicTacState s) {
		
		if ( score(s) != null ) {
			return Collections.emptyList();
		}

		List<TicTacMove> moves = new ArrayList<TicTacMove>();
		
		for (Board.Element<TicTacCell> elem : s.board) {
			if (!elem.isSet()) {
				moves.add( new TicTacMove( elem.x, elem.y) );
			}
		}
		
		return moves;
	}
	
	public double[] score(TicTacState s) {
		TicTacCell winner = null;

		for (int i = 0; i < s.board.getSize(); i++) {
			
			// Horizontal
			winner = check(s.board, 0, i, 1, 0);
			if (winner != null)
				break;
			
			// Vertical
			winner = check(s.board, i, 0, 0, 1);
			if (winner != null)
				break;
		}
		
		// Diagonal \
		if (winner == null)
			winner = check(s.board, 0, 0, 1, 1);
		
		if (winner == null)
			winner = check(s.board, 0, s.board.getSize() - 1, 1, -1);
		
		if (winner == null) {
			for ( Board.Element<TicTacCell> element : s.board ) {
				if ( ! element.isSet() )
					return null;
			}
		}
		
		// Default tie
		double[] result = new double[]{ 0, 0 };
		
		if (winner != null) {
			result[ X.ordinal() ] = X.equals(winner) ? 1 : -1;
			result[ O.ordinal() ] = O.equals(winner) ? 1 : -1;
		}
		
		return result;
	}
	
	private TicTacCell check(Board<TicTacCell> board, int x, int y, int dx, int dy) {
		
		
		TicTacCell winner = null;
		
		for (Element<TicTacCell> elem : new BoardPathIterator<TicTacCell>(board, x, y, dx, dy)) {
			if (!elem.isSet())
				return null;
			if (winner == null)
				winner = elem.elem;
			else if ( !winner.equals( elem.elem ) )
				return null;
		}
		
		return winner;
	}

}

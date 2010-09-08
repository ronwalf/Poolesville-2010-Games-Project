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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.volus.ronwalf.phs2010.games.core.GameTransition;
import net.volus.ronwalf.phs2010.games.util.Board;

import static net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell.X;
import static net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell.O;

public class TicTacTransition implements GameTransition<TicTacState, TicTacMove> {
	public static final TicTacTransition instance = new TicTacTransition();
	
	private TicTacTransition() {}
	
	public TicTacState apply(TicTacState s, TicTacMove a) {
		TicTacCell cell = s.turnCell();
		Board<TicTacCell> newBoard = s.board.change(a.x, a.y, cell);
		return new TicTacState( s.turn + 1, newBoard);
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
		// Check the columns.
	column:
		for (int i = 0; i < s.board.getSize(); i ++) {
			for (int j = 0; j < s.board.getSize(); j++) {
				if ( s.board.get(i, j) == null )
					continue column;
				if ( !s.board.get(i, 0).equals( s.board.get(i, j) ) ) {
					continue column;
				}
			}
			winner = s.board.get(i, 0);
			break;
		}

	row:
		for (int j = 0; j < s.board.getSize(); j ++) {
			for (int i = 0; i < s.board.getSize(); i++) {
				if ( s.board.get(i, j) == null )
					continue row;
				if ( !s.board.get(0, j).equals( s.board.get(i, j) ) ) {
					continue row;
				}
			}
			winner = s.board.get(0, j);
			break;
		}
		
		boolean diagonalD = true;
		// Diagonal \
		for (int i = 0; i < s.board.getSize(); i++) {
			if (s.board.get(i, i) == null) {
				diagonalD = false;
				break;
			}
			if ( !s.board.get(0, 0).equals( s.board.get(i,i) ) ) {
				diagonalD = false;
				break;
			}
		}
		if (diagonalD) {
			winner = s.board.get(0,0);
		}
		// Diagonal /
		boolean diagonalU = true;
		for (int j = 0; j < s.board.getSize(); j++) {
			int i = s.board.getSize() - j - 1;
			if (s.board.get(i, j) == null) {
				diagonalU = false;
				break;
			}
			if ( !s.board.get(s.board.getSize()-1, 0).equals( s.board.get(i,j) ) ) {
				diagonalU = false;
				break;
			}
		}
		if (diagonalU) {
			winner = s.board.get(s.board.getSize()-1,0);
		}
		
		boolean full = true;
		for ( Board.Element<TicTacCell> element : s.board ) {
			if ( ! element.isSet() ) {
				full = false;
				break;
			}
		}
		if ( winner == null && !full ) {
			return null;
		}
		
		double[] result = new double[2];
		
		if (winner == null) {
			result[X.ordinal()] = 0;
			result[O.ordinal()] = 0;
		} else if (X.equals( winner )) {
			result[ X.ordinal() ] = 1;
			result[ O.ordinal() ] = -1;
		} else {
			result[ X.ordinal() ] = -1;
			result[ O.ordinal() ] = 1;
		}
		
		return result;
	}

}

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
package net.volus.ronwalf.phs2010.games.reversi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.volus.ronwalf.phs2010.games.core.GameTransition;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacMove;
import net.volus.ronwalf.phs2010.games.util.Board;
import net.volus.ronwalf.phs2010.games.util.BoardPathIterator;

public class ReversiTransition implements GameTransition<ReversiState, TicTacMove> {

	public static final ReversiTransition instance = new ReversiTransition();
	
	/**
	 * 8 Compass rose directions (starting north)
	 */
	private static final List<int[]> directions = Arrays.asList(new int[][]{
		{0,-1}, {1,-1}, {1,0}, {1,1}, {0,1}, {-1,1}, {-1,0}, {-1,-1}}); 
	
	public ReversiState apply(ReversiState s, TicTacMove a) {
		TicTacCell player = TicTacCell.values()[s.playerTurn()];
		Board<TicTacCell> board = s.board.change(a.x, a.y, player);
		
		for (int[] dxdy : directions) {
			Iterable<Board.Element<TicTacCell>> elemIter = new BoardPathIterator<TicTacCell>(
					s.board, a.x +  dxdy[0], a.y + dxdy[1], dxdy[0], dxdy[1]);
			List<Board.Element<TicTacCell>> path = new ArrayList<Board.Element<TicTacCell>>();
			for (Board.Element<TicTacCell> elem : elemIter) {
				TicTacCell cell = elem.elem;
				if (cell == null)
					break;
				if (cell.equals(player)) {
					// Change cells in between.
					for (Board.Element<TicTacCell> flip : path) {
						board = board.change(flip.x, flip.y, player);
					}
					break;
				}
				path.add(elem);
			}
		}
		
		
		ReversiState sn = new ReversiState( s.playerTurn() + 1, board );
		
		// Whoops, skip player if they can't make a move!
		// Original player might not be able to move either, in which case
		// score() should return non-null.
		if (enumerate(sn).isEmpty())
			sn = new ReversiState( s.playerTurn(), board );
		
		return sn;
	}

	public List<TicTacMove> enumerate(ReversiState s) {
		
		List<TicTacMove> moves = new ArrayList<TicTacMove>();
		
		for (Board.Element<TicTacCell> elem : s.board) {
			if (!elem.isSet() && canFlip(s, elem.x, elem.y)) {
				moves.add(new TicTacMove(elem.x, elem.y));
			}
		}
		
		return moves;
	}

	public double[] score(ReversiState s) {
		
		int[] count = new int[]{ 0, 0 };
		for (Board.Element<TicTacCell> elem : s.board) {
			// Return null if moves are still available
			if (elem.isSet())
				count[elem.elem.ordinal()]++;
			else if (canFlip(s, elem.x, elem.y))
				return null;
			
		}
		
		if (count[0] < count[1]) {
			return new double[]{-1, 1};
		} else if (count[0] > count[1]) {
			return new double[]{1, -1};
		}
		return new double[]{0, 0};
	}
	
	
	/**
	 * Returns true if setting the given location will flip cells.
	 */
	private boolean canFlip(ReversiState s, int x, int y) {
		// This looks suspiciously too much like apply().
		// Probably should refactor search into an interator.
		TicTacCell pcell = TicTacCell.values()[s.playerTurn()];
		
		for (int[] dxdy : directions) {
			Iterable<Board.Element<TicTacCell>> elemIter = new BoardPathIterator<TicTacCell>(
					s.board, x +  dxdy[0], y + dxdy[1], dxdy[0], dxdy[1]);
			int path = 0;
			for (Board.Element<TicTacCell> elem : elemIter) {
				TicTacCell cell = elem.elem;
				if (cell == null)
					break;
				if (cell.equals(pcell)) {
					if (path > 0)
						return true;
					break;
				}
				path++;
			}
		}
		
		return false;
	}

}

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
package net.volus.ronwalf.phs2010.games.reversi.heuristics;

import java.util.Iterator;

import net.volus.ronwalf.phs2010.games.core.HeuristicFunction;
import net.volus.ronwalf.phs2010.games.reversi.ReversiGame;
import net.volus.ronwalf.phs2010.games.reversi.ReversiState;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell;
import net.volus.ronwalf.phs2010.games.util.Board;
import net.volus.ronwalf.phs2010.games.util.BoardPathIterator;

public class ReversiSafeHeuristic implements HeuristicFunction<ReversiState> {

	public final static ReversiSafeHeuristic instance = new ReversiSafeHeuristic();
	
	public double[] score(ReversiState state) {
		
		double val = ((double) countSafer(state)) / 
			((double) state.getBoard().getSize() * state.getBoard().getSize());
		return new double[] { val, -val };
	}
	
	/** 
	 * Counts how many more cells are safe for player 0 than player 1.
	 */
	private int countSafer(ReversiState state) {
		int safe = 0;
		for (Board.Element<TicTacCell> cell : state.getBoard()) {
			if (cell.isSet() && isSafe(state.getBoard(), cell.x, cell.y)) {
				safe += cell.elem.ordinal() == 0 ? 1 : -1;
			}
		}
		return safe;
	}
	
	private boolean isSafe(Board<TicTacCell> board, int x, int y) {
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (j == 0 && i == 0) {
					continue;
				}
				BoardPathIterator<TicTacCell> dir1 = new BoardPathIterator<TicTacCell>( board, x, y, i, j);
				BoardPathIterator<TicTacCell> dir2 = new BoardPathIterator<TicTacCell>( board, x, y, -i, -j);
				if (endsOpen(dir1) && endsSet(dir2)) {
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean endsOpen(Iterator<Board.Element<TicTacCell>> iter) {
		while (iter.hasNext()) {
			if (!iter.next().isSet()) {
				return true;
			}
		}
		return false;
	}
	
	private boolean endsSet(Iterator<Board.Element<TicTacCell>> iter) {
		TicTacCell first = null;
		while (iter.hasNext()) {
			Board.Element<TicTacCell> curr = iter.next();
			if (!curr.isSet()) {
				break;
			}
			else if (first == null) {
				first = curr.elem;
			}
			else if (!first.equals(curr.elem)) {
				return true;
			}
		}
		return false;
	}

	public static void register() {
		ReversiGame.instance.addHeuristic("safe", instance);
	}
}

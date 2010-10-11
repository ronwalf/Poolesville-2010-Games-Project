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

import net.volus.ronwalf.phs2010.games.core.HeuristicFunction;
import net.volus.ronwalf.phs2010.games.reversi.ReversiGame;
import net.volus.ronwalf.phs2010.games.reversi.ReversiState;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell;
import net.volus.ronwalf.phs2010.games.util.Board;
import net.volus.ronwalf.phs2010.games.util.BoardPathIterator;

public class ReversiEdgeHeuristic implements HeuristicFunction<ReversiState> {
	public static final ReversiEdgeHeuristic instance = new ReversiEdgeHeuristic();
	
	public double[] score(ReversiState state) {
		double count = 0;
		int last = state.getBoard().getSize() - 1;
		for (int[] params : new int[][] { 
				{0,0,0,1},
				{0,0,1,0},
				{last,1,0,1},
				{1,last,1,0}
				}){
			count += scoreEdge(state.getBoard(), params);
		}
		
		return new double[]{count, -count};
	}
	
	public int scoreEdge(Board<TicTacCell> board, int... is) {
		int count = 0;
		BoardPathIterator<TicTacCell> iter = new BoardPathIterator<TicTacCell>( board, is[0], is[1], is[2], is[3] );
		for (Board.Element<TicTacCell> cell : iter) {
			if (cell.isSet()) {
				if (cell.elem.ordinal() == 0)
					count++;
				else
					count--;
			}
		}
		return count;
	}

	public static void register() {
		ReversiGame.instance.addHeuristic("count", instance);
	}
	
}

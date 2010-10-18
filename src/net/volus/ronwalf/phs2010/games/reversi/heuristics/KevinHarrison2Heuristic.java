package net.volus.ronwalf.phs2010.games.reversi.heuristics;

import java.util.Iterator;

import net.volus.ronwalf.phs2010.games.core.HeuristicFunction;
import net.volus.ronwalf.phs2010.games.reversi.ReversiGame;
import net.volus.ronwalf.phs2010.games.reversi.ReversiState;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell;
import net.volus.ronwalf.phs2010.games.util.Board;
import net.volus.ronwalf.phs2010.games.util.BoardPathIterator;

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

/**
 * Heuristic in two parts:
 * <p>
 * - A "property" heuristic which assigns a score to every cell a player controls
 *     (this entails a custom score system with the "safe" heuristic thrown in the mix)
 * <p>
 * - A "mobility" heuristic score which assigns a score based on the number of potential moves;
 *   this is designed to expand the player's movement while restricting the opponent's
 *     (mobility score idea taken from "Inside Reversi/Othello", http://home.datacomm.ch/t_wolf/tw/misc/reversi/html/index.html)
 *     
 *     @author Kevin Harrison
 */
public class KevinHarrison2Heuristic implements HeuristicFunction<ReversiState> {
	private final int MIDDLE;
	private final int EDGE;
	private final int CORNER;
	private final int SAFE_EXTRA;
	private final int OPEN_CORNER_DIAGONAL_EXTRA;
	
	private final int MOBILITY;
	private final int POTENTIAL_MOBILITY;
	
	private static final int[][] dir = new int[][]{{0,-1}, {1,-1}, {1,0}, {1,1}, {0,1}, {-1,1}, {-1,0}, {-1,-1}};
	
	public static final KevinHarrison2Heuristic instance = new KevinHarrison2Heuristic();
	
	public KevinHarrison2Heuristic() {
		this(new int[] {
				1, // MIDDLE
				1, // EDGE
				1000000, // CORNER
				1000, // SAFE_EXTRA
				-1000000, // OPEN_CORNER_DIAGONAL_EXTRA
				10000, // MOBILITY
				2000 // POTENTIAL MOBILITY
				});
	}
	
	public KevinHarrison2Heuristic(int[] scores) {
		MIDDLE = scores[0];
		EDGE = scores[1];
		CORNER = scores[2];
		SAFE_EXTRA = scores[3];
		OPEN_CORNER_DIAGONAL_EXTRA = scores[4];
		MOBILITY = scores[5];
		POTENTIAL_MOBILITY = scores[6];
	}
	
	public double[] score(ReversiState s) {
		double[] propertyScore = scoreProperty(s);
		double[] mobilityScore = scoreMobility(s);
		int length = Math.min(propertyScore.length, mobilityScore.length);
		double[] totalScore = new double[length];
		for(int i = 0; i < length; ++i)
			totalScore[i] = propertyScore[i] + mobilityScore[i];
		return totalScore;
	}
	
	public static void register() {
		ReversiGame.instance.addHeuristic("KevinHarrison2Heuristic", new KevinHarrison2Heuristic());
	}
	
	public static void register(int[] scores, String name) {
		ReversiGame.instance.addHeuristic(name, new KevinHarrison2Heuristic(scores));
	}

	private double[] scoreProperty(ReversiState s) {
		int size = s.board.size();
		double[] scores = new double [2];
		for(int y = 0; y < size; ++y)
			for(int x = 0; x < size; ++x) {
				TicTacCell cell = s.board.get(x, y);
				if(cell == null)
					continue;
				int player = cell.ordinal();
				int score;
				if(y == 0 || y == size - 1) {
					if(x == 0 || x == size - 1)
						score = CORNER;
					else
						score = EDGE;
				} else if(x == 0 || x == size - 1) {
					if(y == 0 || y == size - 1)
						score = CORNER;
					else
						score = EDGE;
				} else
					score = MIDDLE;
				if(isSafe(s.board, x, y))
					score += SAFE_EXTRA;
				if(x == 1 && y == 1 && s.board.get(0, 0) == null ||
						x == 1 && y == size - 2 && s.board.get(0, size - 1) == null ||
						x == size - 2 && y == 1 && s.board.get(size - 1, 0) == null ||
						x == size - 2 && y == size - 2 && s.board.get(size - 1, size - 1) == null) {
					score += OPEN_CORNER_DIAGONAL_EXTRA;
				}
				scores[player] += score;
			}
		double s0 = scores[0];
		scores[0] -= scores[1];
		scores[1] -= s0;
		return scores;
	}
	
	private double[] scoreMobility(ReversiState s) {
		double[] scores = new double[2];
		for(int player = 0; player < 2; ++ player) {
			TicTacCell pcell = TicTacCell.values()[player];
			scores[player] = countMoves(s, pcell) * MOBILITY
								+ countPotentialMoves(s, pcell) * POTENTIAL_MOBILITY;
		}
		double s0 = scores[0];
		scores[0] -= scores[1];
		scores[1] -= s0;
		return scores;
	}
	
	private boolean isSafe(Board<TicTacCell> board, int x, int y) {
		TicTacCell player = board.get(x, y);
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (j == 0 && i == 0) {
					continue;
				}
				BoardPathIterator<TicTacCell> dir1 = new BoardPathIterator<TicTacCell>( board, x, y, i, j);
				BoardPathIterator<TicTacCell> dir2 = new BoardPathIterator<TicTacCell>( board, x, y, -i, -j);
				TicTacCell end1 = endCell(dir1);
				TicTacCell end2 = endCell(dir2);
				if(end1 != null && end1 != player && end2 == null)
					return false;
			}
		}
		return true;
	}
	
	private TicTacCell endCell(Iterator<Board.Element<TicTacCell>> iter) {
		TicTacCell first = iter.next().elem;
		if(first == null)
			return null;
		while (iter.hasNext()) {
			TicTacCell curr = iter.next().elem;
			if(curr != first)
				return curr;
		}
		return first;
	}
	
	//=======================
	// ENUMERATE
	//=======================

	private int countPotentialMoves(ReversiState s, TicTacCell pcell) {
		int size = s.board.size(), total = 0;
		for(int y = 0; y < size; ++y)
			for(int x = 0; x < size; ++x) {
				TicTacCell c = s.board.get(x, y);
				if(c != null && !c.equals(pcell))
					for (int[] dxdy : dir) {
						int newX = x + dxdy[0];
						if(newX < 0 || newX >= size)
							continue;
						int newY = y + dxdy[1];
						if(newY < 0 || newY >= size)
							continue;
						if(s.board.get(newX, newY) == null) {
							++total;
							break;
						}
					}
			}
		return total;
	}

	private int countMoves(ReversiState s, TicTacCell player) {
		int total = 0;
		for (Board.Element<TicTacCell> elem : s.board)
			if (!elem.isSet() && canFlip(s, elem.x, elem.y, player))
				++total;
		return total;
	}
	
	private boolean canFlip(ReversiState s, int x, int y, TicTacCell pcell) {
		for (int[] dxdy : dir) {
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

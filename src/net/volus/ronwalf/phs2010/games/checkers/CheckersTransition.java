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
package net.volus.ronwalf.phs2010.games.checkers;

import static net.volus.ronwalf.phs2010.games.checkers.CheckersPiece.Red;
import static net.volus.ronwalf.phs2010.games.checkers.CheckersPiece.RedKing;
import static net.volus.ronwalf.phs2010.games.checkers.CheckersPiece.White;
import static net.volus.ronwalf.phs2010.games.checkers.CheckersPiece.WhiteKing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.volus.ronwalf.phs2010.games.core.GameTransition;
import net.volus.ronwalf.phs2010.games.util.Board;
import net.volus.ronwalf.phs2010.games.util.Pair;

public class CheckersTransition implements
		GameTransition<CheckersState, CheckersMove> {

	public static final CheckersTransition instance = new CheckersTransition();
	
	private static final double TIE = 0;
	private static final double WIN = Long.MAX_VALUE;
	
	private List<CheckersMove> addMoves(Board<CheckersPiece> b, CheckersPiece piece, int x, int y) {
		List<CheckersMove> moves = new ArrayList<CheckersMove>();
		Pair<Integer,Integer> loc = new Pair<Integer,Integer>(x, y);
		
		for (List<CheckersCompass> move : findMoves( b, piece, loc)) {
			moves.add(new CheckersMove(loc, false, move.toArray(new CheckersCompass[1])));
		}
		return moves;
	}
	
	private List<CheckersMove> addJumps(Board<CheckersPiece> b, CheckersPiece piece, int x, int y) {
		List<CheckersMove> moves = new ArrayList<CheckersMove>();
		Pair<Integer,Integer> loc = new Pair<Integer,Integer>(x, y);
		
		for (List<CheckersCompass> jumps : findJumps( b, piece, loc)) {
			if (!jumps.isEmpty())
				moves.add(new CheckersMove(loc, true, jumps.toArray(new CheckersCompass[jumps.size()])));
		}
		
		
		return moves;
	}

	public CheckersState apply(CheckersState s, CheckersMove a) {
		boolean resetStale = false;
		Board<CheckersPiece> b = s.getBoard();
		
		Pair<Integer,Integer> loc = a.location();
		CheckersPiece piece = b.get(loc);
		
		if (piece == null)
			throw new IllegalArgumentException("No piece where move began!");
		
		// Moved a non-king piece.
		if (!piece.isKing())
			resetStale = true;
		
		for (CheckersCompass cc : a) {
			// Unset current location
			b = b.change(loc, null);
			loc = cc.apply(loc);
			if (b.get(loc) != null) {
				// Move was a jump.
				resetStale = true;
				b = b.change(loc, null);
				loc = cc.apply(loc);
			}
		}
		
		// Crown the king?
		if (White.equals(piece) && loc.y == 0) {
			piece = WhiteKing;
		}
		if (Red.equals(piece) && loc.y == b.size() - 1) {
			piece = RedKing;
		}
		
		// Set piece.
		b = b.change(loc, piece);
		
		return new CheckersState(s.playerTurn() + 1, resetStale ? 0 : s.stalemateCounter() + 1, b);
	}

	public List<CheckersMove> enumerate(CheckersState s) {
		List<CheckersMove> moves = new ArrayList<CheckersMove>();
		
		if (s.stalemateCounter() >= 50)
			return moves;
		
		for (Board.Element<CheckersPiece> elem : s.getBoard()) {
			if (elem.isSet() && elem.elem.player() == s.playerTurn()) {
				moves.addAll( addJumps(s.getBoard(), elem.elem, elem.x, elem.y) );
			}
		}
		
		if (moves.size() > 0)
			return moves;
		
		for (Board.Element<CheckersPiece> elem : s.getBoard()) {
			if (elem.isSet() && elem.elem.player() == s.playerTurn()) {
				moves.addAll( addMoves(s.getBoard(), elem.elem, elem.x, elem.y) );
			}
		}
		
		return moves;
	}
	
	/**
	 * Returns singleton lists of the directions a piece can move (mirrors findJumps)
	 */
	public List<List<CheckersCompass>> findMoves(Board<CheckersPiece> b, CheckersPiece piece, Pair<Integer,Integer> loc) {
		List<List<CheckersCompass>> moves = new ArrayList<List<CheckersCompass>>();
		for (CheckersCompass cc : piece) {
			Pair<Integer,Integer> nloc = cc.apply(loc);
			if (!b.contains(nloc))
				continue;
			
			if (b.get(nloc) == null)
				moves.add(Collections.singletonList(cc));
		}
		return moves;
	}
	
	public List<List<CheckersCompass>> findJumps(Board<CheckersPiece> b, CheckersPiece piece, Pair<Integer,Integer> loc) {
		List<List<CheckersCompass>> jumps = new ArrayList<List<CheckersCompass>>();
		
		for (CheckersCompass cc : piece) {
			
			Pair<Integer,Integer> nloc1 = cc.apply(loc);
			if ( !b.contains(nloc1) )
				continue;
			CheckersPiece jumped = b.get(nloc1);
			if ( jumped == null || jumped.player() == piece.player() )
				continue;
			
			Pair<Integer,Integer> nloc2 = cc.apply(nloc1);
			if ( !b.contains(nloc2) || b.get(nloc2) != null )
				continue;
			
			// Remove piece
			Board<CheckersPiece> b2 = b.change(nloc1, null);
			List<List<CheckersCompass>> nextJumps = findJumps(b2, piece, nloc2);
			
			if (nextJumps.isEmpty())
				jumps.add(new ArrayList<CheckersCompass>(Collections.singletonList(cc)));
			else for (List<CheckersCompass> jump : nextJumps) {
				jump.add(0, cc);
				jumps.add(jump);
			}
			
		}
		
		return jumps;
	}
	
	public double[] score(CheckersState s) {
		if ( s.stalemateCounter() >= 50 ) {
			return new double[]{TIE,TIE};
		}
		
		if (enumerate(s).size() == 0) {
			double[] score = new double[2];
			score[s.playerTurn()] = -WIN;
			score[(s.playerTurn() + 1) % 2] = WIN;
			return score;
		}
		
		return null;
	}

}

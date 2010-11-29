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
import java.util.List;

import net.volus.ronwalf.phs2010.games.core.PlayerState;
import net.volus.ronwalf.phs2010.games.util.Board;
import net.volus.ronwalf.phs2010.games.util.BoardState;

public class CheckersState implements PlayerState, BoardState<CheckersPiece> {

	public static final CheckersState instance = new CheckersState(0, 0, new Board<CheckersPiece>(
			8, new CheckersPiece[]{
		null, Red, null, Red, null, Red, null, Red,
		Red, null, Red, null, Red, null, Red, null,
		null, Red, null, Red, null, Red, null, Red,
		null, null, null, null, null, null, null, null,
		null, null, null, null, null, null, null, null,
		White, null, White, null, White, null, White, null,
		null, White, null, White, null, White, null, White,
		White, null, White, null, White, null, White, null
	}));
	
	public static CheckersState parse(String stateStr) {
		String[] parts = stateStr.trim().split(":");
		int turn = Integer.parseInt(parts[0]);
		int counter = Integer.parseInt(parts[1]);
		
		String elems = parts[2];
		int size = new Double(Math.sqrt(new Integer(elems.length()).doubleValue())).intValue();
		List<CheckersPiece> pieces = new ArrayList<CheckersPiece>();
		for (int i = 0; i < elems.length(); i++) {
			switch (elems.charAt(i)) {
			case 'r' : pieces.add(Red); break;
			case 'R' : pieces.add(RedKing); break;
			case 'w' : pieces.add(White); break;
			case 'W' : pieces.add(WhiteKing); break;
			default : pieces.add(null);
			}
		}
		
		return new CheckersState( turn, counter, new Board<CheckersPiece>(size, pieces));
	}
	
	private final Board<CheckersPiece> board;
	private final int counter;
	private final int turn;
	
	public CheckersState(int turn, int counter, Board<CheckersPiece> board) {
		this.board = board;
		this.turn = turn % 2;
		this.counter = counter;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CheckersState other = (CheckersState) obj;
		if (board == null) {
			if (other.board != null)
				return false;
		} else if (!board.equals(other.board))
			return false;
		if (counter != other.counter)
			return false;
		if (turn != other.turn)
			return false;
		return true;
	}

	public Board<CheckersPiece> getBoard() { return board; }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((board == null) ? 0 : board.hashCode());
		result = prime * result + counter;
		result = prime * result + turn;
		return result;
	}
	
	public int playerCount() { return 2; }

	public int playerTurn() { return turn; }

	public int stalemateCounter() { return counter; }
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(turn);
		buf.append(':');
		buf.append(counter);
		buf.append(':');
		for (Board.Element<CheckersPiece> elem : board) {
			if (!elem.isSet()) {
				buf.append('.');
			} else {
				String c = elem.elem.player() == 0 ? "r" : "w";
				if (elem.elem.isKing())
					c = c.toUpperCase();
				buf.append(c);
			}
		}
		return buf.toString();
	}
}

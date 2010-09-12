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

import net.volus.ronwalf.phs2010.games.core.PlayerState;
import net.volus.ronwalf.phs2010.games.util.Board;
import net.volus.ronwalf.phs2010.games.util.BoardState;

public class TicTacState implements PlayerState, BoardState<TicTacCell> {

	public static final TicTacState STANDARD_GAME = new TicTacState(
			0,
			new Board<TicTacCell>(3));
	
	public final int turn;
	public final Board<TicTacCell> board;
	
	
	public TicTacState(final int turn, final Board<TicTacCell> board) {
		this.turn = turn % 2;
		this.board = board;
	}
	
	public TicTacCell turnCell() {
		return turn == 0 ? TicTacCell.X : TicTacCell.O;
	}
	
	public Board<TicTacCell> getBoard() { return board; }
	public int playerCount() { return 2; }
	public int playerTurn() { return turn; }
	
	@Override
	public boolean equals(Object o) {
		if (!getClass().equals(o.getClass()))
			return false;
		
		TicTacState os = (TicTacState) o;
		return turn == os.turn && board.equals(os.board);
	}
	
	@Override 
	public int hashCode() {
		return board.hashCode()*31 + turn;
	}
	
	@Override
	public String toString() {
		return "Player: " + turn + "\n" + board;
	}
}

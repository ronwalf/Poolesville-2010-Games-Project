package net.volus.ronwalf.phs2010.games.reversi;

import static net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell.O;
import static net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell.X;
import net.volus.ronwalf.phs2010.games.core.PlayerState;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell;
import net.volus.ronwalf.phs2010.games.util.Board;
import net.volus.ronwalf.phs2010.games.util.BoardState;

public class ReversiState implements PlayerState, BoardState<TicTacCell> {

	public static final ReversiState reversi = 
		new ReversiState(0, new Board<TicTacCell>(8));
	public static final ReversiState othello = new ReversiState(0,
			new Board<TicTacCell>(8)
				.change(4, 3, X).change(3, 4, X)
				.change(3, 3, O).change(4, 4, O));
	
	
	private final int turn;
	public final Board<TicTacCell> board;
	
	public ReversiState(int turn, Board<TicTacCell> board) {
		this.turn = turn % 2;
		this.board = board;
	}
	
	public Board<TicTacCell> getBoard() { return board; }
	
	public int playerCount() { return 2; }

	public int playerTurn() {
		return turn;
	}
	
	@Override
	public String toString() {
		return "Player " + turn + "\n" + board;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((board == null) ? 0 : board.hashCode());
		result = prime * result + turn;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReversiState other = (ReversiState) obj;
		if (board == null) {
			if (other.board != null)
				return false;
		} else if (!board.equals(other.board))
			return false;
		if (turn != other.turn)
			return false;
		return true;
	}

	
}

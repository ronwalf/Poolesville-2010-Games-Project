package net.volus.ronwalf.phs2010.games.reversi;

import static net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell.O;
import static net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell.X;

import net.volus.ronwalf.phs2010.games.core.PlayerState;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell;
import net.volus.ronwalf.phs2010.games.util.Board;

public class ReversiState implements PlayerState {

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
	
	public int playerCount() { return 2; }

	public int playerTurn() {
		return turn;
	}

}

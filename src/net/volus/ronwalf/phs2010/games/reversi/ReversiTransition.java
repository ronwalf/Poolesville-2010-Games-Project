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
					// Change board
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
		if (enumerate(sn).isEmpty())
			sn = new ReversiState( s.playerTurn(), board );
		
		return sn;
	}

	public List<TicTacMove> enumerate(ReversiState s) {
		
		List<TicTacMove> moves = new ArrayList<TicTacMove>();
		
		for (Board.Element<TicTacCell> elem : s.board) {
			if (!elem.isSet() && canFlip(s, elem.x, elem.y) > 0) {
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
			else if (canFlip(s, elem.x, elem.y) > 0)
				return null;
			
		}
		
		if (count[0] < count[1]) {
			return new double[]{-1, 1};
		} else if (count[0] > count[1]) {
			return new double[]{1, -1};
		}
		return new double[]{0, 0};
	}
	
	public int canFlip(ReversiState s, int x, int y) {
		int flipped = 0;
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
					flipped += path;
					break;
				}
				path++;
			}
		}
		
		return flipped;
	}

}

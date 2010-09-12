package net.volus.ronwalf.phs2010.games.gui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacMove;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacState;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacTransition;

public class TicTacToe implements Runnable {
	TicTacState state;
	BoardPanel<TicTacCell> panel;
	
	public TicTacToe() {
		state = TicTacState.STANDARD_GAME;

		
	}
	
	public void move(int i, int j) {
		System.out.println("Move!: " + i + " " + j);
		state = TicTacTransition.instance.apply( state, new TicTacMove(i, j) );
		panel.setBoard(state.board);
	}

	public void run() {
		JFrame f = new JFrame("TicTacToe!");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        
		panel = new BoardPanel<TicTacCell>(state.board, TicTacCellPainter.instance);
		
		
		panel.setListener(new BoardListener<TicTacCell>(){

			public void cellPressed(BoardPanel<TicTacCell>.Cell cell) {
//				if (state.playerTurn() != 0)
//					return;
				panel.setEnabled(false);
				TicTacToe.this.move(cell.i, cell.j);
				panel.setEnabled(true);
				
			}
			
		});
		
		
       
        f.add(panel);
        f.setSize(250,250);
        f.setVisible(true);
        
	} 
	
	public static void main(String args[]) {
		SwingUtilities.invokeLater(new TicTacToe());
	}
}

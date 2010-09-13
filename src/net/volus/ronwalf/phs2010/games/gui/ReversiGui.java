package net.volus.ronwalf.phs2010.games.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.volus.ronwalf.phs2010.games.core.SearchController;
import net.volus.ronwalf.phs2010.games.core.impl.AlphaBetaFactory;
import net.volus.ronwalf.phs2010.games.core.impl.BestNextMoveFactory;
import net.volus.ronwalf.phs2010.games.core.impl.MinimaxFactory;
import net.volus.ronwalf.phs2010.games.core.impl.RandomMoveFactory;
import net.volus.ronwalf.phs2010.games.reversi.ReversiCountHeuristic;
import net.volus.ronwalf.phs2010.games.reversi.ReversiGame;
import net.volus.ronwalf.phs2010.games.reversi.ReversiState;
import net.volus.ronwalf.phs2010.games.reversi.ReversiTransition;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacMove;

public class ReversiGui implements Runnable {
	ReversiState state;
	BoardPanel<TicTacCell> bpanel;
	private SearchController controller;
	private PlayerSelector<ReversiState, TicTacMove> playerSelector;
	
	private JLabel infoLabel;
	
	public ReversiGui() {
		state = ReversiState.othello;
	}
	
	private void startSearch() {
		bpanel.setEnabled(false);
		Runnable search = new Runnable() {

			public void run() {
				
				final TicTacMove move = playerSelector.getPlayer().move( state );
				
				SwingUtilities.invokeLater(new Runnable(){

					public void run() {
						if (move != null) {
							move(move.x,move.y);
						}
						bpanel.setEnabled(true);
					}
					
				});
			}
			
		};
		new Thread(search).start();
		
	}
	
	public void move(int i, int j) {
		for ( TicTacMove move : ReversiTransition.instance.enumerate( state ) ) {
			if ( move.x == i && move.y == j ) {
				state = ReversiTransition.instance.apply( state, new TicTacMove(i, j) );
				bpanel.setBoard(state.board);
				updateInfo();
				return;
			}
		}
	}

	public void run() {
		JFrame f = new JFrame("Reversi!");
		JPanel p = new JPanel(new BorderLayout());
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        
		bpanel = new BoardPanel<TicTacCell>(state.board, TicTacCellPainter.instance);
		
		
		bpanel.setListener(new BoardListener<TicTacCell>(){

			public void cellPressed(BoardPanel<TicTacCell>.Cell cell) {
				ReversiGui.this.move(cell.i, cell.j);
				
			}
			
		});
		
		
		p.add(bpanel, BorderLayout.CENTER);
        
		JPanel controls = new JPanel(new BorderLayout());
		JButton run = new JButton("Run!");
		run.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				startSearch();
			}
			
		});
		controls.add(run, BorderLayout.WEST);
		
		
        StopPanel stop = new StopPanel();
		controller = stop.getController();
		controls.add(stop);
		
		JButton reset = new JButton("Reset");
		reset.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				state = ReversiState.othello;
				bpanel.setBoard(state.getBoard());
			}
			
		});
		controls.add(reset, BorderLayout.EAST);
        
		p.add(controls, BorderLayout.SOUTH);
		

		JPanel infoPanel = new JPanel(new BorderLayout());
		
		infoLabel = new JLabel("");
		updateInfo();
		infoPanel.add(infoLabel, BorderLayout.WEST);
		
		playerSelector = new PlayerSelector<ReversiState, TicTacMove>(ReversiGame.instance, controller);
		infoPanel.add(playerSelector, BorderLayout.EAST);
		
		p.add(infoPanel, BorderLayout.NORTH);
		
		f.add(p);
        f.setSize(800,600);
        f.setVisible(true);
        
	}
	
	public void updateInfo() {
		double[] score = ReversiTransition.instance.score( state );
		if (score == null) {
			infoLabel.setText("Turn: " + 
					TicTacCell.values()[state.playerTurn()].toString().toUpperCase());
		} else if (score[0] == score[1]) {
			infoLabel.setText("Game tie!");
		} else {
			TicTacCell winner = score[0] > score[1] ? TicTacCell.X : TicTacCell.O;
			infoLabel.setText("Congratulations " + winner.toString().toUpperCase() + "!");
		}
	}
	
	public static void main(String args[]) {
		
		BestNextMoveFactory.register();
		MinimaxFactory.register();
		AlphaBetaFactory.register();
		RandomMoveFactory.register();
		
		ReversiCountHeuristic.register();
		
		
		SwingUtilities.invokeLater(new ReversiGui());
	}
}

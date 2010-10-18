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
package net.volus.ronwalf.phs2010.games.reversi;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import net.volus.ronwalf.phs2010.games.core.Game;
import net.volus.ronwalf.phs2010.games.core.impl.AlphaBetaFactory;
import net.volus.ronwalf.phs2010.games.core.impl.BestNextMoveFactory;
import net.volus.ronwalf.phs2010.games.core.impl.MinimaxFactory;
import net.volus.ronwalf.phs2010.games.core.impl.RandomMoveFactory;
import net.volus.ronwalf.phs2010.games.gui.BoardListener;
import net.volus.ronwalf.phs2010.games.gui.BoardPanel;
import net.volus.ronwalf.phs2010.games.gui.PlayerManager;
import net.volus.ronwalf.phs2010.games.gui.StateChangeListener;
import net.volus.ronwalf.phs2010.games.gui.BoardPanel.Cell;
import net.volus.ronwalf.phs2010.games.reversi.heuristics.ReversiDistanceHeuristic;
import net.volus.ronwalf.phs2010.games.reversi.heuristics.ReversiSafeHeuristic;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacCellPainter;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacMove;

public class ReversiGui implements Runnable, StateChangeListener<ReversiState> {
	JFrame frame;
	BoardPanel<TicTacCell> bpanel;
	TitledBorder boardTitle;
	
	private PlayerManager<ReversiState, TicTacMove> manager;
	
	public ReversiGui() {}
	
	public void move(int i, int j) {
		manager.move(new TicTacMove(i, j));
	}

	public void run() {
		frame = new JFrame("Reversi!");
		JPanel p = new JPanel(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        
        Game<ReversiState, TicTacMove> reversi = ReversiGame.instance;
 
        bpanel = new BoardPanel<TicTacCell>(reversi.getInitialState().getBoard(),
				TicTacCellPainter.instance);
		bpanel.setListener(new BoardListener<TicTacCell>(){

			public void cellPressed(BoardPanel<TicTacCell>.Cell cell) {
				manager.move(new TicTacMove(cell.i, cell.j));
			}
			
		});
		boardTitle = BorderFactory.createTitledBorder("HI");
		bpanel.setBorder(boardTitle);
		p.add(bpanel, BorderLayout.CENTER);
		
        manager = new PlayerManager<ReversiState, TicTacMove>(reversi, this);
        p.add(manager, BorderLayout.EAST);
        
		
        
		
		stateChanged(reversi.getInitialState());
		
		frame.add(p);
        frame.setSize(800,600);
        frame.setVisible(true);
        
	}

	public void stateBusy() {
		bpanel.setEnabled(false);
	}

	public void stateChanged(ReversiState state) {
		double[] score = ReversiTransition.instance.score( state );
		if (score == null) {
			frame.setTitle("Reversi! Turn: " + 
					TicTacCell.values()[state.playerTurn()].toString().toUpperCase());
		} else if (score[0] == score[1]) {
			frame.setTitle("Reversi! Game tie!");
		} else {
			TicTacCell winner = score[0] > score[1] ? TicTacCell.X : TicTacCell.O;
			frame.setTitle("Reversi! Congratulations " + winner.toString().toUpperCase() + "!");
		}
		bpanel.setBoard(state.getBoard());
	}

	public void stateUnbusy() {
		bpanel.setEnabled(true);
	}
	
	public static void main(String args[]) {
		
		BestNextMoveFactory.register();
		MinimaxFactory.register();
		AlphaBetaFactory.register();
		RandomMoveFactory.register();
		
		ReversiDistanceHeuristic.register();
		ReversiSafeHeuristic.register();
		ReversiCombinedHeuristic.register(0.4);
		
		
		SwingUtilities.invokeLater(new ReversiGui());
	}

}

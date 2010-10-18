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

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import net.volus.ronwalf.phs2010.games.checkers.heuristics.CheckersCountHeuristic;
import net.volus.ronwalf.phs2010.games.core.Game;
import net.volus.ronwalf.phs2010.games.core.impl.AlphaBetaFactory;
import net.volus.ronwalf.phs2010.games.core.impl.BestNextMoveFactory;
import net.volus.ronwalf.phs2010.games.core.impl.MinimaxFactory;
import net.volus.ronwalf.phs2010.games.core.impl.RandomMoveFactory;
import net.volus.ronwalf.phs2010.games.gui.BoardListener;
import net.volus.ronwalf.phs2010.games.gui.BoardPanel;
import net.volus.ronwalf.phs2010.games.gui.PlayerManager;
import net.volus.ronwalf.phs2010.games.gui.StateChangeListener;

public class CheckersGui implements Runnable, StateChangeListener<CheckersState> {
	JFrame frame;
	BoardPanel<CheckersPiece> bpanel;
	TitledBorder boardTitle;
	
	private PlayerManager<CheckersState, CheckersMove> manager;
	
	public CheckersGui() {}

	public void run() {
		frame = new JFrame("Checkers!");
		JPanel p = new JPanel(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        
        Game<CheckersState, CheckersMove> checkers = CheckersGame.instance;
 
        bpanel = new BoardPanel<CheckersPiece>(checkers.getInitialState().getBoard(),
				CheckersPiecePainter.instance);
		bpanel.setListener(new BoardListener<CheckersPiece>(){

			public void cellPressed(BoardPanel<CheckersPiece>.Cell cell) {
				//manager.move(new CheckersMove(cell.i, cell.j));
			}
			
		});
		boardTitle = BorderFactory.createTitledBorder("HI");
		bpanel.setBorder(boardTitle);
		p.add(bpanel, BorderLayout.CENTER);
		
        manager = new PlayerManager<CheckersState, CheckersMove>(checkers, this);
        p.add(manager, BorderLayout.EAST);
        
		
        
		
		stateChanged(checkers.getInitialState());
		
		frame.add(p);
        frame.setSize(800,600);
        frame.setVisible(true);
        
	}

	public void stateBusy() {
		bpanel.setEnabled(false);
	}

	public void stateChanged(CheckersState state) {
		double[] score = CheckersTransition.instance.score( state );
		if (score == null) {
			frame.setTitle("Checkers! Turn: " + state.playerTurn());
		} else if (score[0] == score[1]) {
			frame.setTitle("Checkers! Game tie!");
		} else {
			CheckersPiece winner = score[0] > score[1] ? CheckersPiece.Red : CheckersPiece.White;
			frame.setTitle("Checkers! Congratulations " + winner.toString().toUpperCase() + "!");
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
		
		CheckersCountHeuristic.register();
		
		
		SwingUtilities.invokeLater(new CheckersGui());
	}

}

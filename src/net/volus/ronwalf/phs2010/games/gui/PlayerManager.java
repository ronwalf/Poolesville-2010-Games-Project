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
package net.volus.ronwalf.phs2010.games.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.volus.ronwalf.phs2010.games.core.Game;
import net.volus.ronwalf.phs2010.games.core.GamePlayer;
import net.volus.ronwalf.phs2010.games.core.PlayerState;
import net.volus.ronwalf.phs2010.games.reversi.ReversiState;

public class PlayerManager<State extends PlayerState, Action> extends JPanel {
	
	private final StateChangeListener<State> listener;
	private final Game<State, Action> game;
	private State state;
	
	List<PlayerSelector<State, Action>> playerSelectors;
	StopPanel stopPanel;
	boolean keepRunning = false;
	
	
	public PlayerManager(final Game<State, Action> game, final StateChangeListener<State> listener) {
		super(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;
		setBorder(BorderFactory.createEtchedBorder());
		
		stopPanel = new StopPanel();
		
		playerSelectors = new ArrayList<PlayerSelector<State, Action>>();
		for (int i = 0; i < game.getInitialState().playerCount(); i++) {
			PlayerSelector<State,Action> selector = new PlayerSelector<State,Action>(i, game, stopPanel.getController());
			playerSelectors.add(selector);
			add(selector, c);
		}
		
		
		add(stopPanel, c);
		
		this.listener = listener;
		this.game = game;
		state = game.getInitialState();
		
		JButton runOnce = new JButton("Run Once");
		runOnce.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				keepRunning = false;
				startSearch();
			}
			
		});
		add(runOnce, c);
		
		JButton reset = new JButton("Reset");
		reset.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				state = game.getInitialState();
				listener.stateChanged( state );
			}
			
		});
		add(reset, c);
		
		highlightTurn();
	}
	
	public GamePlayer<State, Action> getPlayer(State s) {
		return playerSelectors.get(s.playerTurn()).getPlayer();
	}
	
	public void move(Action a) {
		if (!stopPanel.getController().isStopped())
			return;
		for ( Action move : game.getTransition().enumerate( state ) ) {
			if ( move.equals(a) ) {
				state = game.getTransition().apply( state, a );
				listener.stateChanged(state);
				highlightTurn();
				return;
			}
		}
	}
	
	private void startSearch() {
		listener.stateBusy();
		Runnable search = new Runnable() {

			public void run() {
				
				final Action move = getPlayer(state).move( state );
				
				SwingUtilities.invokeLater(new Runnable(){

					public void run() {
						listener.stateUnbusy();
						if (move != null) {
							move(move);
						}
					}
					
				});
			}
			
		};
		new Thread(search).start();
		
	}
	
	private void highlightTurn() {
		for (int i = 0; i < state.playerCount(); i++) {
			if (i == state.playerTurn())
				playerSelectors.get(i).highlight();
			else
				playerSelectors.get(i).unHighlight();
		}
	}
	
}

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
package net.volus.ronwalf.phs2010.networking.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import net.volus.ronwalf.phs2010.games.core.Game;
import net.volus.ronwalf.phs2010.games.core.GamePlayer;
import net.volus.ronwalf.phs2010.games.core.HeuristicFunction;
import net.volus.ronwalf.phs2010.games.core.PlayerFactoryRegistry;
import net.volus.ronwalf.phs2010.games.core.PlayerState;
import net.volus.ronwalf.phs2010.games.core.SearchController;

public class PlayerSelector<State extends PlayerState, Action> extends JPanel {
	
	
	final Game<State, Action> game;
	
	String playerDesc;
	String playerName = null;
	String heuristicName = null;
	
	
	TitledBorder border;
	
	JButton forceButton;
	boolean stopped = false;
	
	
	public PlayerSelector(Game<State, Action> game) {
		super();
		this.game = game;
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);
		
		playerDesc = "Player Settings";
		border = BorderFactory.createTitledBorder(playerDesc);
		setBorder(border);
		
		final List<String> players = PlayerFactoryRegistry.listFactories();
		playerName = players.get(0);
		
		final JComboBox playerSelect = new JComboBox( players.toArray() );
		playerSelect.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				playerName = players.get(playerSelect.getSelectedIndex());
			}
			
		});
		add(playerSelect);
		
		
		final List<String> functions = game.heuristics();
		heuristicName = functions.get(0);
		
		final JComboBox functionSelect = new JComboBox( functions.toArray() );
		functionSelect.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				heuristicName = functions.get( functionSelect.getSelectedIndex() );
				
			}
			
		});
		add(functionSelect);
		
	}
	
	
	public GamePlayer<State, Action> getPlayer(SearchController controller) {
		HeuristicFunction<State> function = game.getHeuristic( heuristicName );
		GamePlayer<State, Action> player =
			PlayerFactoryRegistry.getFactory(playerName).createPlayer(
				game.getTransition(), function, controller);
		return player;
	}
	
}

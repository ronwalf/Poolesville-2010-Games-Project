package net.volus.ronwalf.phs2010.games.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import net.volus.ronwalf.phs2010.games.core.Game;
import net.volus.ronwalf.phs2010.games.core.GamePlayer;
import net.volus.ronwalf.phs2010.games.core.HeuristicFunction;
import net.volus.ronwalf.phs2010.games.core.PlayerFactoryRegistry;
import net.volus.ronwalf.phs2010.games.core.PlayerState;
import net.volus.ronwalf.phs2010.games.core.SearchController;

public class PlayerSelector<State extends PlayerState, Action> extends JPanel {
	
	
	final Game<State, Action> game;
	final SearchController controller;
	
	String playerName = null;
	String heuristicName = null;
	
	GamePlayer<State, Action> player;
	
	
	public PlayerSelector(Game<State, Action> game, SearchController controller) {
		this.game = game;
		this.controller = controller;
		
		final List<String> players = PlayerFactoryRegistry.listFactories();
		playerName = players.get(0);
		
		final JComboBox playerSelect = new JComboBox( players.toArray() );
		playerSelect.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				playerName = players.get(playerSelect.getSelectedIndex());
				renewPlayer();
			}
			
		});
		add(playerSelect);
		
		
		final List<String> functions = game.heuristics();
		heuristicName = functions.get(0);
		
		final JComboBox functionSelect = new JComboBox( functions.toArray() );
		functionSelect.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				heuristicName = functions.get( functionSelect.getSelectedIndex() );
				renewPlayer();
			}
			
		});
		add(functionSelect);
		
		renewPlayer();
	}
	
	
	public GamePlayer<State, Action> getPlayer() {
		return player;
	}
	
	
	private void renewPlayer() {
		HeuristicFunction<State> function = game.getHeuristic( heuristicName );
		player = PlayerFactoryRegistry.getFactory(playerName).createPlayer(
				game.getTransition(), function, controller);
	}
	
}

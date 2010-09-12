package net.volus.ronwalf.phs2010.games.gui;

import java.awt.BorderLayout;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import net.volus.ronwalf.phs2010.games.core.Game;
import net.volus.ronwalf.phs2010.games.core.PlayerState;
import net.volus.ronwalf.phs2010.games.core.SearchController;
import net.volus.ronwalf.phs2010.games.util.BoardState;

public class GameRunnerGui<CellT, State extends BoardState<CellT> & PlayerState, Action> extends JPanel {

	private JComboBox playerSelection;
	private JComboBox heuristicSelection;
	private SearchController controller;
	
	
	public GameRunnerGui(Game<State, Action> game) {
		super(new BorderLayout());
		
		
		
		StopPanel stop = new StopPanel();
		controller = stop.getController();
		add(stop, BorderLayout.SOUTH);
		
	}
	
}

package net.volus.ronwalf.phs2010.games.core.impl;

import java.util.List;
import java.util.Random;

import net.volus.ronwalf.phs2010.games.core.GamePlayer;
import net.volus.ronwalf.phs2010.games.core.GameTransition;
import net.volus.ronwalf.phs2010.games.core.HeuristicFunction;
import net.volus.ronwalf.phs2010.games.core.PlayerState;
import net.volus.ronwalf.phs2010.games.core.SearchController;

public class RandomMovePlayer<State extends PlayerState, Action>
 	implements GamePlayer<State, Action> {

	GameTransition<State, Action> transition;
	HeuristicFunction<State> function;
	SearchController controller;
	
	Random random;
	
	public RandomMovePlayer(GameTransition<State, Action> transition,
			HeuristicFunction<State> function, SearchController controller) {
		this.transition = transition;
		this.function = function;
		this.controller = controller;
		
		random = new Random();
	}

	public Action move(State s) {
		controller.start();
		List<Action> actions = transition.enumerate(s);
		
		Action move = null;
		if (!actions.isEmpty()) {
			move = actions.get( random.nextInt( actions.size() ) );
		}
		
		return move;
	}

}

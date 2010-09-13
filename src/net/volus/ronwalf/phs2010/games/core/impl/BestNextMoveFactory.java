package net.volus.ronwalf.phs2010.games.core.impl;

import net.volus.ronwalf.phs2010.games.core.GamePlayer;
import net.volus.ronwalf.phs2010.games.core.GamePlayerFactory;
import net.volus.ronwalf.phs2010.games.core.GameTransition;
import net.volus.ronwalf.phs2010.games.core.HeuristicFunction;
import net.volus.ronwalf.phs2010.games.core.PlayerFactoryRegistry;
import net.volus.ronwalf.phs2010.games.core.PlayerState;
import net.volus.ronwalf.phs2010.games.core.SearchController;

public class BestNextMoveFactory implements GamePlayerFactory {

	public final static BestNextMoveFactory instance = new BestNextMoveFactory();
	
	public <State extends PlayerState, Action> GamePlayer<State, Action> createPlayer(
			GameTransition<State, Action> transition,
			HeuristicFunction<State> function, SearchController controller) {
		return new BestNextMove<State, Action>(transition, function, controller);
	}

	public static void register() {
		PlayerFactoryRegistry.addFactory("Best Next Move", instance);
	}

}

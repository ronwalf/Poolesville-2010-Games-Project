package net.volus.ronwalf.phs2010.games.core.impl;

import net.volus.ronwalf.phs2010.games.core.GameTransition;
import net.volus.ronwalf.phs2010.games.core.PlayerState;
import net.volus.ronwalf.phs2010.games.core.StateEvaluator;
import net.volus.ronwalf.phs2010.games.core.StateEvaluatorFactory;

public class StateEvalFactoryImpl implements StateEvaluatorFactory {
	
	public static StateEvalFactoryImpl instance = new StateEvalFactoryImpl();

	public <State extends PlayerState, Action> StateEvaluator<State> create(GameTransition<State, Action> transition) {
		return new StateEvaluatorImpl<State,Action>(transition);
	}

}

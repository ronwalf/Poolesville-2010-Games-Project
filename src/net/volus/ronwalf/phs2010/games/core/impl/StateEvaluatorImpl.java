package net.volus.ronwalf.phs2010.games.core.impl;

import net.volus.ronwalf.phs2010.games.core.GameTransition;
import net.volus.ronwalf.phs2010.games.core.PlayerState;
import net.volus.ronwalf.phs2010.games.core.StateEvaluator;

public class StateEvaluatorImpl<State extends PlayerState, Action> implements
		StateEvaluator<State> {

	private final GameTransition<State, Action> transition;
	
	public StateEvaluatorImpl( GameTransition<State, Action> transition ) {
		this.transition = transition;
	}
	
	public double[] evaluate(State state) {
		// TODO Auto-generated method stub
		return transition.score(state);
	}

}

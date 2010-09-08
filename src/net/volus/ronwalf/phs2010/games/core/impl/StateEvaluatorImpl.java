package net.volus.ronwalf.phs2010.games.core.impl;

import net.volus.ronwalf.phs2010.games.core.GameTransition;
import net.volus.ronwalf.phs2010.games.core.PlayerState;
import net.volus.ronwalf.phs2010.games.core.StateEvaluator;

public class StateEvaluatorImpl<State extends PlayerState, Action> implements StateEvaluator<State> {
	
	private GameTransition<State, Action> transition;
	
	public StateEvaluatorImpl( GameTransition<State, Action> transition ) {
		this.transition = transition;
	}
	
	public double[] evaluate(State state) {
		double[] score = transition.score( state );
		if ( score != null ) {
			return score;
		}
		
		for (Action a : transition.enumerate( state )) {
			State state_a = transition.apply( state, a );
			
			double[] a_score = evaluate(state_a);
			if (score == null)
				score = a_score;
			
			if (score[state.playerTurn()] < a_score[state.playerTurn()]) {
				score = a_score;
			}
		}
		
		return score;
		
	}

}

package net.volus.ronwalf.phs2010.games.core.impl;

import net.volus.ronwalf.phs2010.games.core.GamePlayer;
import net.volus.ronwalf.phs2010.games.core.GameTransition;
import net.volus.ronwalf.phs2010.games.core.HeuristicFunction;
import net.volus.ronwalf.phs2010.games.core.PlayerState;
import net.volus.ronwalf.phs2010.games.core.SearchController;

public class BestNextMove<State extends PlayerState, Action> implements GamePlayer<State, Action> {
	
	private GameTransition<State, Action> transition;
	private HeuristicFunction<State> heuristic;
	private SearchController controller;
	
	
	public BestNextMove(GameTransition<State, Action> transition,
			HeuristicFunction<State> heuristic,
			SearchController controller) {
		this.transition = transition;
		this.heuristic = heuristic;
		this.controller = controller;
		
	}
	
	public Action move(State s) {
		controller.start();
		
		double best = Double.NEGATIVE_INFINITY;
		Action bestAction = null;
		for (Action a : transition.enumerate( s )) {
			State sa = transition.apply(s, a);
			double[] score = evaluate(sa);
			if (score == null)
				score = heuristic.score(sa);
			
			if (score[s.playerTurn()] > best) {
				best = score[s.playerTurn()];
				bestAction = a;
			}
		}
		
		controller.stop();
		return bestAction;
	}

	public double[] evaluate(State s) {
		return transition.score(s);
	}

}

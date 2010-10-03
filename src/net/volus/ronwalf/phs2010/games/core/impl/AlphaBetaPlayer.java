package net.volus.ronwalf.phs2010.games.core.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.volus.ronwalf.phs2010.games.core.GamePlayer;
import net.volus.ronwalf.phs2010.games.core.GameTransition;
import net.volus.ronwalf.phs2010.games.core.HeuristicFunction;
import net.volus.ronwalf.phs2010.games.core.PlayerState;
import net.volus.ronwalf.phs2010.games.core.SearchController;
import net.volus.ronwalf.phs2010.games.util.Pair;
import net.volus.ronwalf.phs2010.games.util.PairXComparator;

public class AlphaBetaPlayer<State extends PlayerState, Action>
 	implements GamePlayer<State, Action> {

	private final GameTransition<State, Action> transition;
	private final HeuristicFunction<State> function;
	private final SearchController controller;
	
	public AlphaBetaPlayer(GameTransition<State, Action> transition,
			HeuristicFunction<State> function, SearchController controller) {
		this.transition = transition;
		this.function = function;
		this.controller = controller;
	}

	public Action move(State s) {
		controller.start();
		// your code here
		controller.stop();
		return null;
	}
	
	// Note there's no best[] !
	public Action move(State s, int depth) {
		double[] bestFound = new double[s.playerCount()];
		Arrays.fill(bestFound, Double.NEGATIVE_INFINITY);
		//TODO
		return null;
	}
	
	public double[] evaluate(State s, int d, double[] bestFound) {
		double[] mybest = new double[bestFound.length];
		System.arraycopy(bestFound, 0, mybest, 0, bestFound.length);
		//TODO
		return null;
	}
	
	/**
	 * @param Current player's number
	 * @param Array of best individual scores the corresponding player has found.
	 * @param Heuristic or evaluator score to check
	 * @return Returns whether we can prune
	 */
	private boolean canPrune(final int turn, final double[] bestFound, final double[] score) {
		return false;
	}
	
	/**
	 * Sorts actions by the current turn's heuristic value (biggest first)
	 * @param s
	 * @param actions
	 * @return new sorted list
	 */
	private List<Action> sortActions(final State s, final List<Action> actions) {
		List<Pair<Double, Action>> paired = new ArrayList<Pair<Double,Action>>();
		for (Action a : actions) {
			State sa = transition.apply(s, a);
			double[] score = transition.score(sa);
			if (score == null)
				score = function.score(sa);
			paired.add(new Pair<Double,Action>(-score[s.playerTurn()], a));
		}
		
		Collections.sort(paired, new PairXComparator<Double, Action>());
		
		List<Action> sorted = new ArrayList<Action>();
		for (Pair<Double, Action> pair : paired) {
			sorted.add(pair.y);
		}
		
		return sorted;
	}

}

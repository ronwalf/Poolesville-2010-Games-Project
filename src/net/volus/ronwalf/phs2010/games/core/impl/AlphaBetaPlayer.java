package net.volus.ronwalf.phs2010.games.core.impl;

import java.util.Arrays;
import java.util.List;

import net.volus.ronwalf.phs2010.games.core.GamePlayer;
import net.volus.ronwalf.phs2010.games.core.GameTransition;
import net.volus.ronwalf.phs2010.games.core.HeuristicFunction;
import net.volus.ronwalf.phs2010.games.core.PlayerState;
import net.volus.ronwalf.phs2010.games.core.SearchController;

public class AlphaBetaPlayer<State extends PlayerState, Action>
 	implements GamePlayer<State, Action> {

	private final GameTransition<State, Action> transition;
	private final HeuristicFunction<State> function;
	private final SearchController controller;
	private boolean estimated;
	
	public AlphaBetaPlayer(GameTransition<State, Action> transition,
			HeuristicFunction<State> function, SearchController controller) {
		this.transition = transition;
		this.function = function;
		this.controller = controller;
	}

	public Action move(State s) {
		controller.start();

		List<Action> actions = transition.enumerate(s);

		if (actions.isEmpty()) {
			controller.stop();
			return null;
		}

		// Silly special case
		if (actions.size() == 1) {
			controller.stop();
			return actions.get(0);
		}

		int depth = 0;
		Action best = null;

		estimated = true;
		while (estimated) {
			estimated = false;
			Action dbest = move(s, depth);
			if (dbest == null)
				break;

			depth++;
			best = dbest;
		}

		controller.stop();
		return best;

	}
	
	// Note there's no best[] !
	public Action move(State s, int depth) {
		double[] bestFound = new double[s.playerCount()];
		Arrays.fill(bestFound, Double.NEGATIVE_INFINITY);

		Action bestAction = null;
		int turn = s.playerTurn();

		for (Action action : transition.enumerate(s)) {
			if (bestAction == null)
				bestAction = action;
			
			double[] aScore = evaluate(transition.apply(s, action), depth, bestFound);
			if (aScore == null)
				return null;
			
			if (bestFound[turn] < aScore[turn]) {
				bestAction = action;
				bestFound[turn] = aScore[turn];
			}
			
		}
		
		return bestAction;
	}
	
	public double[] evaluate(State s, int d, double[] bestFound) {
		double[] mybest = new double[bestFound.length];
		System.arraycopy(bestFound, 0, mybest, 0, bestFound.length);

		double[] best = transition.score(s);
		if (best != null) {
			return best;
		}

		if (controller.isStopped())
			return null;

		if (d <= 0) {
			estimated = true;
			return function.score(s);
		}

		int turn = s.playerTurn();
		for (Action a : transition.enumerate(s)) {
			double[] score = evaluate(transition.apply(s, a), d - 1, mybest);
			if (score == null)
				return null;

			if (best == null || best[turn] < score[turn]) {
				best = score;
			}
			
			if (best[turn] > bestFound[turn]) {
				boolean decreasesOthers = true;
				for (int i = 0; i < bestFound.length; i++) {
					if (i == turn)
						continue;
					decreasesOthers &= best[i] < bestFound[i];
				}
				if (decreasesOthers)
					return best;
			}
			
		}

		return best;

	}

}

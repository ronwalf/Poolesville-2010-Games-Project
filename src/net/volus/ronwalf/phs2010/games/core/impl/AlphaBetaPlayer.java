/*
Copyright (c) 2010 Ron Alford <ronwalf@volus.net>
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:
1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products
   derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
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
		Action best = actions.get(0);

		estimated = true;
		while (estimated) {
			estimated = false;
			Action dbest = move(s, depth);
			if (dbest == null)
				break;

			depth++;
			best = dbest;
		}
		
		//System.out.println("Alpha-Beta search depth: " + depth);

		controller.stop();
		return best;

	}
	
	// Note there's no best[] !
	public Action move(State s, int depth) {
		double[] bestFound = new double[s.playerCount()];
		Arrays.fill(bestFound, Double.NEGATIVE_INFINITY);

		Action bestAction = null;
		int turn = s.playerTurn();

		for (Action action : sortActions(s, transition.enumerate(s))) {
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
		for (Action a : sortActions(s, transition.enumerate(s))) {
			double[] score = evaluate(transition.apply(s, a), d - 1, mybest);
			if (score == null)
				return null;

			if (canPrune( turn, bestFound, score) )
				return score;
			
			if (best == null || best[turn] < score[turn]) {
				best = score;
			}
			
			if (best[turn] > mybest[turn]) {
				mybest[turn] = best[turn];
			}
		}

		return best;
	}
	
	/**
	 * @param Current player's number
	 * @param Array of best individual scores the corresponding player has found.
	 * @param Heuristic or evaluator score to check
	 * @return Returns whether we can prune
	 */
	private boolean canPrune(final int turn, final double[] bestFound, final double[] score) {
		boolean pruneable = bestFound[turn] < score[turn];
		for (int i = 0; i < bestFound.length; i++) {
			if (i == turn)
				continue;
			pruneable &= bestFound[i] > score[i];
		}
		return pruneable;
	}
	

	/**
	 * Sorts actions by the current turn's heuristic value (biggest first)
	 * @param s
	 * @param actions
	 * @return new sorted list
	 */
	private List<Action> sortActions(final State s, final List<Action> actions) {
		if (actions.size() <= 1)
			return actions;
		
		List<Pair<Double, Action>> paired = new ArrayList<Pair<Double,Action>>();
		for (Action a : actions) {
			State sa = transition.apply(s, a);
			double[] score = transition.score(sa);
			if (score == null)
				score = function.score(sa);
			paired.add(new Pair<Double,Action>(-score[s.playerTurn()], a));
		}
		
		Collections.sort(paired, new PairXComparator<Double, Action>());
		//System.out.println("x1: " + paired.get(0).x + " x2: " + paired.get(1).x);
		
		List<Action> sorted = new ArrayList<Action>();
		for (Pair<Double, Action> pair : paired) {
			sorted.add(pair.y);
		}
		
		return sorted;
	}

}

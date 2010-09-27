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

import java.util.List;

import net.volus.ronwalf.phs2010.games.core.GamePlayer;
import net.volus.ronwalf.phs2010.games.core.GameTransition;
import net.volus.ronwalf.phs2010.games.core.HeuristicFunction;
import net.volus.ronwalf.phs2010.games.core.PlayerState;
import net.volus.ronwalf.phs2010.games.core.SearchController;

public class MinimaxPlayer<State extends PlayerState, Action>
 	implements GamePlayer<State, Action> {

	private final GameTransition<State, Action> transition;
	private final HeuristicFunction<State> function;
	private final SearchController controller;
	private boolean estimated;
	
	public MinimaxPlayer(GameTransition<State, Action> transition,
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
		
		//System.out.println("Minimax search depth: " + depth);
	
		controller.stop();
		return best;
	}

	public Action move(State s, int depth) {
		Action best = null;
		int turn = s.playerTurn();
		double bestScore = Double.NEGATIVE_INFINITY;
		

		for (Action action : transition.enumerate(s)) {
			if (best == null)
				best = action;
			
			double[] aScore = evaluate(transition.apply(s, action), depth);
			if (aScore == null)
				return null;
			
			if (bestScore < aScore[turn]) {
				best = action;
				bestScore = aScore[turn];
			}
			
		}
		
		return best;
	}


	public double[] evaluate(State s, int d) {
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
			double[] score = evaluate( transition.apply(s, a), d - 1);
			if (score == null)
				return null;
			
			if (best == null || best[turn] < score[turn]) {
				best = score;
			}
		}

		return best;
	}
}

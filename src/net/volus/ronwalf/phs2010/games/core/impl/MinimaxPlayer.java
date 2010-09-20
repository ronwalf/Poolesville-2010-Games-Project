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
import java.util.List;

import net.volus.ronwalf.phs2010.games.core.GamePlayer;
import net.volus.ronwalf.phs2010.games.core.GameTransition;
import net.volus.ronwalf.phs2010.games.core.HeuristicFunction;
import net.volus.ronwalf.phs2010.games.core.PlayerState;
import net.volus.ronwalf.phs2010.games.core.SearchController;

public class MinimaxPlayer<State extends PlayerState, Action>
 	implements GamePlayer<State, Action> {

	GameTransition<State, Action> transition;
	HeuristicFunction<State> function;
	SearchController controller;
	
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
			// Done!
			return null;
		}
		
		List<State> states = new ArrayList<State>(actions.size());
		for (int i = 0; i < actions.size(); i++) {
			Action a = actions.get(i);
			states.add(transition.apply(s, a));
		}
		
		
		int depth = 0;
		double[][] scores = new double[actions.size()][];
		do {
			for (int i = 0; i < actions.size(); i++) { 
				if (depth > 0 && controller.isStopped())
					break;
				scores[i] = evaluate(states.get(i), depth);
			}
			depth++;
		} while (!controller.isStopped());
		
		int best = 0;
		int turn = s.playerTurn();
		for (int i = 1; i < scores.length; i++) {
			if (scores[best][turn] < scores[i][turn]) {
				best = i;
			}
		}
		
		controller.stop();
		return actions.get(best);
	}

	public Action move(State s, int depth) {
		// TODO
		return null;
	}


	public double[] evaluate(State s, int d) {
		double[] best = transition.score(s);
		if (best != null) {
			return best;
		}
		
		if (d <= 0 || controller.isStopped()) {
			return function.score(s);
		}
		
		int turn = s.playerTurn();
		for (Action a : transition.enumerate(s)) {
			double[] score = evaluate( transition.apply(s, a), d - 1);
			if (best == null || best[turn] < score[turn]) {
				best = score;
			}
		}

		return best;
	}
}

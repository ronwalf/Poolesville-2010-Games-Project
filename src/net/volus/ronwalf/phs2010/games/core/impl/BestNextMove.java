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

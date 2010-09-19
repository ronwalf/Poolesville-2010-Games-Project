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
package net.volus.ronwalf.phs2010.games.util;

import net.volus.ronwalf.phs2010.games.core.HeuristicFunction;
import net.volus.ronwalf.phs2010.games.core.PlayerState;

public class WeightedHeuristic<State extends PlayerState> implements HeuristicFunction<State> {

	private final Pair<HeuristicFunction<State>, Double>[] functions;
	
	public WeightedHeuristic(Pair<HeuristicFunction<State>, Double>... functions) {
		this.functions = functions;
	}
	
	public double[] score(State state) {
		double[] score = new double[state.playerCount()];
		for (int i = 0; i < functions.length; i++) {
			double[] i_score = functions[i].x.score(state);
			double w = functions[i].y;
			for (int p = 0; p < state.playerCount(); p++) {
				score[p] = i_score[p]*w;
			}
		}
		return score;
	}

	public static <State> Pair<HeuristicFunction<State>, Double> pair(HeuristicFunction<State> f, double w) {
		return new Pair<HeuristicFunction<State>, Double>(f, w);
	}
}

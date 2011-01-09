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
package net.volus.ronwalf.phs2010.games.checkers.heuristics;

import static net.volus.ronwalf.phs2010.games.util.WeightedHeuristic.pair;
import net.volus.ronwalf.phs2010.games.checkers.CheckersGame;
import net.volus.ronwalf.phs2010.games.checkers.CheckersState;
import net.volus.ronwalf.phs2010.games.core.HeuristicFunction;
import net.volus.ronwalf.phs2010.games.util.MovesHeuristic;
import net.volus.ronwalf.phs2010.games.util.RandomHeuristic;
import net.volus.ronwalf.phs2010.games.util.WeightedHeuristic;

public class RonsCheckersHeuristic {
	
	@SuppressWarnings("unchecked")
	public static HeuristicFunction<CheckersState> create() {
		return new WeightedHeuristic<CheckersState>(
				pair(new RandomHeuristic<CheckersState>(), 0.001),
				pair(new MovesHeuristic<CheckersState>(CheckersGame.instance.getTransition()), 0.25),
				pair(CheckersKingHeuristic.instance, 0.5),
				pair(CheckersCountHeuristic.instance, 1));
	}
	
	public static void register() {
		HeuristicFunction<CheckersState> heuristic = create();
		CheckersGame.instance.addHeuristic("Rons-Heuristic", heuristic);
	}
	
}

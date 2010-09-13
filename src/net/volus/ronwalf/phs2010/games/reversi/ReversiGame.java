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
package net.volus.ronwalf.phs2010.games.reversi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.volus.ronwalf.phs2010.games.core.Game;
import net.volus.ronwalf.phs2010.games.core.GameTransition;
import net.volus.ronwalf.phs2010.games.core.HeuristicFunction;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacMove;

public final class ReversiGame implements Game<ReversiState, TicTacMove> {

	public final static ReversiGame instance = new ReversiGame();
	
	private List<String> names = new ArrayList<String>();
	private Map<String, HeuristicFunction<ReversiState>> heuristics
		= new HashMap<String, HeuristicFunction<ReversiState>>();
	
	public void addHeuristic(String name, HeuristicFunction<ReversiState> function) {
		if (!heuristics.containsKey(name))
			names.add(name);
		heuristics.put(name, function);
	}
	
	public HeuristicFunction<ReversiState> getHeuristic(String name) {
		return heuristics.get(name);
	}

	public ReversiState getInitialState() {
		return ReversiState.othello;
	}

	public GameTransition<ReversiState, TicTacMove> getTransition() {
		return ReversiTransition.instance;
	}
	
	public List<String> heuristics() {
		return Collections.unmodifiableList(names);
	}

}

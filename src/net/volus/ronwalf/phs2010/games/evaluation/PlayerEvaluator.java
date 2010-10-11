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
package net.volus.ronwalf.phs2010.games.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.volus.ronwalf.phs2010.games.core.Game;
import net.volus.ronwalf.phs2010.games.core.GamePlayer;
import net.volus.ronwalf.phs2010.games.core.GameTransition;
import net.volus.ronwalf.phs2010.games.core.PlayerState;
import net.volus.ronwalf.phs2010.games.core.impl.RandomMoveFactory;
import net.volus.ronwalf.phs2010.games.util.DummyController;

public class PlayerEvaluator<State extends PlayerState, Action> {

	private final Game<State,Action> game;
	private final int premoves;
	private final int games;
	private final List<GamePlayer<State, Action>> players;
	
	public PlayerEvaluator(Game<State,Action> game, final int premoves, final int games) {
		this.game = game;
		this.premoves = premoves;
		this.games = games;
		players = new ArrayList<GamePlayer<State,Action>>();
	}
	
	
	public void addPlayer(GamePlayer<State, Action> player) {
		players.add(player);
	}
	
	private State premove() {
		State state = game.getInitialState();
		GameTransition<State,Action> transition = game.getTransition();
		GamePlayer<State,Action> random = RandomMoveFactory.instance.createPlayer(transition, null, DummyController.instance);
		
		for (int i = 0; i < premoves; i++) {
			state = transition.apply(state, random.move(state));
			if (transition.score(state) != null) {
				i = i/2;
				state = game.getInitialState();
			}
		}
		
		return state;
	}
	
	private int play(GamePlayer<State, Action> player0, GamePlayer<State,Action> player1) {
		GameTransition<State,Action> transition = game.getTransition();
		State state = premove();
		Action action;
		while ((action = (state.playerTurn() == 0 ? player0 : player1).move(state)) != null) {
			state = transition.apply(state, action);
		}
		if (transition.score(state) == null)
			System.out.println(state);
		double p0s = transition.score( state )[0];
		if (p0s == 0)
			return 0;
		else if (p0s > 0)
			return 1;
		return -1;
	}
	
	
	public int play(GamePlayer<State,Action> player) {
		
		int total = 0;
		Random r = new Random();
		for (int i = 0; i < games; i++) {
			total += play(player, players.get(r.nextInt(players.size())));
		}
		
		return total;
	}
	
}

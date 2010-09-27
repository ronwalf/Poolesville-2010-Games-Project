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

import static net.volus.ronwalf.phs2010.games.evaluation.EvaluateProperties.game;
import static net.volus.ronwalf.phs2010.games.evaluation.EvaluateProperties.gameCount;
import static net.volus.ronwalf.phs2010.games.evaluation.EvaluateProperties.heuristic;
import static net.volus.ronwalf.phs2010.games.evaluation.EvaluateProperties.moveTime;
import static net.volus.ronwalf.phs2010.games.evaluation.EvaluateProperties.player;
import static net.volus.ronwalf.phs2010.games.evaluation.EvaluateProperties.preMoves;

import java.util.Arrays;

import net.volus.ronwalf.phs2010.games.core.Game;
import net.volus.ronwalf.phs2010.games.core.GamePlayer;
import net.volus.ronwalf.phs2010.games.core.GameTransition;
import net.volus.ronwalf.phs2010.games.core.HeuristicFunction;
import net.volus.ronwalf.phs2010.games.core.PlayerState;
import net.volus.ronwalf.phs2010.games.core.SearchController;
import net.volus.ronwalf.phs2010.games.core.impl.RandomMoveFactory;

/**
 * Evaluate any combination of games/players/heuristics
 * @author ronwalf
 *
 */
@SuppressWarnings("unchecked")
public class Evaluate {
	
	private static SearchController controller = new SearchController() {

		private final long moveTime = moveTime();
		private long endTime;
		
		public boolean isStopped() {
			return System.currentTimeMillis() > endTime;
		}

		public void start() {
			endTime = System.currentTimeMillis() + moveTime;
		}

		public void stop() {}
		
	};
	
	@SuppressWarnings("rawtypes")
	private static PlayerState premove(Game game) {
		int premoves = preMoves();
		GameTransition transition = game.getTransition();
		GamePlayer random = RandomMoveFactory.instance.createPlayer(transition, null, controller);
		PlayerState state = game.getInitialState();
		
		for (int i = 0; i < premoves; i++) {
			state = transition.apply(state, random.move(state));
		}
		
		return state;
	}

	@SuppressWarnings("rawtypes")
	private static double[] play(Game game, GamePlayer[] players) {
	
		GameTransition transition = game.getTransition();
		PlayerState state = premove(game);
		Object action;
		while ((action = players[state.playerTurn()].move(state)) != null) {
			state = transition.apply(state, action);
		}
		
		
		return transition.score(state);
	}
	
	private static boolean isMax(int turn, double[] score) {
		for (int i = 0; i < score.length; i++) {
			if (i != turn && score[i] > score[turn])
				return false;
		}
		return true;
	}
	
	@SuppressWarnings("rawtypes")
	public static void main(String... args) {
		
		Game game = game();
		int gameCount = gameCount();
		GamePlayer[] players = new GamePlayer[game.getInitialState().playerCount()];
		HeuristicFunction[] heuristics = new HeuristicFunction[players.length];
		
		for (int p = 0; p < players.length; p++) {
			heuristics[p] = heuristic(p);
			players[p] = player(p).createPlayer(game.getTransition(), heuristics[p], controller);
			
		}
		
		double[] total = new double[players.length];
		Arrays.fill(total, 0);
		
		for (int g = 0; g < gameCount; g++) {
			double[] score = play(game, players);
			for (int s = 0; s < score.length; s++) {
				if (isMax(s, score)) 
					total[s]++;
			}
		}
		
		for (int p = 0; p < players.length; p++) {
			System.out.print(""+(total[p]/gameCount) + " ");
		}
		System.out.println();
		
	}
	
}

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

import java.util.Arrays;

import net.volus.ronwalf.phs2010.games.core.GamePlayer;
import net.volus.ronwalf.phs2010.games.core.HeuristicFunction;
import net.volus.ronwalf.phs2010.games.core.impl.BestNextMoveFactory;
import net.volus.ronwalf.phs2010.games.core.impl.RandomMoveFactory;
import net.volus.ronwalf.phs2010.games.reversi.ReversiGame;
import net.volus.ronwalf.phs2010.games.reversi.ReversiState;
import net.volus.ronwalf.phs2010.games.reversi.ReversiTransition;
import net.volus.ronwalf.phs2010.games.reversi.heuristics.KevinHarrison2Heuristic;
import net.volus.ronwalf.phs2010.games.reversi.heuristics.ReversiCountHeuristic;
import net.volus.ronwalf.phs2010.games.reversi.heuristics.ReversiSafeHeuristic;
import net.volus.ronwalf.phs2010.games.reversi.heuristics.ReversiWeightedHeuristic;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacMove;
import net.volus.ronwalf.phs2010.games.util.SimpleController;

public class ReversiWeightFinder {

	private final static int PREMOVES;
	private final static int GAMES;
	private final static long MOVETIME;
	private final static double STEP;
	private final static double DELTA;
	private final static PlayerEvaluator<ReversiState, TicTacMove> evaluator;
	
	static {
		PREMOVES = 20;
		GAMES = 100;
		MOVETIME = 50;
		evaluator = new PlayerEvaluator<ReversiState, TicTacMove>(ReversiGame.instance, PREMOVES, GAMES);
		evaluator.addPlayer(player(KevinHarrison2Heuristic.instance));
		evaluator.addPlayer(player(ReversiCountHeuristic.instance));
		evaluator.addPlayer(player(ReversiSafeHeuristic.instance));
		evaluator.addPlayer(RandomMoveFactory.instance.createPlayer(ReversiTransition.instance, null, new SimpleController(MOVETIME)));
		
		STEP = 32;
		DELTA = 1;
	}
	
	
	private static GamePlayer<ReversiState, TicTacMove> player(HeuristicFunction<ReversiState> heuristic) {
		return BestNextMoveFactory.instance.createPlayer(
				ReversiTransition.instance,heuristic, new SimpleController(MOVETIME));
	}
	
	public static int evaluate(double[] vertex) { 
		
		System.out.println(Arrays.toString(vertex));
		HeuristicFunction<ReversiState> heuristic = ReversiWeightedHeuristic.create(vertex[0], vertex[1], vertex[2], vertex[3], vertex[4]);
		int score = evaluator.play(player(heuristic));
		System.out.println("Score: " + score);
		return score;
	}
	
	private static int optimizeWeight( int index, double[] weights ) {
		
		double step = STEP;
		int score = evaluate(weights);
		int maxscore = score;
		
		while (step > DELTA) {
			double oldW = weights[index];

			weights[index] += step;
			
			score = evaluate(weights);
			if ( score > maxscore ) {
				maxscore = score;
			} else {
				weights[index] = oldW - step;
				score = evaluate(weights);
				if (score > maxscore ) {
					maxscore = score;
				} else {
					weights[index] = oldW;
				}
			}
			
			step /= 2;
			
		}
		return maxscore;
	}
	
	
	public static void main(String args[]) {
		double[] weights = new double[]{50,20,10,1,5};
		
		for (int repeats = 0; repeats < 30; repeats++) {
			for (int index = 0; index < weights.length; index++) {
				double score = optimizeWeight(index, weights);
				System.out.println("Current best (" + score + "): " + Arrays.toString(weights));
			}
			
		}
			
		
	}
	
}

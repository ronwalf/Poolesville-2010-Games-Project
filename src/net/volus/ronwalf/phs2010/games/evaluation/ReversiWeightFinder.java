package net.volus.ronwalf.phs2010.games.evaluation;

import java.util.Arrays;

import net.volus.ronwalf.phs2010.games.core.GamePlayer;
import net.volus.ronwalf.phs2010.games.core.HeuristicFunction;
import net.volus.ronwalf.phs2010.games.core.impl.AlphaBetaFactory;
import net.volus.ronwalf.phs2010.games.reversi.ReversiGame;
import net.volus.ronwalf.phs2010.games.reversi.ReversiState;
import net.volus.ronwalf.phs2010.games.reversi.ReversiTransition;
import net.volus.ronwalf.phs2010.games.reversi.heuristics.KevinHarrison2Heuristic;
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
		PREMOVES = 3;
		GAMES = 10;
		MOVETIME = 50;
		evaluator = new PlayerEvaluator<ReversiState, TicTacMove>(ReversiGame.instance, PREMOVES, GAMES);
		evaluator.addPlayer(player(KevinHarrison2Heuristic.instance));
		
		STEP = 50;
		DELTA = 1;
	}
	
	
	private static GamePlayer<ReversiState, TicTacMove> player(HeuristicFunction<ReversiState> heuristic) {
		return AlphaBetaFactory.instance.createPlayer(
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
		double[] weights = new double[]{0,0,10,1,5};
		
		for (int repeats = 0; repeats < 30; repeats++) {
			for (int index = 0; index < weights.length; index++) {
				double score = optimizeWeight(index, weights);
				System.out.println("Current best (" + score + "): " + Arrays.toString(weights));
			}
			
		}
			
		
	}
	
}

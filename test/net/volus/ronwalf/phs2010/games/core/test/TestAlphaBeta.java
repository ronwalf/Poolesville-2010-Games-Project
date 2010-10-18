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
package net.volus.ronwalf.phs2010.games.core.test;

import static net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell.O;
import static net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell.X;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.volus.ronwalf.phs2010.games.core.HeuristicFunction;
import net.volus.ronwalf.phs2010.games.core.SearchController;
import net.volus.ronwalf.phs2010.games.core.impl.AlphaBetaFactory;
import net.volus.ronwalf.phs2010.games.core.impl.AlphaBetaPlayer;
import net.volus.ronwalf.phs2010.games.reversi.ReversiGame;
import net.volus.ronwalf.phs2010.games.reversi.ReversiState;
import net.volus.ronwalf.phs2010.games.reversi.heuristics.ReversiDistanceHeuristic;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacMove;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacState;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacTransition;
import net.volus.ronwalf.phs2010.games.util.Board;

import org.junit.Test;

public class TestAlphaBeta {

	@Test
	public void stopsQuickly() {
		TimedController controller = new TimedController(1000);
		AlphaBetaPlayer<ReversiState, TicTacMove> player = 
			AlphaBetaFactory.instance.createPlayer(ReversiGame.instance.getTransition(), 
					ReversiDistanceHeuristic.instance,
					controller);
		
		long start = System.currentTimeMillis();
		controller.start();
		player.evaluate(ReversiGame.instance.getInitialState(), 5,
				new double[]{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY} );
		controller.stop();
		long elapsed = System.currentTimeMillis() - start;
		
		assertTrue("Too much time elapsed!", elapsed < 1500);
	}
	
	@Test
	public void moveEndStateNull() {
		SearchController controller = new UntimedController();
		AlphaBetaPlayer<ReversiState, TicTacMove> player = 
			AlphaBetaFactory.instance.createPlayer(ReversiGame.instance.getTransition(), 
					ReversiDistanceHeuristic.instance,
					controller);
		
		ReversiState end = new ReversiState(0, new Board<TicTacCell>(2,
				X,X,X,X));
		
		controller.start();
		TicTacMove move = player.move(end);
		controller.stop();
		
		assertNull(move);
	}
	
	@Test
	public void scoresEndState1() {
		SearchController controller = new UntimedController();
		AlphaBetaPlayer<ReversiState, TicTacMove> player = 
			AlphaBetaFactory.instance.createPlayer(ReversiGame.instance.getTransition(), 
					ReversiDistanceHeuristic.instance,
					controller);
		
		ReversiState end = new ReversiState(0, new Board<TicTacCell>(2,
				X,X,X,X));
		
		controller.start();
		double[] pscore = player.evaluate(end, 0,
				new double[]{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY} );
		controller.stop();
		
		double[] expected = ReversiGame.instance.getTransition().score(end);
		assertArrayEquals(expected, pscore, 0.01);
	}
	
	@Test
	public void scoresEndState2() {
		SearchController controller = new UntimedController();
		AlphaBetaPlayer<ReversiState, TicTacMove> player = 
			AlphaBetaFactory.instance.createPlayer(ReversiGame.instance.getTransition(), 
					ReversiDistanceHeuristic.instance,
					controller);
		
		ReversiState end = new ReversiState(0, new Board<TicTacCell>(2,
				O,O,O,O));
		
		controller.start();
		double[] pscore = player.evaluate(end, 0,
				new double[]{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY} );
		controller.stop();
		
		double[] expected = ReversiGame.instance.getTransition().score(end);
		assertArrayEquals(expected, pscore, 0.01);
	}
	
	@Test
	public void scoresAlmostEndState1() {
		SearchController controller = new UntimedController();
		AlphaBetaPlayer<ReversiState, TicTacMove> player = 
			AlphaBetaFactory.instance.createPlayer(ReversiGame.instance.getTransition(), 
					ReversiDistanceHeuristic.instance,
					controller);
		
		ReversiState state1 = new ReversiState(1, new Board<TicTacCell>(3,
				X,X,X,
				O,X,null,
				X,X,null));
		ReversiState state2 = new ReversiState(0, new Board<TicTacCell>(3,
				X,X,X,
				O,O,O,
				X,X,null));
		
		
		controller.start();
		double[] pscore = player.evaluate(state1, 1,
				new double[]{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY} );
		controller.stop();
		
		double[] expected = ReversiDistanceHeuristic.instance.score(state2);
		assertArrayEquals(expected, pscore, 0.01);
	}
	
	@Test
	public void scoresAlmostEndState2() {
		SearchController controller = new UntimedController();
		AlphaBetaPlayer<ReversiState, TicTacMove> player = 
			AlphaBetaFactory.instance.createPlayer(ReversiGame.instance.getTransition(), 
					ReversiDistanceHeuristic.instance,
					controller);
		
		ReversiState state1 = new ReversiState(0, new Board<TicTacCell>(3,
				O,O,O,
				X,O,null,
				O,O,null));
		ReversiState state2 = new ReversiState(1, new Board<TicTacCell>(3,
				O,O,O,
				X,X,X,
				O,O,null));

		
		controller.start();
		double[] pscore = player.evaluate(state1, 1,
				new double[]{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY} );
		controller.stop();
		
		double[] expected = ReversiDistanceHeuristic.instance.score(state2);
		assertArrayEquals(expected, pscore, 0.01);
	}
	
	@Test
	public void scoresAlmostEndState3() {
		SearchController controller = new UntimedController();
		AlphaBetaPlayer<ReversiState, TicTacMove> player = 
			AlphaBetaFactory.instance.createPlayer(ReversiGame.instance.getTransition(), 
					ReversiDistanceHeuristic.instance,
					controller);
		
		ReversiState state = new ReversiState(1, new Board<TicTacCell>(3,
				X,X,X,
				O,X,null,
				X,X,null));
		ReversiState end = new ReversiState(0, new Board<TicTacCell>(3,
				X,X,X,
				O,O,X,
				X,X,X));
		
		
		controller.start();
		double[] pscore = player.evaluate(state, 2,
				new double[]{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY} );
		controller.stop();
		
		double[] expected = ReversiGame.instance.getTransition().score(end);
		assertArrayEquals(expected, pscore, 0.01);
	}
	
	@Test
	public void scoresAlmostEndState4() {
		SearchController controller = new UntimedController();
		AlphaBetaPlayer<ReversiState, TicTacMove> player = 
			AlphaBetaFactory.instance.createPlayer(ReversiGame.instance.getTransition(), 
					ReversiDistanceHeuristic.instance,
					controller);
		
		ReversiState state = new ReversiState(0, new Board<TicTacCell>(3,
				O,O,O,
				X,O,null,
				O,O,null));
		ReversiState end = new ReversiState(1, new Board<TicTacCell>(3,
				O,O,O,
				X,X,O,
				O,O,O));

		
		controller.start();
		double[] pscore = player.evaluate(state, 2,
				new double[]{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY} );
		controller.stop();
		
		double[] expected = ReversiGame.instance.getTransition().score(end);
		assertArrayEquals(expected, pscore, 0.01);
	}
	
	@Test
	public void ticTacBadHeuristic1() {
		SearchController controller = new UntimedController();
		AlphaBetaPlayer<TicTacState, TicTacMove> player = 
			AlphaBetaFactory.instance.createPlayer(TicTacTransition.instance, 
					new PWinsHeuristic<TicTacState>(0),
					controller);
		
		for (int d = 0; d < 5; d++) {
			double[] pscore = player.evaluate(TicTacState.STANDARD_GAME, d,
					new double[]{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY} );
			assertArrayEquals("Wrong evaluation at depth " + d,
					new double[]{1,-1}, pscore, 0.001);
		}
		double[] pscore = player.evaluate(TicTacState.STANDARD_GAME, 9,
				new double[]{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY} );
		assertArrayEquals("Should tie at d=9", new double[]{0,0}, pscore, 0.001);
	}
	
	@Test
	public void ticTacBadHeuristic2() {
		SearchController controller = new UntimedController();
		AlphaBetaPlayer<TicTacState, TicTacMove> player = 
			AlphaBetaFactory.instance.createPlayer(TicTacTransition.instance, 
					new PWinsHeuristic<TicTacState>(1),
					controller);
		
		for (int d = 0; d < 5; d++) {
			double[] pscore = player.evaluate(TicTacState.STANDARD_GAME, d,
					new double[]{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY} );
			assertArrayEquals("Wrong evaluation at depth " + d,
					new double[]{-1,1}, pscore, 0.001);
		}
		double[] pscore = player.evaluate(TicTacState.STANDARD_GAME, 9,
				new double[]{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY} );
		assertArrayEquals("Should tie at d=9", new double[]{0,0}, pscore, 0.001);
	}
	
	@Test
	public void replace() {
		SearchController controller = new UntimedController();
		HeuristicFunction<ReversiState> worst
			= new MagnifyHeuristic<ReversiState>(ReversiDistanceHeuristic.instance, -5.0);
		HeuristicFunction<ReversiState> bad 
			= new BoardSwitchHeuristic<ReversiState>(
					worst,
					ReversiDistanceHeuristic.instance, 7);
					
		AlphaBetaPlayer<ReversiState, TicTacMove> worstplayer = 
			AlphaBetaFactory.instance.createPlayer(ReversiGame.instance.getTransition(), 
					worst, 
					controller);
		AlphaBetaPlayer<ReversiState, TicTacMove> badplayer = 
			AlphaBetaFactory.instance.createPlayer(ReversiGame.instance.getTransition(), 
					bad,
					controller);
		AlphaBetaPlayer<ReversiState, TicTacMove> player = 
			AlphaBetaFactory.instance.createPlayer(ReversiGame.instance.getTransition(), 
					ReversiDistanceHeuristic.instance,
					controller);
		
		controller.start();
		TicTacMove worst3 = worstplayer.move(ReversiGame.instance.getInitialState(), 3);
		controller.stop();
		
		controller.start();
		TicTacMove bad3 = badplayer.move(ReversiGame.instance.getInitialState(), 3);
		controller.stop();
		
		controller.start();
		TicTacMove player3 = player.move(ReversiGame.instance.getInitialState(), 3);
		controller.stop();
		
		assertEquals( worst3, bad3 );
		assertNotSame( bad3, player3);
		
		controller.start();
		TicTacMove worst4 = worstplayer.move(ReversiGame.instance.getInitialState(), 4);
		controller.stop();
		
		controller.start();
		TicTacMove bad4 = badplayer.move(ReversiGame.instance.getInitialState(), 4);
		controller.stop();
		
		controller.start();
		TicTacMove player4 = player.move(ReversiGame.instance.getInitialState(), 4);
		controller.stop();
		
		assertNotSame( worst4, bad4 );
		assertEquals( bad4, player4);
		
		
	}
	
}

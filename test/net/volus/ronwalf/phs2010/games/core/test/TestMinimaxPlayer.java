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
import static org.junit.Assert.assertTrue;
import net.volus.ronwalf.phs2010.games.core.SearchController;
import net.volus.ronwalf.phs2010.games.core.impl.MinimaxFactory;
import net.volus.ronwalf.phs2010.games.core.impl.MinimaxPlayer;
import net.volus.ronwalf.phs2010.games.reversi.ReversiCountHeuristic;
import net.volus.ronwalf.phs2010.games.reversi.ReversiGame;
import net.volus.ronwalf.phs2010.games.reversi.ReversiState;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacCell;
import net.volus.ronwalf.phs2010.games.tictactoe.TicTacMove;
import net.volus.ronwalf.phs2010.games.util.Board;

import org.junit.Test;

public class TestMinimaxPlayer {

	@Test
	public void stopsQuickly() {
		TimedController controller = new TimedController(1000);
		MinimaxPlayer<ReversiState, TicTacMove> player = 
			MinimaxFactory.instance.createPlayer(ReversiGame.instance.getTransition(), 
					ReversiCountHeuristic.instance,
					controller);
		
		long start = System.currentTimeMillis();
		controller.start();
		player.evaluate(ReversiGame.instance.getInitialState(), 5);
		controller.stop();
		long elapsed = System.currentTimeMillis() - start;
		
		assertTrue("Too much time elapsed!", elapsed < 1500);
	}
	
	@Test
	public void scoresEndState1() {
		SearchController controller = new UntimedController();
		MinimaxPlayer<ReversiState, TicTacMove> player = 
			MinimaxFactory.instance.createPlayer(ReversiGame.instance.getTransition(), 
					ReversiCountHeuristic.instance,
					controller);
		
		ReversiState end = new ReversiState(0, new Board<TicTacCell>(2,
				X,X,X,X));
		
		controller.start();
		double[] pscore = player.evaluate(end, 0);
		controller.stop();
		
		double[] expected = ReversiGame.instance.getTransition().score(end);
		assertArrayEquals(expected, pscore, 0.01);
	}
	
	@Test
	public void scoresEndState2() {
		SearchController controller = new UntimedController();
		MinimaxPlayer<ReversiState, TicTacMove> player = 
			MinimaxFactory.instance.createPlayer(ReversiGame.instance.getTransition(), 
					ReversiCountHeuristic.instance,
					controller);
		
		ReversiState end = new ReversiState(0, new Board<TicTacCell>(2,
				O,O,O,O));
		
		controller.start();
		double[] pscore = player.evaluate(end, 0);
		controller.stop();
		
		double[] expected = ReversiGame.instance.getTransition().score(end);
		assertArrayEquals(expected, pscore, 0.01);
	}
	
	
	@Test
	public void scoresAlmostEndState1() {
		SearchController controller = new UntimedController();
		MinimaxPlayer<ReversiState, TicTacMove> player = 
			MinimaxFactory.instance.createPlayer(ReversiGame.instance.getTransition(), 
					ReversiCountHeuristic.instance,
					controller);
		
		ReversiState end = new ReversiState(0, new Board<TicTacCell>(3,
				X,X,X,
				O,O,X,
				X,X,X));
		ReversiState state = new ReversiState(1, new Board<TicTacCell>(3,
				X,X,X,
				O,X,null,
				X,X,null));
		
		controller.start();
		double[] pscore = player.evaluate(state, 0);
		controller.stop();
		
		double[] expected = ReversiGame.instance.getTransition().score(end);
		assertArrayEquals(expected, pscore, 0.01);
	}
	
	@Test
	public void scoresAlmostEndState2() {
		SearchController controller = new UntimedController();
		MinimaxPlayer<ReversiState, TicTacMove> player = 
			MinimaxFactory.instance.createPlayer(ReversiGame.instance.getTransition(), 
					ReversiCountHeuristic.instance,
					controller);
		
		ReversiState end = new ReversiState(1, new Board<TicTacCell>(3,
				O,O,O,
				X,X,O,
				O,O,O));
		ReversiState state = new ReversiState(1, new Board<TicTacCell>(3,
				O,O,O,
				X,O,null,
				O,O,null));
		
		controller.start();
		double[] pscore = player.evaluate(state, 2);
		controller.stop();
		
		double[] expected = ReversiGame.instance.getTransition().score(end);
		assertArrayEquals(expected, pscore, 0.01);
	}
	
}

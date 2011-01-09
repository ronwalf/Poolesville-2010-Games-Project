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
package net.volus.ronwalf.phs2010.networking.server;

import java.util.Arrays;

import net.volus.ronwalf.phs2010.games.core.Game;
import net.volus.ronwalf.phs2010.games.core.PlayerState;
import net.volus.ronwalf.phs2010.networking.message.Ack;
import net.volus.ronwalf.phs2010.networking.message.GameMove;
import net.volus.ronwalf.phs2010.networking.message.GameResult;
import net.volus.ronwalf.phs2010.networking.message.GameState;
import net.volus.ronwalf.phs2010.networking.message.MessageFactory;

public class NetworkGame implements GameClientListener {

	private static int gameCount = 0;
	
	private final GameServer server;
	private final String type;
	@SuppressWarnings("rawtypes")
	private final Game game;
	private PlayerState state;
	private final String name;
	private GameClient[] players;
	private long timeLimit = 1000;
	
	
	public NetworkGame(GameServer server, String type, @SuppressWarnings("rawtypes") Game game) {
		this.server = server;
		this.type = type;
		this.game = game;
		this.name = "game" + (gameCount++);
		state = game.getInitialState();
		players = new GameClient[state.playerCount()];
	}
	
	public String getName() { return name; }
	
	public boolean join(GameClient client) {
		for (int i = 0; i < players.length; i++) {
			if (players[i] == null) {
				players[i] = client;
				client.addListener(this);
				
				if (i < players.length - 1)
					return false;
			}
			
		}
		state = game.getInitialState();
		sendState();
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public void move(GameClient client, GameMove gmove) {
		
		int turn = state.playerTurn();
		if (players[turn] == null || !players[turn].equals(client)) {
			Ack reply = MessageFactory.instance.reply(gmove, 500);
			reply.setMessage("Move out of turn!");
			client.send(reply);
			return;
		}
		
		for (Object pmove : game.getTransition().enumerate(state)) {
			if (pmove.toString().trim().equals(gmove.getMove().trim())) {
				state = game.getTransition().apply(state, pmove);
				Ack reply = MessageFactory.instance.reply(gmove, 200);
				reply.setMessage("Move accepted");
				client.send(reply);
				

				sendState();
				
				double[] score = game.getTransition().score(state);
				if (score != null) {
					String msg = scoreMessage(score);
					for (GameClient player : players) {
						GameResult result = MessageFactory.instance.gameResult(name, msg);
						player.send(result);
					}
					
					state = null;
					server.gameFinished(this);
					return;
				}
				
				return;
			}
		}
		
	}

	private void sendState() {
		for ( GameClient client : players ) {
			GameState gstate =  MessageFactory.instance.gameState(type, name, 
					players[state.playerTurn()].getName(), timeLimit, state.toString());
			client.send(gstate);
		}
	}
	
	public void gameClientDisconnect(GameClient client) {
		int offender = -1;
		for (int i = 0; i < players.length; i++) {
			if (players[i] != null && players[i].equals(client)) {
				offender = i;
			}
		}
		
		if (offender >= 0 && state != null) {
//			System.out.println("Default!");
			int badscore = -(players.length - 1);
			double[] score = new double[players.length];
			for (int i = 0; i < score.length; i++) {
				if (i != offender) 
					score[i] = 1;
				else
					score[i] = badscore;
				
			}
//			System.out.println(Arrays.toString(score));
			String msg = scoreMessage(score);
			for (GameClient player : players) {
				GameResult result = MessageFactory.instance.gameResult(name, msg);
				player.send(result);
			}
			state = null;
			server.gameFinished(this);
		} else if (offender >= 0) {
			players[offender] = null;
		}
	}
	
	private String scoreMessage(double[] score) {
		double max = score[0];
		int maxi = 0;
		
		boolean tie = true;
		for (int i=1; i < players.length; i++) {
			if (max < score[i]) {
				tie = false;
				max = score[i];
				maxi = i;
			} else if (max > score[i]) {
				tie = false;
			}
			
		}

		if (tie)
			return "Tie!";
		return players[maxi].getName() + " wins!";
	}

}

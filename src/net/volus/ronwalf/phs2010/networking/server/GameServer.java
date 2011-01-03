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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.volus.ronwalf.phs2010.games.checkers.CheckersGame;
import net.volus.ronwalf.phs2010.games.core.Game;
import net.volus.ronwalf.phs2010.games.util.Pair;
import net.volus.ronwalf.phs2010.networking.message.Ack;
import net.volus.ronwalf.phs2010.networking.message.GameMove;
import net.volus.ronwalf.phs2010.networking.message.Login;
import net.volus.ronwalf.phs2010.networking.message.MessageFactory;
import net.volus.ronwalf.phs2010.networking.message.MessageVisitor;
import net.volus.ronwalf.phs2010.networking.message.StartGame;
import net.volus.ronwalf.phs2010.networking.message.Users;
import net.volus.ronwalf.phs2010.networking.raw.RawMessageCodecFactory;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.logging.MdcInjectionFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class GameServer {
	
	@SuppressWarnings("rawtypes")
	public final static Map<String, Game> gameTypes = new HashMap<String, Game>();
	
	static {
		gameTypes.put("checkers", CheckersGame.instance);
	}
	
	public static void main(String... args) throws IOException {
		int localPort = Integer.parseInt(args[0]);
		
		IoFilter LOGGING_FILTER = new LoggingFilter();

		IoAcceptor acceptor = new NioSocketAcceptor();
		acceptor.getFilterChain().addLast("mdc", new MdcInjectionFilter());
		
		acceptor.getFilterChain().addLast(
				"codec",
				new ProtocolCodecFilter( new RawMessageCodecFactory() ));
		acceptor.getFilterChain().addLast("logger", LOGGING_FILTER);
		
		acceptor.setHandler(new GameServerHandler(new GameServer()));
		acceptor.bind( new InetSocketAddress( localPort ) );
	}

	
	private Map<IoSession, GameClient> sessions;
	private List<Pair<GameClient, StartGame>> waiting;
	private Map<String, NetworkGame> games;

	
	
	public GameServer() {
		sessions = new HashMap<IoSession, GameClient>();
		games = new HashMap<String, NetworkGame>();
		waiting = new ArrayList<Pair<GameClient, StartGame>>();
	}
	
	
	public MessageVisitor getClientHandler(IoSession session) {
		if (sessions.containsKey(session)) {
			return sessions.get(session);
		}
		return new NewClientHandler(this, session);
	}


	public void gameFinished(NetworkGame game) {
		games.remove(game.getName());
	}

	public void login(Login login, IoSession session) {
		String user = login.getUser();
		for (GameClient client : sessions.values()) {
			if (client.getName().equals(user)) {
				Ack ack = MessageFactory.instance.reply(login, 500);
				ack.setMessage("Already logged in: " + client);
				session.write(ack.getRawMessage());
				session.close(true);
				return;
			}
		}
		
		sessions.put(session, new GameClient(this, session, user));
		Ack ack = MessageFactory.instance.reply(login, 200);
		ack.setMessage("Logged in as: " + sessions.get(session));
		session.write(ack.getRawMessage());
		updateUsers();
	}
	
	
	public void logout(IoSession session) {
		if (sessions.containsKey(session)) {
			GameClient client = sessions.get(session);
			sessions.remove(session);
			client.logout();
			for (Iterator<Pair<GameClient,StartGame>> iter = waiting.iterator(); iter.hasNext();) {
				GameClient waitingClient = iter.next().x;
				if (waitingClient.equals(client))
					iter.remove();
			}
			updateUsers();
		}
		
		
		
		
	}
	
	public void move(GameClient client, GameMove move) {
		NetworkGame game = games.get(move.getName());
		
		if (game == null) {
			Ack reply = MessageFactory.instance.reply(move, 500);
			reply.setMessage("No such game exists");
			client.send(reply);
		}
		
		game.move(client, move);
		
	}
	
	public void startGame(GameClient client, StartGame start) {
		@SuppressWarnings("rawtypes")
		Game game = gameTypes.get(start.getType());
		if (game == null) {
			Ack ack = MessageFactory.instance.reply(start, 500);
			ack.setMessage("Game not available: " + start.getType());
			client.send(ack);
			return;
		}
		
		
		List<Pair<GameClient,StartGame>> players = new ArrayList<Pair<GameClient,StartGame>>();
		for (Pair<GameClient, StartGame> pair : waiting) {
			if ((start.opponents().isEmpty() || 
					start.opponents().contains(pair.x.getName()))
					&& (pair.y.opponents().isEmpty() || 
							pair.y.opponents().contains(client.getName()))) {
				players.add(pair);
				
			}
		}
		
		Ack ack = MessageFactory.instance.reply(start, 200);
		if (players.size() + 1 >= game.getInitialState().playerCount()) {
			ack.setMessage("Starting game now");
			client.send(ack);
			NetworkGame ngame = new NetworkGame(this, start.getType(), game);
			games.put(ngame.getName(), ngame);
			if (ngame.join(client))
				return;
			for (Pair<GameClient, StartGame> pair : players) {
				waiting.remove(pair);
				if (ngame.join(pair.x)) {
					return;
				}
			}
			
		}
		else {
			ack.setMessage("Waiting for match");
			client.send(ack);
			waiting.add(new Pair<GameClient,StartGame>(client,start));
		}

		
	}
	
	private void updateUsers() {
		ArrayList<String> usernames = new ArrayList<String>();
		for (GameClient client : sessions.values()) {
			usernames.add(client.getName());
		}
		Users users = MessageFactory.instance.users(usernames);
		for (GameClient client : sessions.values()) {
			client.send(users);
		}
	}
}

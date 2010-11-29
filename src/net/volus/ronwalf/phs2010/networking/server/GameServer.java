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
import java.util.HashMap;
import java.util.Map;

import net.volus.ronwalf.phs2010.games.checkers.CheckersGame;
import net.volus.ronwalf.phs2010.games.core.Game;
import net.volus.ronwalf.phs2010.networking.message.Ack;
import net.volus.ronwalf.phs2010.networking.message.GameMove;
import net.volus.ronwalf.phs2010.networking.message.MessageFactory;
import net.volus.ronwalf.phs2010.networking.message.MessageVisitor;
import net.volus.ronwalf.phs2010.networking.message.StartGame;
import net.volus.ronwalf.phs2010.networking.raw.RawMessageCodecFactory;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.logging.MdcInjectionFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class GameServer {
	
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
	private NetworkGame incompleteGame;
	private Map<String, NetworkGame> games;

	
	
	public GameServer() {
		sessions = new HashMap<IoSession, GameClient>();
		games = new HashMap<String, NetworkGame>();
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

	public void login(String user, IoSession session) {
		sessions.put(session, new GameClient(this, session, user));
	}
	
	
	public void logout(IoSession session) {
		if (sessions.containsKey(session)) {
			GameClient client = sessions.get(session);
			client.logout();
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
		Game game = gameTypes.get(start.getType());
		
		// TODO correct matching for games start types.
		
		if (incompleteGame == null) {
			incompleteGame = new NetworkGame(this, start.getType(), game);
		}
		
		boolean complete = incompleteGame.join(client);
		if (complete) {
			games.put(incompleteGame.getName(), incompleteGame);
			incompleteGame = null;
		}
	}
}

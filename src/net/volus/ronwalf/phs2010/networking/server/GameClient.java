package net.volus.ronwalf.phs2010.networking.server;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import net.volus.ronwalf.phs2010.networking.message.BaseMessage;
import net.volus.ronwalf.phs2010.networking.message.GameMove;
import net.volus.ronwalf.phs2010.networking.message.MessageVisitorAdapter;
import net.volus.ronwalf.phs2010.networking.message.StartGame;

import org.apache.mina.core.session.IoSession;

public class GameClient extends MessageVisitorAdapter {

	private final GameServer server;

	private final IoSession session;

	private final String username;

	private final List<WeakReference<GameClientListener>> listeners;

	public GameClient(GameServer server, IoSession session, String username) {
		this.server = server;
		this.session = session;
		this.username = username;
		listeners = new ArrayList<WeakReference<GameClientListener>>();
	}

	public void addListener(GameClientListener listener) {

		listeners.add(new WeakReference<GameClientListener>(listener));
	}
		
	public String getName() { return username; }
	
	public void logout() {
		for ( WeakReference<GameClientListener> listenerRef : listeners ) {
			GameClientListener listener = listenerRef.get();
			if (listener == null)
				continue;
			listener.gameClientDisconnect(this);
		}
	}

	public void send(BaseMessage msg) {
		session.write(msg.getRawMessage());
	}
	
	@Override
	public void visit(GameMove move) {
		server.move(this, move);
	}
	
	@Override
	public void visit(StartGame start) {
		server.startGame(this, start);
	}
	
	public String toString() {
		return "Client " + getName() + " [" + session.getRemoteAddress() + "]";
	}

}

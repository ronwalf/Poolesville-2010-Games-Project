package net.volus.ronwalf.phs2010.networking.server;

import net.volus.ronwalf.phs2010.networking.message.Login;
import net.volus.ronwalf.phs2010.networking.message.MessageVisitorAdapter;

import org.apache.mina.core.session.IoSession;

public class NewClientHandler extends MessageVisitorAdapter {

	final private GameServer server;
	final private IoSession session;
	
	public NewClientHandler(GameServer server, IoSession session) {
		this.server = server;
		this.session = session;
	}
	
	@Override
	public void visit(Login login) {
		server.login(login, session);
	}

}

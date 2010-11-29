package net.volus.ronwalf.phs2010.networking.server;

import net.volus.ronwalf.phs2010.networking.message.Ack;
import net.volus.ronwalf.phs2010.networking.message.Login;
import net.volus.ronwalf.phs2010.networking.message.MessageFactory;
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
		server.login(login.getUser(), session);
		Ack reply =  MessageFactory.instance.reply(login, 200);
		reply.setMessage("Successful login");
		session.write(reply.getRawMessage());
	}

}

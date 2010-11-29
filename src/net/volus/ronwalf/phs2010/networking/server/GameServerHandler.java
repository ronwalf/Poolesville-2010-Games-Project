package net.volus.ronwalf.phs2010.networking.server;

import net.volus.ronwalf.phs2010.networking.message.Message;
import net.volus.ronwalf.phs2010.networking.message.MessageFactory;
import net.volus.ronwalf.phs2010.networking.raw.RawMessage;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;


public class GameServerHandler implements IoHandler {

	private static final MessageFactory factory = MessageFactory.instance;
	
	private final GameServer server;
	
	public GameServerHandler(GameServer server) {
		this.server = server;
	}
	
	public void exceptionCaught(IoSession session, Throwable e)
			throws Exception {
		session.close(true);
		e.printStackTrace();
	}

	public void messageReceived(IoSession session, Object obj) throws Exception {
		RawMessage raw = (RawMessage) obj;
		Message msg = factory.parse(raw);
		System.out.println("Message from " + session.getRemoteAddress());
		System.out.println(msg);
		msg.accept(server.getClientHandler(session));
		
	}

	public void messageSent(IoSession session, Object obj) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void sessionClosed(IoSession session) throws Exception {
		server.logout(session);
	}

	public void sessionCreated(IoSession session) throws Exception {
		System.out.println("New session for " + session.getRemoteAddress());
	}

	public void sessionIdle(IoSession session, IdleStatus arg1) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void sessionOpened(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		
	}

}

package net.volus.ronwalf.phs2010.networking.server;

import net.volus.ronwalf.phs2010.networking.message.Message;
import net.volus.ronwalf.phs2010.networking.message.MessageFactory;
import net.volus.ronwalf.phs2010.networking.raw.RawMessage;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class SampleServerHandler implements IoHandler {

	@SuppressWarnings("unused")
	final private SampleServer server;
	
	public SampleServerHandler(SampleServer server) {
		this.server = server;
	}
		
	
	public void exceptionCaught(IoSession session, Throwable e)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void messageReceived(IoSession session, Object msgObject) throws Exception {
		RawMessage raw = (RawMessage) msgObject;
		Message msg = MessageFactory.instance.parse(raw);
		// To send a message, use session.write(msg.getRawMessage())
		session.write(msg.getRawMessage()); // Just echoing back the received message
		
		
	}

	public void messageSent(IoSession session, Object msgObject) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void sessionClosed(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void sessionCreated(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void sessionOpened(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		
	}

}

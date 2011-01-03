/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package net.volus.ronwalf.phs2010.networking.client;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import net.volus.ronwalf.phs2010.games.checkers.CheckersMove;
import net.volus.ronwalf.phs2010.games.checkers.CheckersState;
import net.volus.ronwalf.phs2010.games.core.GamePlayer;
import net.volus.ronwalf.phs2010.games.util.SimpleController;
import net.volus.ronwalf.phs2010.networking.message.Ack;
import net.volus.ronwalf.phs2010.networking.message.GameResult;
import net.volus.ronwalf.phs2010.networking.message.GameState;
import net.volus.ronwalf.phs2010.networking.message.Message;
import net.volus.ronwalf.phs2010.networking.message.MessageFactory;
import net.volus.ronwalf.phs2010.networking.message.MessageVisitorAdapter;
import net.volus.ronwalf.phs2010.networking.message.Users;
import net.volus.ronwalf.phs2010.networking.raw.RawMessage;
import net.volus.ronwalf.phs2010.networking.raw.RawMessageCodecFactory;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.logging.MdcInjectionFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/**
 * {@link IoHandler} implementation of the client side of the simple chat protocol.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class SwingCheckersClientHandler extends IoHandlerAdapter {
	
	private static final MessageFactory factory = MessageFactory.instance;
	
    public interface AckCallback {
    	public void ack(IoSession session, Ack ack);
    }
    
    public final static AckCallback errorCloseBack = new AckCallback() {
    	public void ack(IoSession session, Ack ack) {
    		if (ack.getCode() >= 400) {
    			System.err.println(ack);
    			session.close(true);
    		}
    	}
    };

    private final SwingCheckersClient client;
    private IoSession session;
    private Map<String, AckCallback> sent = new HashMap<String,AckCallback>();

    public SwingCheckersClientHandler(SwingCheckersClient client) {
        this.client = client;
    }
    
    public boolean connect(SocketAddress address,
            boolean useSsl, String username) {
    	NioSocketConnector connector = new NioSocketConnector();
        if (session != null && session.isConnected()) {
            throw new IllegalStateException(
                    "Already connected. Disconnect first.");
        }

        try {
            IoFilter LOGGING_FILTER = new LoggingFilter();

            connector.getFilterChain().addLast("mdc", new MdcInjectionFilter());
            
//            if (useSsl) {
//                SSLContext sslContext = BogusSslContextFactory
//                        .getInstance(false);
//                SslFilter sslFilter = new SslFilter(sslContext);
//                sslFilter.setUseClientMode(true);
//                connector.getFilterChain().addFirst("sslFilter", sslFilter);
//            }

            connector.getFilterChain().addLast(
    				"codec",
    				new ProtocolCodecFilter( new RawMessageCodecFactory() ));
            connector.getFilterChain().addLast("logger", LOGGING_FILTER);

            connector.setHandler(this);
            ConnectFuture future1 = connector.connect(address);
            future1.awaitUninterruptibly();
            if (!future1.isConnected()) {
                return false;
            }
            session = future1.getSession();
            send(factory.login(username, ""), new AckCallback(){

				public void ack(IoSession session, Ack ack) {
					if (ack.getCode() == 200) {
						client.loggedIn();
					} else {
						session.close(true);
					}
					
				}
            	
            });
            
            return true;
        } catch (Exception e) {
        	e.printStackTrace();
            return false;
        }
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        client.connected();
    }

	@Override
	public void messageReceived(final IoSession session, Object obj) throws Exception {
		RawMessage raw = (RawMessage) obj;
		Message msg = factory.parse(raw);
		client.log(msg.toString() + "\n");
		msg.accept(new MessageVisitorAdapter() {

			@Override
			public void visit(Ack ack) {
				AckCallback callback = sent.get(ack.inReplyTo());
				if (callback != null)
					callback.ack(session, ack);
			}

			@Override
			public void visit(final GameResult result) {
				client.setGameFinished(result);
				
			}

			@Override
			public void visit(final GameState state) {
				client.setGameState(state);
			}

			@Override
			public void visit(final Users users) {
				client.setUsers(users);
			}
		});
    }
	
	public void send(Message message) {
		send(message, errorCloseBack);
	}
	
	public void send(Message message, AckCallback callback) {
		session.write(message.getRawMessage());
		sent.put(message.getId(), callback);
	}

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        client.disconnected();
    }
    
    public void quit() {
    	session.close(true);
    }

}

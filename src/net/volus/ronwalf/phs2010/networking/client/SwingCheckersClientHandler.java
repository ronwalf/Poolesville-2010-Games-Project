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

import net.volus.ronwalf.phs2010.networking.message.Message;
import net.volus.ronwalf.phs2010.networking.message.MessageFactory;
import net.volus.ronwalf.phs2010.networking.raw.RawMessage;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

/**
 * {@link IoHandler} implementation of the client side of the simple chat protocol.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class SwingCheckersClientHandler extends IoHandlerAdapter {
	
	private static final MessageFactory factory = MessageFactory.instance;
	
    public interface Callback {
        void connected();

        void loggedIn();

        void loggedOut();

        void disconnected();

        void messageReceived(Message message);

        void error(String message);
    }

    private final Callback callback;

    public SwingCheckersClientHandler(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        callback.connected();
    }

    @Override
    public void messageReceived(IoSession session, Object obj)
            throws Exception {
    	RawMessage raw = (RawMessage) obj;
		Message msg = factory.parse(raw);
		callback.messageReceived(msg);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        callback.disconnected();
    }

}

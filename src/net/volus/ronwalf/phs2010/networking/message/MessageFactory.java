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
package net.volus.ronwalf.phs2010.networking.message;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import net.volus.ronwalf.phs2010.networking.raw.RawMessage;

public class MessageFactory {
	
	public static final MessageFactory instance = new MessageFactory();
	
	private final String idbase;
	private long idcount = 0;
	private Map<String, MessageParser> parsers = new HashMap<String,MessageParser>();
	
	private MessageFactory() {
		String hostpart;
		try {
			hostpart = "::" + InetAddress.getLocalHost().getHostName() + "::" ;
		} catch (UnknownHostException e) {
			hostpart = "::localhost::";
		}
		idbase = hostpart + System.currentTimeMillis() + "::";
		
		parsers.put(Ack.COMMAND, AckParser.instance);
		parsers.put(GameMove.COMMAND, GameMoveParser.instance);
		parsers.put(GameResult.COMMAND, GameResultParser.instance);
		parsers.put(GameState.COMMAND, GameStateParser.instance);
		parsers.put(Login.COMMAND, LoginParser.instance);
		parsers.put(StartGame.COMMAND, StartGameParser.instance);
	}
	
	private String newId() {
		return idbase + (idcount++);
	}
	
	public Ack reply(BaseMessage msg, int code) {
		return new Ack(newId(), msg.getId(), code);
	}
	

	public GameMove gameMove(String name, String move) {
		return new GameMove(newId(), name, move);
	}
	
	public GameResult gameResult(String name, String message) {
		return new GameResult(newId(), name, message);
	}
	
	public GameState gameState(String type, String name, String turn, long timeLimit, String state) {
		return new GameState(newId(), type, name, turn, timeLimit, state);
	}
	
	public Login login(String user, String password) {
		return new Login(newId(), user, password);
	}
	
	
	public Message parse(RawMessage raw) throws MessageParsingException {
		MessageParser parser = parsers.get(raw.getCommand());
		
		if (parser == null)
			throw new MessageParsingException("No parser for " + raw.getCommand());
		
		return parser.parseMessage(raw);
	}

	public StartGame startGame(String gameType) {
		return new StartGame(newId(), gameType);
	}

 
}

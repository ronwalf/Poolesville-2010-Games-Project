package net.volus.ronwalf.phs2010.networking.message;

import net.volus.ronwalf.phs2010.networking.raw.RawMessage;

public class StartGameParser implements MessageParser {

public final static StartGameParser instance = new StartGameParser();
	
	public Message parseMessage(RawMessage raw) throws MessageParsingException {
		if (raw.getArguments().size() == 1) {
			return new StartGame(raw);
		}
		throw new MessageParsingException("Wrong number of arguments");
	}

}

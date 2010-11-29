package net.volus.ronwalf.phs2010.networking.message;

import net.volus.ronwalf.phs2010.networking.raw.RawMessage;

public class StartGame extends BaseMessage {

	public final static String COMMAND = "STARTGAME";
	
	public StartGame(RawMessage raw) {
		super(raw);
	}

	public StartGame(String id, String gameType) {
		super(id, COMMAND, gameType);
	}
	
	public void accept(MessageVisitor visitor) {
		visitor.visit(this);
	}
	
	public String getType() {
		return getRawMessage().getArguments().get(0);
	}

	@Override
	public String toString() {
		return "StartGame [getType()=" + getType() + "]";
	}

}

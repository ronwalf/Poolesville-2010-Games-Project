package net.volus.ronwalf.phs2010.networking.message;

import net.volus.ronwalf.phs2010.networking.raw.RawMessage;

public class GameMove extends BaseMessage {

	public static final String COMMAND = "GAMEMOVE";
		
	public GameMove(RawMessage raw) {
		super(raw);
		// TODO Auto-generated constructor stub
	}
	
	public GameMove(String id, String name, String move) {
		super(id, COMMAND, name);
		getRawMessage().setBody(move);
		
	}
	
	public String getName() { return getRawMessage().getArguments().get(0); }
	
	public String getMove() { return getRawMessage().getBody(); }

	public void accept(MessageVisitor visitor) {
		visitor.visit(this);
	}

}

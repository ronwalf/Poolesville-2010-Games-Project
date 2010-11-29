package net.volus.ronwalf.phs2010.networking.message;

import net.volus.ronwalf.phs2010.networking.raw.RawMessage;

public class GameResult extends BaseMessage {

	public static final String COMMAND = "GAMERESULT";
	
		
	public GameResult(RawMessage raw) {
		super(raw);
		// TODO Auto-generated constructor stub
	}
	
	public GameResult(String id, String name, String message) {
		super(id, COMMAND, name);
		getRawMessage().setBody(message);
		
	}
	
	public String getName() { return getRawMessage().getArguments().get(0); }
	
	public String getMessage() { return getRawMessage().getBody(); }

	public void accept(MessageVisitor visitor) {
		visitor.visit(this);
	}

}

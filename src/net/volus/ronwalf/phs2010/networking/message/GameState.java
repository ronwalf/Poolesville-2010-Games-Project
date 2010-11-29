package net.volus.ronwalf.phs2010.networking.message;

import net.volus.ronwalf.phs2010.networking.raw.RawMessage;

public class GameState extends BaseMessage {

	public static final String COMMAND = "GAMESTATE";
	
	public static final String TIME = "timelimit";
	
	public static final String TURN = "turn";
	
	public static final String TYPE = "type";
		
	public GameState(RawMessage raw) {
		super(raw);
		// TODO Auto-generated constructor stub
	}
	
	public GameState(String id, String type, String name, String turn, long timeLimit, String state) {
		super(id, COMMAND, name);
		getRawMessage().addHeader(TIME, new Long(timeLimit).toString());
		getRawMessage().addHeader(TYPE, type);
		getRawMessage().addHeader(TURN, turn);
		getRawMessage().setBody(state);
		
	}
	
	public String getName() { return getRawMessage().getArguments().get(0); }
	
	public long getTimeLimit() { return Long.parseLong(getRawMessage().getHeaders(TIME).get(0)); }
	
	public String getType() { return getRawMessage().getHeaders(TYPE).get(0); }
	
	public String getTurn() { return getRawMessage().getHeaders(TURN).get(0); }
	
	public String getState() { return getRawMessage().getBody(); }

	public void accept(MessageVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return "GameState [getName()=" + getName() + ", getType()=" + getType()
				+ ", getTurn()=" + getTurn() + ", getTimeLimit()="
				+ getTimeLimit() + ", getState()=" + getState() + "]";
	}

}

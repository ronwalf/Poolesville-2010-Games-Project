package net.volus.ronwalf.phs2010.networking.message;

import net.volus.ronwalf.phs2010.networking.raw.RawMessage;

public abstract class BaseMessage implements Message {

	private final static String MSGID = "Message-Id";
	
	private final String id;
	private final RawMessage raw;
	
	public BaseMessage(RawMessage raw) {
		this.raw = raw;
		id = raw.getHeaders(MSGID).get(0);
	}
	
	public BaseMessage(String id, String command, String... args) {
		this.id = id;
		this.raw = new RawMessage(command, args);
		this.raw.addHeader(MSGID, "" + id);
		
	}
	
	public String getId() { return id; }
		
	public RawMessage getRawMessage() { return raw; }
}

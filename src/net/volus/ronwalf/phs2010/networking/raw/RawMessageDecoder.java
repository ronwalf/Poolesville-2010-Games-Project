package net.volus.ronwalf.phs2010.networking.raw;

import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineDecoder;

public class RawMessageDecoder extends TextLineDecoder {

	private static class RMDecoderState {
		
		private abstract class RawStateMachine {
			
			abstract public RawStateMachine addLine(String line);
		}
		
		private class RawStartMachine extends RawStateMachine {
			public RawStateMachine addLine(String line) {
				line = line.trim();
				if (line.length() != 0) {
					String command = line.split("\\s+", 1)[0];
					String[] parts = line.replaceFirst("\\S+", "").trim().split("\\s+");
					msg = new RawMessage(command, parts);
					return new RawHeaderMachine();
				}
				return this;
			}
		}
		
		private class RawHeaderMachine extends RawStateMachine {
			public RawStateMachine addLine(String line) {
				line = line.trim();
				if (line.length() != 0) {
					String[] parts = line.split(":",2);
					String header = parts[0].trim();
					String value = parts[1].trim();
					msg.addHeader(header, value);
					return this;
				}
				
				if (msg.getHeaders("content").contains("delimited")) {
					return new RawDelimitedMachine();
				}
				return null;
				
			}
		}
		
		private class RawDelimitedMachine extends RawStateMachine {
			StringBuffer buf = new StringBuffer();
			String delim = null;
			
			public RawStateMachine addLine(String line) {
				if (delim == null) {
					line = line.trim();
					if (line.length() > 0)
						delim = line;
					return this;
				} else if (delim.equals(line.trim())) {
					msg.setBody( buf.toString() );
					return null;
				}
				
				if (buf.length() > 0) {
					buf.append(delimiter.getValue());	
				}
				buf.append(line);

				return this;
			}
		}
		
		private RawStateMachine state;
		private RawMessage msg;
		
		public RMDecoderState() {
			reset();
		}
		
		public RawMessage addLine(String line) {
			state = state.addLine(line);
			if (state == null) {
				RawMessage complete = msg;
				reset();
				return complete;
			}
			return null;
		}
		
		public void reset() {
			msg = null;
			state = new RawStartMachine();
		}
		
	}
	
	private final static LineDelimiter delimiter = LineDelimiter.CRLF;
	private final AttributeKey CONTEXT = new AttributeKey(getClass(), "context");
	
	public RawMessageDecoder() {
		super(delimiter);
	}
	
	@Override
	public void dispose(IoSession session) {
		if (session.getAttribute(CONTEXT) != null)
			session.removeAttribute(CONTEXT);
		
	}
	
	private RMDecoderState getDecoderState(IoSession session) {
		RMDecoderState state = (RMDecoderState) session.getAttribute(CONTEXT);
		if (state == null) {
			state = new RMDecoderState();
			session.setAttribute(CONTEXT, state);
		}
		return state;
	}
	
	@Override
	protected void writeText(IoSession session, String text, ProtocolDecoderOutput out) {
		RMDecoderState state = getDecoderState(session);
		
		RawMessage msg = state.addLine(text);
		if (msg != null)
			out.write(msg);
		
	}

	
}

package net.volus.ronwalf.phs2010.networking.raw;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.textline.LineDelimiter;

public class RawMessageEncoder extends ProtocolEncoderAdapter {

	private final static LineDelimiter delimiter = LineDelimiter.CRLF;
	private final static CharsetEncoder encoder = Charset.defaultCharset().newEncoder();
	
	public void encode(IoSession session, Object obj, ProtocolEncoderOutput output)
			throws Exception {
		RawMessage msg = (RawMessage) obj;
		
		// check to make sure content: delimited is in headers
		if (msg.getHeaders("content").isEmpty() && msg.getBody() != null)
			msg.addHeader("content", "delimited");
		
		IoBuffer buf = IoBuffer.allocate(msg.toString().length()).setAutoExpand(true);
		buf.putString(msg.getCommand(), encoder);
		for (String arg : msg.getArguments()) {
			buf.putString(" ", encoder);
			buf.putString(arg, encoder);
		}
		buf.putString(delimiter.getValue(), encoder);
		
		for (String header : msg.getHeaderFields()) {
			for (String val : msg.getHeaders(header)) {
				buf.putString(header, encoder);
				buf.putString(": ", encoder);
				buf.putString(val, encoder);
				buf.putString(delimiter.getValue(), encoder);
			}
		}
		buf.putString(delimiter.getValue(), encoder);
		
		if (msg.getBody() != null) {
			String delim = "====" + session.getLocalAddress().toString() + "++++" + System.nanoTime() + "====";
			buf.putString(delim, encoder);
			buf.putString(delimiter.getValue(), encoder);
			buf.putString(msg.getBody(), encoder);
			buf.putString(delimiter.getValue(), encoder);
			buf.putString(delim, encoder);
			buf.putString(delimiter.getValue(), encoder);
			buf.putString(delimiter.getValue(), encoder);
		}
		
		buf.flip();
		output.write(buf);
	}

}

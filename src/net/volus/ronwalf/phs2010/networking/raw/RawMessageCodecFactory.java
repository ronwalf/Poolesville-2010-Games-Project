package net.volus.ronwalf.phs2010.networking.raw;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class RawMessageCodecFactory implements ProtocolCodecFactory {

	ProtocolDecoder decoder = new RawMessageDecoder();
	ProtocolEncoder encoder = new RawMessageEncoder();
	
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		return decoder;
	}

	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		return encoder;
	}

}

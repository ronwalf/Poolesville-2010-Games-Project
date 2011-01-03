package net.volus.ronwalf.phs2010.networking.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import net.volus.ronwalf.phs2010.networking.raw.RawMessageCodecFactory;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.logging.MdcInjectionFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class SampleServer {

	public static void main(String... args) throws IOException {
		int localPort = Integer.parseInt(args[0]);
		
		IoFilter LOGGING_FILTER = new LoggingFilter();

		IoAcceptor acceptor = new NioSocketAcceptor();
		acceptor.getFilterChain().addLast("mdc", new MdcInjectionFilter());
		
		acceptor.getFilterChain().addLast(
				"codec",
				new ProtocolCodecFilter( new RawMessageCodecFactory() ));
		acceptor.getFilterChain().addLast("logger", LOGGING_FILTER);
		
		acceptor.setHandler(new SampleServerHandler(new SampleServer()));
		acceptor.bind( new InetSocketAddress( localPort ) );
	}

}

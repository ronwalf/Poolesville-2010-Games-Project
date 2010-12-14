/*
Copyright (c) 2010 Ron Alford <ronwalf@volus.net>
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:
1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products
   derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
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

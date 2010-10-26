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
package net.volus.ronwalf.phs2010.networking.raw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RawMessage {

	private String[] arguments;
	private String body;
	
	private String command;
	private Map<String, List<String>> headers;
	
	public RawMessage(String command, String... arguments) {
		this.command = command;
		this.arguments = arguments;
		headers = new HashMap<String, List<String>>();
		body = null;
	}
	
	public void addHeader(String header, String value) {
		List<String> values;
		if (!headers.containsKey(header)) {
			values = new ArrayList<String>(1);
			headers.put(header, values);
		} else {
			values = headers.get(header);
		}
		values.add(value);
	}
	
	
	public List<String> getArguments() {
		return Collections.unmodifiableList(Arrays.asList(arguments));
	}
	
	public String getBody() {
		return body;
	}
	
	public String getCommand() {
		return command;
	}
	
	public Set<String> getHeaderFields() {
		return Collections.unmodifiableSet(headers.keySet());
	}
	
	public List<String> getHeaders(String header) {
		if (!headers.containsKey(header))
			return Collections.emptyList();
		return Collections.unmodifiableList(headers.get(header));
	}
	
	public void setBody(String body) {
		this.body = body;
	}
}

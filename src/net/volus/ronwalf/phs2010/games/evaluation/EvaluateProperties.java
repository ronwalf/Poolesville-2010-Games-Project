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
package net.volus.ronwalf.phs2010.games.evaluation;

import java.lang.reflect.Field;

import net.volus.ronwalf.phs2010.games.core.Game;
import net.volus.ronwalf.phs2010.games.core.GamePlayerFactory;
import net.volus.ronwalf.phs2010.games.core.HeuristicFunction;

@SuppressWarnings("rawtypes")
public class EvaluateProperties {
	private final static String BASEPROP = "games.evaluate.";
	
	
	public final static String GAME = BASEPROP + "gameClass";
	public final static String GAMECOUNT = BASEPROP + "gameCount";
	public final static String PREMOVES = BASEPROP + "preMoves";
	public final static String MOVETIME = BASEPROP + "moveTime";
	
	
	public final static String PLAYER = BASEPROP + "player";
	public final static String HEURISTIC = BASEPROP + "heuristic";
	
	
	public static Game game() {
		return (Game) getInstance(GAME);
	}
	
	public static int gameCount() {
		return Integer.parseInt(System.getProperty(GAMECOUNT));
	}
	
	public static int preMoves() {
		return Integer.parseInt(System.getProperty(PREMOVES));
	}
	
	public static long moveTime() {
		return Long.parseLong(System.getProperty(MOVETIME));
	}
	
	public static GamePlayerFactory player(int n) {
		return (GamePlayerFactory) getInstance( PLAYER + n );
	}

	public static HeuristicFunction heuristic(int n) {
		return (HeuristicFunction) getInstance( HEURISTIC + n );
	}

		
	private static Object getInstance(String propName) {
		Object obj = null;
		try {
			Class gameCls = Class.forName(System.getProperty(propName));
			Field instField = gameCls.getField("instance");
			obj = instField.get(null);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
		
	}
	
}

package net.volus.ronwalf.phs2010.games.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerFactoryRegistry {
	
	private static List<String> factoryNames = new ArrayList<String>();
	private static Map<String, GamePlayerFactory> factories 
		= new HashMap<String, GamePlayerFactory>();
	
	public static void addFactory(String name, GamePlayerFactory factory) {
		if (!factories.containsKey(name))
			factoryNames.add(name);
		factories.put(name, factory);
	}
	
	
	public static GamePlayerFactory getFactory(String name) {
		return factories.get(name);
	}
	
	public static List<String> listFactories() {
		return Collections.unmodifiableList(factoryNames);
	}
	
}

package net.volus.ronwalf.phs2010.games.util;

import net.volus.ronwalf.phs2010.games.core.SearchController;

public class SimpleController implements SearchController {
	private final long moveTime;
	private long endTime;
	
	public SimpleController(long moveTime) {
		this.moveTime = moveTime;
	}
	
	public boolean isStopped() {
		return System.currentTimeMillis() > endTime;
	}

	public void start() {
		endTime = System.currentTimeMillis() + moveTime;
	}

	public void stop() {}

}

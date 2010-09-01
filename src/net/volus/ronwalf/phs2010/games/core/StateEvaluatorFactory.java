package net.volus.ronwalf.phs2010.games.core;

public interface StateEvaluatorFactory<State, Action> {
	public StateEvaluator<State> create(GameTransition<State,Action> transition);
}

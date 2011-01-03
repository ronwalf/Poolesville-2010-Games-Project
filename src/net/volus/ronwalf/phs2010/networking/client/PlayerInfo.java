package net.volus.ronwalf.phs2010.networking.client;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import net.volus.ronwalf.phs2010.games.core.PlayerState;
import net.volus.ronwalf.phs2010.networking.message.GameResult;
import net.volus.ronwalf.phs2010.networking.message.GameState;

public class PlayerInfo<State extends PlayerState> extends JPanel {

	JList list;
	boolean finished = true;
	String[] players;
	
	public PlayerInfo() {
		super();
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);
		
		TitledBorder border = BorderFactory.createTitledBorder("Player Info");
		setBorder(border);
		
		list = new JList();
		list.setListData(new String[]{"-------------"});
		add(list);
	}
	
	public void stateChanged(GameState msg, State state) {
		if (finished) {
			players = new String[state.playerCount()];
			for (int i = 0; i < state.playerCount(); i++) {
				players[i] = "" + i + ": ???";
			}
			finished = false;
		}
		players[state.playerTurn()] = "" + state.playerTurn() + ": " + msg.getTurn();
		list.setListData(players);
		
	}
	
	public void gameFinished(GameResult result) {
		finished = true;
	}
}

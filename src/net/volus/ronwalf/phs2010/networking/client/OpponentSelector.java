package net.volus.ronwalf.phs2010.networking.client;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class OpponentSelector implements ListSelectionListener {

	private class NameRenderer extends JLabel implements ListCellRenderer {

		public Component getListCellRendererComponent(JList list, Object value, // value
																				// to
																				// display
				int index, // cell index
				boolean isSelected, // is the cell selected
				boolean cellHasFocus) // the list and the cell have the focus
		{
			String s = value.toString();
			setText(s);
			
			setForeground(list.getForeground());
			if (index < selected.size()) {
				if (players.contains(listItems.get(index)))
					setBackground(Color.GREEN);
				else
					setBackground(Color.YELLOW);
			} else {
				setBackground(list.getBackground());
				
			}
			setEnabled(list.isEnabled());
			setFont(list.getFont());
			setOpaque(true);
			return this;
		}
	}

	private JList list;
	
	private DefaultListModel model;
	private Vector<String> listItems = new Vector();
	private Set<String> selected = new TreeSet<String>();
	private Set<String> players = new TreeSet<String>();
	
	public OpponentSelector() {
		
		list = new JList();
		list.addListSelectionListener(this);
		list.setCellRenderer(new NameRenderer());
	}
	
	public JList getList() {
		update();
		return list;
	}
	
	public List<String> getOpponents() { 
		return new ArrayList<String>(selected);
	}

	public void setPlayers(List<String> players) {
		this.players = new TreeSet<String>(players);
		update();
	}
	
	private void update() {
		listItems = new Vector<String>(selected);
		for (String name : players) {
			if (!selected.contains(name))
				listItems.add(name);
		}
		list.setListData(listItems);
	}
	
	public void valueChanged(ListSelectionEvent e) {
		int index = list.getSelectedIndex();
		if (index < 0)
			return;
		if (index < selected.size()) {
			selected.remove(listItems.get(index));
		}
		else {
			selected.add(listItems.get(index));
		}
		update();
	}
	
	
}

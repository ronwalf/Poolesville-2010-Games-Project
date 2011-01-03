package net.volus.ronwalf.phs2010.networking.client;

import java.awt.Color;
import java.awt.Component;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

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
			
			String name = listItems.get(index);
			if (selected.contains(name)) {
				if (users.contains(name))
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
	
	private Vector<String> listItems = new Vector<String>();
	private Set<String> selected = new TreeSet<String>();
	private Set<String> users = new TreeSet<String>();
	
	public OpponentSelector() {
		
		list = new JList();
		list.addListSelectionListener(this);
		list.setCellRenderer(new NameRenderer());
	}
	
	public JList getList() {
		update();
		return list;
	}
	
	public Collection<String> getOpponents() { 
		return Collections.unmodifiableCollection(selected);
	}

	public void setUsers(List<String> players) {
		this.users = new TreeSet<String>(players);
		update();
	}
	
	private void update() {
		//listItems = new Vector<String>(selected);
		Set<String> combined = new TreeSet<String>(selected);
		combined.addAll(users);
		listItems = new Vector<String>(combined);
//		for (String name : combined) {
//			if (!selected.contains(name))
//				listItems.add(name);
//		}
		listItems.add("          ");
		list.setListData(listItems);
	}
	
	public void valueChanged(ListSelectionEvent e) {
		int index = list.getSelectedIndex();
		if (index < 0 || index == listItems.size() - 1) 
			return;
		String name = listItems.get(index);
		
		if (selected.contains(name)) {
			selected.remove(name);
		}
		else {
			selected.add(name);
		}
		update();
	}
	
	
}

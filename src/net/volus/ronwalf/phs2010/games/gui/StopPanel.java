package net.volus.ronwalf.phs2010.games.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.volus.ronwalf.phs2010.games.core.SearchController;
import net.volus.ronwalf.phs2010.games.core.StoppedException;

public class StopPanel extends JPanel {

	private long waitMillis = 5 * 1000;
	private long startTime;
	private boolean stopped;
	
	private StopPanelController controller;
	
	private class StopPanelController implements SearchController {

		public void check() throws StoppedException {
			if (isStopped()) {
				throw new StoppedException();
			}
		}

		public boolean isStopped() {
			return stopped || (System.currentTimeMillis() > startTime + waitMillis);
		}
		
		public void start() {
			stopped = false;
			startTime = System.currentTimeMillis();
		}
		
	}
	
	public StopPanel() {
		final JSlider timeSlider = new JSlider(1, 30, 5);
		timeSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				waitMillis = timeSlider.getValue() * 1000;
			}
			
		});
		
		add(timeSlider);
		
		final JButton stopButton = new JButton("Stop!");
		stopButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				stopped = true;
			}
			
		});
		
		add(stopButton);
	}
	
	public SearchController getController() {
		return controller;
	}
	
}
